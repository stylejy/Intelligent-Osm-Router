package com.github.stylejy.app

import java.io.{File, FileNotFoundException, Writer}

import com.github.stylejy.app.Helpers.WebService.TemplateHelper
import com.github.stylejy.app.PathPlanningSystem.{MapData, PathWriter}
import com.github.stylejy.app.ParserSystem.Osm.OsmParser
import com.github.stylejy.app.ParserSystem.JSON.OverpassJSONParser
import com.github.stylejy.app.PathPlanningSystem.Algorithms.{AlgoClassic, AlgoExplorer, AlgoPreferences}
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
  var algorithmSwitch = 0

  var depth = 0

  /**
    * For AlgoPreferences
    */
  var sourceLat: Float = 0
  var sourceLon: Float = 0

  def displayPage(content: Seq[Node]) = TemplateHelper.page("Intelligent-OSM-Router", content, url(_))
  def displayPageWithHead(content: Seq[Node], head: Seq[Node], foot: Seq[Node] = Nil) = TemplateHelper.page("Intelligent-OSM-Router", content, url(_), head, foot)

  protected implicit lazy val jsonFormats: Formats = DefaultFormats

  def resetPositions: Unit = {
    sourcePosition = -1
    targetPosition = -1
  }

  get("/") {
    if (!isReady) {
      MapData.load
      isReady = true
    }
    resetPositions
    try {
      XML.loadFile("osmdata/data.osm")
      displayPageWithHead(
        <!-- content -->
          <div id="algoSwitch">
            <a href="/dijkstra" class="btn btn-primary btn-lg active" role="button" aria-disabled="true">Dijkstra</a>
            <a href="/astar" class="btn btn-primary btn-lg active" role="button" aria-disabled="true">A*</a>
            <a href="/explorersetting" class="btn btn-primary btn-lg active" role="button" aria-disabled="true">Explorer</a>
            <a href="/pref" class="btn btn-primary btn-lg active" role="button" aria-disabled="true">Preference Based</a>
          </div>
        ,
        <!-- head -->
            <link rel="stylesheet" href="leaflet/leaflet.css"/>
          <script src="leaflet/leaflet.js"></script>
            <link href="PageStyle.css" rel="stylesheet"/>
          <script src="leaflet.geometryutil.js"></script>
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

  get("/dijkstra") {
    if (!isReady) {
      MapData.load
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

  get("/astar") {
    algorithmSwitch = 1
    if (!isReady) {
      MapData.load
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

  get("/explorersetting") {
    displayPage(
      <form>
        <div class="form-group">
          <label for="depth">Depth for Explorer Algorithm</label>
          <input type="number" class="form-control" id="depth" placeholder="Depth number (Must be positive)" />
        </div>

        <a href="/explorer" class="btn btn-primary" role="button" aria-disabled="true" id="explorer-submit">Submit</a>
      </form>
        <script src="/assets/js/ExplorerSetting.js"></script>
    )
  }

  get("/explorer") {
    algorithmSwitch = 2
    if (!isReady) {
      MapData.load
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

  get("/pref") {
    algorithmSwitch = 3
    if (!isReady) {
      MapData.load
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
        MapData.load
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
    val numberOfVisit = 3
    val maxRadius = 100
    val shopping = 2
    val parks = 0
    val pubs = 0
    println("path path")
    println("path source: " + sourcePosition + "  target: " + targetPosition)
    if (sourcePosition > 0 && targetPosition > 0) {
      val start = System.currentTimeMillis()
      //val path = new AlgoClassic(sourcePosition, targetPosition, 0).getPath
      //val path = new AlgoExplorer(sourcePosition, targetPosition, 10).run
      val path = {
        algorithmSwitch match {
          case 0 => new AlgoClassic(sourcePosition, targetPosition, algorithmSwitch).getPath
          case 1 => new AlgoClassic(sourcePosition, targetPosition, algorithmSwitch).getPath
          case 2 => new AlgoExplorer(sourcePosition, targetPosition, depth).run
          case 3 => new AlgoPreferences(sourceLat, sourceLon, sourcePosition, numberOfVisit, maxRadius, shopping, parks, pubs).run
        }
      }


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
    val lat = params("lat").toDouble
    val lon = params("lng").toDouble
    if (sourcePosition < 0) {
      sourcePosition = OverpassJSONParser.run(lat, lon)
      sourceLat = lat.toFloat
      sourceLon = lon.toFloat
      println("source: " + sourcePosition + "  target: " + targetPosition)
    } else {
      targetPosition = OverpassJSONParser.run(lat, lon)
      println("source: " + sourcePosition + "  target: " + targetPosition)
    }
  }

  post("/explorersetting") {
    depth = params("depth").toInt
    println(depth)
  }

}
