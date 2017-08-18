package com.github.stylejy.app.Helpers.System
import scala.collection.mutable.ListBuffer
import scalaj.http._

object YelpRequestHelper {

  def run(lat: Float, lon: Float, maxRadius: Int, calcPrefs: ListBuffer[(String,Int)]): ListBuffer[(String,String)] = {
    val results = ListBuffer[(String,String)]()
    for (i <- calcPrefs) {
      /**
        * i._1 represents detailed preference names(shopping, parks, pubs).
        * i._2 represents the number of the places the user could visit.
        */
      results += Tuple2((i._1), connect(lat, lon, i._1, i._2, maxRadius))
    }
    results
  }

  private def connect(lat: Float, lon: Float, term: String, numOfVisits: Int, maxRadius: Int): String = {
    val url = "https://api.yelp.com/v3/businesses/search?sort_by=rating&term="+ term +
      "&latitude=" + lat + "&longitude=" + lon +
      "&radius=" + maxRadius + "&limit=" + numOfVisits
    val headerKey = "Authorization"
    val headerValue = "Bearer Q1yOFYfNclRRaZZdiSsAGkPWahXEfrntaNkhZxc1yaFsm2YmaTUF4GL2wbg2DZu_qmI75D6ITlbs_b-AZ6FHTFNoQ2REJMMQDqckZ0B7l3Agepr5Q83Bg4KXEcSVWXYx"
    val response = Http(url).header(headerKey, headerValue).asString

    if (response.code.equals(200)) response.body
    else {
      println(response.code)
      null
    }
  }

}
