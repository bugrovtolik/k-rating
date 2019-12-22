package com.abuhrov;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Iterator;

public class Bot extends TelegramLongPollingBot {
    private static final String ADD_PLAYER = "/addplayer";
    private static final String ADD_PLAYER_UKR = "Додати гравця";
    private static final String NEW_RESULT = "/newresult";
    private static final String NEW_RESULT_UKR = "Новий результат";
    private static final String SHOW_RATING = "/showrating";
    private static final String SHOW_RATING_UKR = "Відобрази рейтинг";
    private static final String ABOUT_BOT_UKR = "Як ти рахуєш рейтинг?";
    private static final String ABORT_UKR = "Охрана отмєна";
    private static final String DEFAULT_UKR = "Користуйся краще готовими командами, я не настільки розумний..";
    private static final String ENTER_NICKNAME_UKR = "Введи нікнейм";
    private static final String ADDED_PLAYER_UKR = "Додано гравця ";
    private static final String PICK_YOURSELF_UKR = "Обери себе";
    private static final String PICK_TEAMMATE_UKR = "Обери напарника";
    private static final String PICK_1ST_ENEMY_UKR = "Обери першого суперника";
    private static final String PICK_2ND_ENEMY_UKR = "Обери другого суперника";
    private static final String WHO_WON_UKR = "Хто переміг?";
    private static final String WE_UKR = "Ми";
    private static final String THEY_UKR = "Вони";
    private static final String READY_UKR = "Готово";
    private String prevMessage;
    private Rating player1;
    private Rating player2;
    private Rating player3;
    private Rating player4;

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage message = new SendMessage().setChatId(update.getMessage().getChatId());

        if (ABORT_UKR.equals(update.getMessage().getText())) {
            clean();
        } else if (prevMessage != null) {
            if (ADD_PLAYER.equals(prevMessage)) {
                prevMessage = null;
                Control.savePlayer(Control.getPlayer(update.getMessage().getText()));
                message.setText(ADDED_PLAYER_UKR + update.getMessage().getText()).setReplyMarkup(getDefaultReply());
            } else if (NEW_RESULT.equals(prevMessage)) {
                var builder = ReplyKeyboardBuilder.createReply();
                Iterator<String> iterator = Control.getPlayersIterator();
                while (iterator.hasNext()) {
                    builder.row().addText(iterator.next());
                }
                builder.row().addText(ABORT_UKR);
                message.setReplyMarkup(builder.build());

                Rating player = Control.getPlayer(update.getMessage().getText());
                if (player1 == null) {
                    player1 = player;
                    message.setText(PICK_TEAMMATE_UKR);
                } else if (player2 == null) {
                    player2 = player;
                    message.setText(PICK_1ST_ENEMY_UKR);
                } else if (player3 == null) {
                    player3 = player;
                    message.setText(PICK_2ND_ENEMY_UKR);
                } else if (player4 == null) {
                    player4 = player;

                    message.setText(WHO_WON_UKR);

                    builder = ReplyKeyboardBuilder.createReply();
                    builder.row().addText(WE_UKR);
                    builder.row().addText(THEY_UKR);
                    builder.row().addText(ABORT_UKR);

                    message.setReplyMarkup(builder.build());
                } else {
                    message.setText(READY_UKR).setReplyMarkup(getDefaultReply());

                    if (WE_UKR.equals(update.getMessage().getText())) {
                        Control.calculate(player1, player2, player3, player4);
                    } else if (THEY_UKR.equals(update.getMessage().getText())) {
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
                case ADD_PLAYER, ADD_PLAYER_UKR: {
                    prevMessage = ADD_PLAYER;

                    var builder = ReplyKeyboardBuilder.createReply().row().addText(ABORT_UKR);

                    yield message.setText(ENTER_NICKNAME_UKR).setReplyMarkup(builder.build());
                }
                case NEW_RESULT, NEW_RESULT_UKR: {
                    prevMessage = NEW_RESULT;

                    var builder = ReplyKeyboardBuilder.createReply();
                    Iterator<String> iterator = Control.getPlayersIterator();
                    while (iterator.hasNext()) {
                        builder.row().addText(iterator.next());
                    }

                    builder.row().addText(ABORT_UKR);
                    yield message.setText(PICK_YOURSELF_UKR).setReplyMarkup(builder.build());
                }
                case SHOW_RATING, SHOW_RATING_UKR:
                    yield message.setText(Control.getRatingList()).setReplyMarkup(getDefaultReply());
                default:
                    yield message.setText(DEFAULT_UKR).setReplyMarkup(getDefaultReply());
            };
        }

        try {
            sendApiMethod(message);
        } catch (TelegramApiException ignored) {
        }
    }

    private ReplyKeyboard getDefaultReply() {
        return ReplyKeyboardBuilder.createReply()
                .row().addText(NEW_RESULT_UKR)
                .row().addText(SHOW_RATING_UKR)
                .row().addText(ADD_PLAYER_UKR)
                .row().addText(ABOUT_BOT_UKR)
                .build();
    }

    private void clean() {
        prevMessage = null;
        player1 = null;
        player2 = null;
        player3 = null;
        player4 = null;
    }

    @Override
    public String getBotUsername() {
        return "@KickerRatingBot";
    }

    @Override
    public String getBotToken() {
        return "877837908:AAGFnBbQp2sPQQG67WPmIdT8vXaFRLcIxio";
    }
}