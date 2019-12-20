package com.abuhrov;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class KickerRatingBot extends TelegramLongPollingBot {
    String history;

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage message = new SendMessage().setChatId(update.getMessage().getChatId());

        if (history != null) {
            if ("/addplayer".equals(history)) {
                history = null;
                Control.newPlayer(update.getMessage().getText());
                message.setText("Додано гравця " + update.getMessage().getText());
            };
        } else {
            message = switch (update.getMessage().getText()) {
                case "/addplayer":
                    history = "/addplayer";
                    yield message.setText("Введи нікнейм");
                case "/newresult":
                    yield message.setText("Reply Menu").setReplyMarkup(ReplyKeyboardBuilder.createReply()
                            .row().addText("Player 1")
                            .row().addText("Player 2")
                            .row().addText("Player 3")
                            .build()
                    );
                case "/showrating":
                    yield message.setText(Control.read());
                default:
                    yield message.setText("Користуйся краще готовими командами, я не настільки розумний..");
            };
        }

        try {
            sendApiMethod(message);
        } catch (TelegramApiException ignored) {
        }
    }

    public String getBotUsername() {
        return "kickerratingbot";
    }

    public String getBotToken() {
        return "877837908:AAGFnBbQp2sPQQG67WPmIdT8vXaFRLcIxio";
    }
}