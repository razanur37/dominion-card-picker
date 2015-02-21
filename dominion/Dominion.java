// Copyright (C) 2015 by Casey English

package dominion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

public class Dominion {

    private static final ArrayList<String> ALL_SETS = new ArrayList<String>(Arrays.asList("BASE", "INTRIGUE", "SEASIDE", "ALCHEMY", "PROSPERITY", "CORNUCOPIA", "HINTERLANDS", "DARK AGES", "GUILDS"));
    private ArrayList<String> sets;
    private ArrayList<Card> cardPool;
    private Game game;

    public Dominion(ArrayList<String> sets) {
        this.sets = new ArrayList<String>();

        try {
            for (String singleChosenSet: sets) {
                if (!ALL_SETS.contains(singleChosenSet.toUpperCase())) {
                    throw new IllegalArgumentException("Set " + singleChosenSet + "is not a valid Dominion set");
                }

                this.sets.add(singleChosenSet);
            }
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            System.exit(1);
        }

        cardPool = generateCardPool();
    }

    public Dominion(String[] sets) {
        this.sets = new ArrayList<String>();

        try {
            for (String singleChosenSet: sets) {
                if (!ALL_SETS.contains(singleChosenSet.toUpperCase())) {
                    throw new IllegalArgumentException("Set " + singleChosenSet + "is not a valid Dominion set");
                }

                this.sets.add(singleChosenSet);
            }
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            System.exit(1);
        }

        cardPool = generateCardPool();
    }

    public Dominion(String set) {
        sets = new ArrayList<String>();

        try {
            if (!ALL_SETS.contains(set.toUpperCase())) {
                throw new IllegalArgumentException("Set " + set + "is not a valid Dominion set");
            }

            sets.add(set);
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            System.exit(1);
        }

        cardPool = generateCardPool();
    }

    public void setup() {
        game = new Game(cardPool);

        game.pickCards();
    }

    private ArrayList<Card> generateCardPool() {
        ArrayList<Card> cards = new ArrayList<Card>();

        Connection c;
        Statement stmt;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:cards.db");
            c.setAutoCommit(false);

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("select * from BASE;");
            while (rs.next()) {
                String name = rs.getString("name");
                ArrayList<String> types = new ArrayList<String>(Arrays.asList(rs.getString("types").split(", ")));
                int cost = rs.getInt("cost");
                String attrs = rs.getString("attributes");
                ArrayList<String> attributes = null;
                if (attrs != null) {
                    attributes = new ArrayList<String>(Arrays.asList(rs.getString("attributes").split(", ")));
                }

                Card card;

                if (attributes != null) {
                    card = new Card(name, types, cost, attributes);
                } else {
                    card = new Card(name, types, cost);
                }

                cards.add(card);
            }

            rs.close();
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(1);
        }

        return cards;
    }

    public ArrayList<String> getGameCardNames() {
        ArrayList<String> cardNames = new ArrayList<String>();

        for(Card card : game.getCards()) {
            cardNames.add(card.getName());
        }

        return cardNames;
    }

    public ArrayList<String> getCardPool() {
        ArrayList<String> cards = new ArrayList<String>();

        for(Card card : cardPool) {
            cards.add(card.getName());
        }

        return cards;
    }

}
