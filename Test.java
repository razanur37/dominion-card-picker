// Copyright (C) 2015 by Casey English

import dominion.Dominion;

import java.util.ArrayList;

/**
 * Created by Casey on 20 Feb 2015.
 */
public class Test {
    public static final int NUM_TRIALS = 10000;

    private static class cardCount {
        private String card;
        private int count;

        public cardCount(String card) {
            this.card = card;
            count = 0;
        }

        private void upCount() {
            ++count;
        }

        public String getCard() {
            return card;
        }

        public int getCount() {
            return count;
        }
    }

    public static void main(String[] args) {
        test("BASE");
        test("INTRIGUE");
        test("SEASIDE");
    }

    private static void test(String setName) {
        Dominion dominion = new Dominion(setName);
        ArrayList<cardCount> setCount = new ArrayList<cardCount>();
        for (String card : dominion.getCardPool()) {
            setCount.add(new cardCount(card));
        }

        for (int i=0; i<NUM_TRIALS; ++i) {
            dominion.setup();

            for (String card : dominion.getGameCardNames()) {
                for (cardCount count : setCount) {
                    if (count.getCard() == card)
                        count.upCount();
                }
            }
        }

        results(setCount);
    }

    private static void results(ArrayList<cardCount> setCount) {
        int count = 0;
        double expectedMean = (10*NUM_TRIALS)/setCount.size();
        double outliers = 0;

        for (cardCount card : setCount) {
            count += card.getCount();
            if (card.getCount() < expectedMean*.95 || card.getCount() > expectedMean*1.05) {
                outliers += 1;
            }
        }

        double actualMean = count/setCount.size();

        try {
            assert expectedMean == actualMean : "Expected and Actual means not equal";
            assert (outliers / setCount.size()) < 0.05 : "Too many outliers";
        } catch (AssertionError e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

}
