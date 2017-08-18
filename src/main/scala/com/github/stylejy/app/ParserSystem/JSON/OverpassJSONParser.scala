package com.github.stylejy.app.ParserSystem.JSON

import com.github.stylejy.app.Helpers.System.{DataTypeHelper, VariableCleanHelper}
import com.github.stylejy.app.PathPlanningSystem.MapData
import org.json4s._
import org.json4s.jackson.JsonMethods._

import scala.collection.mutable.{ArrayBuffer, Map}

object OverpassJSONParser extends VariableCleanHelper with DataTypeHelper{
  /**
    * To extract String values using json4s.
    */
  implicit lazy val formats = DefaultFormats
  var ways = Map[Long,List[Long]]()
  var lat: Double = 0
  var lon: Double = 0
  //Updated by Graph
  var nodeIds = ArrayBuffer[Long]()

  def run(lat: Double, lng: Double) = {
    println("JSON Parser :D")
    this.lat = lat
    this.lon = lng
    resetVariable(ways)
    parseOverpassJson()
    calcDistance
  }

  def parseOverpassJson() = {
    println(lat + " " + lon)
    val overpassJson = parse(scala.io.Source.fromURL("http://overpass-api.de/api/interpreter?data=[out:json];(way(around:50," + lat + "," + lon + ")[\"highway\"~\"^(primary|secondary|tertiary|residential|unclassified|path)$\"];);out;").mkString)
    println(overpassJson)
    val data = overpassJson.extract[GeoJson]
    for(i <- data.elements) {
      ways += (i.id -> i.nodes)
    }
    ways.keys.foreach{ i =>
      print( "Key = " + i )
      println(" Value = " + ways(i) )}
  }

  def calcDistance: Int = {
    println("node id size: " + nodeIds.size)
    println("latlon size: " + MapData.latlon.size)
    var shortestDist: Double = -1
    var shortestPosition = 0

    for (i <- 0 until nodeIds.size-1) {
      val distLat = MapData.latlon(i).lat - lat
      val distLng = MapData.latlon(i).lon - lon
      /** We don't need an actual distance, so we don't calculate a square root of the sum. */
      val dist = (distLat*distLat) + (distLng*distLng)
      println(nodeIds(i) + " " + MapData.latlon(i))
      println(dist)
      if (shortestDist < 0) {
        shortestController(dist, i)
      } else {
        if (shortestDist > dist) {
          shortestController(dist, i)
        }
      }
    }
    def shortestController(dist: Double, position: Int): Unit = {
      shortestDist = dist
      shortestPosition = position
    }
    println("shortest dist: " +shortestDist)
    println("shortest Latlon: "+ MapData.latlon(shortestPosition))
    /** returns the final shortest node's position in the array which will be used in AlgoDijkstra */
    shortestPosition
  }
}
