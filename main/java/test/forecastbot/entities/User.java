package test.forecastbot.entities;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import test.forecastbot.constants.UserState;

import java.util.ArrayDeque;
@Component
public class User {
    private long chatID;
    private String userName;
    private UserState state;
    private ArrayDeque<Message> lastMessages = new LimitedDeque<>(2);

    public User(long chatID, String fullName, String userName) {
        this.chatID = chatID;
        this.userName = userName;
    }

    public User(long chatID, UserState userState) {
        this.chatID = chatID;
        this.state = userState;
    }

    public User() {

    }

    public long getChatID() {
        return chatID;
    }

    public void setChatID(long chatID) {
        this.chatID = chatID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public UserState getState() {
        return state;
    }

    public void setState(UserState state) {
        this.state = state;
    }

    public ArrayDeque<Message> getLastMessages() {
        return lastMessages;
    }

    public void setLastMessages(ArrayDeque<Message> lastMessages) {
        this.lastMessages = lastMessages;
    }
}