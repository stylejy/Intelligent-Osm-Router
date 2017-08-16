package com.github.stylejy.app

import java.io.{File, FileNotFoundException, Writer}

import com.github.stylejy.app.Helpers.WebService.TemplateHelper
import com.github.stylejy.app.PathPlanningSystem.{Graph, PathWriter}
import com.github.stylejy.app.ParserSystem.Osm.OsmParser
import com.github.stylejy.app.ParserSystem.OverpassApi.JSONParser
import com.github.stylejy.app.PathPlanningSystem.Algorithms.{AlgoDijkstra, AlgoExplorer}
import org.scalatra._

import scala.xml.{Node, XML}
import servlet.FileUploadSupport
// JSON-related libraries
import org.json4s.{DefaultFormats, Formats}
// JSON handling support from Scalatra
import org.scalatra.json._

class RouterServlet extends IntelligentOsmRouterStack with FileUploadSupport with FlashMapSupport with JacksonJsonSupport {

  var isAvailable = false
  var isReady = false
  var sourcePosition: Int = -1
  var targetPosition: Int = -1
  def displayPage(content: Seq[Node]) = TemplateHelper.page("Intelligent-OSM-Router", content, url(_))
  def displayPageWithHead(content: Seq[Node], head: Seq[Node], foot: Seq[Node]) = TemplateHelper.page("Intelligent-OSM-Router", content, url(_), head, foot)

  protected implicit lazy val jsonFormats: Formats = DefaultFormats

  def resetPositions: Unit = {
    sourcePosition = -1
    targetPosition = -1
  }

  get("/") {
    if (!isReady) {
      Graph.load()
      isReady = true
    }
    resetPositions
    try {
      XML.loadFile("osmdata/data.osm")
      displayPageWithHead(
        <!-- content -->
          <script src="/assets/js/MapSizeController.js"></script>
          <div id="leaflet"></div>
          <script type="text/javascript">mapHeight()</script>
          <script src="/assets/js/LeafletInitializer.js"></script>
          <script src="/assets/js/LeafletController.js"></script>
        ,
        <!-- head -->
            <link rel="stylesheet" href="leaflet/leaflet.css"/>
          <script src="leaflet/leaflet.js"></script>
            <link href="PageStyle.css" rel="stylesheet"/>
          <script src="leaflet.geometryutil.js"></script>
        ,
        <!-- foot -->
          <p id="source">Source</p>
          <p id="target">Target</p>
          <div id="getpath">
          </div>
      )
    } catch {
      case e: FileNotFoundException =>
        displayPage(
          <h3>
            ----> Upload an OSM file to store in the server.
          </h3>
            <form action={url("/update")} method="post" enctype="multipart/form-data">
              <p>File to upload: <input type="file" name="map" /></p>
              <p><input type="submit" value="Upload" /></p>
            </form>
        )
    }

  }

  post("/update") {

    //Define a regex pattern to identify osm files.
    val pattern = "\\w.osm".r

    //Accepts only osm files to store in the server.
    pattern findFirstIn fileParams("map").name match {
      case Some(name) =>
        isAvailable = true
        val saveFile = new File("osmdata/data.osm")
        Ok(fileParams("map").write(saveFile))
        OsmParser.run
        PathWriter.update
        Graph.load()
        redirect("/")

      case _ =>
        BadRequest(displayPage(
          <p>
            Please choose a correct OSM file before submitting!
          </p>)
        )
    }
  }

  get("/path") {
    println("path path")
    println("path source: " + sourcePosition + "  target: " + targetPosition)
    if (sourcePosition > 0 && targetPosition > 0) {
      val start = System.currentTimeMillis()
      //val path = new AlgoDijkstra(sourcePosition, targetPosition).getPath
      val path = new AlgoExplorer(sourcePosition, targetPosition, 10).run
      println((System.currentTimeMillis() - start) + "ms  (" + path.size + " nodes)\n")

      contentType = formats("json")
      PathWriter.write(path)
    }
  }

  get("/update") {
    displayPage(
      <p>
        ( If you want to update, you can used the update form below. )
      </p>
        <p>
          Update:
        </p>
        <form method="post" enctype="multipart/form-data">
          <p>File to update: <input type="file" name="map" /></p>
          <p><input type="submit" value="Update" /></p>
        </form>
    )
  }

  get("/overpass") {
    if (sourcePosition < 0) {
      sourcePosition = JSONParser.run(params("lat").toDouble, params("lng").toDouble)
      println("source: " + sourcePosition + "  target: " + targetPosition)
    } else {
      targetPosition = JSONParser.run(params("lat").toDouble, params("lng").toDouble)
      println("source: " + sourcePosition + "  target: " + targetPosition)
    }
  }
}
