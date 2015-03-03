// Copyright (C) 2015 by Casey English

package dominion;

import java.util.ArrayList;

public class Card {

    private String name;
    private ArrayList<String> types;
    private int cost;
    private ArrayList<String> attributes;
    private String set;

    protected Card(String name, ArrayList<String> types, int cost, String set) {
        this.name = name;
        this.types = new ArrayList<String>(types);
        this.cost = cost;
        this.attributes = null;
        this.set = set;
    }

    protected Card(String name, ArrayList<String> types, int cost, ArrayList<String> attributes, String set) {
        this.name = name;
        this.types = new ArrayList<String>(types);
        this.cost = cost;
        this.attributes = new ArrayList<String>(attributes);
        this.set = set;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getTypes() {
        return types;
    }

    public int getCost() {
        return cost;
    }

    public ArrayList<String> getAttributes() {
        return attributes;
    }

    public String getSet() {
        return set;
    }
}
