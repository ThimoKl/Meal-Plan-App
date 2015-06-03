package de.cubiclabs.mensax.util;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultLong;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

import java.util.Set;

/**
 * Created by thimokluser on 5/10/15.
 */
@SharedPref
public interface Preferences {

    @DefaultBoolean(false)
    boolean userLearnedDrawer();

    @DefaultLong(0)
    long timestampLastCafeteriaUpdate();

    @DefaultString("")
    String cafeteriasJson();

    Set<String> mealJsonCache();
    Set<String> mealDateCache();

}
