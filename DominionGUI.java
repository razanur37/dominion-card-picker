/*
 * Copyright (c) 2015. by Casey English
 */

import dominion.Dominion;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.text.StringContent;

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
    private JButton generateButton;
    private JTextArea cardList;

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
                cardList.setText(null);

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

                Dominion dominion = new Dominion(chosenSets);

                dominion.setup();

                ArrayList<String> gameCards = new ArrayList<String >(dominion.getGameCardNames());

                for (String card : gameCards) {
                    cardList.append(card + '\n');
                }
            }
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
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}