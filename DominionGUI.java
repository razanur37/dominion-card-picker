// Copyright (c) 2015 by Casey English

import dominion.Dominion;
import dominion.Card;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.*;

public class DominionGUI {
    private JPanel panel;
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
    private static final String VERSION = "1.0.1";

    public DominionGUI() {
        ItemListener listener = e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                ++setsSelected;
                if (alchemyCheckBox.isSelected() && setsSelected > 1)
                    use3_5PotionCheckBox.setEnabled(true);

                if (baseCheckBox.isSelected() || seasideCheckBox.isSelected())
                    requireDefenseCheckbox.setEnabled(true);

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
                JOptionPane.showMessageDialog(panel, "Young Witch has been selected, yet all possible Bane cards from selected sets are already in the game. \nEither generate a new game, make changes to this one, or use a card from a different set as the Bane card.");
            }

            ArrayList<Card> gameCards = new ArrayList<>(dominion.getCards());
            Card baneCard = null;
            if (gameCards.size() == 11) {
                baneCard = new Card(gameCards.get(10));
                gameCards.remove(10);
            }
            Collections.sort(gameCards, Comparator.comparing(Card::getCost).thenComparing(Card::getName));

            String cardsTable = "<table><tr>";

            for (int i=0; i<gameCards.size(); ++i) {
                if (i == 5)
                    cardsTable = cardsTable + "</tr><tr>";
                cardsTable = cardsTable + "<td>" + getFile(gameCards.get(i).getName()) + "</td>";
            }

            if (baneCard != null) {
                cardsTable = cardsTable + "</tr><tr><td bgcolor=\"rgb(0,255,0)\"><h1><center>Bane Card<center></h1>" + getFile(baneCard.getName())
                        + "</td>";
            }

            cardsTable = cardsTable + "</tr></table>";

            cardList.setText(cardsTable);
        });
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.exit(1);
        }

        JFrame frame = new JFrame("Dominion Card Picker");
        frame.setContentPane(new DominionGUI().panel);
        addMenus(frame);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private static void addMenus(JFrame frame) {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu helpMenu = new JMenu("Help");
        JMenuItem exitItem = new JMenuItem("Exit");
        JMenuItem helpItem= new JMenuItem("Help");
        JMenuItem aboutItem = new JMenuItem("About");

        menuBar.add(fileMenu);
        fileMenu.add(exitItem);

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

    private String getFile(String filename) {
        String path;
        try {
            path = "<img src=\"" + this.getClass().getClassLoader().getResource("img/" + filename.replace(' ', '_') + ".jpg") + "\">";
        } catch (Exception e) {
            path = filename + ".jpg";
        }
        return path;
    }
}
