package com.github.stylejy.app.PathPlanningSystem.Algorithms

import com.github.stylejy.app.Helpers.System.YelpRequestHelper
import com.github.stylejy.app.ParserSystem.JSON.{OverpassJSONParser, YelpJSONParser}
import com.github.stylejy.app.PathPlanningSystem.MapData

import scala.collection.mutable.{ArrayBuffer, ListBuffer}

class AlgoPreferences(lat: Float, lon: Float, source: Int,
                      numberOfTotalVisits: Int, maxRadius: Int,
                      shopping: Int, parks: Int, pubs: Int) {

  /**
    * [(String, (String, Int, (Float, Float)))]
    * means [(Type of business, (business name, distance, (latitude, longitude)))]
    */
  val sortedPlacesByDist = ListBuffer[(String, (String, Int, (Float, Float)))]()
  def run: List[ListBuffer[Int]] = {
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
    sortedPlacesByDist ++= results.sortWith(_._2._2 > _._2._2)
    println(sortedPlacesByDist)

    getPath(sortedPlacesByDist)
  }

  private def getPath(sortedPlaces: ListBuffer[(String, (String, Int, (Float, Float)))]): List[ListBuffer[Int]] = {

    val destNodes = ListBuffer[Int]()
    val indexedPaths = ListBuffer[ListBuffer[Int]]()


    for (i <- sortedPlaces) {
      val lat = i._2._3._1
      val lon = i._2._3._2

      /**
        * If the lat and lon exceed the map boundary,
        * They are not added to the destNodes.
        */
      if (checkBoundaryValidity(lat, lon))
        destNodes += findNodes(lat, lon)
    }
    /**
      * This path is for return trip. So indexedPaths should be wrapped around by the user starting point.
      */
    destNodes.insert(0, source)
    destNodes += source

    /**
      * This makes it avoid drawing wrong paths that the empty path results from a classic algorithm cause.
      */
    var iterator = 0
    while (iterator < destNodes.size - 1) {
      println("getPath : " + destNodes(iterator) + " " + destNodes(iterator+1))
      val astartResult = getAstarPath(destNodes(iterator), destNodes(iterator+1))

      /**
        * If the classic result is empty,
        * First, remove the correspond place from the sorted places list.
        * Second, remove the sorrespond node from the desk node list.
        * Last, iterator "will not" increase in this case because the new value has come at the next location automatically
        * by the previous step.
        */
      if (astartResult.isEmpty) {
        sortedPlacesByDist.remove(iterator)
        destNodes.remove(iterator+1)
      } else {
        val paths = ListBuffer[Int]()
        paths ++= astartResult
        indexedPaths.insert(0, paths)
        iterator += 1
      }
    }
    println("AlgoPreferences " + indexedPaths)
    indexedPaths.toList
  }

  /**
    * It checks if the coordinates are withing the map boundary.
    * @return if there are in, it returns true, otherwise, false.
    */
  private def checkBoundaryValidity(lat: Float, lon: Float): Boolean = {
    val bound = MapData.getBound

    if (lat > bound.minlat && lat < bound.maxlat && lon > bound.minlon && lon < bound.maxlon) true
    else false
  }

  private def getAstarPath(source: Int, target: Int): ListBuffer[Int] = {
    new AlgoClassic(source, target, 1).getPath.to[ListBuffer]
  }

  private def findNodes(lat: Float, lon: Float): Int = {
    OverpassJSONParser.run(lat, lon)
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
          /**
            * If num/prefSize is lower than 1, integer division always makes it zero.
            * Therefore, some cases must be defined to get correct answer.
            * 0 < num < 1, it will be rounded up to 1.
            * num >= 1, it will be rounded down to the closest lower integer number.
            */
          val numOfVisits = {
            val doubleNum = num.toDouble/prefSize
            if (doubleNum > 0.0 && doubleNum < 1) math.ceil(doubleNum).toInt
            else if (doubleNum >= 1) math.floor(doubleNum).toInt
            else 0
          }
          println("numOfVisits " + numOfVisits)
          results += Tuple2(pref.remove(0), numOfVisits)
          numOfTotalVisits -= numOfVisits
        }
      }
      results
    }

    println("calcpref: " + numOfVisitsForEach)
    numOfVisitsForEach
  }
}
