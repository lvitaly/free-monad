package com.lvitaly.fm.naive

import scala.util.Random
import scala.io.StdIn.readLine

//format: off
/**
 * Welcome to the Guessing Game!
 * I'm thinking of a number between 1 and 10.
 * What's your guess?
 * 5
 * Sorry, that's not the correct number. Try again.
 * What's your guess?
 * 8
 * Sorry, that's not the correct number. Try again.
 * What's your guess?
 * 3
 * Congratulations! You guessed it!
 */
//format: on

sealed trait Monad[M[_]]:
  def pure[A](a: A): M[A] // to wrap a plain value into a "wrapper" type (It's our burrito :-P)
  def flatMap[A, B](ma: M[A])(f: A => M[B]): M[B]

object Monad:
  def apply[M[_]](using monad: Monad[M]): Monad[M] = monad

// The Free monad describes a similar “sequential” capability for a wrapper type and for a well-defined value type.

sealed trait Free[S[_], A]:
  def foldMap[G[_]: Monad](natTrans: S ~> G): G[A] =
    this match
      case Free.Pure(a)        => Monad[G].pure(a)
      case Free.FlatMap(fa, f) => Monad[G].flatMap(fa.foldMap(natTrans))(a => f(a).foldMap(natTrans))
      case Free.Suspend(ma)    => natTrans.apply(ma)

  def flatMap[B](f: A => Free[S, B]): Free[S, B] = Free.FlatMap(this, f)

  def map[B](f: A => B): Free[S, B] = flatMap(a => Free.pure(f(a))) // This map method will come in handy later.
end Free

object Free:
  private case class Pure[S[_], A](a: A)                                     extends Free[S, A]
  private case class FlatMap[S[_], A, B](fa: Free[S, A], f: A => Free[S, B]) extends Free[S, B]
  private case class Suspend[S[_], A](sa: S[A])                              extends Free[S, A]

  def pure[S[_], A](a: A): Free[S, A]      = Pure(a)
  def liftM[S[_], A](sa: S[A]): Free[S, A] = Suspend(sa)

// Natural transformation
sealed trait ~>[S[_], G[_]]:
  def apply[A](fa: S[A]): G[A]

// ADT
sealed trait Console[A]
case class PrintLine(line: String) extends Console[Unit]
case object ReadLine               extends Console[String]

// Use tagless-final instead of Monad[Console]
type ConsoleM[A] = Free[Console, A]

// DSL
def print(line: String): ConsoleM[Unit]     = Free.liftM(PrintLine(line))
def read: ConsoleM[String]                  = Free.liftM(ReadLine)
def ask(question: String): ConsoleM[String] = print(question).flatMap(_ => read)

case class IO[A](unsafeRun: () => A)
object IO:
  def create[A](a: => A): IO[A] = IO(() => a)

given ioMonad: Monad[IO] with
  def pure[A](a: A): IO[A]                           = IO.create(a)
  def flatMap[A, B](ma: IO[A])(f: A => IO[B]): IO[B] = IO.create(f(ma.unsafeRun()).unsafeRun())

object ConsoleInterpreter extends (Console ~> IO):
  def apply[A](fa: Console[A]): IO[A] =
    fa match
      case PrintLine(line) => IO.create(println(line))
      case ReadLine        => IO.create(readLine())

def guessing(secret: Int): Free[Console, Unit] =
  for {
    guess <- ask("What's your guess?")
    _     <- if secret == guess.toInt
             then Free.pure[Console, Unit](())
             else print("Sorry, that's not the correct number. Try again.").flatMap(_ => guessing(secret))
  } yield ()

val guessingGame: Free[Console, Unit] =
  for {
    _     <- print("Welcome to the Guessing Game!")
    _     <- print("I'm thinking of a number between 1 and 10.")
    secret = Random.between(1, 11)
    _     <- guessing(secret)
    _     <- print("Congratulations! You guessed it!")
  } yield ()

@main def run(): Unit =
  val game: IO[Unit] = guessingGame.foldMap(ConsoleInterpreter)
  game.unsafeRun()
