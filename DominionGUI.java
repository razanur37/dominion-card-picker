// Copyright (c) 2015 by Casey English

import dominion.Dominion;
import dominion.Card;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.prefs.Preferences;
import javax.swing.*;

public class DominionGUI {
    private JPanel panel;
    private static JFrame frame;
    private JCheckBox baseCheckBox;
    private JCheckBox intrigueCheckBox;
    private JCheckBox seasideCheckBox;
    private JCheckBox alchemyCheckBox;
    private JCheckBox prosperityCheckBox;
    private JCheckBox cornucopiaCheckBox;
    private JCheckBox hinterlandsCheckBox;
    private JCheckBox darkAgesCheckBox;
    private JCheckBox guildsCheckBox;
    private JTextPane cardList;
    private JCheckBox blackMarketCheckBox;
    private JCheckBox envoyCheckBox;
    private JCheckBox walledVillageCheckBox;
    private JCheckBox governorCheckBox;
    private JCheckBox stashCheckBox;
    private JCheckBox princeCheckBox;
    private JButton generateButton;
    private JCheckBox use3_5PotionCheckBox;
    private JCheckBox noAttacksCheckBox;
    private JCheckBox requireDefenseCheckbox;
    private JCheckBox requireBuysCheckBox;
    private JCheckBox requireTrashingCheckBox;
    private JCheckBox requireCardDrawCheckBox;
    private JCheckBox requireExtraActionsCheckBox;
    private JCheckBox noCursingCheckBox;

    Preferences prefs = Preferences.userNodeForPackage(DominionGUI.class);
    private int setsSelected = 0;
    private final String VERSION = "1.1.0";

    private ArrayList<Card> gameCards;
    private Card baneCard;

    private enum SortOption {NAME, COST, SET, SET_COST}

    private static final String SORT_OPTION = "0";
    private SortOption sortOption = SortOption.values()[prefs.getInt(SORT_OPTION, 0)];


    private static final String IMAGES_ON = "true";
    private boolean imagesOn = prefs.getBoolean(IMAGES_ON, true);

    public DominionGUI() {
        addMenus();
        ItemListener listener = e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                // Keep track of the number of sets being used.
                ++setsSelected;

                // If Alchemy and at least one other set are being used, allow
                // the 3-5 Potion Requirement to be selected.
                if (alchemyCheckBox.isSelected() && setsSelected > 1) {
                    use3_5PotionCheckBox.setEnabled(true);
                }

                // Currently only Base and Seaside have defenses, so make sure
                // one of them is in use before allowing Defenses to be required.
                if (baseCheckBox.isSelected() || seasideCheckBox.isSelected())
                    requireDefenseCheckbox.setEnabled(true);

                // If Attacks aren't being used, then there's no reason to let
                // the user disable Curses as well (all cursers are Attacks).
                // There's also no reason to let the user require a defense if
                // there's no possibility of an Attack being selected.
                if (noAttacksCheckBox.isSelected()) {
                    noCursingCheckBox.setSelected(false);
                    noCursingCheckBox.setEnabled(false);
                    requireDefenseCheckbox.setSelected(false);
                    requireDefenseCheckbox.setEnabled(false);
                }

            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                // These just undo what was done above.
                --setsSelected;
                if (!alchemyCheckBox.isSelected() || setsSelected <= 1) {
                    use3_5PotionCheckBox.setSelected(false);
                    use3_5PotionCheckBox.setEnabled(false);
                }

                if (!baseCheckBox.isSelected() && !seasideCheckBox.isSelected()) {
                    requireDefenseCheckbox.setSelected(false);
                    requireDefenseCheckbox.setEnabled(false);
                }

                if (!noAttacksCheckBox.isSelected()) {
                    noCursingCheckBox.setEnabled(true);
                    requireDefenseCheckbox.setEnabled(true);
                }
            }

