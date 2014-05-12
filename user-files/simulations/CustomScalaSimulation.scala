package computerdatabase

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._

class CustomScalaSimulation extends Simulation {

  val baseURL = "http://computer-database.herokuapp.com"

  val httpConf = http
          .baseURL(baseURL)

  val scn 
    = scenario("Play with the Computer Database")
      .exec(
        http("Index page")
          .get("/")
          .check(
            css("head title").is("Computers database"),
            currentLocation.is(baseURL + "/computers")
          )
      )
      .exec(
        http("Apple computers")
          .get("/computers?f=Apple")
          .check(
            regex("""(?s)<a href="([^"]+)">Apple Lisa</a>""").find.saveAs("appleLisaLocation")
          )
      )
      
      .exec((s: Session) => {
        val url = s("appleLisaLocation").validate[String]
        println("DEBUG url=" + url)
        s
      })

      .exec(
        http("Apple Lisa")
          .get("${appleLisaLocation}")
          .check(
            css("#name", "value").is("Apple Lisa")
          )
      )

  setUp(scn.inject(rampUsers(5) over (5 seconds))).protocols(httpConf)
}
