package com.moviemanager.api;

import io.github.cdimascio.dotenv.Dotenv;

public class LogoAPI {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String API_KEY = dotenv.get("LOGO_DEV_API_KEY");
    private static final String API_URL = "https://img.logo.dev/";

    public String getLogoUrl(String domain) {
        return API_URL + domain + "?token=" + API_KEY;
    }
}
