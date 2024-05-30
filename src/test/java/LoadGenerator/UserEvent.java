package LoadGenerator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class UserEvent extends Simulation {
    private String[] eventNames = {"main_bottomNavigation", "home_imp", "order_category_imp", "order_subcategory", "order_category_selectsku", "home_notice_close", "order_detail_sirenorder", "abx:purchase", "home_orderstatus_imp", "order_category_back"};
    private Random rand = new Random();
    int randomInt = this.rand.nextInt(100);
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

        switch (eventName) {

            case "main_bottomnavigation":
                paramMap.put("page_type", randomValue.get());
                paramMap.put("screen_name", "main");
                break;

            case "home_imp":
                paramMap.put("seq", String.valueOf(randomInt - randomInt % 10));
                paramMap.put("content_id", String.valueOf(randomInt - randomInt % 10));
                paramMap.put("content_name", randomValue.get());
                paramMap.put("screen_name", "home");
                break;

            case "order_category_imp":
                paramMap.put("main_category", rand.nextBoolean() ? "Drink" : "Food");
                paramMap.put("sub_category", rand.nextBoolean() ? "Y" : "N");
                paramMap.put("screen_name", "order_category");
                break;

            case "order_subcategory":
                paramMap.put("sub_category", randomValue.get());
                paramMap.put("sub_category_no", String.valueOf(randomInt - randomInt % 10));
                paramMap.put("non_member_yn", rand.nextBoolean() ? "Y" : "N");
                paramMap.put("green_dot_yn", rand.nextBoolean() ? "Y" : "N");
                paramMap.put("screen_name","order");
                break;

            case "order_category_selectsku":
                paramMap.put("sku_name", randomValue.get());
                paramMap.put("screen_name", "order_category");
                break;

            case "home_notice_close":
                paramMap.put("content_name", randomValue.get());
                paramMap.put("screen_name", "home_notice");
                break;

            case "order_detail_sirenorder":
                paramMap.put("sku_name", randomValue.get());
                paramMap.put("sku_no", String.valueOf(randomInt - randomInt % 10));
                paramMap.put("screen_name", "order_detail");
                break;

            case "abx:purchase":
                paramMap.put("skuinfo", String.valueOf(randomInt - randomInt % 10));
                paramMap.put("qty", String.valueOf(randomInt - randomInt % 10));
                paramMap.put("receivedata", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(randomDateTime()));
                paramMap.put("receivestore", randomValue.get());
                paramMap.put("patmenttype", rand.nextBoolean() ? "Card" : "Cash");
                paramMap.put("paymentprice", String.valueOf(randomInt - randomInt % 10));
                paramMap.put("usedcoupon", rand.nextBoolean() ? "Y" : "N");
                paramMap.put("usedcouponprice", String.valueOf(randomInt - randomInt % 10));
                paramMap.put("usedgift", rand.nextBoolean() ? "Y" : "N");;
                paramMap.put("usedgiftprice", String.valueOf(randomInt - randomInt % 10));
                paramMap.put("orderprice", String.valueOf(randomInt - randomInt % 10));
                paramMap.put("discounttype", rand.nextBoolean() ? "Y" : "N");
                paramMap.put("discountprice", String.valueOf(randomInt - randomInt % 10));
                paramMap.put("totalorderprice", String.valueOf(randomInt - randomInt % 10));
                paramMap.put("receipyn", rand.nextBoolean() ? "Y" : "N");
                paramMap.put("screen_name", "order_payment_orderconfirm");
                break;

            case "home_orderstatus_imp":
                paramMap.put("order_type", rand.nextBoolean() ? "At store" : "Drive-thru");
                paramMap.put("store_name", randomValue.get());
                paramMap.put("eating_type", rand.nextBoolean() ? "For here" : "To go");
                paramMap.put("screen_name", "home_orderstatus");
                break;

            case "order_category_back":
                paramMap.put("main_category", rand.nextBoolean() ? "Drink" : "Food");
                paramMap.put("sub_category", rand.nextBoolean() ? "Y" : "N");
                paramMap.put("screen_name", "order_category");
                break;
        }
        return this.objectMapper.writeValueAsString(paramMap);
    }

    private Iterator<Map<String, Object>> customFeeder =
            Stream.generate((Supplier<Map<String, Object>>) () -> {

                String eventName = this.eventNames[rand.nextInt(10)];
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
                hmap.put("os", "android " + rand.nextInt(10));
                hmap.put("model", randomValue.get());
                hmap.put("vendor", randomValue.get());
                hmap.put("resolution", rand.nextInt(1920) + "x" + rand.nextInt(1080));
                hmap.put("is_portrait", rand.nextBoolean());
                hmap.put("platform", rand.nextBoolean() ? "Android" : "Ios");
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

    private ScenarioBuilder scn = scenario("Starducks User's Event Info Sending Simulation")
            .feed(customFeeder)
            .exec(http("Send User's Event Info")
                    .post("/postback/event")
                    .header("Content-Type", "application/json")
                    .body(StringBody(
                            "\n\n{"
                                    + "\"adid\": \"#{adid}\","
                                    + "\"gaid\": \"#{gaid}\","
                                    + "\"idfa\": \"#{idfa}\","
                                    + "\"idfv\": \"#{idfv}\","
                                    + "\"ad_id_opt_out\": \"#{ad_id_opt_out}\","
                                    + "\"os\": \"#{os}\","
                                    + "\"model\": \"#{model}\","
                                    + "\"vendor\": \"#{vendor}\","
                                    + "\"resolution\": \"#{resolution}\","
                                    + "\"is_portrait\": \"#{is_portrait}\","
                                    + "\"platform\": \"#{platform}\","
                                    + "\"network\": \"#{network}\","
                                    + "\"is_wifi_only\": \"#{is_wifi_only}\","
                                    + "\"carrier\": \"#{carrier}\","
                                    + "\"language\": \"#{language}\","
                                    + "\"country\": \"#{country}\","
                                    + "\"build_id\": \"#{build_id}\","
                                    + "\"package_name\": \"#{package_name}\","
                                    + "\"appkey\": \"#{appkey}\","
                                    + "\"sdk_version\": \"#{sdk_version}\","
                                    + "\"installer\": \"#{installer}\","
                                    + "\"app_version\": \"#{app_version}\","
                                    + "\"event_name\": \"#{event_name}\","
                                    + "\"group\": \"#{group}\","
                                    + "\"event_datetime\": \"#{event_datetime}\","
                                    + "\"event_timestamp\": \"#{event_timestamp}\","
                                    + "\"event_timestamp_d\": \"#{event_timestamp_d}\","
                                    + "\"event_datetime_kst\": \"#{event_datetime_kst}\","
                                    + "\"event_timestamp_kst\": \"#{event_timestamp_kst}\","
                                    + "\"event_timestamp_d_kst\": \"#{event_timestamp_d_kst}\","
                                    + "\"order_id\": \"#{abx:order_id}\","
                                    + "\"order_sales\": \"#{abx:order_sales}\","
                                    + "\"event_param\": #{event_param}"
                                    + "}\n\n")
                    ).asJson().check(status().is(200))
            );
    {
        setUp(
                scn.injectOpen(atOnceUsers(5))
        ).protocols(httpProtocol);
    }
}