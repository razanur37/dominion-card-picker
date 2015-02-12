// Copyright (C) 2015 by Casey English

package dominion;

import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;

public class Cards {
    
    protected static ArrayList<String> BASE_CARDS = new ArrayList<String>(Arrays.asList("Cellar", "Chapel", "Moat", "Chancellor", "Village", "Woodcutter", "Workshop", "Bureaucrat", "Feast", "Gardens", "Militia", "Moneylender", "Remodel", "Smithy", "Spy", "Thief", "Throne Room", "Council Room", "Festival", "Laboratory", "Library", "Market", "Mine", "Witch", "Adventurer"));
    
    protected ArrayList<String> game_cards = new ArrayList<String>();
    
    protected void pick_cards() {
	    Random randomizer = new Random();
	    
	    while (game_cards.size() != 10) {
		String chosen_card = "";
		do {
		    chosen_card = BASE_CARDS.get(randomizer.nextInt(BASE_CARDS.size()));
		} while (game_cards.contains(chosen_card));
		game_cards.add(chosen_card);
	    }
    }

    protected ArrayList<String> getGameCards() {
	return game_cards;
    }
}
