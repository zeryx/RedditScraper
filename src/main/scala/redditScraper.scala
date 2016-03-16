/**
  * Created by james on 15/03/16.
  */

import java.io.{InputStreamReader, BufferedReader, FileOutputStream, File}
import java.net.{URLConnection, URL}
import java.nio.channels.Channels
import java.util.Scanner
import javax.imageio.{IIOException, ImageIO}

import org.apache.commons.io.{FilenameUtils, FileUtils}
import play.api.libs.json._
import scala.io.Source
import scala.util.Try


object redditScraper extends App{

  this.apply("Amateur", 1000)


  def apply(subreddit: String, totalNum: Int) = {

    val dir = new File(s"/tmp/reddit//$subreddit/")
    dir.mkdirs()

    //makes a new request
    var last: String = ""
    for (i <- 0 until totalNum by 20) {
      val url = new URL(s"http://www.reddit.com/r/$subreddit/new.json?sort=new&after=$last")
      val conn = url.openConnection()
      conn.setRequestProperty("User-Agent", "scala:com.algorithmia.redditImageScraper:v1.0.0 (by /u/zeryx1211)")
      conn.connect()
      val response: BufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream))


      val input = Json.parse(response.readLine())

      response.close()

      val items = (input \ "data" \ "children" \\ "data").toList

      if(Try(last = (Json.parse(items.last.toString()) \ "name").get.toString.replaceAll("^\"|\"$", "")).isSuccess) {

        val images = items.map((item: JsValue) => (Json.parse(item.toString()) \ "url").get.toString().replaceAll("^\"|\"$", ""))

        images.foreach((link: String) => {
          if (FilenameUtils.getExtension(link) == "jpg"
            || FilenameUtils.getExtension(link) == "png") {
            val file = new File(s"${dir.getAbsolutePath}/${FilenameUtils.getName(link)}")
            Try(FileUtils.copyURLToFile(new URL(link), file, 3000, 3000))

            try{ImageIO.read(file)} catch{case e: IIOException =>  FileUtils.forceDelete(file)}
          }
        })
      }
    }
  }
}
