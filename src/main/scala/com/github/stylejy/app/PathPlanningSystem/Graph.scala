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

import java.io.{DataInputStream, File, FileInputStream}

import com.github.stylejy.app.Helpers.System.VariableCleanHelper
import com.github.stylejy.app.ParserSystem.OverpassApi.JSONParser

import scala.collection.mutable.ArrayBuffer

object Graph extends VariableCleanHelper{

  val nodeArray = ArrayBuffer[Int]()
  val originalNodeIds = ArrayBuffer[Long]()
  val edgeArray = ArrayBuffer[Int]()
  val distArray = ArrayBuffer[Int]()


  def resetVars = {
    resetVariable(nodeArray)
    resetVariable(originalNodeIds)
    resetVariable(edgeArray)
    resetVariable(distArray)
  }

  def load() {
    resetVars
    print("loading graph..")
    val nodes = new DataInputStream(new FileInputStream(new File("nodes.bin")))
    while (nodes.available != 0) {
      nodeArray += nodes.readInt
      //println("short Node Ids " + nodeArray)
      originalNodeIds += nodes.readLong
      //println("original Node Ids " + originalNodeIds)
    }
    /**Every time new nodes are loaded, nodeIds in JSONParser also should be updated. */
    JSONParser.nodeIds = originalNodeIds

    val edges = new DataInputStream(new FileInputStream(new File("edges.bin")))
    while (edges.available != 0) { edgeArray += edges.readInt
      //println("edge_array : " + edgeArray)
    }

    val dists = new DataInputStream(new FileInputStream(new File("dists.bin")))
    while (dists.available != 0) { distArray += dists.readInt }
  }


  case class Node(val id: Int, var dist: Int, var pred: Node, var index: Int) {
    def visited = index > 0
    //def settled = index == -1
    //def relaxed = index >= +1

    def foreach_outgoing(fun: (Int,Int) => Unit) {
      //println("!!!!!!!!!!!!final node_array: " + nodeArray)
      //println("!!!!!!!!!!!!final edge_array: " + edgeArray)
      //println("!!!!!!!!!!!!node_array id: " + nodeArray(id) + " until " + nodeArray(id+1))
      for (i <- nodeArray(id.toInt) until nodeArray(id+1)) {
        fun(edgeArray(i.toInt), distArray(i.toInt))  // call function foreach neighbour
      }
    }
  }

  object Node {
    def apply(id: Int, dist: Int = Int.MaxValue) = new Node(id, dist, null, 0)
  }
}
