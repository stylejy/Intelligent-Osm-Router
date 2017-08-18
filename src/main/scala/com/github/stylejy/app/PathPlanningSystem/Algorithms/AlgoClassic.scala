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

import com.github.stylejy.app.Helpers.System.DistanceCalculator
import com.github.stylejy.app.ParserSystem.Osm.OsmParser
import com.github.stylejy.app.PathPlanningSystem.MapData
import com.github.stylejy.app.PathPlanningSystem.NodeModel.GraphNode

import scala.collection.mutable.{ListBuffer, Map}

/**
  * Created by stylejy on 17/06/2017.
  */
class AlgoClassic(source: Int, target: Int, selector: Int) {



  private def selectAlgo(currentNode: GraphNode, neighbour: GraphNode, weight:Int): Int = {
    val cost = {
      /**
        * If selector is 0, then the cost is for the Dijkstra algorithm
        * Otherwise, A* algorithm.
        */
      if (selector.equals(0)) currentNode.dist + weight //For Dijkstra
      else currentNode.dist + weight + straightLineDistance(neighbour) //For A*
    }
    cost
  }

  private def straightLineDistance(neighbour: GraphNode): Int = {
    val distance = DistanceCalculator
    val startCoordinates = MapData.getCoordinates(neighbour.id)
    val endCoordinates = MapData.getCoordinates(target)

    val startNode = OsmParser.Node(startCoordinates._1.toFloat, startCoordinates._2.toFloat)
    val endNode = OsmParser.Node(endCoordinates._1.toFloat, endCoordinates._2.toFloat)

    distance.calculator(startNode, endNode)
  }

  def run {
    val Q = new AlgoBinaryHeap()
    /** Q.insert will label coordinates with new ids. After this point,
      * OSM ids are no longer used.
      */
    Q.insert(GraphNode.apply(source, dist = 0))

    while (!Q.isEmpty) {

      //println("£££ heap: " + Q.heap)
      val currentNode = Q.extractMin // now settled.
      //println()
      //println("----------------------------------------------- Chosen currentNode id: " + currentNode.id)

      if (currentNode.id == target) { //are we already done?
        println("PATH FOUND (searched "+spt.size+" nodes)")
        return
      }

      currentNode.foreach_outgoing(
        {
          /**
            * This function block finds neighbours.
            */
          (neighbour , weight) => // relaxation
            //println("currentNode dist: " + currentNode.dist + " weight " + weight)
            val cost = selectAlgo(currentNode, neighbour, weight)
            if (neighbour.dist > cost) {
              neighbour.dist = cost //Update the neighbour's distance with the shorter distance found
              neighbour.pred = currentNode //predecessor
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
    var graphNode: GraphNode = target
    val path = ListBuffer[Int]()
    while (graphNode.pred != null) {
      path += graphNode.id //
      graphNode = graphNode.pred
    }
    path += graphNode.id
    path.toList
  }

  def getDist = {
    run // alg
    target.dist
  }

  // some magic
  var spt: Map[Int, GraphNode] = Map[Int, GraphNode]()
  implicit def getSPTNode(id: Int): GraphNode = {
    spt.get(id) match { // even more magic
      case Some(graphNode) => graphNode
      case None =>
        val nd = GraphNode(id)
        spt(id) = nd
        nd
    }}
}
