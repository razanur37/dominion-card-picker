// Copyright (C) 2015 by Casey English

package dominion;

import java.util.Random;
import java.util.ArrayList;

public class Game {

    private ArrayList<Card> cardPool;
    private ArrayList<Card> gameCards = new ArrayList<Card>();

    protected Game(ArrayList<Card> cardPool) {
        this.cardPool = new ArrayList<Card>(cardPool);
    }
    
    protected void pickCards() {
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

    protected void displayCards() {
        for(Card singleCard : gameCards) {
            System.out.println(singleCard.getName());
        }
    }
}
