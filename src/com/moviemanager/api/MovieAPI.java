package com.moviemanager.api;

import com.google.gson.Gson;
import com.moviemanager.model.Movie;
import com.moviemanager.api.ApiResponse;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MovieAPI {
        private static final Dotenv dotenv = Dotenv.load();
    private static final String API_KEY = dotenv.get("OMDB_API_KEY");
    private static final String API_URL = "http://www.omdbapi.com/?apikey=" + API_KEY;

    public Movie searchMovie(String title) {
        try {
            URL url = new URL(API_URL + "&t=" + title.replace(" ", "+"));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();
            conn.disconnect();

            Gson gson = new Gson();
            ApiResponse apiResponse = gson.fromJson(content.toString(), ApiResponse.class);

            if (apiResponse.getResponse().equals("True")) {
                return gson.fromJson(content.toString(), Movie.class);
            } else {
                return null;
            }

        } catch (Exception e) {
            System.err.println("Error searching for movie: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
