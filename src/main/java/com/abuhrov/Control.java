package com.abuhrov;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Control {
    private static RatingCalculator ratingSystem = new RatingCalculator(0.06, 0.5);

    static void calculate(Rating player1, Rating player2, Rating player3, Rating player4) {
        RatingPeriodResults results = new RatingPeriodResults();

        results.addResult(player1, player3);
        results.addResult(player1, player4);
        results.addResult(player2, player3);
        results.addResult(player2, player4);

        ratingSystem.updateRatings(results);
    }

    static Rating getPlayer(String name) {
        JSONObject db = getDB();
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

//    static String read() {
//        StringBuilder rating = new StringBuilder();
//        JSONObject db = getDB();
//        Iterator<String> keys = db.keys();
//        Map<String, Double> map = new TreeMap<>();
//
//        while (keys.hasNext()) {
//            String playerName = keys.next();
//            try {
//                JSONArray stats = (JSONArray) db.get(playerName);
//                rating.append(playerName).append(" - ").stats.getDouble(0);
//
//                sorted =
//                        map.entrySet().stream()
//                                .sorted(Map.Entry.comparingByValue());
//            } catch (JSONException ignored) {
//            }
//        }
//    }

    static Iterator<String> getPlayersIterator() {
        return getDB().keys();
    }

    static void savePlayer(Rating player) {
        JSONObject db = getDB();
        try {
            db.put(player.getUid(), player.toArray());
            Files.writeString(Path.of("db.json"), db.toString());
        } catch (Exception ignored) {
        }
    }

    static JSONObject getDB() {
        JSONObject db;
        try {
             db = new JSONObject(Files.readString(Path.of("db.json")));
        } catch (Exception e) {
            db = new JSONObject();
            try {
                Files.writeString(Path.of("db.json"), "{}");
            } catch (IOException ignored) {
            }
        }

        return db;
    }
}
