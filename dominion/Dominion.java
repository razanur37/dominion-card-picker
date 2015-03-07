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

        this.restrictions = new Restrictions(restrictions);
        cardPool = getCardPool();
    }

    public Dominion(String[] sets, Restrictions restrictions) {
        this(new ArrayList<>(Arrays.asList(sets)), restrictions);
    }

    public Dominion(String sets, Restrictions restrictions) {
        this(new ArrayList<>(Arrays.asList(sets.split(", "))), restrictions);
    }

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

    public void setup() throws Exception {
        gameCards = new ArrayList<>();

        Random random = new Random();
        boolean needBane = false;

        if (restrictions.isNoAttacks())
            removeAttacks();
        
        if (!restrictions.isNoAttacks() && restrictions.isNoCursing())
            removeCursing();
        
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
                addBuy();
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
                addTrashing();
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
                addCardDraw();
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
                addExtraActions();
        }

        boolean attackSelected = false;
        boolean defenseSelected = false;

        for (Card card : gameCards) {
            if (card.getTypes().contains("Attack"))
                attackSelected = true;
            if (card.getAttributes().contains("Defense"))
                defenseSelected = true;
        }
        
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
        
        if (restrictions.isRequireDefense() && attackSelected && !defenseSelected) {
            getADefense();
        }
    }
    
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
    
    private void addBuy() {
        ArrayList<Card> allBuys = new ArrayList<>();
        Card buy;

        for (Card card : cardPool) {
            if (card.getAttributes().contains("Buys")) {
                allBuys.add(card);
            }
        }
        
        buy = allBuys.get(new Random().nextInt(allBuys.size()));
        
        gameCards.add(new Card(buy));
    }

    private void addTrashing() {
        ArrayList<Card> allTrash = new ArrayList<>();
        Card trash;

        for (Card card : cardPool) {
            if (card.getAttributes().contains("Trash")) {
                allTrash.add(card);
            }
        }

        trash = allTrash.get(new Random().nextInt(allTrash.size()));

        gameCards.add(new Card(trash));
    }
    
    private void addCardDraw() {
        ArrayList<Card> allCardDraw = new ArrayList<>();
        Card cardDraw;

        for (Card card : cardPool) {
            if (card.getAttributes().contains("Cards")) {
                allCardDraw.add(card);
            }
        }

        cardDraw = allCardDraw.get(new Random().nextInt(allCardDraw.size()));

        gameCards.add(new Card(cardDraw));
    }

    private void addExtraActions() {
        ArrayList<Card> allExtraActions = new ArrayList<>();
        Card extraActions;

        for (Card card : cardPool) {
            if (card.getAttributes().contains("Actions")) {
                allExtraActions.add(card);
            }
        }

        extraActions = allExtraActions.get(new Random().nextInt(allExtraActions.size()));

        gameCards.add(new Card(extraActions));
    }
    
    private void removeAttacks() {
        Iterator<Card> iterator = cardPool.iterator();
        while (iterator.hasNext()) {
            Card card = iterator.next();
            if (card.getTypes().contains("Attack")) {
                iterator.remove();
            }
        }
    }
    
    private void removeCursing() {
        Iterator<Card> iterator = cardPool.iterator();
        while (iterator.hasNext()) {
            Card card = iterator.next();
            if (card.getAttributes().contains("Curse")) {
                iterator.remove();
            }
        }
    }
    
    private void getADefense() {
        int i = -1;
        ArrayList<Card> allDefense = new ArrayList<>();
        Card defense;

        for (Card card : cardPool) {
            if (card.getAttributes().contains("Defense"))
                allDefense.add(card);
        }
        
        defense = allDefense.get(new Random().nextInt(allDefense.size()));

        for (int j = gameCards.size()-1; j>=0; --j) {
            if (!gameCards.get(j).getTypes().contains("Attack")) {
                i = j;
                break;
            }
        }

        if (i == -1) {
            i = gameCards.size()-1;
        }
        
        cardPool.add(new Card(gameCards.get(i)));
        gameCards.set(i, defense);
        cardPool.remove(defense);
    }
    
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
