package de.cubiclabs.mensax.util;

import java.util.List;

import de.cubiclabs.mensax.models.Cafeteria;
import de.cubiclabs.mensax.models.Day;

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
        public List<Day> mDays;
        public int mCafeteriaId;
        public MealsDownloadedEvent(int cafeteriaId, List<Day> days) {
            mCafeteriaId = cafeteriaId;
            mDays = days;
        }
    }

    public static class MealDownloadFailedEvent {
        public int mCafeteriaId;
        public MealDownloadFailedEvent(int cafeteriaId) {
            mCafeteriaId = cafeteriaId;
        }
    }

    public static class RatingsDownloadedEvent {
        public List<Day> mDays;
        public int mCafeteriaId;

        public RatingsDownloadedEvent(int cafeteriaId, List<Day> days) {
            mCafeteriaId = cafeteriaId;
            mDays = days;
        }
    }

    public static class RatingsDownloadFailedEvent {
        public int mCafeteriaId;

        public RatingsDownloadFailedEvent(int cafeteriaId) {
            mCafeteriaId = cafeteriaId;
        }
    }
}
