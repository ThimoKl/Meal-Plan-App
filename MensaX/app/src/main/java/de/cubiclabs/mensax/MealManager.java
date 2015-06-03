package de.cubiclabs.mensax;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.cubiclabs.mensax.models.Cafeteria;
import de.cubiclabs.mensax.models.Meal;
import de.cubiclabs.mensax.util.Events;
import de.cubiclabs.mensax.util.UrlProvider;
import de.cubiclabs.mensax.util.Preferences_;
import de.greenrobot.event.EventBus;

/**
 * Created by thimokluser on 6/2/15.
 */
@EBean
public class MealManager {

    @Bean
    protected UrlProvider mUrlProvider;

    @Pref
    protected Preferences_ mPreferences;

    private final OkHttpClient mClient = new OkHttpClient();

    private static final long CACHE_EXPIRATION = 1 * 24 * 60 * 60 * 1000;

    public void request(int cafeteriaId) {
        // Use cache first, but download anyways
        List<Meal> cache = fromCache(cafeteriaId);
        boolean didSendCache = false;
        if(cache != null && cache.size() != 0) {
            EventBus.getDefault().post(new Events.MealsDownloadedEvent(cafeteriaId, cache));
            didSendCache = true;
        }

        download(cafeteriaId, didSendCache);
    }

    @Background
    protected void download(int cafeteriaId, boolean didSendCache) {
        List<Meal> meals= new ArrayList<Meal>();
        String json = "";
        try {
            Request request = new Request.Builder()
                    .url(mUrlProvider.getMealUrl(cafeteriaId))
                    .build();

            Response response = mClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                if(!didSendCache) {
                    EventBus.getDefault().post(new Events.MealDownloadFailedEvent(cafeteriaId));
                }
                return;
            }

            json = response.body().string();

            Type listType = new TypeToken<ArrayList<Meal>>() {}.getType();
            Gson gson = new Gson();
            meals = (ArrayList<Meal>)gson.fromJson(json, listType);
        } catch (Exception e) {
            if(!didSendCache) {
                EventBus.getDefault().post(new Events.MealDownloadFailedEvent(cafeteriaId));
            }
            return;
        }

        if(meals == null || meals.size() == 0) {
            if(!didSendCache) {
                EventBus.getDefault().post(new Events.MealDownloadFailedEvent(cafeteriaId));
            }
            return;
        }

        updateCache(cafeteriaId, meals);
        EventBus.getDefault().post(new Events.MealsDownloadedEvent(cafeteriaId, meals));
    }

    private List<Meal> fromCache(int cafeteriaId) {
        List<Meal> meals = new ArrayList<Meal>();

        // Check date
        boolean validDate = false;
        long timestamp = 0;
        Set<String> mealDateCache = mPreferences.mealJsonCache().get();
        if(mealDateCache == null || mealDateCache.size() == 0) return meals;
        for(String entry : mealDateCache) {
            if(entry.startsWith(cafeteriaId + "=")) {
                String content = entry.substring(entry.indexOf("=")+1);
                try {
                    timestamp = Long.parseLong(content);
                } catch(Exception e) {
                }

                if(((new Date()).getTime() - timestamp) <= CACHE_EXPIRATION) validDate = true;

                break;
            }
        }

        if(!validDate) return meals;

        // Check json
        Set<String> mealJsonCache = mPreferences.mealJsonCache().get();
        if(mealJsonCache == null || mealJsonCache.size() == 0) return meals;
        for(String entry : mealJsonCache) {
            if(entry.startsWith(cafeteriaId + "=")) {
                String json = entry.substring(entry.indexOf("=")+1);

                try {
                    Type listType = new TypeToken<ArrayList<Meal>>() {}.getType();
                    Gson gson = new Gson();
                    meals = (ArrayList<Meal>)gson.fromJson(json, listType);
                } catch(Exception e) {
                }
                break;
            }
        }

        return meals;
    }

    private void updateCache(int cafeteriaId, List<Meal> meals) {
        // Remove possible old entry
        Set<String> mealDateCache = mPreferences.mealDateCache().get();
        Set<String> mealJsonCache = mPreferences.mealJsonCache().get();

        Iterator<String> iterator = mealDateCache.iterator();
        while(iterator.hasNext()) {
            String entry = iterator.next();
            if(entry.startsWith(cafeteriaId + "=")) {
                iterator.remove();
                break;
            }
        }

        iterator = mealJsonCache.iterator();
        while(iterator.hasNext()) {
            String entry = iterator.next();
            if(entry.startsWith(cafeteriaId + "=")) {
                iterator.remove();
                break;
            }
        }

        // Add new entry
        Gson gson = new Gson();
        mealDateCache.add(cafeteriaId + "=" + String.valueOf((new Date()).getTime()));
        mealJsonCache.add(cafeteriaId + "=" + gson.toJson(meals));

        mPreferences.edit()
                .mealDateCache()
                .put(mealDateCache)
                .mealJsonCache()
                .put(mealJsonCache)
                .apply();

    }
}
