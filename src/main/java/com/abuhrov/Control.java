package com.abuhrov;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Control {
    private static RatingCalculator ratingSystem;
    private static JSONObject db;
    private RatingPeriodResults results = new RatingPeriodResults();
    private Rating player1;
    private Rating player2;
    private Rating player3;
    private Rating player4;

    static {
        ratingSystem = new RatingCalculator(0.06, 0.5);
        try {
            db = new JSONObject(Files.readString(Path.of("db.json")));
        } catch (Exception e) {
            try {
                Files.writeString(Path.of("db.json"), "{}");
            } catch (IOException ex) {
                System.out.println("ex " + ex);
            }
        }
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

    private static Rating getPlayer(String name) throws JSONException {
        JSONArray jsonArray = db.getJSONArray(name);
        if (jsonArray != null) {
            return new Rating(name, (Double) jsonArray.get(0), (Double) jsonArray.get(1), (Double) jsonArray.get(2),
                    (Integer) jsonArray.get(3));
        }

        return new Rating(name, ratingSystem);
    }

    private static void init(String name1, String name2, String name3, String name4) throws IOException, JSONException {
        db = new JSONObject(Files.readString(Path.of("db.json")));
    }

    private void printResults(String text) throws IOException, JSONException {
        System.out.println(text + "...");
        System.out.println(player1);
        System.out.println(player2);
        System.out.println(player3);
        System.out.println(player4);


        JSONObject db = new JSONObject(Files.readString(Path.of("db.json")));

        System.out.println(db.toString());
    }

    static String read() {
        try {
            return new JSONObject(Files.readString(Path.of("db.json"))).toString(4);
        } catch (Exception e) {
            return "";
        }
    }

    static void newPlayer(String name) {
        try {
            Rating player = getPlayer(name);
            db.put(player.getUid(), player.toArray());
            Files.writeString(Path.of("db.json"), db.toString());
        } catch (Exception ignored) {
        }
    }
}
