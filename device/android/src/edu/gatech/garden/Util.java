package edu.gatech.garden;

import java.text.DecimalFormat;

public class Util {

    public final static double MILE_TO_KILOMETER = 1.609344;
    public final static double POUND_TO_KILOGRAM = 0.45359237;
    public final static DecimalFormat DoubleFormat = new DecimalFormat("#.##");

    public static int getCaloriesByDistanceMilePound(double distance,
            double weight) {
        return getCaloriesByDistance(distance * MILE_TO_KILOMETER, weight
                * POUND_TO_KILOGRAM);
    }

    public static int getCaloriesByDistance(double distance, double weight) {
        // calories = kilometers x kilograms x 1.036
        return (int) Math.round(distance * weight * 1.036);
    }

    public static double getDistanceByCaloriesMilePound(int calories,
            double weight) {
        return getDistanceByCalories(calories, weight / POUND_TO_KILOGRAM)
                / MILE_TO_KILOMETER;
    }

    public static double getDistanceByCalories(int calories, double weight) {
        return (calories / 1.036 / weight);
    }

}
