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

object NodeModel extends VariableCleanHelper {

  case class GraphNode(val id: Int, var dist: Int, var pred: GraphNode, var index: Int) {
    def visited = index > 0
    //def settled = index == -1
    //def relaxed = index >= +1

    def foreach_outgoing(fun: (Int,Int) => Unit) {
      //println("!!!!!!!!!!!!final node_array: " + nodeArray)
      //println("!!!!!!!!!!!!final edge_array: " + edgeArray)
      //println("!!!!!!!!!!!!node_array id: " + nodeArray(id) + " until " + nodeArray(id+1))
      for (i <- MapData.nodeArray(id.toInt) until MapData.nodeArray(id+1)) {
        fun(MapData.edgeArray(i.toInt), MapData.distArray(i.toInt))  // call function foreach neighbour
      }
    }
  }

  object GraphNode {
    def apply(id: Int, dist: Int = Int.MaxValue) = new GraphNode(id, dist, null, 0)
  }
}
