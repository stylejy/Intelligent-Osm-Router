package com.github.stylejy.app

import com.github.stylejy.app.Helpers.System.{DataTypeHelper, YelpRequestHelper}
import com.github.stylejy.app.PathPlanningSystem.Algorithms.{AlgoClassic, AlgoExplorer}
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
    //new AlgoClassic(54, 14, 1).run
    val ud = new YelpRequestHelper.UserDetail(1, 1, 2)
    val prefs = new YelpRequestHelper.Preferences(5, 100, ud)
    val r = new YelpRequestHelper.YelpRequest(new YelpRequestHelper.LatLon(51.512272.toFloat, -0.122135.toFloat), prefs)

    case class Preferences(numOfTotalVisit: Int, maxRadius: Int, userDetails: UserDetail)
    case class UserDetail(shopping: Int, parks: Int, pubs: Int)
    YelpRequestHelper.run(r)
    new AlgoExplorer()
  }
}
