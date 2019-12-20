package com.abuhrov;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class KickerRatingBot extends TelegramLongPollingBot {

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage message = new SendMessage().setChatId(update.getMessage().getChatId());

        message = switch (update.getMessage().getText()) {
            case "/newresult":
                yield message.setReplyMarkup(ReplyKeyboardBuilder.createReply()
                        .row().addText("Player 1")
                        .row().addText("Player 2")
                        .row().addText("Player 3")
                        .build()
                );
            case "/showrating":
                yield message.setText("""
                    player1 - 2000
                    player3 - 2400
                """);
            default:
                yield message.setText("Користуйся краще готовими командами, я не настільки розумний..");
        };

        try {
            execute(message);
        } catch (TelegramApiException ignored) {
        }
    }

    public String getBotUsername() {
        return "KickerRatingBot";
    }

    public String getBotToken() {
        return "877837908:AAGFnBbQp2sPQQG67WPmIdT8vXaFRLcIxio";
    }
}