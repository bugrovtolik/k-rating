package com.abuhrov;

import org.cloudinary.json.JSONArray;
import org.cloudinary.json.JSONException;
import org.cloudinary.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Control {
    private static RatingCalculator ratingSystem = new RatingCalculator(0.06, 0.5);
    private static Database database = Database.getInstance();

    static void calculate(Rating player1, Rating player2, Rating player3, Rating player4) {
        RatingPeriodResults results = new RatingPeriodResults();

        results.addResult(player1, player3);
        results.addResult(player1, player4);
        results.addResult(player2, player3);
        results.addResult(player2, player4);

        ratingSystem.updateRatings(results);
    }

    static Rating getPlayer(String name) {
        JSONObject db = database.get();
        if (db.has(name)) {
            JSONArray jsonArray = db.getJSONArray(name);
            return new Rating(name, (Double) jsonArray.get(0), (Double) jsonArray.get(1), (Double) jsonArray.get(2),
                    (Integer) jsonArray.get(3));

        }

        return new Rating(name, ratingSystem);
    }

    static String getRatingList() {
        StringBuilder rating = new StringBuilder();
        JSONObject db = database.get();

        Iterator<String> keys = db.keys();
        if (keys.hasNext()) {
            Map<String, Double> map = new HashMap<>();

            while (keys.hasNext()) {
                String key = keys.next();
                map.put(key, (Double) ((JSONArray) db.get(key)).get(0));
            }

            map.entrySet().stream()
                    .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                    .forEach((e) -> rating.append(e.getKey()).append("  -  ")
                            .append(e.getValue()).append(System.getProperty("line.separator")));
        }

        return rating.toString();
    }

    static Iterator<String> getPlayersIterator() {
        return database.get().keys();
    }

    static void savePlayer(Rating player) {
        JSONObject db = database.get();
        db.put(player.getUid(), player.toArray());
        database.save(db);
    }
}
