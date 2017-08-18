package com.github.stylejy.app.ParserSystem.JSON

import com.github.stylejy.app.Helpers.System.VariableCleanHelper
import scala.collection.mutable.ListBuffer
import org.json4s._
import org.json4s.jackson.JsonMethods._

object YelpJSONParser extends VariableCleanHelper {
  implicit lazy val formats = DefaultFormats

  case class Json(businesses: List[YelpDetails])
  case class YelpDetails(name: String, distance: Int, coordinates: Map[String, Float])

  def run(yelpData: String): ListBuffer[(String, Int, (Float, Float))] = {
    parseData(yelpData)
  }

  def parseData(yelpData: String): ListBuffer[(String, Int, (Float, Float))] = {

    val yelpJson = parse(yelpData)
    val data = yelpJson.extract[Json]
    val results = ListBuffer[(String, Int, (Float, Float))]()

    for (i <- data.businesses) {
      results += Tuple3(i.name, i.distance, Tuple2(i.coordinates("latitude"), i.coordinates("longitude")))
    }

    results
  }



}
