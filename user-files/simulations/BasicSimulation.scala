package computerdatabase

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._

class BasicSimulation extends Simulation {

  val baseURL = "computer-database.heroku.com"

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
      .exec(
        http("Apple Lisa")
          .get("${appleLisaLocation}")
          .check(
            css("#name", "value").is("Apple Lisa")
          )
      )

  setUp(scn.inject(rampUsers(100) over (30 seconds))).protocols(httpConf)      
}
