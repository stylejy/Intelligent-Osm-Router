package com.github.stylejy.app

import com.github.stylejy.app.PathPlanningSystem.Algorithms.{AlgoClassic, AlgoExplorer, AlgoPreferences}
import com.github.stylejy.app.PathPlanningSystem.MapData

/**
  * Created by stylejy on 15/06/2017.
  */
object Main {
  def main(args: Array[String]): Unit = {
    MapData.load
    test
  }

  def test = {
    val smallList = List(
      (1913,763),
      (2450,728),
      (900,2075),
      (2498,334),
      (1507,359),
      (1429,2929),
      (1404,2144),
      (36,334),
      (2609,1127),
      (536,1260))

    val mediumList = List(
      (1219,2110),
      (53,8166),
      (3418,10908),
      (2190,3501),
      (1961,2171),
      (2293,7902),
      (6789,1831),
      (8464,1831),
      (3862,666),
      (10612,3491))

    val largeList = List(
      (14241,5664),
      (26088,27814),
      (38663,5775),
      (3168,16809),
      (31217,5653),
      (44975,41871),
      (6626,58458),
      (15422,12529),
      (17339,7859),
      (33397,58049))

    var counter = 1
    for (node <- largeList) {
      println(counter)
      for (i <- 0 until 10) {
        val start = System.currentTimeMillis()
        //val path = new AlgoClassic(node._1, node._2, 1).getPath
        val path = new AlgoExplorer(node._1, node._2, 100).run
        println((System.currentTimeMillis() - start) + "ms  (" + path.size + " nodes)")
      }
      counter += 1
      println()
    }
  }
}
