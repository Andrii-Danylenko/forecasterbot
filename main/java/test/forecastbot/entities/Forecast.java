package test.forecastbot.entities;

import java.util.HashMap;

public class Forecast {
    private final String longitude; // Довжина
    private final String latitude; // Широта
    private final String country; // Країна
    private final String weatherDescription; // Яка очікується погода
    private final String temp; // Реальна температура
    private final String feelsLike; // Як відчувається
    private final String tempMin; // Мінімальна температура за проміжок часу
    private final String tempMax; // Максимальна температура за проміжок часу
    private final String humidity; // Вологість за проміжок часу
    private final String date; // Дата
    private final String city; // Назва міста
    public Forecast(HashMap<String, String> forecastMap) {
        longitude = forecastMap.get("lon");
        latitude = forecastMap.get("lat");
        country = forecastMap.get("country");
        weatherDescription = forecastMap.get("description");
        temp = forecastMap.get("temp");
        feelsLike = forecastMap.get("feels_like");
        tempMin = forecastMap.get("temp_min");
        tempMax = forecastMap.get("temp_max");
        humidity = forecastMap.get("humidity");
        date = forecastMap.get("dt_txt");
        city = forecastMap.get("city");
    }

    @Override
    public String toString() {
        return """
                У %s
                Очікувана погода: %s,
                Температура: %s°C(мінімальна - %s°C, максимальна - %s°C), відчувається як %s°C,
                Вологість: %sг/м3.
                """.formatted(date.substring(11, date.length() - 3), weatherDescription, temp, tempMin, tempMax, feelsLike, humidity);
    }

    public String getDate() {
        return date;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }
}
