package com.github.stylejy.app

import java.io.{File, FileNotFoundException, Writer}

import org.scalatra._

import scala.xml.{Node, XML}
import servlet.FileUploadSupport
// JSON-related libraries
import org.json4s.{DefaultFormats, Formats}
// JSON handling support from Scalatra
import org.scalatra.json._

class RouterServlet extends IntelligentOsmRouterStack with FileUploadSupport with FlashMapSupport with JacksonJsonSupport {

  var isAvailable = false
  def displayPage(content: Seq[Node]) = Template.page("Intelligent-OSM-Router", content, url(_))
  def displayPageWithHead(content: Seq[Node], head: Seq[Node]) = Template.page("Intelligent-OSM-Router", content, url(_), head)

  protected implicit lazy val jsonFormats: Formats = DefaultFormats

  get("/") {

    try {
      XML.loadFile("osmdata/data.osm")
      displayPageWithHead(
        <!-- content -->
        <h3>
          ----> OSM data is ready to use in the server.
        </h3>
          <div id="map"></div>
          <script src="LeafletController.js"></script>

        <p>
          ( If you want to update, you can used the update form below. )
        </p>
        <p>
          Update:
        </p>
          <form action={url("/")} method="post" enctype="multipart/form-data">
            <p>File to update: <input type="file" name="map" /></p>
            <p><input type="submit" value="Update" /></p>
          </form>
        ,
        <!-- head -->
          <link rel="stylesheet" href="leaflet/leaflet.css"/>
          <script src="leaflet/leaflet.js"></script>
            <link href="PageStyle.css" rel="stylesheet" />
      )
    } catch {
      case e: FileNotFoundException =>
        displayPage(
          <h3>
            ----> Upload an OSM file to store in the server.
          </h3>
            <form action={url("/")} method="post" enctype="multipart/form-data">
              <p>File to upload: <input type="file" name="map" /></p>
              <p><input type="submit" value="Upload" /></p>
            </form>
        )
    }

  }

  post("/") {

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
    Graph.load()
    val start = System.currentTimeMillis()
    val path = new AlgoDijkstra(5, 12).getPath
    println((System.currentTimeMillis()-start)+"ms  ("+path.size+" nodes)\n")

    contentType = formats("json")
    PathWriter.write(path)
  }
}
