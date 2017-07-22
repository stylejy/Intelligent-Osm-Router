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

import scala.math._
import scala.xml.{Elem, XML}
import scala.collection.mutable.Map
import scala.collection.mutable.ArrayBuffer

object OsmParser extends VariableCleaner {

  private val nodes: Map[Long, Node] = Map()
  private var edges: ArrayBuffer[Edge] = ArrayBuffer()

  case class Node(lat:Float, lon:Float)
  case class Edge(from:Long, to:Long, dist:Int)

  def run = {
    resetVariable(nodes)
    resetVariable(edges)
    val start = System.currentTimeMillis()
    parse("osmdata/data.osm")
    println(((start-System.currentTimeMillis())/60000)+"min")
  }

  private def parse(osm_file: String) {

    val xml = XML.loadFile(osm_file)

    readNodes(xml)
    readWays(xml)
    sortEdges
    writeFiles
  }

  private def readNodes(xml: Elem) = {
    println("\n -> reading nodes..")
    println(xml)
    (xml \ "node") foreach { (node) =>

      val id = (node\"@id").text.toLong
      val lat = (node\"@lat").text.toFloat
      val lon = (node\"@lon").text.toFloat
      nodes(id) = Node(lat, lon)
      println("***PARSER*** OSM ID: " + id + " Latitude: " + lat + " Longitude: " + lon)
    }
    println("   ("+nodes.size+") Done.")
  }

  private def readWays(xml: Elem) = {
    println(" -> reading ways...")

    (xml \ "way") foreach { (way) =>

      val speed = ((way\"tag" filter { (t) => (t\"@k").text == "highway" })
        \ "@v").text match {
        case "secondary_link" => 30 // km/h
        case "motorway_link" => 50 // km/h
        case "living_street" => 30
        case "unclassified" => 30
        case "primary_link" => 30
        case "residential" => 20
        case "trunk_link" => 30
        case "secondary" => 60
        case "motorway" => 90
        case "tertiary" => 40
        case "service" => 30
        case "primary" => 80
        case "track" => 30
        case "trunk" => 50
        case "road" => 50
        case "path" => 10
        case "steps" => 0
        case "footway" => 10
        //case "cycleway" => 20
        case "pedestrian" => 10
        //case (hw:String) if (!hw.equals("")) => println("highway "+hw); 10
        case _ => 0
      }

      if (speed > 0) {
        println(way\"nd")
        val ids = (way\"nd").map((nd) => ((nd\"@ref").text.toLong))
        println(way\"nd")
        for ((u,v) <- ids zip ids.tail) {
          edges += Edge(u, v, dist(Long2Node(u),Long2Node(v)))
          edges += Edge(v, u, dist(Long2Node(v),Long2Node(u)))
        }
      }
    }

    println("   ("+edges.size+") Done.")
  }

  private def sortEdges = {
    println(" -> sorting edges..")

    println("before edges: " + edges)
    edges = edges.sortWith(_.from < _.from)
    println("after edges: " + edges)

    println("          Done.")
  }


  private def writeFiles = {
    println(" -> writing file...")

    val osm_id_map = Map[Long, Int]()
    var edgeBufferLong = ArrayBuffer[Long]()
    var edgeBufferInt = ArrayBuffer[Int]()
    val nodeOut = FileIOController.out("nodes.bin")
    val edgeOut = FileIOController.out("edges.bin")
    val distOut = FileIOController.out("dists.bin")
    val latlons = FileIOController.out("latlns.bin")
    var id: Long = 0

    for (e <- edges) {  // build adjacency array
      if (e.from != id) {
        id = e.from
        nodeOut.writeInt(edgeBufferLong.size)
        println("%%%%%%%%%%%%%%%edgeBufferLong.size : "+edgeBufferLong.size)
        osm_id_map(id) = osm_id_map.size
        nodes.get(e.from) match {
          case Some(node) =>
            latlons.writeFloat(node.lat)
            latlons.writeFloat(node.lon)
            println("id " + id + " Lat and Lon " + node.lat + " " + node.lon)
          case None =>
        }
      }
      distOut.writeInt(e.dist)   // write dists
      edgeBufferLong += e.to     // collect edge array
    }

    nodeOut.writeInt(edgeBufferLong.size)
    println("%%%%%%%%%%%%%%%edgeBufferLong.size : "+edgeBufferLong.size)

    println("edgeBufferLong original: " + edgeBufferLong)

    /** replace osm ids with adjacency array ids */
    edgeBufferInt = edgeBufferLong.map(osm_id_map)

    println("edgeBufferLong replaced with edgeBufferInt: " + edgeBufferInt)

    //  write edge array to adjacency array file
    edgeBufferInt foreach (edgeOut.writeInt)

    println("          Done.\n")
  }

  private def dist(from: Node, to: Node): Int = {
    val lat1 = toRadians(from.lat)
    val lon1 = toRadians(from.lon)
    val lat2 = toRadians(to.lat)
    val lon2 = toRadians(to.lon)


    ((6378.388f * acos(
      sin(lat1) * sin(lat2)
        + cos(lat1) * cos(lat2)
        * cos(lon2 - lon1)))*1000).toInt
  }

  private def Long2Node(id:Long): Node =
    nodes.get(id) match {
      case Some(node) => node
      case None => Node(0f,0f)
    }
}
