package com.github.stylejy.app

import org.scalatra._

class RouterServlet extends IntelligentOsmRouterStack {

  println("---------------> Processing OSM data")
  MainController.run()

  println("---------------> Processing web presentation")
  get("/") {
    <html>
      <body>
        <h1>[ Intelligent-osm-router ]</h1>
        <h2>---> Server is working.</h2>
      </body>
    </html>
  }

  get("/test") {
    /*if (!params.contains("from") || !params.contains("to"))
       <h1>please specify from and to parameters</h1>
    else {*/
      val start = System.currentTimeMillis()
      //val path = new AlgoDijkstra(params("from").toInt, params("to").toInt).getPath
      //test
      val path = new AlgoDijkstra(5, 10).getPath
      println((System.currentTimeMillis()-start)+"ms  ("+path.size+" nodes)\n")
      contentType = "application/vnd.google-earth.kml+xml"
      KmlWriter.write(path, "output")
    //}
  }
}
