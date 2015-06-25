package de.cubiclabs.mensax.database;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by thimokluser on 6/23/15.
 */
@Table(name = "RatedMeals")
public class RatedMeal extends Model {

    @Column(name = "mealUid", index = true)
    public String mMealUid;

    @Column(name = "cafeteriaId", index = true)
    public int mCafeteriaId;

    @Column(name = "starsRated")
    public int mStarsRated;


    @Column(name = "createdAt", index = true)
    public Date mDate;

    public RatedMeal() {
        super();
    }

    public RatedMeal(String uid, int cafeteriaId, int starsRated, Date date) {
        this(uid, cafeteriaId, starsRated);
    }

    public RatedMeal(String uid, int cafeteriaId, int starsRated) {
        super();
        mMealUid = uid;
        mCafeteriaId = cafeteriaId;
        mStarsRated = starsRated;
        mDate = new Date();
    }

    /**
     * Deletes all database SwipedDeal entries that are older than 1 month
     */
    public static void removeOldDbEntries() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);

        new Delete().from(RatedMeal.class).where("createdAt <= ?", cal.getTimeInMillis()).execute();
    }

    public static RatedMeal get(String uid, int cafeteriaId) {
        RatedMeal rm = new Select()
                .from(RatedMeal.class)
                .where("mealUid = ?", uid).and("cafeteriaId = ?", cafeteriaId)
                .executeSingle();
        return rm;
    }
}
