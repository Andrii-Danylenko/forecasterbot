package test.forecastbot.entities;

import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.List;

public class Day {
    private List<Forecast> forecasts;
    public Day(List<Forecast> forecasts) {
        this.forecasts = forecasts;
    }

    public Day() {
        forecasts = new ArrayList<>();
    }
    public void addForecast(Forecast forecast) {
        forecasts.add(forecast);
    }
    public static List<Day> divideByDays(List<Forecast> forecasts) {
        String date = forecasts.get(0).getDate().substring(0, 11);
        List<Day> days = new ArrayList<>();
        Day day = new Day();
        for (Forecast forecast : forecasts) {
            if (forecast.getDate().startsWith(date)) day.addForecast(forecast);
            else {
                date = forecast.getDate().substring(0, 11);
                days.add(day);
                day = new Day();
                day.addForecast(forecast);
            }
        }
        return days;
    }
    public static String getPartOfPrognosis(List<Day> days, int limit) {
        if (limit - 1 < 0 || limit > 5) {
            throw new IllegalArgumentException("Ліміт днів для виведення не може бути менше 1 та більше 5");
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < limit; i++) {
            builder.append(days.get(i));
        }
        return builder.toString();
    }
    public static void getPartOfPrognosis(List<Day> days, int limit, SendMessage sendMessage, SilentSender sender) {
        if (limit - 1 < 0 || limit > 5) {
            sendMessage.setText("Ліміт днів для виведення не може бути менше 1 та більше 5");
            sender.execute(sendMessage);
            return;
        }
        for (int i = 0; i < limit; i++) {
            sendMessage.setText(days.get(i).toString());
            sender.execute(sendMessage);
        }
    }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("=".repeat(53) + "%n%nПрогноз погоди у місті %s(%s) на %s:%n%n"
                .formatted(forecasts.get(0).getCity(), forecasts.get(0).getCountry(), forecasts.get(0).getDate().substring(0, 10)));
        forecasts.forEach(x -> builder.append(x).append("\n"));
        return builder.append("=".repeat(53)).toString();
    }
}
