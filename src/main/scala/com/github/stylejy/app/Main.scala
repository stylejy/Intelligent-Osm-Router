package com.github.stylejy.app

import com.github.stylejy.app.PathPlanningSystem.Algorithms.AlgoExplorer
import com.github.stylejy.app.PathPlanningSystem.Graph

/**
  * Created by stylejy on 15/06/2017.
  */
object Main {
  def main(args: Array[String]): Unit = {
    run
  }

  def run = {
    Graph.load()
    new AlgoExplorer(54, 14, 10).run
  }
}
