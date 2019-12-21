package com.abuhrov;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Control {
    private static RatingCalculator ratingSystem;
    private static JSONObject db;

    static {
        ratingSystem = new RatingCalculator(0.06, 0.5);
        try {
            db = new JSONObject(Files.readString(Path.of("db.json")));
        } catch (Exception e) {
            try {
                Files.writeString(Path.of("db.json"), "{}");
                db = new JSONObject(Files.readString(Path.of("db.json")));
            } catch (Exception ex) {
                System.out.println("ex " + ex);
            }
        }
    }

    static void calculate(Rating player1, Rating player2, Rating player3, Rating player4) {
        RatingPeriodResults results = new RatingPeriodResults();

        results.addResult(player1, player3);
        results.addResult(player1, player4);
        results.addResult(player2, player3);
        results.addResult(player2, player4);

        ratingSystem.updateRatings(results);
    }

    static Rating getPlayer(String name) {
        if (db.has(name)) {
            try {
                JSONArray jsonArray = db.getJSONArray(name);
                return new Rating(name, (Double) jsonArray.get(0), (Double) jsonArray.get(1), (Double) jsonArray.get(2),
                        (Integer) jsonArray.get(3));
            } catch (JSONException ignored) {
            }
        }

        return new Rating(name, ratingSystem);
    }

    static String read() {
        try {
            return new JSONObject(Files.readString(Path.of("db.json"))).toString(4);
        } catch (Exception e) {
            return "";
        }
    }

    static Iterator<String> getPlayersIterator() {
        try {
            return new JSONObject(Files.readString(Path.of("db.json"))).keys();
        } catch (Exception e) {
            return Collections.emptyIterator();
        }
    }

    static void savePlayer(Rating player) {
        try {
            db.put(player.getUid(), player.toArray());
            Files.writeString(Path.of("db.json"), db.toString());
        } catch (Exception ignored) {
        }
    }
}
