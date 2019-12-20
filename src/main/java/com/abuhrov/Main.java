package com.abuhrov;

import com.pengrad.telegrambot.request.SetWebhook;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static spark.Spark.*;

public class Main {
	private JSONObject db;
	private RatingCalculator ratingSystem = new RatingCalculator(0.06, 0.5);
	private RatingPeriodResults results = new RatingPeriodResults();
	private Rating player1;
	private Rating player2;
	private Rating player3;
	private Rating player4;

	public static void main(String[] args) {

		final String portNumber = System.getenv("PORT");
		if (portNumber != null) {
			port(Integer.parseInt(portNumber));
		}

		// current app url to set webhook
		// should be set via heroku config vars
		// https://devcenter.heroku.com/articles/config-vars
		// heroku config:set APP_URL=https://app-for-my-bot.herokuapp.com
		final String appUrl = System.getenv("APP_URL");

		// define list of bots
		BotHandler[] bots = new BotHandler[]{new TestTelegramBot()};

		// set bot to listen https://my-app.heroku.com/BOTTOKEN
		// register this URL as Telegram Webhook
		for (BotHandler bot : bots) {
			String token = bot.getToken();
			post("/" + token, bot);
			if (appUrl != null) {
				bot.getBot().execute(new SetWebhook().url(appUrl + "/" + token));
			}
		}

		// can declare other routes
		get("/", (req, res) -> "index page");
		get("/hello", (req, res) -> "Hello World");
		post("/test", new Test());
		get("/test", new Test());
	}

	/**
	 * This test uses the values from the example towards the end of Glickman's paper as a simple test of the
	 * calculation engine
	 * In addition, we have another player who doesn't compete, in order to test that their deviation will have
	 * increased over time.
	 */
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

	private static class Test implements Route {
		@Override
		public Object handle(Request request, Response response) {
			return "ok, test";
		}
	}
}
