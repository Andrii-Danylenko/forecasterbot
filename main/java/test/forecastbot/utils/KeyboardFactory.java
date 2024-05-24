package test.forecastbot.utils;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class KeyboardFactory {
    public static ReplyKeyboardMarkup getMenuKeyBoard() {
        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(new KeyboardButton("Отримати прогноз погоди"));
        keyboardRowList.add(keyboardRow1);
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(keyboardRowList);
        markup.setResizeKeyboard(true);
        markup.setSelective(true);
        markup.setOneTimeKeyboard(true);
        return markup;
    }
}
