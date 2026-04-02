package org.example.fugitivefinder.model;

import java.util.List;

public class WantedResponse {
    private int total;
    private List<WantedPerson> items;

    public WantedResponse(int total, List<WantedPerson> items) {
        this.total = total;
        this.items = items;
    }

    public int getTotal() { return total; }
    public List<WantedPerson> getItems() { return items; }
}