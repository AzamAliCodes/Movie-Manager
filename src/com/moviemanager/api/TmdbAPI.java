package com.moviemanager.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.moviemanager.model.TmdbShow;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TmdbAPI {
        private static final Dotenv dotenv = Dotenv.load();
    private static final String API_KEY = dotenv.get("TMDB_API_KEY");
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500";

    public List<TmdbShow> getTrendingShows(String timeWindow) {
        List<TmdbShow> trendingShows = new ArrayList<>();
        try {
            URL url = new URL(BASE_URL + "trending/all/" + timeWindow + "?api_key=" + API_KEY);
            System.out.println("TMDb API URL: " + url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            System.out.println("TMDb API Response Code: " + responseCode);

            BufferedReader in;
            if (responseCode >= 200 && responseCode < 300) {
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            conn.disconnect();

            System.out.println("TMDb API Response: " + content.toString());

            Gson gson = new Gson();
            JsonObject jsonResponse = gson.fromJson(content.toString(), JsonObject.class);
            JsonArray results = jsonResponse.getAsJsonArray("results");

            for (int i = 0; i < results.size(); i++) {
                JsonObject showJson = results.get(i).getAsJsonObject();
                TmdbShow show = gson.fromJson(showJson, TmdbShow.class);
                trendingShows.add(show);
            }

        } catch (Exception e) {
            System.err.println("Error getting trending shows: " + e.getMessage());
            e.printStackTrace();
        }
        return trendingShows;
    }

    public String getFullPosterPath(String posterPath) {
        if (posterPath != null && !posterPath.isEmpty()) {
            return IMAGE_BASE_URL + posterPath;
        }
        return null;
    }
}
