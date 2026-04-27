package org.example.fugitivefinder.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class FirestoreService {

    private static final String PROJECT_ID = "fugitivefinderproject";
    private static final ObjectMapper mapper = new ObjectMapper();

    // =========================
    // CREATE USER DOCUMENT
    // =========================
    public static void createUser(String uid, String email) {
        try {
            String urlStr = "https://firestore.googleapis.com/v1/projects/"
                    + PROJECT_ID + "/databases/(default)/documents/users/" + uid;

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            conn.setRequestProperty("Content-Type", "application/json");

            // 🔥 AUTH TOKEN (THIS FIXES YOUR 403)
            conn.setRequestProperty("Authorization", "Bearer " + FirebaseAuthService.getIdToken());

            conn.setDoOutput(true);

            Map<String, Object> fields = new HashMap<>();

            Map<String, Object> emailField = new HashMap<>();
            emailField.put("stringValue", email);

            Map<String, Object> savedTargetsField = new HashMap<>();
            savedTargetsField.put("arrayValue", new HashMap<>());

            fields.put("email", emailField);
            fields.put("savedTargets", savedTargetsField);

            Map<String, Object> body = new HashMap<>();
            body.put("fields", fields);

            String json = mapper.writeValueAsString(body);

            OutputStream os = conn.getOutputStream();
            os.write(json.getBytes());
            os.close();

            int responseCode = conn.getResponseCode();
            System.out.println("Firestore Response Code: " + responseCode);

            if (responseCode == 200) {
                System.out.println("User created in Firestore ✅");
            } else {
                System.out.println("Firestore FAILED ❌");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // SAVE TARGET
    // =========================
    public static void saveTarget(String uid, String targetId) {
        try {
            String urlStr = "https://firestore.googleapis.com/v1/projects/"
                    + PROJECT_ID + "/databases/(default)/documents/users/" + uid;

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            conn.setRequestProperty("Content-Type", "application/json");

            // 🔥 AUTH TOKEN AGAIN
            conn.setRequestProperty("Authorization", "Bearer " + FirebaseAuthService.getIdToken());

            conn.setDoOutput(true);

            Map<String, Object> value = new HashMap<>();
            value.put("stringValue", targetId);

            Map<String, Object> array = new HashMap<>();
            array.put("values", new Object[]{value});

            Map<String, Object> arrayValue = new HashMap<>();
            arrayValue.put("arrayValue", array);

            Map<String, Object> fields = new HashMap<>();
            fields.put("savedTargets", arrayValue);

            Map<String, Object> body = new HashMap<>();
            body.put("fields", fields);

            String json = mapper.writeValueAsString(body);

            OutputStream os = conn.getOutputStream();
            os.write(json.getBytes());
            os.close();

            int responseCode = conn.getResponseCode();
            System.out.println("Save Target Response: " + responseCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}