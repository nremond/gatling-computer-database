package computerdatabase

import com.excilys.ebi.gatling.core.Predef._
import com.excilys.ebi.gatling.http.Predef._

class LoopSimulation extends Simulation {

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
				
				.exec((session: Session) 
						=> session.setAttribute("pageIndex", 0) )
				.loop(
					chain
						.pauseExp(2)
						.exec(
							http("Apple computers")
								.get("/computers?f=ibm&p=${pageIndex}")
								.check(
									status.is(200),
									regex("""(?s)<a href="([^"]+)">IBM System z</a>""").whatever.saveAs("ibmSystemZLocation")
							)
						)
						.exec((session: Session) 
							=> session.setAttribute("pageIndex", session.getTypedAttribute[Integer]("pageIndex") + 1) ) 
					)
					.asLongAs((s:Session) => !s.isAttributeDefined("ibmSystemZLocation") && 
											 s.getTypedAttribute[Integer]("pageIndex") < 3)
				

				.pauseExp(2)
				.exec(
					http("IBM System z")
						.get("${ibmSystemZLocation}")
						.check(
							status.is(200),
							css("#name", "value").is("IBM System z")
						)
				)


		List(scn.configure.users(100).ramp(20).protocolConfig(httpConf))
	}
}
