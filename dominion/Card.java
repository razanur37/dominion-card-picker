// Copyright (C) 2015 by Casey English

package dominion;

import java.util.ArrayList;

public class Card {

    private String name;
    private ArrayList<String> types;
    private int cost;
    private ArrayList<String> attributes;

    protected Card(String name, ArrayList<String> types, int cost) {
        this.name = name;
        this.types = new ArrayList<String>(types);
        this.cost = cost;
        this.attributes = null;
    }

    protected Card(String name, ArrayList<String> types, int cost, ArrayList<String> attributes) {
        this.name = name;
        this.types = new ArrayList<String>(types);
        this.cost = cost;
        this.attributes = new ArrayList<String>(attributes);
    }

    protected String getName() {
        return name;
    }

    protected ArrayList<String> getTypes() {
        return types;
    }

    protected int getCost() {
        return cost;
    }

    protected ArrayList<String> getAttributes() {
        return attributes;
    }
}
