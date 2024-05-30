package java.LoadGenerator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class Basic_Combine_Param extends Simulation {
    private String[] eventNames = {"evt_user_properties_c:user_age", "evt_user_properties_c:user_gender", "evt_group", "common_a_region", "evt_param_screen_name", "evt_param_sku_count", "evt_param_page_type", "evt_user_properties_c:nick_name", "evt_user_properties_c:push_yn", "evt_user_properties_c:promotion_event_push_yn", "perties_c:user_gender"};
    private Random rand = new Random();
    private Supplier<String> randomValue = () -> RandomStringUtils.randomAlphanumeric(8);
    private ObjectMapper objectMapper = new ObjectMapper();
    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:7955")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    private LocalDateTime randomDateTime() {
        Random rand = new Random();
        int days = rand.nextInt(365);
        int hours = rand.nextInt(24);
        int minutes = rand.nextInt(60);
        int seconds = rand.nextInt(60);

        return LocalDateTime.now().minusDays(days).minusHours(hours).minusMinutes(minutes).minusSeconds(seconds);
    }

    private String getEventParamMap(String eventName) throws JsonProcessingException {
        Map<String, String> paramMap = new HashMap<>();

        paramMap.put("evt_user_properties_c:user_age", rand.nextBoolean() ? "30대 후반" : "20대 후반");
        paramMap.put("evt_user_properties_c:user_gender", rand.nextBoolean() ? "Americano" : "Latte");
        paramMap.put("evt_group", rand.nextBoolean() ? "abx" : "custom");
        paramMap.put("common_a_region", rand.nextBoolean() ? "seoul" : "gyeonggi-do");
        paramMap.put("evt_param_sku_count", String.valueOf(this.rand.nextInt(10)));
        paramMap.put("evt_param_page_type", rand.nextBoolean() ? "order" : "pay");
        paramMap.put("evt_user_properties_c:nick_name", randomValue.get());
        paramMap.put("evt_user_properties_c:push_yn", rand.nextBoolean() ? "true" : "false");
        paramMap.put("evt_user_properties_c:promotion_event_push_yn", rand.nextBoolean() ? "Y" : "N");
        int randomInt = this.rand.nextInt(100);
        paramMap.put("perties_c:user_gender", String.valueOf(randomInt - randomInt % 10));
        paramMap.put("evt_param_screen_name", randomValue.get());

        return this.objectMapper.writeValueAsString(paramMap);
    }

    private Iterator<Map<String, Object>> customFeeder =
            Stream.generate((Supplier<Map<String, Object>>) () -> {

                String eventName = this.eventNames[rand.nextInt(11)];
                String eventParamString = "{}";
                try {
                    eventParamString = this.getEventParamMap(eventName);

                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                System.out.println(eventParamString);
                HashMap<String, Object> hmap = new HashMap<>();
                hmap.put("adid", randomValue.get());
                hmap.put("gaid", randomValue.get());
                hmap.put("idfa", randomValue.get());
                hmap.put("idfv", randomValue.get());
                hmap.put("ad_id_opt_out", rand.nextBoolean());
                hmap.put("os", "Android " + rand.nextInt(10));
                hmap.put("model", randomValue.get());
                hmap.put("vendor", randomValue.get());
                hmap.put("resolution", rand.nextInt(1920) + "x" + rand.nextInt(1080));
                hmap.put("is_portrait", rand.nextBoolean());
                hmap.put("platform", rand.nextBoolean() ? "Android" : "iOS");
                hmap.put("network", rand.nextBoolean() ? "Wi-Fi" : "Cellular");
                hmap.put("is_wifi_only", rand.nextBoolean());
                hmap.put("carrier", randomValue.get());
                hmap.put("language", randomValue.get());
                hmap.put("country", randomValue.get());
                hmap.put("build_id", randomValue.get());
                hmap.put("package_name", randomValue.get());
                hmap.put("appkey", randomValue.get());
                hmap.put("sdk_version", randomValue.get());
                hmap.put("installer", randomValue.get());
                hmap.put("app_version", rand.nextInt(10) + "." + rand.nextInt(10) + "." + rand.nextInt(10));
                hmap.put("event_name", eventName);
                hmap.put("group", randomValue.get());
                hmap.put("event_datetime", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(randomDateTime()));
                hmap.put("event_timestamp", System.currentTimeMillis());
                hmap.put("event_timestamp_d", System.currentTimeMillis());
                hmap.put("event_datetime_kst", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(randomDateTime()));
                hmap.put("event_timestamp_kst", System.currentTimeMillis());
                hmap.put("event_timestamp_d_kst", System.currentTimeMillis());
                hmap.put("abx:order_id", randomValue.get());
                hmap.put("abx:order_sales", rand.nextInt(100) * 100);
                hmap.put("event_param", eventParamString);
                return hmap;
            }).iterator();

    private ScenarioBuilder scn = scenario("Starducks User Info Sending Simulation")
            .feed(customFeeder)
            .exec(http("Send User's Info")
                    .post("/postback/event")
                    .header("Content-Type", "application/json")
                    .body(StringBody(
                            "{"
                                    + "\"adid\": \"${adid}\","
                                    + "\"gaid\": \"${gaid}\","
                                    + "\"idfa\": \"${idfa}\","
                                    + "\"idfv\": \"${idfv}\","
                                    + "\"ad_id_opt_out\": \"${ad_id_opt_out}\","
                                    + "\"os\": \"${os}\","
                                    + "\"model\": \"${model}\","
                                    + "\"vendor\": \"${vendor}\","
                                    + "\"resolution\": \"${resolution}\","
                                    + "\"is_portrait\": \"${is_portrait}\","
                                    + "\"platform\": \"${platform}\","
                                    + "\"network\": \"${network}\","
                                    + "\"is_wifi_only\": \"${is_wifi_only}\","
                                    + "\"carrier\": \"${carrier}\","
                                    + "\"language\": \"${language}\","
                                    + "\"country\": \"${country}\","
                                    + "\"build_id\": \"${build_id}\","
                                    + "\"package_name\": \"${package_name}\","
                                    + "\"appkey\": \"${appkey}\","
                                    + "\"sdk_version\": \"${sdk_version}\","
                                    + "\"installer\": \"${installer}\","
                                    + "\"app_version\": \"${app_version}\","
                                    + "\"event_name\": \"${event_name}\","
                                    + "\"group\": \"${group}\","
                                    + "\"event_datetime\": \"${event_datetime}\","
                                    + "\"event_timestamp\": \"${event_timestamp}\","
                                    + "\"event_timestamp_d\": \"${event_timestamp_d}\","
                                    + "\"event_datetime_kst\": \"${event_datetime_kst}\","
                                    + "\"event_timestamp_kst\": \"${event_timestamp_kst}\","
                                    + "\"event_timestamp_d_kst\": \"${event_timestamp_d_kst}\","
                                    + "\"order_id\": \"${abx:order_id}\","
                                    + "\"order_sales\": \"${abx:order_sales}\","
                                    + "\"param_json\": ${event_param}"
                                    + "}")
                    ).asJson().check(status().is(200))
            );

    {
        setUp(
                scn.injectOpen(atOnceUsers(1))
        ).protocols(httpProtocol);
    }
}