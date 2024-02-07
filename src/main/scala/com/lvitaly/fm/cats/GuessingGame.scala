package com.lvitaly.fm.cats

import cats.free.Free

import scala.util.Random

// ADT
sealed trait Console[A]
case class PrintLine(line: String) extends Console[Unit]
case object ReadLine               extends Console[String]

// Use tagless-final instead of Monad[Console]
type ConsoleM[A] = Free[Console, A]

// DSL
def print(line: String): ConsoleM[Unit]     = Free.liftF(PrintLine(line))
def read: ConsoleM[String]                  = Free.liftF(ReadLine)
def ask(question: String): ConsoleM[String] = print(question).flatMap(_ => read)

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
