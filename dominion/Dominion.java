// Copyright (C) 2015 by Casey English

package dominion;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;;

public class Dominion {

    public void setup() {
	
	Cards cards = new Cards();
	
	cards.pick_cards();

	System.out.println(cards.getGameCards());
    }
}
