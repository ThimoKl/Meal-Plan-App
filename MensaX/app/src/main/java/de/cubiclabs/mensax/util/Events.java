package de.cubiclabs.mensax.util;

import java.util.List;

import de.cubiclabs.mensax.models.Cafeteria;
import de.cubiclabs.mensax.models.Meal;

/**
 * Created by thimokluser on 6/2/15.
 */
public class Events {

    public static class CafeteriasDownloadedEvent {
        public List<Cafeteria> mCafeterias;
        public CafeteriasDownloadedEvent(List<Cafeteria> cafeterias) {
            mCafeterias = cafeterias;
        }
    }

    public static class CafeteriaDownloadFailedEvent {

    }

    public static class CafeteriaSelected {
        public Cafeteria mCafeteria;

        public CafeteriaSelected(Cafeteria cafeteria) {
            mCafeteria = cafeteria;
        }
    }

    public static class MealsDownloadedEvent {
        public List<Meal> mMeals;
        public int mCafeteriaId;
        public MealsDownloadedEvent(int cafeteriaId, List<Meal> meals) {
            mCafeteriaId = cafeteriaId;
            mMeals = meals;
        }
    }

    public static class MealDownloadFailedEvent {
        public int mCafeteriaId;
        public MealDownloadFailedEvent(int cafeteriaId) {
            mCafeteriaId = cafeteriaId;
        }
    }
}
