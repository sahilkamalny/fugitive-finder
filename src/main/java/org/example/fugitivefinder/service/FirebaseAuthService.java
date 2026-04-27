package org.example.fugitivefinder.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.fugitivefinder.model.FirebaseUser;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class FirebaseAuthService {

    private static final String API_KEY = "AIzaSyCGpapAxj5BVSQmFczkCPlIFSwMSyMB0oI";
    private static final ObjectMapper mapper = new ObjectMapper();

    private static String idToken;
    private static String localId;

    public static String getIdToken() {
        return idToken;
    }

    public static String getLocalId() {
        return localId;
    }

    // REGISTER
    public static FirebaseUser register(String email, String password) {
        return authenticate(email, password, true);
    }

    // LOGIN
    public static FirebaseUser login(String email, String password) {
        return authenticate(email, password, false);
    }

    // CORE AUTH METHOD
    private static FirebaseUser authenticate(String email, String password, boolean isRegister) {
        try {
            String endpoint = isRegister
                    ? "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + API_KEY
                    : "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + API_KEY;

            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            Map<String, Object> body = new HashMap<>();
            body.put("email", email);
            body.put("password", password);
            body.put("returnSecureToken", true);

            String json = mapper.writeValueAsString(body);

            OutputStream os = conn.getOutputStream();
            os.write(json.getBytes());
            os.close();

            if (conn.getResponseCode() == 200) {

                // 🔥 READ RESPONSE JSON
                JsonNode response = mapper.readTree(conn.getInputStream());

                // 🔥 STORE TOKENS (CRITICAL)
                idToken = response.get("idToken").asText();
                localId = response.get("localId").asText();

                System.out.println("Firebase Auth Success: " + response.get("email").asText());
                System.out.println("ID TOKEN: " + idToken);
                System.out.println("UID: " + localId);

                // Convert to your model
                FirebaseUser user = new FirebaseUser();
                user.setEmail(response.get("email").asText());
                user.setLocalId(localId);

                return user;

            } else {
                System.out.println("Firebase Auth Failed: " + conn.getResponseCode());
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}