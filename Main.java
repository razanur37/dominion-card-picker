// Copyright (C) 2015 by Casey English

import dominion.Dominion;
import dominion.Card;

public class Main {
    public static void main(String[] args) {
        Dominion dominion = new Dominion("GUILDS, BASE, INTRIGUE, CORNUCOPIA, DARK AGES, BLACK MARKET");

        dominion.setup();

        System.out.print("NAME");
        String spaces = "";
        for (int i=0; i<20; ++i) {
            spaces = spaces + " ";
        }
        System.out.print(spaces);
        System.out.print("SET");

        spaces = "";
        for (int i=0; i<13; ++i) {
            spaces = spaces + " ";
        }

        System.out.print(spaces);
        System.out.print("TYPES");

        spaces = "";
        for (int i=0; i<25; ++i) {
            spaces = spaces + " ";
        }

        System.out.print(spaces);
        System.out.print("COST");

        spaces = "";
        for (int i=0; i<12; ++i) {
            spaces = spaces + " ";
        }

        System.out.print(spaces);
        System.out.println("Attributes");

        spaces = "";
        for (int i=0; i<100; ++i) {
            spaces = spaces + "-";
        }

        System.out.println(spaces);

        for(Card card : dominion.getCards()) {
            System.out.print(card.getName());
            spaces = "";
            for (int i=0; i<24-card.getName().length(); ++i) {
                spaces = spaces + " ";
            }
            System.out.print(spaces);
            System.out.print(card.getSet());

            spaces = "";
            for (int i=0; i<16-card.getSet().length(); ++i) {
                spaces = spaces + " ";
            }

            System.out.print(spaces);
            System.out.print(card.getTypes().toString());

            spaces = "";
            for (int i=0; i<30-card.getTypes().toString().length(); ++i) {
                spaces = spaces + " ";
            }

            System.out.print(spaces);
            System.out.print(card.getCost());

            spaces = "";
            for (int i=0; i<16-Integer.toString(card.getCost()).length(); ++i) {
                spaces = spaces + " ";
            }

            System.out.print(spaces);
            System.out.println(card.getAttributes().toString());
        }
    }
}
