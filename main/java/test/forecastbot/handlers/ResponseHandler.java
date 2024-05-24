package test.forecastbot.handlers;

import org.apache.velocity.runtime.directive.Parse;
import org.json.simple.parser.ParseException;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import test.forecastbot.entities.Day;
import test.forecastbot.entities.User;
import test.forecastbot.utils.ApiUtils;
import test.forecastbot.utils.KeyboardFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static test.forecastbot.constants.UserState.*;
public class ResponseHandler {
    private final SilentSender sender;
    private final Map<Long, User> users;
    public ResponseHandler(SilentSender sender) {
        this.sender = sender;
        users = new HashMap<>();
    }

    public synchronized void replyToStart(long chatId) {
        try {
            User user = new User(chatId, AWAITING_INPUT);
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText("""
                    Привіт! Я бот, який дозволяє отримувати прогноз погоди!:
                    Що хочеш зробити?
                    """);
            message.setReplyMarkup(KeyboardFactory.getMenuKeyBoard());
            sender.execute(message);
            users.put(chatId, user);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


    public boolean userIsActive(long chatId) {
        return users.containsKey(chatId);
    }

    public void replyToButtons(Long chatId, Update update) {
        System.out.println(update.getMessage().getText());
        if (update.getMessage().getText().equalsIgnoreCase("/stop")) {
            users.get(chatId).setState(STOP);
        }
        else if (update.getMessage().getText().equalsIgnoreCase("Отримати прогноз погоди"))
            users.get(chatId).setState(WAITING_FOR_CITY_ENTRY);
        switch (users.get(chatId).getState()) {
            case WAITING_FOR_CITY_ENTRY -> prepareToGetCityName(chatId);
            case AWAITING_INPUT -> getCityName(chatId, update);
            case STOP -> goodbye(chatId);
        }
    }

    private void prepareToGetCityName(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Будь ласка, введіть назву міста, в якому в хочете отримати прогноз погоди.\n" +
                "За бажанням, також можно уточнити кількість днів для прогнозу, якщо ввести \"Назва міста, кількість днів\" (від 1 до 5)");
        sender.execute(sendMessage);
        users.get(chatId).setState(AWAITING_INPUT);
    }
    private void getCityName(Long chatId, Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        if (update.getMessage().getText() != null && !update.getMessage().getText().isEmpty() &&
                !update.getMessage().getText().isBlank()) {
            try {
                String[] split = update.getMessage().getText().split(",\\s?");
                if (split.length == 1) {
                    Day.getPartOfPrognosis(ApiUtils.getFiveDayForecastByCityName(update.getMessage().getText()),
                            5, sendMessage, sender);
                } else {
                    try {
                        Day.getPartOfPrognosis(ApiUtils.getFiveDayForecastByCityName(update.getMessage().getText()),
                                Integer.parseInt(split[1].strip()), sendMessage, sender);
                    } catch (NumberFormatException exception) {
                        sendMessage.setText("Помилка при обробці кількості днів!");
                    }
                }
            } catch (IOException exception) {
                sendMessage.setText("Помилка під час обробки запиту");
                sender.execute(sendMessage);
            } catch (ParseException exception) {
                sendMessage.setText("Помилка під час обробки відповіді серверу");
                sender.execute(sendMessage);
            }
        }
        users.get(chatId).setState(WAITING_FOR_CITY_ENTRY);
    }
    private void goodbye(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove());
        sender.execute(sendMessage);
        users.remove(chatId);
    }
    private boolean checkIfPresent(long chatId) {
        return users.get(chatId).getUserName() != null;
    }
}