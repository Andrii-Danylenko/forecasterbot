package test.forecastbot.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
@Component("Requester")
public class Requester {
    private final String apiKey;
    private final RequestBuilder requestBuilder;

    @Autowired
    public Requester(Environment environment) {
        apiKey = environment.getProperty("weatherApiKey");
        requestBuilder = new RequestBuilder();
    }
    // Тут ми створюємо сам HTTPS GET-запрос
    public String makeRequest(String baseUrl, HashMap<String, String> params) throws IOException {
        URL url = new URL(baseUrl + requestBuilder.getParameters(params));
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setRequestMethod("GET");
        return readRequest(connection);
    }
    // Тут ми зчитуємо відповідь сервера
    private String readRequest(HttpsURLConnection connection) throws IOException {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        connection.disconnect();
        return content.toString();
    }
    // Тут ми створюємо параметри GET-запросу
    private class RequestBuilder {
        public String getParameters(HashMap<String, String> params) {
            StringBuilder builder = new StringBuilder();
            params.forEach((key, value) -> builder.append(URLEncoder.encode(key, StandardCharsets.UTF_8))
                    .append("=").append(URLEncoder.encode(value, StandardCharsets.UTF_8)).append("&"));
            return !builder.isEmpty() ? builder.append("appid=").append(apiKey).toString() : builder.toString();
        }
    }
}
