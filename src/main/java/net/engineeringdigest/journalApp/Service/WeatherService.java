package net.engineeringdigest.journalApp.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.journalApp.api_response.WeatherResponse;
import net.engineeringdigest.journalApp.cache.AppCache;
import net.engineeringdigest.journalApp.constants.Placeholders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class WeatherService {

    @Value("${weather.api.key}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AppCache appCache;

    @Autowired
    private RedisService redisService;


    public WeatherResponse getWeather(String city) {

        String redisKey = "weather_of_" + city;

        log.info("=================================================");
        log.info("WEATHER SERVICE STARTED");
        log.info("City       : {}", city);
        log.info("Redis Key  : {}", redisKey);

        // =========================================================
        // CHECK REDIS CACHE
        // =========================================================

        log.info("Checking Redis cache...");

        WeatherResponse weatherResponse =
                redisService.get(redisKey, WeatherResponse.class);

        if (weatherResponse != null) {
            log.info("✅ REDIS CACHE HIT");
            log.info("WeatherResponse from Redis: {}", weatherResponse);

            if (weatherResponse.getCurrent() != null) {
                log.info("✅ Current weather is NOT NULL");
                log.info("Temperature: {}", weatherResponse.getCurrent().getTemperature());
                log.info("Feels Like: {}", weatherResponse.getCurrent().getFeelslike());
            } else {
                log.warn("⚠️  Current weather is NULL inside Redis response");
            }

            log.info("Returning weather data from Redis");
            log.info("=================================================");
            return weatherResponse;
        }

        log.info("❌ REDIS CACHE MISS - Calling real API");

        // =========================================================
        // BUILD API URL
        // =========================================================

        String finalAPI =
                appCache.appCache
                        .get(AppCache.keys.WEATHER_API.toString())
                        .replace(Placeholders.CITY, city)
                        .replace(Placeholders.API_KEY, apiKey);

        String safeAPI = finalAPI.replace(apiKey, "******");
        log.info("Weather API URL: {}", safeAPI);

        // =========================================================
        // STEP 1: GET RAW JSON STRING
        // =========================================================

        try {
            log.info("📡 Step 1: Fetching raw JSON from API...");

            ResponseEntity<String> rawResponse =
                    restTemplate.exchange(
                            finalAPI,
                            HttpMethod.GET,
                            null,
                            String.class
                    );

            String rawJson = rawResponse.getBody();
            log.info("🔴 RAW JSON FROM API: {}", rawJson);
            log.info("HTTP Status: {}", rawResponse.getStatusCode());

            // =========================================================
            // STEP 2: MANUALLY DESERIALIZE WITH OBJECTMAPPER
            // =========================================================

            log.info("📡 Step 2: Deserializing JSON with Jackson...");

            ObjectMapper mapper = new ObjectMapper();
            WeatherResponse body = mapper.readValue(rawJson, WeatherResponse.class);

            log.info("✅ Jackson Deserialization Successful");
            log.info("WeatherResponse object: {}", body);

            if (body.getCurrent() != null) {
                log.info("✅ Current object is NOT NULL");
                log.info("  - Temperature: {}", body.getCurrent().getTemperature());
                log.info("  - Feels Like: {}", body.getCurrent().getFeelslike());
                log.info("  - Observation Time: {}", body.getCurrent().getObservationTime());
            } else {
                log.error("❌ Current object is NULL after deserialization!");
                log.error("This means the JSON 'current' field doesn't exist or has a different name");
            }

            // =========================================================
            // STEP 3: SAVE TO REDIS
            // =========================================================

            log.info("💾 Saving to Redis...");

            redisService.set(redisKey, body, 300L);

            log.info("✅ Weather response saved to Redis (TTL: 300s)");
            log.info("=================================================");

            return body;

        } catch (Exception e) {
            log.error("❌ ERROR in WeatherService: {}", e.getMessage());
            log.error("Stack trace: ", e);
            log.info("=================================================");
            return null;
        }
    }
}