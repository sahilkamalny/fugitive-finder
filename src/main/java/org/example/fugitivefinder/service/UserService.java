package org.example.fugitivefinder.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.fugitivefinder.model.AppUser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public final class UserService {
    private static final String BASE_URL = "https://fbi-backend-wilt.onrender.com/api/";

    public static AppUser createAccount(String firstName, String lastName,
                                        String username, String email, String password) {
        try {
            URL url = new URL(BASE_URL + "register/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInput = String.format("""
        {
            "firstName": "%s",
            "lastName": "%s",
            "username": "%s",
            "email": "%s",
            "password": "%s"
        }
        """, firstName, lastName, username, email, password);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonInput.getBytes());
                os.flush();
            }

            if (conn.getResponseCode() != 200 && conn.getResponseCode() != 201) {
                return null;
            }

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response.toString(), AppUser.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static AppUser login(String email, String password) {
        try {
            URL url = new URL(BASE_URL + "login/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInput = String.format("""
        {
            "email": "%s",
            "password": "%s"
        }
        """, email, password);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonInput.getBytes());
                os.flush();
            }

            if (conn.getResponseCode() != 200) {
                return null;
            }

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response.toString(), AppUser.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void saveTargetForUser(AppUser user, String wantedUid) {
        if (user == null || wantedUid == null || wantedUid.isBlank()) {
            return;
        }

        if (!user.getSavedTargetIds().contains(wantedUid)) {
            user.getSavedTargetIds().add(wantedUid);
        }
    }

    public static void removeTargetForUser(AppUser user, String wantedUid) {
        if (user == null || wantedUid == null) {
            return;
        }
        user.getSavedTargetIds().remove(wantedUid);
    }
}
