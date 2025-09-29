package com.moviemanager.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.moviemanager.model.StreamingInfo;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WatchmodeAPI {
        private static final Dotenv dotenv = Dotenv.load();
    private static final String API_KEY = dotenv.get("WATCHMODE_API_KEY");
    private static final String BASE_URL = "https://api.watchmode.com/v1/";

    public List<StreamingInfo> getStreamingInfo(String title, String countryCode) {
        try {
            // First, search for the title to get the watchmode ID
            URL searchUrl = new URL(BASE_URL + "search/?search_field=name&search_value=" + URLEncoder.encode(title, "UTF-8") + "&countries=" + countryCode);
            HttpURLConnection searchConn = (HttpURLConnection) searchUrl.openConnection();
            searchConn.setRequestMethod("GET");
            searchConn.setRequestProperty("Authorization", "Bearer " + API_KEY);

            BufferedReader searchIn = new BufferedReader(new InputStreamReader(searchConn.getInputStream()));
            String searchInputLine;
            StringBuilder searchContent = new StringBuilder();
            while ((searchInputLine = searchIn.readLine()) != null) {
                searchContent.append(searchInputLine);
            }
            searchIn.close();
            searchConn.disconnect();

            Gson gson = new Gson();
            JsonObject searchResult = gson.fromJson(searchContent.toString(), JsonObject.class);
            JsonArray titleResults = searchResult.getAsJsonArray("title_results");

            if (titleResults.size() > 0) {
                int watchmodeId = titleResults.get(0).getAsJsonObject().get("id").getAsInt();

                // Now get the title details, including streaming sources
                URL detailsUrl = new URL(BASE_URL + "title/" + watchmodeId + "/sources/?countries=" + countryCode);
                HttpURLConnection detailsConn = (HttpURLConnection) detailsUrl.openConnection();
                detailsConn.setRequestMethod("GET");
                detailsConn.setRequestProperty("Authorization", "Bearer " + API_KEY);

                BufferedReader detailsIn = new BufferedReader(new InputStreamReader(detailsConn.getInputStream()));
                String detailsInputLine;
                StringBuilder detailsContent = new StringBuilder();
                while ((detailsInputLine = detailsIn.readLine()) != null) {
                    detailsContent.append(detailsInputLine);
                }
                detailsIn.close();
                detailsConn.disconnect();

                JsonArray sources = gson.fromJson(detailsContent.toString(), JsonArray.class);
                Map<String, StreamingInfo> streamingInfoMap = new HashMap<>();

                for (int i = 0; i < sources.size(); i++) {
                    JsonObject source = sources.get(i).getAsJsonObject();
                    String name = source.get("name").getAsString();
                    String url = source.get("web_url").getAsString();
                    String type = source.get("type").getAsString();
                    streamingInfoMap.putIfAbsent(name, new StreamingInfo(name, url, type));
                }

                return new ArrayList<>(streamingInfoMap.values());
            }

        } catch (Exception e) {
            System.err.println("Error getting streaming info: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
