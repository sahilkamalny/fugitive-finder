package org.example.fugitivefinder.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WantedResponse {

    private List<WantedPerson> items;

    public List<WantedPerson> getItems() {
        return items;
    }

    public void setItems(List<WantedPerson> items) {
        this.items = items;
    }
}