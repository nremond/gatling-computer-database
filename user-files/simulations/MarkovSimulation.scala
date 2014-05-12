package computerdatabase

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._

class MarkovSimulation extends Simulation {

  val baseURL = "http://computer-database.herokuapp.com"

  val httpConf = http
          .baseURL(baseURL)


  val browseAppleLisa = 
      exec(
        http("Apple computers")
          .get("/computers?f=apple")
          .check(
            regex("""(?s)<a href="([^"]+)">Apple Lisa</a>""").find.saveAs("appleLisaLocation")
          )
      )
      .pause(2 seconds)
      .exec(
        http("Apple Lisa")
          .get("${appleLisaLocation}")
          .check(
            css("#name", "value").is("Apple Lisa")
          )
      )

  val browseIbms = 
      exec(
        http("IBM computers")
          .get("/computers?f=ibm")
          .check(
            regex("""(?s)<a href="([^"]+)">IBM 305</a>""").find.saveAs("ibm305Location"),
            regex("""(?s)<a href="([^"]+)">IBM 701</a>""").find.saveAs("ibm701Location")
          )
      )
      .pause(2 seconds)
      .randomSwitch(
          54d -> exec(
              http("IBM 305")
                .get("${ibm305Location}")
                .check(
                  css("#name", "value").is("IBM 305")
                )
            ),
          40d -> exec(
              http("IBM 701")
                .get("${ibm701Location}")
                .check(
                  css("#name", "value").is("IBM 701")
                )
            )
        )


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
      .pause(3 seconds)
      .randomSwitch(
        43d -> browseIbms,
        31d -> browseAppleLisa
      )

  setUp(scn.inject(rampUsers(100) over (30 seconds))).protocols(httpConf)      
}
