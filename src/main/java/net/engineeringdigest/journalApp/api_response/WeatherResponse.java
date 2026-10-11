package net.engineeringdigest.journalApp.api_response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherResponse {

    private Current current;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)   // <-- this was missing
    public static class Current {

        @JsonProperty("temperature")
        private int temperature;

        @JsonProperty("observation_time")
        private String observationTime;

        @JsonProperty("feelslike")
        private Double feelslike;
    }
}