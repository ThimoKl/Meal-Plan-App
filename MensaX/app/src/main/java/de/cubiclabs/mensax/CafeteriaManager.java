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
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.cubiclabs.mensax.models.Cafeteria;
import de.cubiclabs.mensax.util.Events;
import de.cubiclabs.mensax.util.UrlProvider;
import de.cubiclabs.mensax.util.Preferences_;
import de.greenrobot.event.EventBus;

/**
 * Created by thimokluser on 6/2/15.
 */
@EBean(scope = EBean.Scope.Singleton)
public class CafeteriaManager {

    @Bean
    protected UrlProvider mSettings;

    @Pref
    protected Preferences_ mPreferences;

    private final OkHttpClient mClient = new OkHttpClient();

    private static final long CACHE_EXPIRATION = 7 * 24 * 60 * 60 * 1000;

    private List<Cafeteria> mCafeterias = null;

    @AfterInject
    public void afterInject() {
    }

    public void request() {
        if(mCafeterias != null && mCafeterias.size() != 0) {
            sendDownloadCompletedEvent(mCafeterias);
        }

        if(cachedCafeteriasValid()) {
            mCafeterias = fromCache();
            sendDownloadCompletedEvent(mCafeterias);
        } else {
            download();
        }
    }

    private boolean cachedCafeteriasValid() {
        List<Cafeteria> list = fromCache();
        if(list != null && list.size() == 0) return false;

        long timestamp  = mPreferences.timestampLastCafeteriaUpdate().get();
        if((new Date()).getTime() - timestamp > CACHE_EXPIRATION) {
            return false;
        }

        return true;
    }

    @Background
    protected void download() {
        List<Cafeteria> cafeterias= new ArrayList<Cafeteria>();
        String json = "";
        try {
            Request request = new Request.Builder()
                    .url(mSettings.getCafeteriaListUrl())
                    .build();

            Response response = mClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                sendFailedEvent();
            }

            json = response.body().string();

            Type listType = new TypeToken<ArrayList<Cafeteria>>() {}.getType();
            Gson gson = new Gson();
            cafeterias = (ArrayList<Cafeteria>)gson.fromJson(json, listType);
        } catch (Exception e) {
            e.printStackTrace();
            // Download failed. Use cache, even if it's expired.
            cafeterias = fromCache();
        }

        if(cafeterias == null || cafeterias.size() == 0) {
            sendFailedEvent();
            return;
        }

        mPreferences.edit()
                .cafeteriasJson()
                .put(json)
                .apply();

        mPreferences.edit()
                .timestampLastCafeteriaUpdate()
                .put((new Date()).getTime())
                .apply();

        mCafeterias = cafeterias;
        sendDownloadCompletedEvent(cafeterias);
    }

    private List<Cafeteria> fromCache() {
        List<Cafeteria> list = new ArrayList<Cafeteria>();
        String json = mPreferences.cafeteriasJson().get();
        try {
            Type listType = new TypeToken<ArrayList<Cafeteria>>() {}.getType();
            Gson gson = new Gson();
            list = (ArrayList<Cafeteria>)gson.fromJson(json, listType);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @UiThread
    protected void sendFailedEvent() {
        EventBus.getDefault().post(new Events.CafeteriaDownloadFailedEvent());
    }

    protected void sendDownloadCompletedEvent(List<Cafeteria> list) {
        EventBus.getDefault().post(new Events.CafeteriasDownloadedEvent(list));
    }

}
