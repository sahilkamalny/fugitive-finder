package org.example.fugitivefinder.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreService {

    private static final String PROJECT_ID = "fugitivefinderproject";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void createUser(String uid, String email, String username, String firstName, String lastName) {
        try {
            String urlStr = "https://firestore.googleapis.com/v1/projects/"
                    + PROJECT_ID + "/databases/(default)/documents/users/" + uid;

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + FirebaseAuthService.getIdToken());
            conn.setDoOutput(true);

            Map<String, Object> fields = new HashMap<>();

            fields.put("email", Map.of("stringValue", email));
            fields.put("username", Map.of("stringValue", username));
            fields.put("firstName", Map.of("stringValue", firstName));
            fields.put("lastName", Map.of("stringValue", lastName));

            Map<String, Object> savedTargetsField = new HashMap<>();
            savedTargetsField.put("arrayValue", new HashMap<>());

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
                System.out.println("User FULL profile stored in Firestore ✅");
            } else {
                System.out.println("Firestore FAILED ❌");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveTarget(String uid, String targetId) {
        try {
            String urlStr = "https://firestore.googleapis.com/v1/projects/"
                    + PROJECT_ID + "/databases/(default)/documents/users/" + uid
                    + "?updateMask.fieldPaths=savedTargets";

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + FirebaseAuthService.getIdToken());

            conn.setDoOutput(true);

            Map<String, Object> value = new HashMap<>();
            value.put("stringValue", targetId);

            Map<String, Object> valuesArray = new HashMap<>();
            valuesArray.put("values", new Object[]{value});

            Map<String, Object> arrayValue = new HashMap<>();
            arrayValue.put("arrayValue", valuesArray);

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

    public static List<String> getSavedTargets(String uid) {
        List<String> result = new ArrayList<>();

        try {
            String urlStr = "https://firestore.googleapis.com/v1/projects/"
                    + PROJECT_ID + "/databases/(default)/documents/users/" + uid;

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + FirebaseAuthService.getIdToken());

            if (conn.getResponseCode() == 200) {
                JsonNode root = mapper.readTree(conn.getInputStream());
                JsonNode values = root.path("fields").path("savedTargets").path("arrayValue").path("values");

                if (values.isArray()) {
                    for (JsonNode v : values) {
                        result.add(v.get("stringValue").asText());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static Map<String, String> getUserData(String uid) {
        try {
            String urlStr = "https://firestore.googleapis.com/v1/projects/"
                    + PROJECT_ID + "/databases/(default)/documents/users/" + uid;

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + FirebaseAuthService.getIdToken());

            if (conn.getResponseCode() == 200) {
                Map response = mapper.readValue(conn.getInputStream(), Map.class);
                Map fields = (Map) response.get("fields");

                Map<String, String> userData = new HashMap<>();

                userData.put("email", ((Map) fields.get("email")).get("stringValue").toString());
                userData.put("username", ((Map) fields.get("username")).get("stringValue").toString());
                userData.put("firstName", ((Map) fields.get("firstName")).get("stringValue").toString());
                userData.put("lastName", ((Map) fields.get("lastName")).get("stringValue").toString());

                return userData;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}