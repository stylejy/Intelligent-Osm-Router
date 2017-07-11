package com.github.stylejy.app

/**
  * Created by stylejy on 15/06/2017.
  */
object MainController {
  def main(args: Array[String]): Unit = {
    run
  }

  def run = {
    OsmParser.run
    Graph.load
  }
}
