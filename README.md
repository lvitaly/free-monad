[![Made in Ukraine](https://img.shields.io/badge/made_in-Ukraine-ffd700.svg?labelColor=0057b7)](https://stand-with-ukraine.pp.ua)
[![Cats Friendly Badge](https://typelevel.org/cats/img/cats-badge-tiny.png)](https://typelevel.org/cats/)

Guessing Game with Free Monads
===

Welcome to the Guessing Game project written in [Scala 3](https://www.scala-lang.org), designed as a practical
demonstration for presentations on Free Monads. This project serves as a learning tool to showcase the creation of a
Free Monad, the DSL, and interpreting it with different interpreters. It was originally used as a live coding example
during a presentation to illustrate these concepts.

## Project Structure

The project is divided into the following main components:

- **DSL**: Defines the domain-specific language for the guessing game.
- **Free Monad**: Implements the Free Monad pattern.
- **Interpreters**: Provides different interpreters for the DSL, including a console interpreter and a WebSocket
  interpreter.
- **Http4s Server**: Sets up an HTTP server using Http4s to handle WebSocket connections.

## Dependencies

- [Cats](https://typelevel.org/cats/): A library which provides abstractions for functional programming in Scala.
- [Http4s](https://http4s.org): A library for building type-safe, functional HTTP services.
- [Fs2](https://fs2.io/): A library for functional streams in Scala.

## Getting Started

### Prerequisites

- [Scala 3](https://www.scala-lang.org)
- [SBT](https://www.scala-sbt.org)
- [Java 8+](https://www.oracle.com/java/technologies/javase-downloads.html)

You can use the provided `flake.nix` file to create a development environment with [Nix](https://nixos.org) and
[Flake](https://nixos.wiki/wiki/Flakes). To do this, run:

```shell
nix develop
```

### Building the Project

#### Console Version

To run the console version of the guessing game, use:

```shell
sbt "runMain com.lvitaly.fm.cats.consoleGame"
```

After starting the console version, you can interact with the game using the console. In the console, you will be
prompted to enter your guesses. The game will provide feedback on whether your guess is correct or not. In this version,
the game is available only only for a single player.

#### WebSocket Version

To start the WebSocket based version of the guessing game, use:

```shell
sbt "runMain com.lvitaly.fm.cats.WebSocketGame"
```

After starting the server, you can connect to it using a WebSocket client. For example, you can use
the [websocat](https://github.com/vi/websocat):

```shell
websocat -E ws://localhost:8080/ws
```

In the WebSocket version, multiple players can connect to the server and play the game simultaneously. The server will
keep track of the players' progress and provide feedback on the guesses. So the user experience will be similar to the
console version.

## Learn More

### Talks

- [Free monads in Scala](https://youtu.be/lzlCjgRWPDU?si=6L5vwng4N-UlQ21f), Rock the JVM, 2022
- [An Intuitive Guide to Combining Free Monad and Free Applicative](https://youtu.be/Qg48XucSvlg?si=Vy-PbhzViT75aCU7),
  Cameron Joannidis, Typelevel, 2018
- [Free monad or tagless final? How not to commit to a monad too early](https://youtu.be/IhVdU4Xiz2U?si=DIQUoXB61trUSm8F),
  Scala IO FR, Adam Warski, 2017
- [Why the free Monad isn't free](https://youtu.be/U0lK0hnbc4U?si=DWyUQ0M-cxXkmolT), Kelley Robinson, Scala Days NY 2016

### Articles

- [The debatably Free monad](https://nrinaudo.github.io/articles/free_monad.html), Nicolas Rinaudo, 2023
- [Free Monad](https://blog.rockthejvm.com/free-monad/), Rock the JVM, 2022
- [Free monads - what? and why?](https://softwaremill.com/free-monads/), Adam Warski, 2015
