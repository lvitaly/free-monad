package com.lvitaly.fm.cats

import cats.effect.std.Queue
import cats.effect.{ExitCode, IO, IOApp, Resource}
import cats.syntax.all.*
import cats.~>
import com.comcast.ip4s.*
import fs2.*
import fs2.concurrent.SignallingRef
import org.http4s.dsl.io.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import org.http4s.server.websocket.WebSocketBuilder2
import org.http4s.websocket.WebSocketFrame
import org.http4s.{HttpRoutes, Response}

class WebSocketInterpreter(
    signal: SignallingRef[IO, Boolean],
    in: Queue[IO, WebSocketFrame],
    out: Queue[IO, WebSocketFrame]
) extends (Console ~> IO):
  def apply[A](fa: Console[A]): IO[A] =
    fa match
      case PrintLine(line) => out.offer(WebSocketFrame.Text(line)).as(())
      case ReadLine        =>
        for {
          _ <- signal.set(true)
          s <- Stream
                 .fromQueueUnterminated(in, 1)
                 .collect {
                   case WebSocketFrame.Text(text, _) if text.trim.nonEmpty =>
                     text.trim
                 }
                 .head
                 .compile
                 .onlyOrError
          _ <- signal.set(false)
        } yield s

object WebSocketGame extends IOApp:
  private def routes(ws: WebSocketBuilder2[IO]): HttpRoutes[IO] =
    HttpRoutes.of[IO] { case GET -> Root / "ws" =>
      for {
        signal <- SignallingRef[IO, Boolean](false)
        inQ    <- Queue.bounded[IO, WebSocketFrame](1)
        outQ   <- Queue.bounded[IO, WebSocketFrame](1)
        _      <- guessingGame
                    .foldMap(new WebSocketInterpreter(signal, inQ, outQ))
                    .flatMap(_ => outQ.offer(WebSocketFrame.Close()))
                    .start
        send    = Stream.fromQueueUnterminated(outQ)
        receive = (in: Stream[IO, WebSocketFrame]) => in.evalFilter(_ => signal.get).evalMap(inQ.offer)
        res    <- ws.build(send, receive)
      } yield res
    }

  private val server: Resource[IO, Server] =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpWebSocketApp(ws => routes(ws).orNotFound)
      .build

  def run(args: List[String]): IO[ExitCode] =
    Stream
      .resource(server >> Resource.never)
      .compile
      .drain
      .as(ExitCode.Success)
