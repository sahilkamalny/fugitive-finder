package org.example.fugitivefinder.service;


import org.example.fugitivefinder.model.WantedPerson;
import org.example.fugitivefinder.model.WantedResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;

public final class FbiApiService {

    private static final String BASE_URL = "https://fbi-backend-wilt.onrender.com/api/wanted/?pageSize=500";

    private FbiApiService() {
    }

    public static List<WantedPerson> getWantedPeople() {
        try {
            URL url = new URL(BASE_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            System.out.println("Calling API: " + BASE_URL);
            System.out.println("Response Code: " + connection.getResponseCode());

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return Collections.emptyList();
            }

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream())
            );

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            System.out.println("Response: " + response.toString());
            ObjectMapper mapper = new ObjectMapper();
            WantedResponse wantedResponse = mapper.readValue(response.toString(), WantedResponse.class);

            if (wantedResponse != null && wantedResponse.getItems() != null) {
                System.out.println("Items count: " + wantedResponse.getItems().size());
            } else {
                System.out.println("WantedResponse or items is NULL");
            }
            return wantedResponse != null && wantedResponse.getItems() != null
                    ? wantedResponse.getItems()
                    : Collections.emptyList();

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }

    }
}