// Copyright (C) 2015 by Casey English

package dominion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

public class Dominion {

    private static final ArrayList<String> ALL_SETS = new ArrayList<>(
            Arrays.asList(
                    "BASE", "INTRIGUE", "SEASIDE", "ALCHEMY", "PROSPERITY",
                    "CORNUCOPIA", "HINTERLANDS", "DARK AGES", "GUILDS"
            )
    );
    private static final ArrayList<String> PROMOS = new ArrayList<>(
            Arrays.asList(
                    "BLACK MARKET", "ENVOY", "WALLED VILLAGE", "GOVERNOR",
                    "STASH", "PRINCE"
            )
    );
    private ArrayList<String> sets;
    private ArrayList<Card> cardPool;
    private ArrayList<Card> gameCards;
    private Restrictions restrictions;

    public Dominion(ArrayList<String> sets, Restrictions restrictions) {
        this.sets = new ArrayList<>();

        // Make sure the sets (or promo cards) that were entered in are
        // legitimate Dominion sets or promos.
        try {
            for (String singleChosenSet: sets) {
                if (!ALL_SETS.contains(singleChosenSet.toUpperCase()) &&
                        !PROMOS.contains(singleChosenSet.toUpperCase())) {
                    throw new IllegalArgumentException("Set " + singleChosenSet + "is not a valid Dominion set");
                }

                // Tag promo cards as such so that they can be retrieved from the database
                String set = PROMOS.contains(singleChosenSet) ? "PROMO-"+properCase(singleChosenSet) : singleChosenSet;

                this.sets.add(set);
            }
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            System.exit(1);
        }

        this.restrictions = new Restrictions(restrictions);
        cardPool = getCardPool();
    }

    public Dominion(String[] sets, Restrictions restrictions) {
        this(new ArrayList<>(Arrays.asList(sets)), restrictions);
    }

    public Dominion(String sets, Restrictions restrictions) {
        this(new ArrayList<>(Arrays.asList(sets.split(", "))), restrictions);
    }

