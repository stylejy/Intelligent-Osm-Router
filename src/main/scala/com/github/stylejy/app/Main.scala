package com.github.stylejy.app

import com.github.stylejy.app.PathPlanningSystem.Algorithms.{AlgoClassic, AlgoExplorer, AlgoPreferences}
import com.github.stylejy.app.PathPlanningSystem.MapData

/**
  * Created by stylejy on 15/06/2017.
  */
object Main {
  def main(args: Array[String]): Unit = {
    run
  }

  def run = {
    MapData.load
    println(new AlgoClassic(215, 280, 1).getPath)

    /*val lat = 51.512272
    val lon = -0.122135
    val numberOfVisit = 3
    val maxRadius = 100
    val shopping = 2
    val parks = 0
    val pubs = 0

    new AlgoPreferences(lat.toFloat, lon.toFloat, 36, numberOfVisit, maxRadius, shopping, parks, pubs).run*/
  }
}
