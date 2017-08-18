package com.github.stylejy.app.Helpers.System

import scala.collection.mutable.ListBuffer
import scalaj.http._

object YelpRequestHelper extends DataTypeHelper{

  def run(requestDetail: YelpRequest): Unit = {
    val connection = request(requestDetail)
    /*if (!connection.equals(null)) {
      val response = connection
      YelpJSONParser.run(response.body)
    }*/
  }

  private def request(r: YelpRequest): Unit = {
    val analysedPrefs = calcPrefs(r.pref)
    for (i <- analysedPrefs) {
      println(connect(r.latlon, i._1, i._2, r.pref.maxRadius))
    }
  }




  private def connect(latlon: LatLon, term: String, numOfVisits: Int, maxRadius: Int): HttpResponse[String] = {
    val url = "https://api.yelp.com/v3/businesses/search?sort_by=rating&term="+ term +
      "&latitude=" + latlon.lat + "&longitude=" + latlon.lon +
      "&radius=" + maxRadius + "&limit=" + numOfVisits
    val headerKey = "Authorization"
    val headerValue = "Bearer Q1yOFYfNclRRaZZdiSsAGkPWahXEfrntaNkhZxc1yaFsm2YmaTUF4GL2wbg2DZu_qmI75D6ITlbs_b-AZ6FHTFNoQ2REJMMQDqckZ0B7l3Agepr5Q83Bg4KXEcSVWXYx"
    val response = Http(url).header(headerKey, headerValue).asString

    if (response.code.equals(200)) response
    else {
      println(response.code)
      null
    }
  }

}
