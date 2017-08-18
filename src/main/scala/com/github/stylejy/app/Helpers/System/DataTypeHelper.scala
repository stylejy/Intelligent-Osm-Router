package com.github.stylejy.app.Helpers.System

trait DataTypeHelper {

  /**
    * For Map Data
    */
  case class LatLon(lat:Float, lon:Float)
  case class LatLonModel(lat: Double, lon: Double)

  /**
    * For Overpass JSON Parser
    * These are for the extracting structure.
    */
  case class OverpassDetails(id: Long, nodes: List[Long])
  case class GeoJson(elements: List[OverpassDetails])


  /**
    * For Yelp JSON Parser
    * These are for the extracting structure.
    */
  case class Json(businesses: List[YelpDetails])
  case class YelpDetails(id: String, name: String)

  /**
    * For Yelp Request Helper
    */
  case class YelpRequest(latlon: LatLon, pref: Preferences)
  case class Preferences(numOfTotalVisit: Int, maxRadius: Int, userDetails: UserDetail)
  case class UserDetail(shopping: Int, parks: Int, pubs: Int)
}
