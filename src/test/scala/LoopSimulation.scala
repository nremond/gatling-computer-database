package computerdatabase

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._

class LoopSimulation extends Simulation {

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
      
      .exec((session: Session) 
          => session.set("pageIndex", 0) )
      .asLongAs((s:Session) => !s.contains("ibmSystemILocation") 
            && s("pageIndex").as[Int] < 3) {
          
          exec(
            http("IBM computers")
              .get("/computers?f=ibm&p=${pageIndex}")
              .check(
                regex("""(?s)<a href="([^"]+)">IBM System i</a>""").dontValidate.saveAs("ibmSystemILocation")
            )
          )
          .exec((session: Session) 
            => session.set("pageIndex", session("pageIndex").as[Int] + 1) ) 
        }
        
      

      .pause(1 seconds)
      .exec(
        http("IBM System i")
          .get("${ibmSystemILocation}")
          .check(
            css("#name", "value").is("IBM System i")
          )
      )


  setUp(scn.inject(rampUsers(100) over (30 seconds))).protocols(httpConf)      
  
}
