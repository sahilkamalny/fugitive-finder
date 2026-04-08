package org.example.fugitivefinder.model;

import java.util.List;

public class WantedResponse {

    private List<WantedPerson> items;

    public List<WantedPerson> getItems() {
        return items;
    }

    public void setItems(List<WantedPerson> items) {
        this.items = items;
    }
}