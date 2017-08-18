package com.github.stylejy.app.PathPlanningSystem.Algorithms

import com.github.stylejy.app.Helpers.System.{DataTypeHelper, YelpRequestHelper}

import scala.collection.mutable.ListBuffer


class AlgoPreferences(requestDetail: YelpRequestHelper.YelpRequest) extends DataTypeHelper{
  def run: Unit = {
    calcPrefs(requestDetail.pref)
  }

  private def calcPrefs(r: Preferences): ListBuffer[(String, Int)] = {
    var numOfTotalVisits = r.numOfTotalVisit
    var likePref = ListBuffer[String]()
    var neutralPref = ListBuffer[String]()
    val numOfPositiveAnswers = {
      val details = r.userDetails

      /**
        * shopping goes to the first place, and the pubs goes to the last.
        * This sequence will be used later.
        */
      details.shopping match {
        case 2 => likePref += "shopping"
        case 1 => neutralPref += "shopping"
        case _ =>
      }

      details.parks match {
        case 2 => likePref += "parks"
        case 1 => neutralPref += "parks"
        case _ =>
      }

      details.pubs match {
        case 2 => likePref += "pubs"
        case 1 => neutralPref += "pubs"
        case _ =>
      }

      likePref.size + neutralPref.size
    }

    /**
      * Like visits values are weighted more then neutral visits values
      */
    val numOfLikeVisits = (likePref.size.toFloat / numOfPositiveAnswers) * numOfTotalVisits * 1.25
    val numOfNeutralVisits = (neutralPref.size.toFloat / numOfPositiveAnswers) * (numOfTotalVisits)

    val numOfVisitsForEach = {
      val results = ListBuffer[(String, Int)]()

      calc(likePref, numOfLikeVisits.toInt)
      calc(neutralPref, numOfNeutralVisits.toInt)

      def calc(pref: ListBuffer[String], num: Int): Unit = {
        val prefSize = pref.size
        /**
          * shopping has the most priority, and then parks and pub in order.
          */
        while (numOfTotalVisits > 0 && pref.size > 0) {
          println(pref + " " + num)
          val numOfVisits = num/prefSize
          results += Tuple2(pref.remove(0), numOfVisits)
          numOfTotalVisits -= numOfVisits
        }
      }
      results
    }

    println(numOfVisitsForEach)
    numOfVisitsForEach
  }
}
