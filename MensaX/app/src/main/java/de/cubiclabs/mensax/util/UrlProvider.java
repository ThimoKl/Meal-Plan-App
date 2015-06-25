package de.cubiclabs.mensax.util;

import org.androidannotations.annotations.EBean;

import de.cubiclabs.mensax.Config;

/**
 * Created by thimokluser on 6/2/15.
 */
@EBean(scope = EBean.Scope.Singleton)
public class UrlProvider {

    private final static String BASE_URL = "http://android.studicluster.com/";

    public String getCafeteriaListUrl() {
        return BASE_URL + Config.CAFETERIA_URL_NAME + "/cafeterias.json";
    }

    public String getMealsUrl(int cafeteriaId) {
        return BASE_URL + Config.CAFETERIA_URL_NAME + "/api.php?action=getMeals&cid=" + cafeteriaId;
        //return BASE_URL + Config.CAFETERIA_URL_NAME + "/" + cafeteriaId + ".json";
    }
}
