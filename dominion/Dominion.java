// Copyright (C) 2015 by Casey English

package dominion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Dominion {

    private static final ArrayList<String> ALL_SETS = new ArrayList<String>(
            Arrays.asList(
                    "BASE", "INTRIGUE", "SEASIDE", "ALCHEMY", "PROSPERITY",
                    "CORNUCOPIA", "HINTERLANDS", "DARK AGES", "GUILDS"
            )
    );
    private static final ArrayList<String> PROMOS = new ArrayList<String>(
            Arrays.asList(
                    "BLACK MARKET", "ENVOY", "WALLED VILLAGE", "GOVERNOR",
                    "STASH", "PRINCE"
            )
    );
    private ArrayList<String> sets;
    private ArrayList<Card> cardPool;
    private ArrayList<Card> gameCards;
    private Game game;

    public Dominion(ArrayList<String> sets) {
        this.sets = new ArrayList<String>();

        try {
            for (String singleChosenSet: sets) {
                if (!ALL_SETS.contains(singleChosenSet.toUpperCase()) &&
                        !PROMOS.contains(singleChosenSet.toUpperCase())) {
                    throw new IllegalArgumentException("Set " + singleChosenSet + "is not a valid Dominion set");
                }

                String set = PROMOS.contains(singleChosenSet) ? "PROMO-"+properCase(singleChosenSet) : singleChosenSet;

                this.sets.add(set);
            }
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            System.exit(1);
        }

        cardPool = getCardPool();
    }

    public Dominion(String[] sets) {
        this(new ArrayList<String>(Arrays.asList(sets)));
    }

    public Dominion(String sets) {
        this(new ArrayList<String>(Arrays.asList(sets.split(", "))));
    }

    public void setup() {
        gameCards = new ArrayList<Card>();

        Random randomizer = new Random();
        boolean needBane = false;

        while (gameCards.size() != 10) {
            Card chosenCard;
            do {
                chosenCard = cardPool.get(randomizer.nextInt(cardPool.size()));
            } while (gameCards.contains(chosenCard));
            gameCards.add(chosenCard);

            if (chosenCard.getName().equals("Young Witch"))
                needBane = true;
        }

        if (needBane) {
            Card baneCard;

            do {
                baneCard = cardPool.get(randomizer.nextInt(cardPool.size()));
            } while (gameCards.contains(baneCard) && baneCard.getCost() != 2 && baneCard.getCost() != 3);

            gameCards.add(baneCard);
        }
    }

    private ArrayList<Card> getCardPool() {
        ArrayList<Card> cards = new ArrayList<Card>();

        Connection c;
        Statement stmt;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:cards.db");
            c.setAutoCommit(false);

            for(String set : sets) {
                stmt = c.createStatement();
                String workingSet = set.contains("PROMO") ? set.split("-")[0] : set;
                String promo = set.contains("PROMO") ? "WHERE name = '" + set.split("-")[1] + "'" : "";
                ResultSet rs = stmt.executeQuery("select * from '" + workingSet + "'" + promo + ";");
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
                        card = new Card(name, types, cost, attributes, workingSet);
                    } else {
                        card = new Card(name, types, cost, workingSet);
                    }

                    cards.add(card);
                }

                rs.close();
                stmt.close();
            }
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(1);
        }

        return cards;
    }

    public ArrayList<Card> getCards() {
        return gameCards;
    }

    private String properCase (String input) {
        // Empty strings should be returned as-is.

        if (input.length() == 0) return "";

        // Strings with only one character uppercased.

        if (input.length() == 1) return input.toUpperCase();

        // Otherwise uppercase first letter, lowercase the rest.

        String[] inputs = input.split(" ");
        String output = "";

        for (String word : inputs) {
            if (output.length() == 0)
                output = word.substring(0, 1).toUpperCase()
                    + word.substring(1).toLowerCase();
            else
                output = output + " " + word.substring(0, 1).toUpperCase()
                        + word.substring(1).toLowerCase();
        }
        return output;
    }
}
