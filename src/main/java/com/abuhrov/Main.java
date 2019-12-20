package com.abuhrov;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
	private JSONObject db;
	private RatingCalculator ratingSystem = new RatingCalculator(0.06, 0.5);
	private RatingPeriodResults results = new RatingPeriodResults();
	private Rating player1;
	private Rating player2;
	private Rating player3;
	private Rating player4;

	public static void main(String[] args) throws TelegramApiRequestException {
		KickerRatingBot bot = new KickerRatingBot();
		ApiContextInitializer.init();
		TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
		telegramBotsApi.registerBot(bot);
	}

	public void test() throws IOException, JSONException {
		init("player1", "player2", "player3", "player4");
		printResults("Before");

		results.addResult(player1, player3);
		results.addResult(player1, player4);
		results.addResult(player2, player3);
		results.addResult(player2, player4);

		ratingSystem.updateRatings(results);

		printResults("After");
	}

	private Rating getPlayer(String name) throws JSONException {
		JSONArray jsonArray = db.getJSONArray(name);
		if (jsonArray != null) {
			return new Rating(name, (Double) jsonArray.get(0), (Double) jsonArray.get(1), (Double) jsonArray.get(2),
					(Integer) jsonArray.get(3));
		}

		return new Rating(name, ratingSystem);
	}

	private void init(String name1, String name2, String name3, String name4) throws IOException, JSONException {
		db = new JSONObject(Files.readString(Path.of("db.json")));

		player1 = getPlayer(name1);
		player2 = getPlayer(name2);
		player3 = getPlayer(name3);
		player4 = getPlayer(name4);
	}

	private void printResults(String text) throws IOException, JSONException {
		System.out.println(text + "...");
		System.out.println(player1);
		System.out.println(player2);
		System.out.println(player3);
		System.out.println(player4);


		db = new JSONObject(Files.readString(Path.of("db.json")));

		System.out.println(db.toString());
	}

	void read() throws IOException, JSONException {
		JSONObject db = new JSONObject(Files.readString(Path.of("db.json")));
	}

	void save() throws JSONException, IOException {
		JSONObject db = new JSONObject();
		db.put(player1.getUid(), player1.toArray());
		db.put(player2.getUid(), player2.toArray());
		db.put(player3.getUid(), player3.toArray());
		db.put(player4.getUid(), player4.toArray());
		Files.writeString(Path.of("db.json"), db.toString(4));//TODO remove indent
	}
}