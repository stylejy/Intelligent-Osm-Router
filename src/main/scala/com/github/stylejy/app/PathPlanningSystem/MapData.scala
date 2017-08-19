package com.github.stylejy.app.PathPlanningSystem

import com.github.stylejy.app.Helpers.System.{FileIOHelper, VariableCleanHelper}
import com.github.stylejy.app.ParserSystem.JSON.OverpassJSONParser

import scala.collection.mutable.ArrayBuffer

object MapData extends VariableCleanHelper {
  case class LatLon(lat:Float, lon:Float)
  case class LatLonModel(lat: Double, lon: Double)
  case class Boundary(minlat: Float, minlon: Float, maxlat: Float, maxlon: Float)

  val nodeArray = ArrayBuffer[Int]()
  val originalNodeIds = ArrayBuffer[Long]()
  val edgeArray = ArrayBuffer[Int]()
  val distArray = ArrayBuffer[Int]()
  val latlon = ArrayBuffer[LatLon]()
  val boundary: Boundary = {
    val bIn = FileIOHelper.in("bound.bin")
    Boundary(bIn.readFloat,bIn.readFloat,bIn.readFloat,bIn.readFloat)
  }

  println(boundary)

  def load: Unit = {
    resetVars
    print("loading graph..")

    constructGraphStructure
    /**Every time new nodes are loaded(contructing the graph structure),
      *nodeIds in JSONParser also should be updated.
      */
    OverpassJSONParser.nodeIds = originalNodeIds
    constructCoordinatesStructure
  }

  def getBound: Boundary = boundary

  def getCoordinates(nodeId: Int): (Double, Double) = (latlon(nodeId).lat, latlon(nodeId).lon)

  private def constructCoordinatesStructure: Unit = {
    val latlons = FileIOHelper.in("latlns.bin")
    while(latlons.available != 0) {
      latlon += LatLon(latlons.readFloat, latlons.readFloat)
      //println("latlon: " + latlon)
    }
  }

  private def constructGraphStructure: Unit = {
    nodes
    edges
    dists
    def nodes: Unit = {
      val nodes = FileIOHelper.in("nodes.bin")
      while (nodes.available != 0) {
        nodeArray += nodes.readInt
        //println("short Node Ids " + nodeArray)
        originalNodeIds += nodes.readLong
        //println("original Node Ids " + originalNodeIds)
      }
    }

    def edges: Unit = {
      val edges = FileIOHelper.in("edges.bin")
      while (edges.available != 0) { edgeArray += edges.readInt
        //println("edge_array : " + edgeArray)
      }
    }

    def dists: Unit = {
      val dists = FileIOHelper.in("dists.bin")
      while (dists.available != 0) { distArray += dists.readInt }
    }
  }

  private def resetVars: Unit = {
    resetVariable(nodeArray)
    resetVariable(originalNodeIds)
    resetVariable(edgeArray)
    resetVariable(distArray)
    resetVariable(latlon)
  }
}
