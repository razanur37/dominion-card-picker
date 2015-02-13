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
	    
	    while (gameCards.size() != 10) {
            Card chosen_card;
            do {
                chosen_card = cardPool.get(randomizer.nextInt(cardPool.size()));
            } while (gameCards.contains(chosen_card));
            gameCards.add(chosen_card);
        }
    }

    protected void displayCards() {
        for(Card singleCard : gameCards) {
            System.out.println(singleCard.getName());
        }
    }
}
