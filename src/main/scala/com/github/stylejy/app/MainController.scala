package com.github.stylejy.app

/**
  * Created by stylejy on 15/06/2017.
  */
object MainController {
  def main(args: Array[String]): Unit = {
    run()
  }

  def run(): Unit = {
    OsmParser.run
    Graph.load
  }
}
