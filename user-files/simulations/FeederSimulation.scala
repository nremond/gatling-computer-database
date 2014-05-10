package computerdatabase

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._

class FeederSimulation extends Simulation {

  val computerFeeder = new io.gatling.core.feeder.Feeder[String] {

    import org.joda.time.LocalDate
    import org.joda.time.format.DateTimeFormat
    import scala.util.Random

    private val rng = new Random
    private val dateTimeFormat = DateTimeFormat.forPattern("yyyy-MM-dd")

    // random number in between [a...b]
    private def randInt(a: Int, b: Int) = rng.nextInt(b - a) + a

    override def hasNext = true

    override def next: Map[String, String] = {
      val introduceDate = new LocalDate(randInt(1960, 2000), randInt(1, 12), 1)
      val discontinuedDate = introduceDate.plusYears(randInt(1, 10))

      Map("company" -> "12", 
        "introduced" -> dateTimeFormat.print(introduceDate), 
        "discontinued" -> dateTimeFormat.print(discontinuedDate), 
        "name" -> ("SuperComputer v_" + java.util.UUID.randomUUID))
    }
  }

  val baseURL = "http://computer-database.herokuapp.com"

  val httpConf = http
          .baseURL(baseURL)

  val scn 
    = scenario("Play with the Computer Database")
      .feed(computerFeeder)
      .exec(
        http("Index page")
          .get("/")
          .check(
            css("head title").is("Computers database"),
            currentLocation.is(baseURL + "/computers")
          )
      )

      .pause(3 seconds)
      .exec(
        http("Register a computer")
          .post("/computers")
          .param("company", "${company}")
          .param("introduced", "${introduced}")
          .param("discontinued", "${discontinued}")
          .param("name", "${name}")
          .check(
            css("div.alert-message strong").is("Done!")
          )
      )

  setUp(scn.inject(rampUsers(100) over (30 seconds))).protocols(httpConf)      
}
