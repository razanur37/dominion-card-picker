// Copyright (C) 2015 by Casey English

import dominion.Dominion;

public class Main {
    public static void main(String[] args) {
        Dominion dominion = new Dominion("CORNUCOPIA, DARK AGES, BASE, SEASIDE");

        dominion.setup();

        for(String card : dominion.getGameCardNames()) {
            System.out.println(card);
        }
    }
}
