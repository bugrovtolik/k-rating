package com.abuhrov;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import spark.Request;
import spark.Response;
import spark.Route;

public class KickerRatingBot implements Route {

	private static final String TOKEN = "877837908:AAGFnBbQp2sPQQG67WPmIdT8vXaFRLcIxio";
	private final TelegramBot bot = new TelegramBot(TOKEN);

	@Override
	public Object handle(Request request, Response response) {
		Update update = BotUtils.parseUpdate(request.body());
		Message message = update.message();
		Long chatId = message.chat().id();
		System.out.println("handling: " + chatId);

		if ("newResult".equals(message.text())) {
			bot.execute(new SendMessage(chatId, "new Result indeed"));
		} else if ("showRating".equals(message.text())) {
			bot.execute(new SendMessage(chatId, "show"));
		}

		return "ok";
	}

	String getToken() {
		return TOKEN;
	}

	TelegramBot getBot() {
		return bot;
	}
}