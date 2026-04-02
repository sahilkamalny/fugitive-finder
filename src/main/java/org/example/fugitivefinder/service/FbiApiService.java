package org.example.fugitivefinder.service;

import org.example.fugitivefinder.model.WantedPerson;
import org.example.fugitivefinder.model.WantedResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class FbiApiService {

    private static final String BASE_URL = "https://api.fbi.gov/wanted/v1/list";
    private final HttpClient client = HttpClient.newHttpClient();

    public WantedResponse fetchWantedPersons(int page, int pageSize) {
        try {
            String url = BASE_URL + "?page=" + page + "&pageSize=" + pageSize;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Status: " + response.statusCode());
            System.out.println("Raw JSON: " + response.body());

            return parseResponse(response.body());

        } catch (Exception e) {
            System.err.println("API call failed: " + e.getMessage());
            return null;
        }
    }

    private WantedResponse parseResponse(String json) {
        JSONObject root = new JSONObject(json);
        int total = root.optInt("total", 0);
        JSONArray items = root.optJSONArray("items");

        List<WantedPerson> persons = new ArrayList<>();

        if (items != null) {
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);

                // Parse subjects array
                List<String> subjects = new ArrayList<>();
                JSONArray subjectsArr = item.optJSONArray("subjects");
                if (subjectsArr != null)
                    for (int j = 0; j < subjectsArr.length(); j++)
                        subjects.add(subjectsArr.getString(j));

                // Parse field_offices array
                List<String> fieldOffices = new ArrayList<>();
                JSONArray officesArr = item.optJSONArray("field_offices");
                if (officesArr != null)
                    for (int j = 0; j < officesArr.length(); j++)
                        fieldOffices.add(officesArr.getString(j));

                // Parse images array
                List<String> images = new ArrayList<>();
                JSONArray imagesArr = item.optJSONArray("images");
                if (imagesArr != null)
                    for (int j = 0; j < imagesArr.length(); j++) {
                        JSONObject imgObj = imagesArr.getJSONObject(j);
                        images.add(imgObj.optString("original", ""));
                    }

                WantedPerson person = new WantedPerson(
                        item.optString("uid", ""),
                        item.optString("title", ""),
                        item.optString("description", ""),
                        item.optString("status", ""),
                        item.optString("sex", ""),
                        item.optString("race", ""),
                        item.optString("nationality", ""),
                        item.optString("hair", ""),
                        item.optString("eyes", ""),
                        item.optString("reward_text", ""),
                        item.optDouble("reward_min", 0.0),
                        item.optDouble("reward_max", 0.0),
                        subjects,
                        fieldOffices,
                        images,
                        item.optString("publication", "")
                );

                persons.add(person);
            }
        }

        return new WantedResponse(total, persons);
    }
}