
import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

class MyGatlingTest extends Simulation {

  val httpProtocol = http
    .baseURL("http://localhost:8080")
    .inferHtmlResources()
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
    .acceptEncodingHeader("gzip, deflate, sdch")
    .acceptLanguageHeader("en-US,en;q=0.8")
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36")

  val headers_0 = Map("Upgrade-Insecure-Requests" -> "1")

  val uri1 = "http://localhost:8080/health"

  val scn = scenario("RecordedSimulation")
    .exec(http("request_0")
      .get("/health")
      .headers(headers_0))


setUp(scn.inject(( constantUsersPerSec(1) during(5 seconds))).protocols(httpProtocol))
}