            // If at least one set is being used, allow the user to generate
            // the game.
            if (setsSelected > 0) {
                generateButton.setEnabled(true);
            } else {
                generateButton.setEnabled(false);
            }
        };

        baseCheckBox.addItemListener(listener);
        intrigueCheckBox.addItemListener(listener);
        seasideCheckBox.addItemListener(listener);
        alchemyCheckBox.addItemListener(listener);
        prosperityCheckBox.addItemListener(listener);
        cornucopiaCheckBox.addItemListener(listener);
        cornucopiaCheckBox.addItemListener(listener);
        hinterlandsCheckBox.addItemListener(listener);
        darkAgesCheckBox.addItemListener(listener);
        guildsCheckBox.addItemListener(listener);
        noAttacksCheckBox.addItemListener(listener);

        generateButton.addActionListener(e -> {
            ArrayList<String> chosenSets = new ArrayList<>();

            // Create the list of sets to use.
            if (baseCheckBox.isSelected())
                chosenSets.add("BASE");
            if (intrigueCheckBox.isSelected())
                chosenSets.add("INTRIGUE");
            if (seasideCheckBox.isSelected())
                chosenSets.add("SEASIDE");
            if (alchemyCheckBox.isSelected())
                chosenSets.add("ALCHEMY");
            if (prosperityCheckBox.isSelected())
                chosenSets.add("PROSPERITY");
            if (cornucopiaCheckBox.isSelected())
                chosenSets.add("CORNUCOPIA");
            if (hinterlandsCheckBox.isSelected())
                chosenSets.add("HINTERLANDS");
            if (darkAgesCheckBox.isSelected())
                chosenSets.add("DARK AGES");
            if (guildsCheckBox.isSelected())
                chosenSets.add("GUILDS");
            if (blackMarketCheckBox.isSelected())
                chosenSets.add("BLACK MARKET");
            if (envoyCheckBox.isSelected())
                chosenSets.add("ENVOY");
            if (walledVillageCheckBox.isSelected())
                chosenSets.add("WALLED VILLAGE");
            if (governorCheckBox.isSelected())
                chosenSets.add("GOVERNOR");
            if (stashCheckBox.isSelected())
                chosenSets.add("STASH");
            if (princeCheckBox.isSelected())
                chosenSets.add("PRINCE");

            boolean use3_5Potions = false;
            boolean noAttacks = false;
            boolean noCursing = false;
            boolean requireDefense = false;
            boolean requireBuys = false;
            boolean requireTrashing = false;
            boolean requireCardDraw = false;
            boolean requireExtraActions = false;

            // Check our restrictions and set accordingly.
            if (use3_5PotionCheckBox.isSelected())
                use3_5Potions = true;

            if (noAttacksCheckBox.isSelected())
                noAttacks = true;

            if (noCursingCheckBox.isSelected())
                noCursing = true;

            if (requireDefenseCheckbox.isSelected())
                requireDefense = true;

            if (requireBuysCheckBox.isSelected())
                requireBuys = true;

            if (requireTrashingCheckBox.isSelected())
                requireTrashing = true;

            if (requireCardDrawCheckBox.isSelected())
                requireCardDraw = true;

            if (requireExtraActionsCheckBox.isSelected())
                requireExtraActions = true;

            // Package the restrictions together.
            Dominion.Restrictions  restrictions= new Dominion.Restrictions(
                    use3_5Potions, noAttacks, noCursing, requireDefense, requireBuys,
                    requireTrashing, requireCardDraw, requireExtraActions);

            // Create the game.
            Dominion dominion = new Dominion(chosenSets, restrictions);

            // Setup the game. Throw a warning message if Young Witch is in the
            // game but no Bane card could be chosen.
            try {
                dominion.setup();
            } catch (Exception bane) {
                JOptionPane.showMessageDialog(panel, "Young Witch has been" +
                        "selected, yet all possible Bane cards from selected" +
                        "sets are already in the game. \nEither generate a" +
                        "new game, make changes to this one, or use a card" +
                        "from a different set as the Bane card.","Warning", JOptionPane.WARNING_MESSAGE);
            }

            // Get the card list and the Bane Card (if it exists).
            gameCards = new ArrayList<>(dominion.getCards());
            baneCard = null;
            if (gameCards.size() == 11) {
                baneCard = new Card(gameCards.get(10));
                gameCards.remove(10);
            }

            // Sort the list according to user preference.
            sort();

            // Display the cards chosen for the game.
            cardList.setText(imagesOn ? generateImageCardsTable() : generateTextCardsTable());
        });
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.exit(1);
        }

        frame = new JFrame("Dominion Card Picker");
        frame.setContentPane(new DominionGUI().panel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void addMenus() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");

        JMenu viewMenu = new JMenu("View");
        JCheckBoxMenuItem viewImages = new JCheckBoxMenuItem("Display Card Images");

        JMenu optionsMenu = new JMenu("Options");
        JMenu sortByMenu = new JMenu("Sort By");
        JCheckBoxMenuItem sortByNameItem = new JCheckBoxMenuItem("Name");
        JCheckBoxMenuItem sortByCostItem = new JCheckBoxMenuItem("Cost");
        JCheckBoxMenuItem sortBySetItem = new JCheckBoxMenuItem("Set, then Name");
        JCheckBoxMenuItem sortBySetCostItem = new JCheckBoxMenuItem("Set, then Cost");

        JMenu helpMenu = new JMenu("Help");
        JMenuItem helpItem= new JMenuItem("Help");
        JMenuItem aboutItem = new JMenuItem("About");

        menuBar.add(fileMenu);
        fileMenu.add(exitItem);

        menuBar.add(viewMenu);
        viewImages.setSelected(prefs.getBoolean(IMAGES_ON, true));
        viewMenu.add(viewImages);

        ButtonGroup sortGroup = new ButtonGroup();

        switch (sortOption) {
            case NAME:
                sortByNameItem.setSelected(true);
                break;
            case COST:
                sortByCostItem.setSelected(true);
                break;
            case SET:
                sortBySetItem.setSelected(true);
                break;
            case SET_COST:
                sortBySetCostItem.setSelected(true);
                break;
        }

        sortGroup.add(sortByNameItem);
        sortGroup.add(sortByCostItem);
        sortGroup.add(sortBySetItem);
        sortGroup.add(sortBySetCostItem);

        menuBar.add(optionsMenu);
        sortByMenu.add(sortByNameItem);
        sortByMenu.add(sortByCostItem);
        sortByMenu.add(sortBySetItem);
        sortByMenu.add(sortBySetCostItem);
        optionsMenu.add(sortByMenu);

        menuBar.add(helpMenu);
        helpMenu.add(helpItem);
        helpMenu.add(aboutItem);

        fileMenu.setMnemonic('F');
        exitItem.setMnemonic('X');

        viewMenu.setMnemonic('V');
        viewImages.setMnemonic('I');

        optionsMenu.setMnemonic('O');
        sortByMenu.setMnemonic('S');
        sortByNameItem.setMnemonic('N');
        sortByCostItem.setMnemonic('C');
        sortBySetItem.setMnemonic('S');
        sortBySetCostItem.setMnemonic('E');

        helpMenu.setMnemonic('H');
        helpItem.setMnemonic('H');
        aboutItem.setMnemonic('A');

        helpItem.setAccelerator(KeyStroke.getKeyStroke("F1"));

        JEditorPane helpEditorPane = new JEditorPane();

        helpEditorPane.setEditable(false);

        java.net.URL helpURL = DominionGUI.class.getResource("README.HTML");

        try {
            helpEditorPane.setPage(helpURL);
        } catch (IOException io) {
            io.printStackTrace();
        }

        JScrollPane helpScrollPane = new JScrollPane(helpEditorPane);

        helpScrollPane.setPreferredSize(new Dimension(800, 600));

        viewImages.addItemListener(e -> {
            imagesOn = !imagesOn;
            prefs.putBoolean(IMAGES_ON, imagesOn);
            if (gameCards != null && gameCards.size() == 10)
                cardList.setText(imagesOn ? generateImageCardsTable() : generateTextCardsTable());
        });

        ItemListener listener = e -> {
            if (sortByNameItem.isSelected())
                sortOption = SortOption.NAME;
            else if (sortByCostItem.isSelected())
                sortOption = SortOption.COST;
            else if (sortBySetItem.isSelected())
                sortOption = SortOption.SET;
            else
                sortOption = SortOption.SET_COST;

            if (e.getStateChange() == ItemEvent.SELECTED)
                prefs.putInt(SORT_OPTION, sortOption.ordinal());

            // If the game has already been generated, resort the current game
            // according to the new choice.
            if (gameCards != null && gameCards.size() == 10) {
                sort();
                cardList.setText(imagesOn ? generateImageCardsTable() : generateTextCardsTable());
            }
        };

        sortByNameItem.addItemListener(listener);
        sortByCostItem.addItemListener(listener);
        sortBySetItem.addItemListener(listener);
        sortBySetCostItem.addItemListener(listener);

        exitItem.addActionListener(e -> System.exit(0));

        helpItem.addActionListener(e -> JOptionPane.showMessageDialog(null, helpScrollPane, "Help", JOptionPane.PLAIN_MESSAGE));

        aboutItem.addActionListener(e -> {
            String aboutString =
                    "Dominion Card Picker\n"
                    + VERSION + "\n"
                    + "Copyright (c) 2015 by Casey English\n"
                    + "Licensed under The MIT License\n"
                    + "Dominion is Copyright (c) by Donald X. Vaccarino and Rio Grande Games\n"
                    + "This work is neither licensed nor endorsed by Donald X. Vaccarino or Rio Grande Games";

            JOptionPane.showMessageDialog(frame, aboutString, "About Dominion Card Picker", JOptionPane.INFORMATION_MESSAGE);
        });

        frame.setJMenuBar(menuBar);
    }

    private void sort() {
        // Sort the cards based on the user's choice in the menu.
        switch (sortOption) {
            case NAME:
                Collections.sort(gameCards, Comparator.comparing(Card::getName));
                break;
            case COST:
                Collections.sort(gameCards, Comparator.comparing(Card::getCost).thenComparing(Card::getName));
                break;
            case SET:
                Collections.sort(gameCards, Comparator.comparing(Card::getSet).thenComparing(Card::getName));
                break;
            case SET_COST:
                Collections.sort(gameCards, Comparator.comparing(Card::getSet).thenComparing(Card::getCost).thenComparing(Card::getName));
                break;
        }
    }

    // Create an HTML table to display images of all the cards in the game.
    private String generateImageCardsTable() {
        String cardsTable = "<table>";
        cardsTable = cardsTable + "<tr>";

        for (int i=0; i<gameCards.size(); ++i) {
            if (i == 5)
                cardsTable = cardsTable + "</tr><tr>";
            cardsTable = cardsTable + "<td>" + getFile(gameCards.get(i)) + "</td>";
        }

        if (baneCard != null) {
            cardsTable = cardsTable + "</tr>";
            cardsTable = cardsTable + "<tr>";
            cardsTable = cardsTable + "<td bgcolor=\"rgb(0,255,0)\">";
            cardsTable = cardsTable + "<h1 style=\"font:sans-serif;font-size:1.25em;font-weight:bold\">";
            cardsTable = cardsTable + "<center>Bane Card<center>";
            cardsTable = cardsTable + "</h1>";
            cardsTable = cardsTable + getFile(baneCard);
            cardsTable = cardsTable + "</td>";
        }

        cardsTable = cardsTable + "</tr>";
        cardsTable = cardsTable + "</table>";

        return cardsTable;
    }

    // Create an HTML table to generate the game in a text-only format
    private String generateTextCardsTable() {
        String cardsTable = "<table style=\"font:sans-serif\">";
        cardsTable = cardsTable + "<tr>";
        cardsTable = cardsTable + "<th>Name</th>";
        cardsTable = cardsTable + "<th>Types</th>";
        cardsTable = cardsTable + "<th>Cost</th>";
        cardsTable = cardsTable + "<th>Attributes</th>";
        cardsTable = cardsTable + "<th>Set</th>";
        cardsTable = cardsTable + "</tr>";

        String finalCost;
        String types;
        String attributes;
        for (Card card : gameCards) {
            finalCost = convertCost(card.getCost());
            types = convertTypes(card.getTypes());
            attributes = convertAttributes(card.getAttributes());

            cardsTable = cardsTable + "<tr>";
            cardsTable = cardsTable + "<td>" + card.getName() + "</td>";
            cardsTable = cardsTable + "<td>" + types + "</td>";
            cardsTable = cardsTable + "<td>" + finalCost + "</td>";
            cardsTable = cardsTable + "<td>" + attributes + "</td>";
            cardsTable = cardsTable + "<td>" + card.getSet() + "</td>";
            cardsTable = cardsTable + "</tr>";
        }

        if (baneCard != null) {
            finalCost = convertCost(baneCard.getCost());
            types = convertTypes(baneCard.getTypes());
            attributes = convertAttributes(baneCard.getAttributes());

            cardsTable = cardsTable + "<tr bgcolor=\"rgb(0,255,0)\"><th colspan=\"5\">Bane Card</th></tr>";
            cardsTable = cardsTable + "<tr>";
            cardsTable = cardsTable + "<td>" + baneCard.getName() + "</td>";
            cardsTable = cardsTable + "<td>" + types + "</td>";
            cardsTable = cardsTable + "<td>" + finalCost + "</td>";
            cardsTable = cardsTable + "<td>" + attributes + "</td>";
            cardsTable = cardsTable + "<td>" + baneCard.getSet() + "</td>";
            cardsTable = cardsTable + "</tr>";
        }

        cardsTable = cardsTable + "</table>";

        return cardsTable;
    }

    private String convertCost(int cost) {
        String finalCost;
        if (cost > 80)
            finalCost = Integer.toString(cost-80) + 'P';
        else if (cost == 80)
            finalCost = "P";
        else
            finalCost = Integer.toString(cost);

        return finalCost;
    }

    private String convertTypes(ArrayList<String> typeList) {
        String types = "";

        for (String type : typeList) {
            types = types + "—" + type;
        }
        types = types.substring(1, types.length());

        return types;
    }

    private String convertAttributes(ArrayList<String> attributeList) {
        String attributes = "";
        for (String attribute : attributeList) {
            attributes = attributes + ", " + attribute;
        }
        if (attributes.length() > 0)
            attributes = attributes.substring(2, attributes.length());

        return attributes;
    }

    // Converts the name of the given card into the format used in the 'img'
    // folder
    private String getFile(Card card) {
        String path;
        try {
            path = "<img src=\"" + DominionGUI.class.getClassLoader().getResource("resources/img/" + card.getSet() + "/" + card.getName().replace(' ', '_') + ".jpg") + "\">";
        } catch (Exception e) {
            path = card + ".jpg";
        }
        return path;
    }
}
