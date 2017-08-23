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

import com.github.stylejy.app.Helpers.System.VariableCleanHelper
import com.github.stylejy.app.PathPlanningSystem.MapData.LatLonModel

import scala.collection.mutable.{ArrayBuffer, ListBuffer}

/**
  * Created by stylejy on 20/06/2017.
  */
object PathWriter extends VariableCleanHelper{
  def write(path: List[Int]): ListBuffer[MapData.LatLonModel] = {
    val pathOut = ListBuffer[MapData.LatLonModel]()
    for (node <- path) {
      pathOut += LatLonModel(MapData.latlon(node).lat, MapData.latlon(node).lon)
    }
    //println("****************************************" + pathOut)
    pathOut
  }

  def writeForPrefs(path: List[ListBuffer[Int]]): ArrayBuffer[ListBuffer[MapData.LatLonModel]] = {
    case class PathLatLon(lat: Float, lon: Float)
    case class indexPath(index: Int, latlon: PathLatLon)

    val pathOut = ArrayBuffer[ListBuffer[MapData.LatLonModel]]()

    for (sub <- path) {
      val subPath = ListBuffer[MapData.LatLonModel]()
      for (i <- sub) {
        subPath += LatLonModel(MapData.latlon(i).lat, MapData.latlon(i).lon)
      }
      pathOut += subPath
    }
    //println("****************************************" + pathOut)
    pathOut
  }
}
