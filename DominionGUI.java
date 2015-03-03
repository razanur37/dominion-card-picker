/*
 * Copyright (c) 2015. by Casey English
 */

import dominion.Dominion;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
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

    private int setsSelected = 0;

    public DominionGUI() {
        ItemListener listener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    ++setsSelected;
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    --setsSelected;
                }

                if (setsSelected > 0) {
                    generateButton.setEnabled(true);
                } else {
                    generateButton.setEnabled(false);
                }
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
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> chosenSets = new ArrayList<String>();

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

                Dominion dominion = new Dominion(chosenSets);

                dominion.setup();

                ArrayList<String> gameCards = new ArrayList<String>(dominion.getGameCardNames());
                Collections.sort(gameCards);

                String cardsTable = "<table><tr>";
                
                for (int i=0; i<gameCards.size(); ++i) {
                    if (i == 5)
                        cardsTable = cardsTable + "</tr><tr>";
                    cardsTable = cardsTable + "<td>" + getFile(gameCards.get(i)) + "</td>";
                }
                
                cardsTable = cardsTable + "</tr></table>";

                cardList.setText(cardsTable);
            }
        });
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

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.exit(1);
        }

        JFrame frame = new JFrame("Dominion Card Picker");
        frame.setContentPane(new DominionGUI().panel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
