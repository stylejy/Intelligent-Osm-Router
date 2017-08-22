package com.github.stylejy.app.Helpers.System

import com.github.stylejy.app.ParserSystem.Osm.OsmParser.Node

import scala.math.{acos, cos, sin, toRadians}

object DistanceCalculator {
  def calculator(from: Node, to: Node): Int = {
    val lat1 = toRadians(from.lat)
    val lon1 = toRadians(from.lon)
    val lat2 = toRadians(to.lat)
    val lon2 = toRadians(to.lon)

    //Using Spherical Law of Cosines to get the distance between the two pairs of coordinates.
    ((6378.388f * acos(
      sin(lat1) * sin(lat2)
        + cos(lat1) * cos(lat2)
        * cos(lon2 - lon1)))*1000).toInt

  }
}
