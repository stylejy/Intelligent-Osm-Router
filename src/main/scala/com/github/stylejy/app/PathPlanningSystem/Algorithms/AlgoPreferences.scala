package com.github.stylejy.app.PathPlanningSystem.Algorithms

import com.github.stylejy.app.Helpers.System.YelpRequestHelper
import com.github.stylejy.app.ParserSystem.JSON.YelpJSONParser

import scala.collection.mutable.ListBuffer

class AlgoPreferences(lat: Float, lon: Float,
                      numberOfTotalVisits: Int, maxRadius: Int,
                      shopping: Int, parks: Int, pubs: Int) {
  def run: Unit = {
    val yelpData = YelpRequestHelper.run(lat, lon, maxRadius, calcPrefs(numberOfTotalVisits, shopping, parks, pubs))
    val results = ListBuffer[(String, (String, Int, (Float, Float)))]()
    /**
      * The number of the found places might exceed the number of total visits that the user has specified.
      * Therefore, additional variable is used to check if it doesn't exceed.
      */
    var totalVisits = numberOfTotalVisits
    for (i <- yelpData) {
      /**
        * i._1 represents detailed preference names(shopping, parks, pubs).
        * i._2 represents the details of found places from Yelp.
        */
      for (j <- YelpJSONParser.run(i._2)) {
        /**
          * Yelp api might return some place with the longer distance than what it requests.
          * Therefore, need to filter the places with the exceeded distances.
          */
        if (totalVisits > 0 && j._2 <= maxRadius) {
          results += Tuple2(i._1, j)
          totalVisits -= 1
        }
      }
    }
    val sortedResultsByDist = results.sortWith(_._2._2 < _._2._2)
    println(sortedResultsByDist)
  }

  private def calcPrefs(numTotal: Int, shopping: Int, parks: Int, pubs: Int): ListBuffer[(String, Int)] = {
    var numOfTotalVisits = numTotal
    var likePref = ListBuffer[String]()
    var neutralPref = ListBuffer[String]()
    val numOfPositiveAnswers = {
      /**
        * shopping goes to the first place, and the pubs goes to the last.
        * This sequence will be used later.
        */
      shopping match {
        case 2 => likePref += "shopping"
        case 1 => neutralPref += "shopping"
        case _ =>
      }

      parks match {
        case 2 => likePref += "parks"
        case 1 => neutralPref += "parks"
        case _ =>
      }

      pubs match {
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
