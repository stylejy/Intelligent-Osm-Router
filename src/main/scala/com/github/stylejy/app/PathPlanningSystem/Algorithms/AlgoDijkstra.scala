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

package com.github.stylejy.app.PathPlanningSystem.Algorithms

import com.github.stylejy.app.PathPlanningSystem.Graph.Node

import scala.collection.mutable.{ListBuffer, Map}

/**
  * Created by stylejy on 17/06/2017.
  */
class AlgoDijkstra(source: Int, target: Int) {

  def run {
    val Q = new AlgoBinaryHeap()
    /** Q.insert will label coordinates with new ids. After this point,
      * OSM ids are no longer used.
      */
    Q.insert(Node.apply(source, dist = 0))

    while (!Q.isEmpty) {

      //println("£££ heap: " + Q.heap)
      var node = Q.extractMin // now settled.
      //println()
      //println("----------------------------------------------- Chosen node id: " + node.id)

      if (node.id == target) { //are we already done?
        println("PATH FOUND (searched "+spt.size+" nodes)")
        return
      }

      node.foreach_outgoing(
        {
          /**
            * This function block finds neighbours.
            */
          (neighbour , weight) => // relaxation
            //println("neighbour id: " + neighbour.id + " dist " + weight)
            if (neighbour.dist > node.dist + weight) {
              neighbour.dist = node.dist + weight //Update the neighbour's distance with the shorter distance found
              neighbour.pred = node //predecessor
              //println("Predecessor id: " + neighbour.pred.id)

             if (neighbour.visited) //before
              Q.decreaseKey(neighbour)
             else // first time seen
              Q.insert(neighbour)
            }
        }
      )
      //println("-----------------------------------------------")
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
