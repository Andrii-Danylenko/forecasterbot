package test.forecastbot.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import test.forecastbot.entities.Day;
import test.forecastbot.entities.Forecast;

import java.io.IOException;
import java.util.*;
@Component("ApiUtils")
public class ApiUtils {
    private static final JSONParser parser = new JSONParser();
    private static Requester requester;

    @Autowired
    public ApiUtils(Requester requester) {
        ApiUtils.requester = requester;
    }
    public static Map<String, String> getCityNameByCoords(HashMap<String, String> hashMap) throws IOException, ParseException {
        JSONArray jsonArray = (JSONArray) parser.parse(requester.makeRequest("https://api.openweathermap.org/geo/1.0/reverse?", hashMap));
        return new HashMap<>() {
            {
                put("city", ((JSONObject)((JSONObject) jsonArray.get(0)).get("local_names")).get("uk").toString());
                put("country", ((JSONObject) jsonArray.get(0)).get("country").toString());
                put("lat", hashMap.get("lat"));
                put("lon", hashMap.get("lon"));
            }
        };
    }

    public static Map<String, String> getCoordsByCityName(String cityName) throws IOException, ParseException {
        JSONArray jsonArray = (JSONArray) parser.parse(requester.makeRequest("https://api.openweathermap.org/geo/1.0/direct?",
                new HashMap<>() {
                    {
                        put("q", cityName);
                    }
                }));
        return new HashMap<>() {
            {
                put("city", ((JSONObject)((JSONObject) jsonArray.get(0)).get("local_names")).get("uk").toString());
                put("country", ((JSONObject) jsonArray.get(0)).get("country").toString());
                put("lat", ((JSONObject) jsonArray.get(0)).get("lat").toString());
                put("lon", ((JSONObject) jsonArray.get(0)).get("lon").toString());
            }
        };
    }
    public static List<Day> getFiveDayForecastByCityName(String cityName) throws IOException, ParseException {
        Map<String, String> coords = getCoordsByCityName(cityName);
        JSONObject jsonObject = (JSONObject) parser.parse(requester.makeRequest("https://api.openweathermap.org/data/2.5/forecast?",
                new HashMap<>() {
                    {
                        put("lang", "ua");
                        put("lat", coords.get("lat"));
                        put("lon", coords.get("lon"));
                        put("units", "metric");
                    }
                }));
        HashMap<String, String> forecastHashMap = new LinkedHashMap<>();
        List<Forecast> forecasts = new ArrayList<>();
        for (Object object : (JSONArray) jsonObject.get("list")) {
            forecastHashMap.put("lat", coords.get("lat"));
            forecastHashMap.put("lon", coords.get("lon"));
            forecastHashMap.put("country", coords.get("country"));
            forecastHashMap.put("city", coords.get("city"));
            forecastHashMap.put("dt_txt", ((JSONObject) object).get("dt_txt").toString());
            forecastHashMap.put("temp", ((JSONObject) (((JSONObject) object).get("main"))).get("temp").toString());
            forecastHashMap.put("feels_like", ((JSONObject) (((JSONObject) object).get("main"))).get("feels_like").toString());
            forecastHashMap.put("temp_min", ((JSONObject) (((JSONObject) object).get("main"))).get("temp_min").toString());
            forecastHashMap.put("temp_max", ((JSONObject) (((JSONObject) object).get("main"))).get("temp_max").toString());
            forecastHashMap.put("humidity", ((JSONObject) (((JSONObject) object).get("main"))).get("humidity").toString());
            forecastHashMap.put("description", ((JSONObject)((JSONArray) ((JSONObject) object).get("weather")).get(0)).get("description").toString());
            forecasts.add(new Forecast(forecastHashMap));
            forecastHashMap.clear();
        }
        return Day.divideByDays(forecasts);
    }
}
