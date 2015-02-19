// Copyright (C) 2015 by Casey English

import dominion.Dominion;

import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        Dominion dominion = new Dominion("BASE, INTRIGUE");

        dominion.setup();

        dominion.display();
    }
}
