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
import java.io.{DataOutputStream, File, FileInputStream, FileOutputStream}

import scala.xml.{Elem, XML}
import scala.collection.mutable.Map
import scala.collection.mutable.ArrayBuffer

object OsmParser {

  var nodes: Map[Long, Node] = Map()
  var edges: ArrayBuffer[Edge] = ArrayBuffer()

  case class Node(lat:Float, lon:Float)
  case class Edge(from:Long, to:Long, dist:Int)

  def run = {
    val start = System.currentTimeMillis()
    parse("osmdata/data.osm")
    println(((start-System.currentTimeMillis())/60000)+"min")
  }

  def parse(osm_file: String) {

    val xml = XML.loadFile(osm_file)

    readNodes(xml)
    readWays(xml)
    sortEdges
    writeFiles
  }

  def readNodes(xml: Elem) = {
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

  def readWays(xml: Elem) = {
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
        val ids = way\"nd" map { (nd) => ((nd\"@ref").text.toLong)}
        for ((u,v) <- ids zip ids.tail) {
          edges += Edge(u, v, dist(Long2Node(u),Long2Node(v)))
          edges += Edge(v, u, dist(Long2Node(v),Long2Node(u)))
        }
      }
    }

    println("   ("+edges.size+") Done.")
  }

  def sortEdges = {
    println(" -> sorting edges..")

    edges = edges.sortWith(_.from < _.from)

    println("          Done.")
  }


  def writeFiles = {
    println(" -> writing file...")

    val osm_id_map = Map[Long, Long]()
    var edge_buf = ArrayBuffer[Long]()
    val node_out = dataOutputStream("nodes.bin")
    val edge_out = dataOutputStream("edges.bin")
    val dist_out = dataOutputStream("dists.bin")
    val latlons = dataOutputStream("latlns.bin")
    var id: Long = 0

    for (e <- edges) {  // build adjacency array
      if (e.from != id) {
        id = e.from
        node_out.writeInt(edge_buf.size)
        //println("\n\n\nedge_buf.size : "+edge_buf.size)
        osm_id_map(id) = osm_id_map.size
        nodes.get(e.from) match {
          case Some(node) =>
            latlons.writeFloat(node.lat)
            latlons.writeFloat(node.lon)
            println("id " + id + " Lat and Lon " + node.lat + " " + node.lon)
          case None =>
        }
      }
      dist_out.writeInt(e.dist)   // write dists
      edge_buf += e.to     // collect edge array
    }

    node_out.writeInt(edge_buf.size)

    println("edge_buf original: " + edge_buf)

    /** replace osm ids with adjacency array ids */
    edge_buf = edge_buf map osm_id_map

    println("edge_buf replaced: " + edge_buf)

    //  write edge array to adjacency array file
    edge_buf foreach (edge_out.writeLong)

    println("          Done.\n")
  }

  def dataOutputStream(file: String) = new DataOutputStream(new FileOutputStream(new File(file)))

  def dist(from: Node, to: Node): Int = {
    val lat1 = toRadians(from.lat)
    val lon1 = toRadians(from.lon)
    val lat2 = toRadians(to.lat)
    val lon2 = toRadians(to.lon)


    ((6378.388f * acos(
      sin(lat1) * sin(lat2)
        + cos(lat1) * cos(lat2)
        * cos(lon2 - lon1)))*1000).toInt
  }

  def Long2Node(id:Long): Node =
    nodes.get(id) match {
      case Some(node) => node
      case None => Node(0f,0f)
    }
}
