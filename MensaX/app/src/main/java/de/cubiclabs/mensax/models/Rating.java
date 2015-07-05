package de.cubiclabs.mensax.models;

import de.cubiclabs.mensax.database.RatedMeal;

/**
 * Created by thimokluser on 6/23/15.
 */
public class Rating {
    public String count = "0";
    public String rating = "0";
    public String stars = "0";

    public transient RatedMeal mMyRateInformation = null;

    public void addToCount(int number) {
        try {
            count = String.valueOf(Integer.parseInt(count) + number);
        } catch(Exception e) {

        }
    }

    public void addToRating(int number) {
        try {
            rating = String.valueOf(Integer.parseInt(rating) + number);
        } catch(Exception e) {

        }
    }

    public void addToStars(int number) {
        try {
            stars = String.valueOf(Integer.parseInt(stars) + number);
        } catch(Exception e) {

        }
    }

    public int getCount() {
        try {
            return Integer.parseInt(count);
        } catch(Exception e) {
            return 0;
        }
    }

    public int getRating() {
        try {
            return Integer.parseInt(rating);
        } catch(Exception e) {
            return 0;
        }
    }

    public int getStars() {
        try {
            return Integer.parseInt(stars);
        } catch(Exception e) {
            return 0;
        }
    }

    public int calcStars() {
        try {
            int newStars = (Integer.parseInt(rating) / Integer.parseInt(count));
            stars = String.valueOf(newStars);
            return newStars;
        } catch(Exception e) {
            return 0;
        }
    }


}
