package com.github.stylejy.app

import org.scalatra._

class RouterServlet extends IntelligentOsmRouterStack {

  var isAvailable = false

  println("---------------> Processing OSM data")
  MainController.run

  println("---------------> Processing web presentation")
  get("/") {
    isAvailable = true
    <html>
      <body>
        <h1>[ Intelligent-osm-router ]</h1>
        <h2>---> Server is working</h2>
        <h2><a href="/test">Get paths</a></h2>
      </body>
    </html>
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
