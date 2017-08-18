package com.github.stylejy.app.ParserSystem.JSON

import com.github.stylejy.app.Helpers.System.{DataTypeHelper, VariableCleanHelper}
import org.json4s._
import org.json4s.jackson.JsonMethods._

object YelpJSONParser extends VariableCleanHelper with DataTypeHelper{
  implicit lazy val formats = DefaultFormats

  def run(yelpData: String): Unit = {
    parseData(yelpData)

  }

  def parseData(yelpData: String): Unit = {


    val yelpJson = parse(yelpData)
    val data = yelpJson.extract[Json]
    for (i <- data.businesses) println(i)
    //println(yelpJson)
    //println(pretty(render(yelpJson)))

    //val data = yelpJson.extract[Json]

    //for (i <- data) println(i)
  }



}
