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

import com.github.stylejy.app.OsmParser.dataOutputStream

import scala.collection.mutable.ArrayBuffer
import scala.xml.XML

/**
  * Created by stylejy on 20/06/2017.
  */
object PathWriter {
  val latlon = ArrayBuffer[LatLon]()
  case class LatLon(lat:Float, lon:Float)
  val in = new DataInputStream(new FileInputStream(new File("latlns.bin")))
  while (in.available != 0) { latlon += LatLon(in.readFloat,in.readFloat) }

  //Test - start
  case class latlonTest(lat: Double, lon: Double)

  var all = List(
    latlonTest(51.512253, -0.1223717),
    latlonTest(51.511852, -0.1225379)
  )
  //Test - end


  def write(path: List[Int], file: String) {



    /*
    val kml = build(path)
    val kmlLine = buildLine(path)
    print("writing kml..")
    XML.save(file+"default.kml", kml, "UTF-8", true, null)
    XML.save(file+"Line.kml", kmlLine, "UTF-8", true, null)
    println(" Done.")
    */
  }

  def build(path: List[Int]) = {

    <kml xmlns="http://www.opengis.net/kml/2.2">
      <Document>
        <Style id="yellowLineStyle">
          <LineStyle>
            <width>42</width>
          </LineStyle>
        </Style>
        { for (node <- path) yield
        <Placemark>
          <name>{node}</name>
          <description>foo bar</description>
          <styleUrl>#yellowLineStyle</styleUrl>
          <Point>
            <coordinates>
              { latlon(node).lon },{ latlon(node).lat }
            </coordinates>
          </Point>
        </Placemark>
        }
      </Document>
    </kml>
  }

  def buildLine(path: List[Int]) = {

    <kml xmlns="http://www.opengis.net/kml/2.2">
      <Document>
        <Style id="yellowLineStyle">
          <LineStyle>
            <width>4</width>
            <color>ff33ccff</color>
          </LineStyle>
        </Style>
        <Placemark>
          <styleUrl>#yellowLineStyle</styleUrl>
          <LineString>

            <coordinates>
          { for (node <- path) yield {

              "\t\t\t\t" + {
                {
                  latlon(node).lon
                } + "," + {
                  latlon(node).lat
                }
              } + "\n\n"
              println("latitude and longitude for node " + node + " " + latlon(node).lat + " " + latlon(node).lon)
            }
          }
            </coordinates>

          </LineString>
        </Placemark>
      </Document>
    </kml>
  }
}
