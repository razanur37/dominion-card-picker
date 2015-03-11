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

    private int setsSelected = 0;
    private static final String VERSION = "1.0.2";

    private static ArrayList<Card> gameCards;
    private static Card baneCard;

    private enum SortOption {NAME, COST, SET, SET_COST}

    private static SortOption sortOption = SortOption.NAME;

    public DominionGUI() {
        addMenus();
        ItemListener listener = e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                ++setsSelected;
                if (alchemyCheckBox.isSelected() && setsSelected > 1) {
                    use3_5PotionCheckBox.setEnabled(true);
                }

                if (baseCheckBox.isSelected() || seasideCheckBox.isSelected())
                    requireDefenseCheckbox.setEnabled(true);

                if (noAttacksCheckBox.isSelected()) {
                    noCursingCheckBox.setSelected(false);
                    noCursingCheckBox.setEnabled(false);
                }

            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
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
                }
            }

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

            Dominion.Restrictions  restrictions= new Dominion.Restrictions(
                    use3_5Potions, noAttacks, noCursing, requireDefense, requireBuys,
                    requireTrashing, requireCardDraw, requireExtraActions);
            Dominion dominion = new Dominion(chosenSets, restrictions);

            try {
                dominion.setup();
            } catch (Exception bane) {
                JOptionPane.showMessageDialog(panel, "Young Witch has been selected, yet all possible Bane cards from selected sets are already in the game. \nEither generate a new game, make changes to this one, or use a card from a different set as the Bane card.","Warning", JOptionPane.WARNING_MESSAGE);
            }

            gameCards = new ArrayList<>(dominion.getCards());
            baneCard = null;
            if (gameCards.size() == 11) {
                baneCard = new Card(gameCards.get(10));
                gameCards.remove(10);
            }

            sort();

            cardList.setText(generateCardsTable());
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

        JMenu optionsMenu = new JMenu("Options");
        JMenu sortByMenu = new JMenu("Sort By");
        JCheckBoxMenuItem sortByNameItem = new JCheckBoxMenuItem("Name");
        JCheckBoxMenuItem sortByCostItem = new JCheckBoxMenuItem("Cost, then Name");
        JCheckBoxMenuItem sortBySetItem = new JCheckBoxMenuItem("Set, then Name");
        JCheckBoxMenuItem sortBySetCostItem = new JCheckBoxMenuItem("Set, then Cost, then Name");

        JMenu helpMenu = new JMenu("Help");
        JMenuItem helpItem= new JMenuItem("Help");
        JMenuItem aboutItem = new JMenuItem("About");

        menuBar.add(fileMenu);
        fileMenu.add(exitItem);

        ButtonGroup sortGroup = new ButtonGroup();

        sortByNameItem.setSelected(true);

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

        ItemListener listener = e -> {
            if (sortByNameItem.isSelected())
                sortOption = SortOption.NAME;
            else if (sortByCostItem.isSelected())
                sortOption = SortOption.COST;
            else if (sortBySetItem.isSelected())
                sortOption = SortOption.SET;
            else
                sortOption = SortOption.SET_COST;

            if (gameCards != null && gameCards.size() == 10) {
                sort();
                cardList.setText(generateCardsTable());
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

    private String generateCardsTable() {
        String cardsTable = "<table><tr>";

        for (int i=0; i<gameCards.size(); ++i) {
            if (i == 5)
                cardsTable = cardsTable + "</tr><tr>";
            cardsTable = cardsTable + "<td>" + getFile(gameCards.get(i)) + "</td>";
        }

        if (baneCard != null) {
            cardsTable = cardsTable + "</tr><tr><td bgcolor=\"rgb(0,255,0)\"><h1 style=\"font:sans-serif;font-size:1.25em;font-weight:bold\"><center>Bane Card<center></h1>" + getFile(baneCard)
                    + "</td>";
        }

        cardsTable = cardsTable + "</tr></table>";

        return cardsTable;
    }

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
