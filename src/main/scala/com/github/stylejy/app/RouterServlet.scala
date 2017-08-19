package com.github.stylejy.app

import java.io.{File, FileNotFoundException, Writer}

import com.github.stylejy.app.Helpers.WebService.TemplateHelper
import com.github.stylejy.app.PathPlanningSystem.{MapData, PathWriter}
import com.github.stylejy.app.ParserSystem.Osm.OsmParser
import com.github.stylejy.app.ParserSystem.JSON.OverpassJSONParser
import com.github.stylejy.app.PathPlanningSystem.Algorithms.{AlgoClassic, AlgoExplorer, AlgoPreferences}
import com.github.stylejy.app.PathPlanningSystem.MapData.Boundary
import org.scalatra._

import scala.xml.{Node, XML}
import servlet.FileUploadSupport

import scala.collection.mutable.ListBuffer
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

  //For the explorer algorithm
  var depth = 0

  //For the preference based algorithm
  var numberOfVisit = 0
  var maxRadius = 0
  var shopping = 0
  var parks = 0
  var pubs = 0

  var mapBoundary: Boundary = null
  /**
    * Coordinates values for the map initialization should be string in the server side
    * to pass them to the javascript to read. They will be transformed to the number type in the javascript.
    */
  var minLat = ""
  var minLon = ""
  var maxLat = ""
  var maxLon = ""
  /**
    * For AlgoPreferences
    */
  var sourceLat: Float = 0
  var sourceLon: Float = 0

  def displayPage(title: String ,content: Seq[Node]) = TemplateHelper.page(title, content, url(_))
  def displayPageWithHead(title: String, content: Seq[Node], head: Seq[Node], foot: Seq[Node] = Nil) = TemplateHelper.page(title, content, url(_), head, foot)

  protected implicit lazy val jsonFormats: Formats = DefaultFormats

  def resetPositions: Unit = {
    sourcePosition = -1
    targetPosition = -1
  }

  def loadMapData: Unit = {
    MapData.load
    mapBoundary = MapData.getBound
    minLat = mapBoundary.minlat.toString
    minLon = mapBoundary.minlon.toString
    maxLat = mapBoundary.maxlat.toString
    maxLon = mapBoundary.maxlon.toString
    println("Updated Boundary : " + minLat + " " + minLon + " " + maxLat + " " + maxLon)
  }

  get("/") {
    if (!isReady) {
      loadMapData
      isReady = true
    }
    resetPositions
    try {
      XML.loadFile("osmdata/data.osm")
      displayPageWithHead(
        "Intelligent-OSM-Router",
        <!-- content -->
          <div class="btn-group-vertical">
            <a href="/dijkstra" class="btn btn-primary btn-lg active" role="button" aria-disabled="true">Dijkstra</a>
            <a href="/astar" class="btn btn-primary btn-lg active" role="button" aria-disabled="true">A*</a>
            <a href="/explorersetting" class="btn btn-primary btn-lg active" role="button" aria-disabled="true">Explorer</a>
            <a href="/prefsetting" class="btn btn-primary btn-lg active" role="button" aria-disabled="true">Preference Based</a>
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
          "",
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
      loadMapData
      isReady = true
    }
    resetPositions
    try {
      XML.loadFile("osmdata/data.osm")
      displayPageWithHead(
        "Dijkstra Algorithm",
        <!-- content -->
          <script src="/assets/js/MapSizeController.js"></script>
          <div id="leaflet"></div>
          <script type="text/javascript">mapHeight()</script>
          <script id="leafletInit" type="text/javascript"
                  data-minLat={minLat} data-minLon={minLon}
                  data-maxLat={maxLat} data-maxLon={maxLon} src="/assets/js/LeafletInitializer.js"></script>
          <script type="text/javascript" src="/assets/js/LeafletController.js"></script>
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
          "",
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
      loadMapData
      isReady = true
    }
    resetPositions
    try {
      XML.loadFile("osmdata/data.osm")
      displayPageWithHead(
        "A* Algorithm",
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
          "",
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
      "Explorer algorithm",
      <form>
        <div class="form-group">
          <label for="depth">Depth for Explorer Algorithm</label>
          <input type="number" class="form-control" id="depth" placeholder="Depth number (Must be positive)" />
        </div>

        <button type="button" class="btn btn-primary btn-lg" id="explorer-submit">Submit</button>
      </form>
        <script src="/assets/js/ExplorerSetting.js"></script>
    )
  }

  get("/explorer") {
    algorithmSwitch = 2
    if (!isReady) {
      loadMapData
      isReady = true
    }
    resetPositions
    try {
      XML.loadFile("osmdata/data.osm")
      displayPageWithHead(
        "Explorer Algorithm",
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
          "",
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

  get("/prefsetting") {
    displayPage(
      "Pref. based algorithm",
      <form class="form-inline">
        <label class="mr-sm-2" for="inlineFormCustomSelectPref">Preferences</label>
        <select class="custom-select mb-2 mr-sm-2 mb-sm-0" id="shopping">
          <option selected="">Shopping</option>
          <option value="2">Like \ (•◡•) /</option>
          <option value="1">Neutral  ¯\_(ツ)_/¯</option>
          <option value="0">Hate -_-</option>
        </select>
        <select class="custom-select mb-2 mr-sm-2 mb-sm-0" id="parks">
          <option selected="">Stroll in parks</option>
          <option value="2">Like \ (•◡•) /</option>
          <option value="1">Neutral  ¯\_(ツ)_/¯</option>
          <option value="0">Hate -_-</option>
        </select>
        <select class="custom-select mb-2 mr-sm-2 mb-sm-0" id="pubs">
          <option selected="">enjoy in pubs</option>
          <option value="2">Like \ (•◡•) /</option>
          <option value="1">Neutral  ¯\_(ツ)_/¯</option>
          <option value="0">Hate -_-</option>
        </select>
      </form>
      <form>
        <div class="form-group">
          <label for="numOfVisit">How many places would you like to go? - Maximum (Explorer Algorithm)</label>
          <input type="number" class="form-control" id="numOfVisit" placeholder="Number of visits (Must be positive)" />
          <small id="emailHelp" class="form-text text-muted">Due to the restriction of use of Overpass API, please put a small
          amount of number like 3 or 4, or it might be stuck in the middle of process.</small>
        </div>
        <div class="form-group">
          <label for="maxRadius">How far could be suitable for you? *in metres* - Maximum (Explorer Algorithm)</label>
          <input type="number" class="form-control" id="maxRadius" placeholder="Maximum radius distance (Must be positive)" />
          <small id="emailHelp" class="form-text text-muted">It is for the radius from the chosen point.</small>
        </div>
        <button type="button" class="btn btn-primary btn-lg" id="pref-submit">Submit</button>
      </form>
      <script src="/assets/js/PrefSetting.js"></script>
    )
  }

  get("/pref") {
    algorithmSwitch = 3
    if (!isReady) {
      loadMapData
      isReady = true
    }
    resetPositions
    try {
      XML.loadFile("osmdata/data.osm")
      displayPageWithHead(
        "Prefs. Based Algorithm",
        <!-- content -->
          <script src="/assets/js/MapSizeController.js"></script>
          <div id="leaflet"></div>
          <script type="text/javascript">mapHeight()</script>
          <script src="/assets/js/LeafletInitializer.js"></script>
          <script src="/assets/js/LeafletControllerForPrefs.js"></script>
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
          "",
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
        loadMapData
        redirect("/")

      case _ =>
        BadRequest(displayPage(
          "",
          <p>
            Please choose a correct OSM file before submitting!
          </p>)
        )
    }
  }

  get("/path") {
    println("path source: " + sourcePosition + "  target: " + targetPosition)
    if (algorithmSwitch.equals(3) || (sourcePosition > 0 && targetPosition > 0)) {
      val start = System.currentTimeMillis()
      //val path = new AlgoClassic(sourcePosition, targetPosition, 0).getPath
      //val path = new AlgoExplorer(sourcePosition, targetPosition, 10).run
      val path = {
        algorithmSwitch match {
          case 0 => new AlgoClassic(sourcePosition, targetPosition, algorithmSwitch).getPath
          case 1 => new AlgoClassic(sourcePosition, targetPosition, algorithmSwitch).getPath
          case 2 => new AlgoExplorer(sourcePosition, targetPosition, depth).run
          case 3 =>
            new AlgoPreferences(sourceLat, sourceLon, sourcePosition, numberOfVisit, maxRadius, shopping, parks, pubs).run

        }
      }


      println((System.currentTimeMillis() - start) + "ms  (" + path.size + " nodes)\n")

      contentType = formats("json")
      PathWriter.write(path)
    }
  }

  get("/update") {
    displayPage(
      "Map Update",
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

  post("/prefsetting") {
    println("yes")
    numberOfVisit = params("numOfVisit").toInt
    println(numberOfVisit)
    maxRadius = params("maxRadius").toInt
    println(maxRadius)
    shopping = params("prefShopping").toInt
    println(shopping)
    parks = params("prefParks").toInt
    println(parks)
    pubs = params("prefPubs").toInt
    println(pubs)
  }

}
