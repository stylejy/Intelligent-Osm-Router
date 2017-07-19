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

package com.github.stylejy.app

import java.io.{DataInputStream, File, FileInputStream}

import scala.collection.mutable.{ArrayBuffer, ListBuffer, Map}
import Graph.Node

/**
  * Created by stylejy on 17/06/2017.
  */
class AlgoDijkstra(source: Int, target: Int) {
  def run {

    //For testing. This part is the same as one in KmlWriter
    val latlon = ArrayBuffer[LatLon]()
    case class LatLon(lat:Float, lon:Float)
    val in = new DataInputStream(new FileInputStream(new File("latlns.bin")))
    while (in.available != 0) { latlon += LatLon(in.readFloat,in.readFloat) }
    //End testing code.

    val Q = new AlgoBinaryHeap()
    /** Q.insert will label coordinates with new ids. After this point,
      * OSM ids are no longer used.
      */
    Q.insert(Node(source, dist = 0))

    while (!Q.isEmpty) {

      //println("Q: " + Q.heap)
      var node = Q.extractMin // now settled.

      //println("node id " + node.id + " in AlgoDijkstra ---> node: " + node)
      //println("******** short node id: " + node.id + " -> osm node id: ")

      if (node.id == target) { //are we already done?
        println("PATH FOUND (searched "+spt.size+" nodes)")
        return
      }

      node.foreach_outgoing { (neighbour , weight) => // relaxation

        //println("neighbour: " + neighbour + " weight " + weight)
        if (neighbour.dist > node.dist + weight) {

          neighbour.dist = node.dist + weight
          neighbour.pred = node //predecessor

          if (neighbour.visited) //before
            Q.decreaseKey(neighbour)
          else // first time seen
            Q.insert(neighbour)

        }
      }
    }
    println("NO PATH FOUND! (searched "+spt.size+" nodes)")
  }


  def getPath = {
    run // the algorithm
    var node: Node = target
    val path = ListBuffer[Int]()
    while (node.pred != null) {
      path += node.id //
      node = node.pred
    }
    path += node.id
    path.toList
  }


  def getDist = {
    run // alg
    target.dist
  }

  // some magic
  var spt: Map[Int, Node] = Map[Int, Node]()
  implicit def getSPTNode(id: Int): Node = {
    spt.get(id) match { // even more magic
      case Some(node) => node; case None =>
        val nd = Node(id); spt(id) = nd; nd
    }}
}
