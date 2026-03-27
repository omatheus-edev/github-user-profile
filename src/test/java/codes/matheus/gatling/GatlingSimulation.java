package codes.matheus.gatling;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class GatlingSimulation extends Simulation {

    private final HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080")
            .acceptHeader("application/json")
            .userAgentHeader("Gatling Load Test")
            .maxConnectionsPerHost(20);

    private final FeederBuilder<String> userFeeder = csv("users.csv").random();

    private final ScenarioBuilder scn = scenario("GitHub Proxy - Profile + Repos + Events")
            .feed(userFeeder)
            .exec(
                    http("GET User Profile")
                            .get("/users/#{username}")
                            .check(status().in(200, 304))
            )
            .pause(1, 3)
            .exec(
                    http("GET User Repositories")
                            .get("/users/#{username}/repos/")
                            .check(status().in(200, 304))
            )
            .pause(1, 2)
            .exec(
                    http("GET User Events")
                            .get("/users/#{username}/events/")
                            .check(status().in(200, 304))
            );

    {
        setUp(
                scn.injectOpen(
                        rampUsersPerSec(5).to(60).during(Duration.ofSeconds(30)),
                        constantUsersPerSec(60).during(Duration.ofMinutes(2)),
                        rampUsersPerSec(60).to(5).during(Duration.ofSeconds(20))
                )
        )
                .protocols(httpProtocol)
                .assertions(
                        global().successfulRequests().percent().gt(95.0),
                        global().responseTime().percentile(95.0).lt(1200)
                );
    }
}