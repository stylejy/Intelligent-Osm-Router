package com.github.stylejy.app

import org.json4s._
import org.json4s.jackson.JsonMethods._
import scala.collection.mutable.Map

object JSONParser extends VariableCleaner{
  implicit lazy val formats = DefaultFormats
  var ways = Map[Long,List[Long]]()
  var lat: Double = 0
  var lng: Double = 0

  case class Details(id: Long, nodes: List[Long])
  case class geoJson(elements: List[Details])

  def run(lat: Double, lng: Double) = {
    println("JSON Parser :D")
    this.lat = lat
    this.lng = lng
    resetVariable(ways)
    parseOverpassJson()
  }

  def parseOverpassJson() = {
    println(lat + " " + lng)
    val overpassJson = parse(scala.io.Source.fromURL("http://overpass-api.de/api/interpreter?data=[out:json];(way(around:10," + lat + "," + lng + ")[\"highway\"~\"^(primary|secondary|tertiary|residential|unclassified|path)$\"];);out;").mkString)
    val data = overpassJson.extract[geoJson]
    println(data)
    for(i <- data.elements) {
      ways += (i.id -> i.nodes)
    }
    ways.keys.foreach{ i =>
      print( "Key = " + i )
      println(" Value = " + ways(i) )}
  }

  def calcDistance = {


  }

}
