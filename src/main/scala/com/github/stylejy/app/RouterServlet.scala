package com.github.stylejy.app

import java.io.{File, FileNotFoundException}

import org.scalatra._

import scala.xml.{Node, XML}
import servlet.FileUploadSupport

class RouterServlet extends IntelligentOsmRouterStack with FileUploadSupport with FlashMapSupport {

  var isAvailable = false
  def displayPage(content: Seq[Node]) = Template.page("Intelligent-OSM-Router", content, url(_))


  get("/") {
    isAvailable = true

    try {
      XML.loadFile("osmdata/data.osm")
      displayPage(
        <h3>
          ----> OSM data is ready to use in the server.
        </h3>
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
        val saveFile = new File("osmdata/data.osm")
        Ok(fileParams("map").write(saveFile))
        OsmParser.run
        redirect("/")

      case _ =>
        BadRequest(displayPage(
          <p>
            Please choose a correct OSM file before submitting!
          </p>)
        )
    }
  }

  get("/test") {
    if( isAvailable ) {
      val start = System.currentTimeMillis()
      val path = new AlgoDijkstra(5, 10).getPath
      println((System.currentTimeMillis()-start)+"ms  ("+path.size+" nodes)\n")
      contentType = "application/vnd.google-earth.kml+xml"
      KmlWriter.write(path, "output")
    } else {
      <html>
        <body>
          <h1>This link is not directly accessible.</h1>
        </body>
      </html>
    }
  }
}
