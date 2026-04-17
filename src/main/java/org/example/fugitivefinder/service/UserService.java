package org.example.fugitivefinder.service;

import org.example.fugitivefinder.model.AppUser;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public final class UserService {

    public static boolean createAccount(String firstName, String lastName,
                                        String username, String email, String password) {
        try {
            URL url = new URL("http://127.0.0.1:8000/api/register/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInput = String.format(
                    "{\"firstName\":\"%s\",\"lastName\":\"%s\",\"username\":\"%s\",\"email\":\"%s\",\"password\":\"%s\"}",
                    firstName, lastName, username, email, password
            );

            OutputStream os = conn.getOutputStream();
            os.write(jsonInput.getBytes());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();

            return responseCode == 200;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean login(String email, String password) {
        try {
            URL url = new URL("http://127.0.0.1:8000/api/login/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInput = String.format(
                    "{\"email\":\"%s\",\"password\":\"%s\"}",
                    email, password
            );

            OutputStream os = conn.getOutputStream();
            os.write(jsonInput.getBytes());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();

            return responseCode == 200;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
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
