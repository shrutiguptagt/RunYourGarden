package edu.gatech.garden;

import java.util.ArrayList;

public class Garden {

    public static final int[] CalorieList = { 5, 10, 20, 25, 55, 80, 100, 200,
            250, 5 };

    // references to our images
    public static Integer[] mThumbIds = { R.drawable.sample_1,
            R.drawable.sample_2, R.drawable.sample_3, R.drawable.sample_4,
            R.drawable.sample_5, R.drawable.sample_6, R.drawable.sample_7,
            R.drawable.sample_8, R.drawable.sample_9, R.drawable.sample_10 };

    // public static Inventory inventory = null;
    public static ArrayList<Plant> plantsList = null;

    public static ArrayList<Plant> Inventory = null;

    public static void initGarden() {
        if (plantsList != null) {
            return;
        }
        plantsList = new ArrayList<Plant>();
        int length = mThumbIds.length;
        for (int i = 0; i < length; ++i) {
            plantsList.add(new Plant(mThumbIds[i], CalorieList[i]));
        }
    }

    public static void initInventory() {
        if (Inventory != null) {
            return;
        }
        Inventory = new ArrayList<Plant>();
    }

    public static class Plant {

        public int drawableId;
        public int calorie;
        public int amount;
        public boolean isInInventory;

        public Plant(int drawableId, int calorie) {
            this.drawableId = drawableId;
            this.calorie = calorie;
            amount = 0;
            isInInventory = false;
        }

        public void increaseAmount() {
            ++amount;
            if (amount > 5) {
                amount = 0;
            }
        }
    }
}
