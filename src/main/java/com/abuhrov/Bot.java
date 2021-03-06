package com.abuhrov;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.stream.Collectors;

public class Bot extends TelegramLongPollingBot {
    private static final String ADD_PLAYER = "/addPlayer";
    private static final String ADD_PLAYER_UKR = "Додати гравця";
    private static final String NEW_RESULT_1ON1 = "/newResult1on1";
    private static final String NEW_RESULT_1ON1_UKR = "Новий результат 1 на 1";
    private static final String NEW_RESULT_2ON2 = "/newResult2on2";
    private static final String NEW_RESULT_2ON2_UKR = "Новий результат 2 на 2";
    private static final String SHOW_RATING = "/showRating";
    private static final String SHOW_RATING_UKR = "Відобрази рейтинг";
    private static final String ABOUT_BOT = "/about";
    private static final String ABOUT_BOT_UKR = "Як ти рахуєш рейтинг?";
    private static final String ABORT = "/abort";
    private static final String ABORT_UKR = "Охрана отмєна";
    private static final String DEFAULT_UKR = "Користуйся краще готовими командами, я не настільки розумний..";
    private static final String ENTER_NICKNAME_UKR = "Введи нікнейм";
    private static final String ADDED_PLAYER_UKR = "Додано гравця ";
    private static final String PICK_YOURSELF_UKR = "Обери себе";
    private static final String PICK_TEAMMATE_UKR = "Обери напарника";
    private static final String PICK_ENEMY_UKR = "Обери суперника";
    private static final String PICK_1ST_ENEMY_UKR = "Обери першого суперника";
    private static final String PICK_2ND_ENEMY_UKR = "Обери другого суперника";
    private static final String WHO_WON_UKR = "Хто переміг?";
    private static final String WE_UKR = "Ми";
    private static final String I_UKR = "Я";
    private static final String THEY_UKR = "Вони";
    private static final String ENEMY_UKR = "Суперник";
    private static final String READY_UKR = "Готово";
    private static final String OKAY_UKR = "Як скажеш";
    private static final String CLEAR_RESULTS = "/clear";
    private static final String CLEAR_RESULTS_UKR = "Видаляй результати";
    private static final String ADMIN_ONLY_UKR = "А ще чого? Ти тут не адмін";
    private static final Long ADMIN_CHAT_ID = 393239554L;
    private String prevMessage;
    private Rating player1;
    private Rating player2;
    private Rating player3;
    private Rating player4;

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage message = new SendMessage().setChatId(update.getMessage().getChatId());

        if (ABORT_UKR.equals(update.getMessage().getText()) || ABORT.equals(update.getMessage().getText())) {
            clean();
            message.setText(OKAY_UKR).setReplyMarkup(getDefaultReply());
        } else if (prevMessage != null) {
            switch (prevMessage) {
                case ADD_PLAYER -> {
                    prevMessage = null;
                    Control.savePlayers(List.of(Control.getPlayer(update.getMessage().getText())));
                    message.setText(ADDED_PLAYER_UKR + update.getMessage().getText()).setReplyMarkup(getDefaultReply());
                }
                case NEW_RESULT_1ON1, NEW_RESULT_1ON1_UKR -> {
                    final var builder = ReplyKeyboardBuilder.createReply();
                    List<String> players = Control.getPlayersList().stream().sorted().collect(Collectors.toList());
                    players.forEach(player -> builder.row().addText(player));
                    builder.row().addText(ABORT_UKR);
                    message.setReplyMarkup(builder.build());

                    Rating player = Control.getPlayer(update.getMessage().getText());
                    if (player1 == null) {
                        player1 = player;
                        message.setText(PICK_ENEMY_UKR);
                    } else if (player2 == null) {
                        player2 = player;

                        message.setText(WHO_WON_UKR);

                        var replyBuilder = ReplyKeyboardBuilder.createReply();
                        replyBuilder.row().addText(I_UKR);
                        replyBuilder.row().addText(ENEMY_UKR);
                        replyBuilder.row().addText(ABORT_UKR);

                        message.setReplyMarkup(replyBuilder.build());
                    } else {
                        message.setText(READY_UKR).setReplyMarkup(getDefaultReply());

                        if (I_UKR.equals(update.getMessage().getText())) {
                            Control.calculate(player1, player2);
                        } else if (ENEMY_UKR.equals(update.getMessage().getText())) {
                            Control.calculate(player2, player1);
                        }

                        Control.savePlayers(List.of(player1, player2));
                        clean();
                    }
                }
                case NEW_RESULT_2ON2, NEW_RESULT_2ON2_UKR -> {
                    final var builder = ReplyKeyboardBuilder.createReply();
                    List<String> players = Control.getPlayersList().stream().sorted().collect(Collectors.toList());
                    players.forEach(player -> builder.row().addText(player));
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

                        var replyBuilder = ReplyKeyboardBuilder.createReply();
                        replyBuilder.row().addText(WE_UKR);
                        replyBuilder.row().addText(THEY_UKR);
                        replyBuilder.row().addText(ABORT_UKR);

                        message.setReplyMarkup(replyBuilder.build());
                    } else {
                        message.setText(READY_UKR).setReplyMarkup(getDefaultReply());

                        if (WE_UKR.equals(update.getMessage().getText())) {
                            Control.calculate(player1, player2, player3, player4);
                        } else if (THEY_UKR.equals(update.getMessage().getText())) {
                            Control.calculate(player3, player4, player1, player2);
                        }

                        Control.savePlayers(List.of(player1, player2, player3, player4));
                        clean();
                    }
                }
            }
        } else {
            message = switch (update.getMessage().getText()) {
                case ADD_PLAYER, ADD_PLAYER_UKR: {
                    prevMessage = ADD_PLAYER;

                    var builder = ReplyKeyboardBuilder.createReply().row().addText(ABORT_UKR);

                    yield message.setText(ENTER_NICKNAME_UKR).setReplyMarkup(builder.build());
                }
                case NEW_RESULT_1ON1, NEW_RESULT_1ON1_UKR, NEW_RESULT_2ON2, NEW_RESULT_2ON2_UKR: {
                    prevMessage = update.getMessage().getText();

                    final var builder = ReplyKeyboardBuilder.createReply();
                    List<String> players = Control.getPlayersList().stream().sorted().collect(Collectors.toList());
                    players.forEach(player -> builder.row().addText(player));
                    builder.row().addText(ABORT_UKR);

                    yield message.setText(PICK_YOURSELF_UKR).setReplyMarkup(builder.build());
                }
                case SHOW_RATING, SHOW_RATING_UKR:
                    yield message.setText(Control.getRatingList()).setReplyMarkup(getDefaultReply());
                case ABOUT_BOT, ABOUT_BOT_UKR:
                    yield message.setText("http://www.glicko.net/glicko/glicko2.pdf").setReplyMarkup(getDefaultReply());
                case CLEAR_RESULTS, CLEAR_RESULTS_UKR:
                    if (update.getMessage().getChatId().equals(ADMIN_CHAT_ID)) {
                        Control.clearResults();
                    }
                    yield message.setText(update.getMessage().getChatId().equals(ADMIN_CHAT_ID) ? OKAY_UKR :
                            ADMIN_ONLY_UKR).setReplyMarkup(getDefaultReply());
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
		        .row().addText(NEW_RESULT_1ON1_UKR)
		        .row().addText(NEW_RESULT_2ON2_UKR)
		        .row().addText(SHOW_RATING_UKR)
		        .row().addText(ADD_PLAYER_UKR)
		        .row().addText(ABOUT_BOT_UKR)
		        .row().addText(CLEAR_RESULTS_UKR).build();
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
