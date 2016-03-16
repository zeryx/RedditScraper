/**
  * Created by james on 15/03/16.
  */

import java.io._
import java.net.URL
import javax.imageio.{IIOException, ImageIO}

import org.apache.commons.io.{FilenameUtils, FileUtils}
import play.api.libs.json._
import scala.util.Try


object redditScraper {

  def main(args: Array[String]): Unit = {

    val dir = new File(s"${args(2)}/reddit/${args(0)}/")
    dir.mkdirs()

    //open a new csv file
    val csvFile = new File(s"${dir.getAbsolutePath}/${args(0)}.csv")
    //makes a new request
    var last: String = ""

    for (i <- 0 until args(1).toInt by 20) {
      try {
        val url = new URL(s"http://www.reddit.com/r/${args(0)}/new.json?sort=new&after=$last")
        val conn = url.openConnection()
        conn.setRequestProperty("User-Agent", "scala:com.algorithmia.redditImageScraper:v1.0.0 (by /u/zeryx1211)")
        conn.connect()
        val response: BufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream))


        val input = Json.parse(response.readLine())

        response.close()

        val bw = new BufferedWriter(new FileWriter(csvFile, true))

        val items = (input \ "data" \ "children" \\ "data").toList

        if (Try(last = (Json.parse(items.last.toString()) \ "name").get.toString.replaceAll("^\"|\"$", "")).isSuccess) {

          val images = items.map((item: JsValue) => (Json.parse(item.toString()) \ "url").get.toString().replaceAll("^\"|\"$", ""))


          images.foreach((link: String) => {
            if (FilenameUtils.getExtension(link) == "jpg"
              || FilenameUtils.getExtension(link) == "png") {
              val file = new File(s"${dir.getAbsolutePath}/${FilenameUtils.getName(link)}")
              Try(FileUtils.copyURLToFile(new URL(link), file, 3000, 3000))
              //write image url to csv file.

              try {
                ImageIO.read(file)
                bw.write(s"$link,")
                bw.newLine()
              } catch {
                case e: IIOException => FileUtils.forceDelete(file)
              }
            }
          })
          bw.close()
        }
      } catch {
        case e: IOException => println(e.getMessage);Thread.sleep(1000)
      }
    }
  }
}
