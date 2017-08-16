package com.github.stylejy.app.PathPlanningSystem.Algorithms

import scala.collection.mutable.ListBuffer
import com.github.stylejy.app.PathPlanningSystem.Graph.Node

class AlgoExplorer(source: Int, target: Int, depth: Int) {
  val optimalPath: List[Int] = getDijkstraPath(source, target)
  val node = Node
  val sourceNodeId = source
  val targetNodeId = target
  val randomPath = ListBuffer[Int]()
  val exploredCompletePath = ListBuffer[Int]()
  /**
    * If all explored nodes are already in the explored path in turn, no new nodes are added to the explored path.
    * This boolean value indicates whether at leat one explored node is added or not.
    */
  var isExploredNodeAdded = false

  def run: List[Int] = {
    if (depth.equals(0)) optimalPath
    else {
      modifyPath
      exploredCompletePath.toList
    }
  }

  private def modifyPath: Unit = {
    findNeighbour
    if (isExploredNodeAdded) {
      linkExploredNodeToOriginal
      removeExceedingNodes
    }
    println(" [ modifyPath ][for loop] exploredCompletePath : " + exploredCompletePath)
    println(optimalPath)
  }

  /**
    * One of the rules is that there should not be nodes placed to exceed the target node or the source node boundary.
    * It drops all nodes placed to exceed the target node or the source node.
    */
  private def removeExceedingNodes: Unit = {
    exploredCompletePath --= exploredCompletePath.drop(exploredCompletePath.indexOf(source)+1)
    exploredCompletePath --= exploredCompletePath.dropRight(exploredCompletePath.lastIndexOf(source)+1)
  }

  /**
    * If the DijkstraPath is empty, it returns false. Otherwise, true.
    */
  private def linkExploredNodeToOriginal: Unit = {
    //The head of the exploredNodes is the last node explored which should be linked to the original node.
    val lastExploredNode = randomPath.head
    val endNode = targetNodeId
    val linkingPath = getDijkstraPath(lastExploredNode, endNode).to[ListBuffer]
    println("   [ modifyPath ][ linkExploredNodeToOriginal ] linkingPath : " + linkingPath)

    //Remove the target node(successorNode) already in the exploredCompletePath.
    //linkingPath -= linkingPath.head
    //Remove the source node(lastExploredNode) already in the exploredNodes.
    linkingPath -= linkingPath.last

    println("   [ modifyPath ][ linkExploredNodeToOriginal ] linkingPath : " + linkingPath)
    randomPath.insertAll(0, linkingPath)
    println("   [ modifyPath ][ linkExploredNodeToOriginal ] randomPath : " + randomPath)

    exploredCompletePath ++= randomPath
    //exploredCompletePath.insertAll(exploredCompletePath.indexOf(lastExploredNode), linkingPath)
  }

  private def findNeighbour: Unit = {
    //Find neighbours from the source node.
    var startNode = sourceNodeId
    val neighbours = ListBuffer[Int]()
    /**
      * Keep finding the randomly chosen neighbours for the depth number of times.
      */
    for (i <- 1 to depth) {
      println("   [ modifyPath ][ findNeighbour ][for loop] i : " + i)
      println("   [ modifyPath ][ findNeighbour ][for loop] startNode : " + startNode)
      neighbours.clear()
      node.apply(startNode, 0).foreach_outgoing(
        {
          (neighbour, weight) =>
            println("   [ modifyPath ][ findNeighbour ][foreach_outgoing] neighbour : " + neighbour)
            /**
              * This excludes the successor node from the neighbour list and the explored path.
              */
            if (!neighbour.equals(targetNodeId)
              && existenceTest(neighbour, neighbours)
              && existenceTest(neighbour, randomPath)
              && existenceTest(neighbour, optimalPath.to[ListBuffer])) neighbours += neighbour

        }
      )
      println("   [ modifyPath ][ findNeighbour ][for loop] neighbours : " + neighbours)
      if (!neighbours.isEmpty) {
        val randIndex = scala.util.Random.nextInt(neighbours.size)
        val chosenNeighbour = neighbours(randIndex)
        println("   [ modifyPath ][ findNeighbour ][for loop] chosenNeighbour : " + chosenNeighbour)
        randomPath.insert(0, chosenNeighbour)
        isExploredNodeAdded = true
        startNode = chosenNeighbour
      }
    }
    //Add the start node to the randomPath
    randomPath += sourceNodeId
    println("   [ modifyPath ][ findNeighbour ] temporaryPath : " + randomPath)
  }

  private def existenceTest(testNode: Int, pathDomain: ListBuffer[Int]): Boolean = {
    /**
      * If the test node doesn't exist, it returns true, otherwise, false.
      */
    if (!pathDomain.exists(_.equals(testNode))) true
    else false
  }

  private def getDijkstraPath(source: Int, target: Int): List[Int] ={
    new AlgoDijkstra(source, target).getPath
  }
}
