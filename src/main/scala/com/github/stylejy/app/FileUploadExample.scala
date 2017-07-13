package com.github.stylejy.app

import java.io.{DataOutputStream, File, FileOutputStream}

import com.github.stylejy.app.OsmParser.dataOutputStream
import org.scalatra._
import servlet.{FileUploadSupport, MultipartConfig, SizeConstraintExceededException}

import xml.{Node, XML}

class RouterServletTest extends IntelligentOsmRouterStack with FileUploadSupport with FlashMapSupport {
  configureMultipartHandling(MultipartConfig(maxFileSize = Some(3*1024*1024)))

  def displayPage(content: Seq[Node]) = Template.page("File upload example", content, url(_))

  error {
    case e: SizeConstraintExceededException =>
      RequestEntityTooLarge(displayPage(
        <p>The file you uploaded exceeded the 3 MB limit.</p>))
  }

  get("/") {
    displayPage(
      <form action={url("/")} method="post" enctype="multipart/form-data">
        <p>File to upload: <input type="file" name="file" /></p>
        <p><input type="submit" value="Upload" /></p>
      </form>
        <p>
          Upload a file using the above form. After you hit "Upload"
          the file will be uploaded and your browser will start
          downloading it.
        </p>

        <p>
          The maximum file size accepted is 3 MB.
        </p>)
  }

  post("/") {
    val file = new File("osmdata/data.osm")
    fileParams("file").write(file)

    /*fileParams.get("file") match {
      case Some(file) =>

        Ok(file.get(), Map(
          "Content-Type"        -> (file.contentType.getOrElse("application/octet-stream")),
          "Content-Disposition" -> ("attachment; filename=\"" + file.name + "\"")
        ))



      case None =>
        BadRequest(displayPage(
          <p>
            Hey! You forgot to select a file.
          </p>))
    }*/
  }
}
