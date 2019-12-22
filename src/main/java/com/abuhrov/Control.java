package com.abuhrov;

import org.cloudinary.json.JSONArray;
import org.cloudinary.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

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
        return database.get().keys();
    }

    static void savePlayer(Rating player) {
        JSONObject db = database.get();
        db.put(player.getUid(), player.toArray());
        try {
            Files.writeString(Path.of("../db.json"), db.toString());//no more files :)
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
