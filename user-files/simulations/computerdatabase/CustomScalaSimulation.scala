package computerdatabase

import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.http.Predef._

class CustomScalaSimulation extends Simulation {

	def apply = {

		val urlBase = "http://computer-database.herokuapp.com"

		val httpConf = httpConfig
						.baseURL(urlBase)


		val scn 
			= scenario("Play with the Computer Database")
				.exec(
					http("Index page")
						.get("/")
						.check(
							status.is(200),
							css("head title").is("Computers database"),
							currentLocation.is(urlBase + "/computers")
						)
				)
				.exec(
					http("Apple computers")
						.get("/computers?f=apple")
						.check(
							status.is(200),
							regex("""(?s)<a href="([^"]+)">Apple Lisa</a>""").find.saveAs("appleLisaLocation")
						)
				)
				
				.exec((s: Session) => {
					val url = s.getTypedAttribute[String]("appleLisaLocation")
					println("DEBUG url=" + url)
					s
				})

				.exec(
					http("Apple Lisa")
						.get("${appleLisaLocation}")
						.check(
							status.is(200),
							css("#name", "value").is("Apple Lisa")
						)
				)

		List(scn.configure.users(10).ramp(60).protocolConfig(httpConf))
	}
}
