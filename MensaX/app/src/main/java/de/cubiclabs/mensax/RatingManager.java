package de.cubiclabs.mensax;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.api.rest.MediaType;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.cubiclabs.mensax.database.RatedMeal;
import de.cubiclabs.mensax.models.Day;
import de.cubiclabs.mensax.models.Meal;
import de.cubiclabs.mensax.models.Rating;
import de.cubiclabs.mensax.util.Events;
import de.greenrobot.event.EventBus;

/**
 * Created by thimokluser on 6/23/15.
 */
@EBean
public class RatingManager {

    private final OkHttpClient mClient = new OkHttpClient();

    @Background
    public void getRatings(int cafeteriaId, String cafeteriaRatingUid, List<Day> days) {
        RatedMeal.removeOldDbEntries();
        if(cafeteriaRatingUid.equals("0")) {
            EventBus.getDefault().post(new Events.RatingsDownloadFailedEvent(cafeteriaId));
            return;
        }

        String requestUrl = "http://api.meinemensa.esined.net/json/ratings/get/" + cafeteriaRatingUid;
        List<Rating> ratings = new ArrayList<Rating>();
        int amountOfMeals = 0;
        try {
            String body = "";
            for(Day day : days) {
                for(Meal meal : day.mMeals) {
                    body += "meals%5B%5D=" + meal.uid + "&";
                    amountOfMeals++;
                }
            }

            Request request = new Request.Builder()
                    .url(requestUrl)
                    .post(
                            RequestBody.create(
                                    com.squareup.okhttp.MediaType.parse("application/x-www-form-urlencoded"),
                                    body))
                    .build();

            Response response = mClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                EventBus.getDefault().post(new Events.RatingsDownloadFailedEvent(cafeteriaId));
                return;
            }

            Type listType = new TypeToken<RatingsWrapper>() {}.getType();
            Gson gson = new Gson();
            RatingsWrapper wrapper = (RatingsWrapper)gson.fromJson(response.body().string(), listType);

            if(wrapper == null || wrapper.rating == null || wrapper.rating.size() != amountOfMeals) {
                throw new Exception("Response ratings don't match the expected results");
            }

            ratings = wrapper.rating;

            // Merge
            int ratingIndex = 0;
            for(int i=0; i<days.size(); i++) {
                Day day = days.get(i);
                for(int j=0; j<day.mMeals.size(); j++) {
                    Rating rating = ratings.get(ratingIndex++);
                    rating.mMyRateInformation = RatedMeal.get(days.get(i).mMeals.get(j).uid, cafeteriaId);
                    days.get(i).mMeals.get(j).rating = rating;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            EventBus.getDefault().post(new Events.RatingsDownloadFailedEvent(cafeteriaId));
            return;
        }

        EventBus.getDefault().post(new Events.RatingsDownloadedEvent(cafeteriaId, days));
    }

    private class RatingsWrapper {
        List<Rating> rating;
    }

    public static RatedMeal sendRating(final String mealUid, final String cafeteriaRatingUid, final int cafeteriaId, final int rating) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    String url = "http://api.meinemensa.esined.net/json/ratings/set/" +
                            URLEncoder.encode(cafeteriaRatingUid, "UTF-8") +
                            "/" +
                            URLEncoder.encode(mealUid, "UTF-8") +
                            "/" +
                            String.valueOf(rating);

                    Request request = new Request.Builder()
                            .url(url)
                            .build();

                    OkHttpClient client = new OkHttpClient();
                    client.newCall(request).execute();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

        RatedMeal ratedMeal = new RatedMeal(mealUid, cafeteriaId, rating);
        ratedMeal.save();
        return ratedMeal;
    }
}
