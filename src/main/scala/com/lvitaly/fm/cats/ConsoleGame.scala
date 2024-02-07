package com.lvitaly.fm.cats

import cats.{~>, Id}
import scala.io.StdIn.readLine

object ConsoleInterpreter extends (Console ~> Id):
  override def apply[A](fa: Console[A]): Id[A] =
    fa match
      case PrintLine(line) => println(line)
      case ReadLine        => readLine()

@main def consoleGame(): Unit =
  guessingGame.foldMap(ConsoleInterpreter)