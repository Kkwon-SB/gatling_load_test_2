package videogamedb.simulation;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class VideoGameDbSimulations extends Simulation {

    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://videogamedb.uk/api")
            .acceptHeader("application/json");

    private static final int USER_COUNT = Integer.parseInt(System.getProperty("USERS", "5"));
    private static final int RAMP_DURATION = Integer.parseInt(System.getProperty("RAMP_DURATION", "10"));
    private static final int TEST_DURATION = Integer.parseInt(System.getProperty("TEST_DURATION", "20"));

    @Override
    public void before() { //테스트 실행 전에 사용자 수, 램프 기간 및 테스트 기간을 콘솔에 출력
        System.out.printf("Running test with %d users%n", USER_COUNT);
        System.out.printf("Ramping users over %d seconds%n", RAMP_DURATION);
        System.out.printf("Total test duration: %d seconds%n", TEST_DURATION);
    }

    private static ChainBuilder getAllVideoGames =
            exec(http("Get all video games")
                    .get("/videogame"));

    private static ChainBuilder getSpecificGame =
            exec(http("Get specific game")
                    .get("/videogame/2"));

    private ScenarioBuilder scn = scenario("Video game db - Section 7 code")
            .forever().on(
                    exec(getAllVideoGames)
                            .pause(5)
                            .exec(getSpecificGame)
                            .pause(5)
                            .exec(getAllVideoGames)
            );


    {
        setUp(
                scn.injectOpen(
                        nothingFor(5),
                        rampUsers(USER_COUNT).during(RAMP_DURATION)
                ).protocols(httpProtocol)
        ).maxDuration(TEST_DURATION);
    }
}