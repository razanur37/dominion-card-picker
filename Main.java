// Copyright (C) 2015 by Casey English

import dominion.Dominion;

public class Main {
    public static void main(String[] args) {
        Dominion dominion = new Dominion("GUILDS, BLACK MARKET");

        dominion.setup();

        for(String card : dominion.getGameCardNames()) {
            System.out.println(card);
        }
    }
}