    // Pull all cards from every set being used in this game, plus any promo
    // cards being used, from the database.
    private ArrayList<Card> getCardPool() {
        ArrayList<Card> cards = new ArrayList<>();

        Connection c;
        Statement stmt;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite::resource:" + getClass().getResource("/resources/cards.sqlite").toString());
            c.setAutoCommit(false);

            for(String set : sets) {
                stmt = c.createStatement();
                String workingSet = set.contains("PROMO") ? set.split("-")[0] : set;
                String promo = set.contains("PROMO") ? "WHERE name = '" + set.split("-")[1] + "'" : "";
                ResultSet rs = stmt.executeQuery("select * from '" + workingSet + "'" + promo + ";");
                while (rs.next()) {
                    String name = rs.getString("name");
                    ArrayList<String> types = new ArrayList<>(Arrays.asList(rs.getString("types").split(", ")));
                    int cost = rs.getInt("cost");
                    String attrs = rs.getString("attributes");
                    ArrayList<String> attributes;
                    if (attrs != null) {
                        attributes = new ArrayList<>(Arrays.asList(rs.getString("attributes").split(", ")));
                    } else {
                        attributes = new ArrayList<>();
                    }

                    Card card = new Card(name, types, cost, attributes, workingSet);
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

    // Choose the Kingdom cards to be used for the game.
    public void setup() throws Exception {
        gameCards = new ArrayList<>();

        Random random = new Random();
        boolean needBane = false;

        // Check all the possible restrictions, then remove any banned cards,
        // then add cards with desired attributes.
        if (restrictions.isNoAttacks())
            removeCardsByType("Attack");
        
        if (!restrictions.isNoAttacks() && restrictions.isNoCursing())
            removeCardsByAttribute("Curse");
        
        if (restrictions.isUse3_5PotionCards())
            addAlchemyCards();
        if (restrictions.isRequireBuys()) {
            boolean buyAlreadySelected = false;
            
            for (Card card : gameCards) {
                if (card.getAttributes().contains("Buys")) {
                    buyAlreadySelected = true;
                    break;
                }                
            }
            
            if (!buyAlreadySelected)
                addCardsByAttribute("Buys");
        }
        if (restrictions.isRequireTrashing()) {
            boolean trashAlreadySelected = false;

            for (Card card : gameCards) {
                if (card.getAttributes().contains("Trash")) {
                    trashAlreadySelected = true;
                    break;
                }
            }

            if (!trashAlreadySelected)
                addCardsByAttribute("Trash");
        }
        if (restrictions.isRequireCardDraw()) {
            boolean cardDrawAlreadySelected = false;

            for (Card card : gameCards) {
                if (card.getAttributes().contains("Cards")) {
                    cardDrawAlreadySelected = true;
                    break;
                }
            }

            if (!cardDrawAlreadySelected)
                addCardsByAttribute("Cards");
        }

        if (restrictions.isRequireExtraActions()) {
            boolean extraActionsAlreadySelected = false;

            for (Card card : gameCards) {
                if (card.getAttributes().contains("Actions")) {
                    extraActionsAlreadySelected = true;
                    break;
                }
            }

            if (!extraActionsAlreadySelected)
                addCardsByAttribute("Actions");
        }

        boolean attackSelected = false;
        boolean defenseSelected = false;

        // Check to see if an attack or a defense have already been added via
        // the above methods.
        for (Card card : gameCards) {
            if (card.getTypes().contains("Attack"))
                attackSelected = true;
            if (card.getAttributes().contains("Defense"))
                defenseSelected = true;
        }

        // Fill out the remaining slots for the game with cards randomly
        // selected from the remaining card pool.
        while (gameCards.size() != 10) {
            Card chosenCard = cardPool.get(random.nextInt(cardPool.size()));
            gameCards.add(chosenCard);
            cardPool.remove(chosenCard);

            if (chosenCard.getName().equals("Young Witch"))
                needBane = true;
            
            if (chosenCard.getTypes().contains("Attack"))
                attackSelected = true;
            
            if (chosenCard.getAttributes().contains("Defense"))
                defenseSelected = true;
        }

        // If Young Witch was chosen, an 11th stack is needed, so choose it.
        if (needBane) {
            Card baneCard;

            ArrayList<Card> baneCandidates = new ArrayList<>(getBaneCandidates());
            if (baneCandidates.size() == 0) {
                throw new IllegalArgumentException("No possible candidates for bane cards");
            }
            baneCard = new Card(baneCandidates.get(random.nextInt(baneCandidates.size())));

            if (baneCard.getAttributes().contains("Defense"))
                defenseSelected = true;

            gameCards.add(baneCard);
        }

        // If we are supposed to have a defense and didn't choose one, replace
        // a chosen (non-attack) card with a defense.
        if (restrictions.isRequireDefense() && attackSelected && !defenseSelected) {
            getADefense();
        }
    }

    // Add 3-5 cards from the Alchemy set to the game, removing all others from
    // the card pool so that no other Alchemy cards can be chosen.
    private void addAlchemyCards() {
        ArrayList<Card> allAlchemy = new ArrayList<>();
        Random random = new Random();
        int numToAdd = random.nextInt(3) + 3;
        
        Iterator<Card> iterator = cardPool.iterator();
        while (iterator.hasNext()) {
            
            Card card = iterator.next();
            if (card.getSet().equals("ALCHEMY")) {
                allAlchemy.add(card);
                iterator.remove();
            }
        }
        
        for (int i=0; i<numToAdd; ++i) {
            Card card = allAlchemy.get(random.nextInt(allAlchemy.size()));
            gameCards.add(card);
            allAlchemy.remove(card);
        }
    }

    // Add a single card from the card pool that has the given attribute, then
    // remove it from the card pool.
    private void addCardsByAttribute(String attribute) {
        ArrayList<Card> allDesiredCards = new ArrayList<>();

        Card desiredCard;

        for (Card card : cardPool) {
            if (card.getAttributes().contains(attribute)) {
                allDesiredCards.add(card);
            }
        }

        desiredCard = allDesiredCards.get(new Random().nextInt(allDesiredCards.size()));

        cardPool.remove(desiredCard);

        gameCards.add(new Card(desiredCard));
    }

    // Remove all cards that contain the specified type from the card pool.
    private void removeCardsByType(String type) {
        Iterator<Card> iterator = cardPool.iterator();
        while (iterator.hasNext()) {
            Card card = iterator.next();
            if (card.getTypes().contains(type)) {
                iterator.remove();
            }
        }
    }

    // Remove all cards with the specified attribute from the card pool.
    private void removeCardsByAttribute(String attribute) {
        Iterator<Card> iterator = cardPool.iterator();
        while (iterator.hasNext()) {
            Card card = iterator.next();
            if (card.getTypes().contains(attribute)) {
                iterator.remove();
            }
        }
    }

    // Pick a defense card, then pick a non-attack card to replace with the defense.
    private void getADefense() {
        int i = -1;
        ArrayList<Card> allDefense = new ArrayList<>();
        Card defense;

        for (Card card : cardPool) {
            if (card.getAttributes().contains("Defense"))
                allDefense.add(card);
        }
        
        defense = allDefense.get(new Random().nextInt(allDefense.size()));

        // Start at the back of the list to make sure no cards that were added
        // because of restrictions are replaced.
        for (int j = gameCards.size()-1; j>=0; --j) {
            if (!gameCards.get(j).getTypes().contains("Attack")) {
                i = j;
                break;
            }
        }

        // If all cards in the game are attacks, simply replace the very last
        // one in the list. This guarantees that restrictions are not violated.
        // Additionally, this guarantees that Young Witch (should it be in the
        // game), cannot be replaced.
        if (i == -1) {
            i = gameCards.size()-1;
        }
        
        cardPool.add(new Card(gameCards.get(i)));
        gameCards.set(i, defense);
        cardPool.remove(defense);
    }

    // Pick out all the possible cards that can fill the 'Bane card' role
    // (cost 2 or 3).
    private ArrayList<Card> getBaneCandidates() {
        ArrayList<Card> baneCandidates = new ArrayList<>();
        
        for (Card card : cardPool) {
            if (card.getCost() >= 2 && card.getCost() <= 3) {
                baneCandidates.add(card);
            }
        }
        
        return baneCandidates;
    }

    public ArrayList<Card> getCards() {
        return gameCards;
    }

    // Keeps track of all restrictions for the game.
    public static class Restrictions {
        private boolean use3_5PotionCards;
        private boolean noAttacks;
        private boolean noCursing;
        private boolean requireDefense;
        private boolean requireBuys;
        private boolean requireTrashing;
        private boolean requireCardDraw;
        private boolean requireExtraActions;
        
        public Restrictions () {
            use3_5PotionCards = false;
            noAttacks = false;
            noCursing = false;
            requireDefense = false;
            requireBuys = false;
            requireTrashing = false;
            requireCardDraw = false;
            requireExtraActions = false;
        }
        
        public Restrictions(boolean use3_5PotionCards, boolean noAttacks, boolean noCursing, boolean requireDefense,
                            boolean requireBuys, boolean requireTrashing, boolean requireCardDraw,
                            boolean requireExtraActions) {
            this.use3_5PotionCards = use3_5PotionCards;
            this.noAttacks = noAttacks;
            this.noCursing = noCursing;
            this.requireDefense = requireDefense;
            this.requireBuys = requireBuys;
            this.requireTrashing = requireTrashing;
            this.requireCardDraw = requireCardDraw;
            this.requireExtraActions = requireExtraActions;
        }
        
        public Restrictions(Restrictions restrictions) {
            this(restrictions.isUse3_5PotionCards(), 
                    restrictions.isNoAttacks(),
                    restrictions.isNoCursing(),
                    restrictions.isRequireDefense(),
                    restrictions.isRequireBuys(),
                    restrictions.isRequireTrashing(),
                    restrictions.isRequireCardDraw(),
                    restrictions.isRequireExtraActions());
        }

        public boolean isUse3_5PotionCards() {
            return use3_5PotionCards;
        }

        public boolean isNoAttacks() {
            return noAttacks;
        }

        public boolean isNoCursing() {
            return noCursing;
        }

        public boolean isRequireDefense() {
            return requireDefense;
        }

        public boolean isRequireBuys() {
            return requireBuys;
        }

        public boolean isRequireTrashing() {
            return requireTrashing;
        }

        public boolean isRequireCardDraw() {
            return requireCardDraw;
        }

        public boolean isRequireExtraActions() {
            return requireExtraActions;
        }
    }

    private String properCase (String input) {
        // Empty strings should be returned as-is.

        if (input.length() == 0) return "";

        // Strings with only one character should be uppercase.

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
