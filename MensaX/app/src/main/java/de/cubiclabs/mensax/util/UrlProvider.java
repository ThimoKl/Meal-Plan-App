package de.cubiclabs.mensax.util;

import android.content.Context;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import de.cubiclabs.mensax.R;

/**
 * Created by thimokluser on 6/2/15.
 */
@EBean(scope = EBean.Scope.Singleton)
public class UrlProvider {

    private final static String BASE_URL = "http://android.studicluster.com/";

    @RootContext
    Context mContext;

    public String getCafeteriaListUrl() {
        return BASE_URL + mContext.getResources().getString(R.string.url_city) + "/cafeterias.json";
    }

    public String getMealsUrl(int cafeteriaId) {
        return BASE_URL + mContext.getResources().getString(R.string.url_city) + "/api.php?action=getMeals&cid=" + cafeteriaId;
        //return BASE_URL + Config.CAFETERIA_URL_NAME + "/" + cafeteriaId + ".json";
    }
}
