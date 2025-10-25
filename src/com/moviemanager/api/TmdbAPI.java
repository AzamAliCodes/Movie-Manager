package com.moviemanager.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
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

    public String getFullPosterPath(String posterPath) {
        if (posterPath != null && !posterPath.isEmpty()) {
            return IMAGE_BASE_URL + posterPath;
        }
        return null;
    }

    public List<com.moviemanager.model.Movie> getDailyTrendingMovies() {
        int retries = 3;
        while (retries > 0) {
            try {
                List<com.moviemanager.model.Movie> movies = new ArrayList<>();
                URL url = new URL(BASE_URL + "trending/movie/day?api_key=" + API_KEY);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                if (conn.getResponseCode() != 200) {
                    System.err.println("Failed : HTTP error code : " + conn.getResponseCode());
                    return movies;
                }

                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                StringBuilder sb = new StringBuilder();
                String output;
                while ((output = br.readLine()) != null) {
                    sb.append(output);
                }

                conn.disconnect();

                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(sb.toString(), JsonObject.class);
                JsonArray results = jsonObject.getAsJsonArray("results");

                for (int i = 0; i < results.size(); i++) {
                    JsonObject movieJson = results.get(i).getAsJsonObject();
                    String title = movieJson.get("title").getAsString();
                    String year = movieJson.has("release_date") && !movieJson.get("release_date").isJsonNull() ? movieJson.get("release_date").getAsString().substring(0, 4) : "";
                    String plot = movieJson.has("overview") && !movieJson.get("overview").isJsonNull() ? movieJson.get("overview").getAsString() : "";
                    String posterPath = movieJson.has("poster_path") && !movieJson.get("poster_path").isJsonNull() ? movieJson.get("poster_path").getAsString() : "";
                    String posterUrl = getFullPosterPath(posterPath);
                    double rating = movieJson.has("vote_average") && !movieJson.get("vote_average").isJsonNull() ? movieJson.get("vote_average").getAsDouble() : 0.0;

                    movies.add(new com.moviemanager.model.Movie(title, year, "", "", "", plot, posterUrl, rating, ""));
                }
                return movies.subList(0, Math.min(5, movies.size()));
            } catch (java.net.SocketException e) {
                retries--;
                if (retries == 0) {
                    e.printStackTrace();
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    public List<com.moviemanager.model.Movie> getWeeklyTrendingMovies() {
        int retries = 3;
        while (retries > 0) {
            try {
                List<com.moviemanager.model.Movie> movies = new ArrayList<>();
                URL url = new URL(BASE_URL + "trending/movie/week?api_key=" + API_KEY);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                if (conn.getResponseCode() != 200) {
                    System.err.println("Failed : HTTP error code : " + conn.getResponseCode());
                    return movies;
                }

                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                StringBuilder sb = new StringBuilder();
                String output;
                while ((output = br.readLine()) != null) {
                    sb.append(output);
                }

                conn.disconnect();

                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(sb.toString(), JsonObject.class);
                JsonArray results = jsonObject.getAsJsonArray("results");

                for (int i = 0; i < results.size(); i++) {
                    JsonObject movieJson = results.get(i).getAsJsonObject();
                    String title = movieJson.get("title").getAsString();
                    String year = movieJson.has("release_date") && !movieJson.get("release_date").isJsonNull() ? movieJson.get("release_date").getAsString().substring(0, 4) : "";
                    String plot = movieJson.has("overview") && !movieJson.get("overview").isJsonNull() ? movieJson.get("overview").getAsString() : "";
                    String posterPath = movieJson.has("poster_path") && !movieJson.get("poster_path").isJsonNull() ? movieJson.get("poster_path").getAsString() : "";
                    String posterUrl = getFullPosterPath(posterPath);
                    double rating = movieJson.has("vote_average") && !movieJson.get("vote_average").isJsonNull() ? movieJson.get("vote_average").getAsDouble() : 0.0;

                    movies.add(new com.moviemanager.model.Movie(title, year, "", "", "", plot, posterUrl, rating, ""));
                }
                return movies.subList(0, Math.min(5, movies.size()));
            } catch (java.net.SocketException e) {
                retries--;
                if (retries == 0) {
                    e.printStackTrace();
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }
}
