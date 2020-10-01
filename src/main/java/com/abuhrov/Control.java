package com.abuhrov;

import org.cloudinary.json.JSONArray;
import org.cloudinary.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Control {
    private static final RatingCalculator ratingSystem = new RatingCalculator(0.06, 0.5);
    private static final Database database = Database.getInstance();

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
            return new Rating(name, Double.parseDouble(jsonArray.get(0).toString()),
                    Double.parseDouble(jsonArray.get(1).toString()), Double.parseDouble(jsonArray.get(2).toString()),
                    (Integer) jsonArray.get(3));

        }

        return new Rating(name, ratingSystem);
    }

    static String getRatingList() {
        StringBuilder rating = new StringBuilder();
        JSONObject db = database.get();

        Iterator<String> keys = db.keys();
        if (keys.hasNext()) {
            Map<String, Integer> map = new HashMap<>();

            while (keys.hasNext()) {
                String key = keys.next();
                map.put(key + " (" + (((int) ((JSONArray) db.get(key)).get(3)) / 2) + ")",
                        Double.valueOf(((JSONArray) db.get(key)).get(0).toString()).intValue());
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
        JSONArray data = new JSONArray();

        data.put(player.getRating());
        data.put(player.getRatingDeviation());
        data.put(player.getVolatility());
        data.put(player.getNumberOfResults());
        db.put(player.getUid(), data);

        database.save(db);
    }

    public static void clearResults() {
        JSONObject db = new JSONObject();

        database.save(db);
    }
}
