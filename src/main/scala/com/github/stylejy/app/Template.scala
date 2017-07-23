package com.github.stylejy.app

import scala.xml.{Text, Node}

object Template {

  def page(title:String, content:Seq[Node], url: String => String = identity _, head: Seq[Node] = Nil, foot: Seq[Node] = Nil, scripts: Seq[String] = Seq.empty, defaultScripts: Seq[String] = Seq("osmTools.js")) = {
    <html lang="en">
      <head>
        <title>{ title }</title>
        <meta charset="utf-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <meta name="description" content="" />
        <meta name="author" content="" />

        <!-- Latest compiled and minified CSS -->
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous" />
        <!-- Optional theme -->
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css" integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp" crossorigin="anonymous" />
        <!-- Latest compiled and minified JavaScript -->
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
        <link href="/assets/css/PageStyle.css" rel="stylesheet" />

        {head}
      </head>

      <body onresize="mapHeight()">
        <div class="navbar navbar-inverse navbar-fixed-top">
          <div class="container">
            <div class="navbar-header">
              <a class="navbar-brand" href="/"><p class="header-content">{ title }</p></a>
            </div>
            <div class="collapse navbar-collapse" id="myNavbar">
              <ul class="nav navbar-nav navbar-right">
                <li><a class="header-content">JooYoung Lee, MSc Advanced Computing</a></li>
              </ul>
            </div>
          </div>
        </div>

        <div class="container" id="map">
          {content}
        </div>

        <footer class="container-fluid text-center navbar-fixed-bottom" id="controller">
          {foot}
          <p id="test">Thanks God for All :D</p>
        </footer>

        <!-- javascript -->
        { (defaultScripts ++ scripts) map { pth =>
        <script type="text/javascript" src={pth}></script>
      } }

      </body>

    </html>
  }
}