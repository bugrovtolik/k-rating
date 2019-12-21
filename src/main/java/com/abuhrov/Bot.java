package com.abuhrov;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Properties;

public class Bot extends TelegramLongPollingBot {
    private static String BOT_NAME;
    private static String BOT_TOKEN;

    private String history;
    private Rating player1;
    private Rating player2;
    private Rating player3;
    private Rating player4;

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage message = new SendMessage().setChatId(update.getMessage().getChatId());

        if ("Охрана отмєна".equals(update.getMessage().getText())) {
            clean();
        } else if (history != null) {
            if ("/addplayer".equals(history)) {
                history = null;
                Control.savePlayer(Control.getPlayer(update.getMessage().getText()));
                message.setText("Додано гравця " + update.getMessage().getText())
                        .setReplyMarkup(ReplyKeyboardBuilder.createReply().build());
            } else if ("/newresult".equals(history)) {
                var builder = ReplyKeyboardBuilder.createReply();
                Iterator<String> iterator = Control.getPlayersIterator();
                while (iterator.hasNext()) {
                    builder.row().addText(iterator.next());
                }
                builder.row().addText("Охрана отмєна");
                message.setReplyMarkup(builder.build());

                Rating player = Control.getPlayer(update.getMessage().getText());
                if (player1 == null) {
                    player1 = player;
                    message.setText("Обери напарника");
                } else if (player2 == null) {
                    player2 = player;
                    message.setText("Обери першого суперника");
                } else if (player3 == null) {
                    player3 = player;
                    message.setText("Обери другого суперника");
                } else if (player4 == null) {
                    player4 = player;

                    message.setText("Хто переміг?");

                    builder = ReplyKeyboardBuilder.createReply();
                    builder.row().addText("Ми");
                    builder.row().addText("Вони");
                    builder.row().addText("Охрана отмєна");

                    message.setReplyMarkup(builder.build());
                } else {
                    message.setText("Готово").setReplyMarkup(ReplyKeyboardBuilder.createReply().build());

                    if ("Ми".equals(update.getMessage().getText())) {
                        Control.calculate(player1, player2, player3, player4);
                    } else if ("Вони".equals(update.getMessage().getText())) {
                        Control.calculate(player3, player4, player1, player2);
                    }

                    Control.savePlayer(player1);
                    Control.savePlayer(player2);
                    Control.savePlayer(player3);
                    Control.savePlayer(player4);

                    clean();
                }
            }
        } else {
            message = switch (update.getMessage().getText()) {
                case "/addplayer": {
                    history = "/addplayer";

                    var builder = ReplyKeyboardBuilder.createReply().row().addText("Охрана отмєна");

                    yield message.setText("Введи нікнейм").setReplyMarkup(builder.build());
                }
                case "/newresult": {
                    history = "/newresult";

                    var builder = ReplyKeyboardBuilder.createReply();
                    Iterator<String> iterator = Control.getPlayersIterator();
                    while (iterator.hasNext()) {
                        builder.row().addText(iterator.next());
                    }

                    builder.row().addText("Охрана отмєна");
                    yield message.setText("Обери себе").setReplyMarkup(builder.build());
                }
                case "/showrating":
                    yield message.setText(Control.read()).setReplyMarkup(ReplyKeyboardBuilder.createReply().build());
                default:
                    yield message.setText("Користуйся краще готовими командами, я не настільки розумний..")
                            .setReplyMarkup(ReplyKeyboardBuilder.createReply().build());
            };
        }

        try {
            sendApiMethod(message);
        } catch (TelegramApiException ignored) {
        }
    }

    private void clean() {
        history = null;
        player1 = null;
        player2 = null;
        player3 = null;
        player4 = null;
    }

    public static void main(String[] args) {
        ApiContextInitializer.init();
        readConfig();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new Bot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private static void readConfig() {
        FileInputStream fis;
        Properties property = new Properties();
        try {
            fis = new FileInputStream("src/main/resources/config.properties");
            property.load(fis);

            BOT_NAME = property.getProperty("BOT_NAME");
            BOT_TOKEN = property.getProperty("BOT_TOKEN");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }
}