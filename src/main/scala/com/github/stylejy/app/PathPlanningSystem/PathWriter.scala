/*
 * Copyright 2011 Andlabs, GbR.
 *
 * This is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OsmRouting is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.stylejy.app.PathPlanningSystem

import com.github.stylejy.app.Helpers.System.{FileIOHelper, VariableCleanHelper}

import scala.collection.mutable.{ArrayBuffer, ListBuffer}

/**
  * Created by stylejy on 20/06/2017.
  */
object PathWriter extends VariableCleanHelper{
  val latlon = ArrayBuffer[LatLon]()
  case class LatLon(lat:Float, lon:Float)
  case class latlonModel(lat: Double, lon: Double)
  var in = FileIOHelper.in("latlns.bin")
  makeLatlon

  def update = {
    resetVariable(latlon)
    in = FileIOHelper.in("latlns.bin")
    makeLatlon
  }

  def write(path: List[Int]): ListBuffer[latlonModel] = {
    var pathOut = new ListBuffer[latlonModel]
    for (node <- path) yield {
      pathOut += latlonModel(latlon(node).lat, latlon(node).lon)
    }
    println("****************************************" + pathOut)
    pathOut
  }

  private def makeLatlon = {while(in.available != 0) {
    latlon += LatLon(in.readFloat,in.readFloat)
    println("latlon: " + latlon)
  }}
}
