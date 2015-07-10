package de.cubiclabs.mensax.util;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.DefaultLong;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

import java.util.Set;

/**
 * Created by thimokluser on 5/10/15.
 */
@SharedPref(value=SharedPref.Scope.UNIQUE)
public interface Preferences {

    @DefaultBoolean(false)
    boolean userLearnedDrawer();

    @DefaultLong(0)
    long timestampLastCafeteriaUpdate();

    @DefaultInt(0)
    int appOpenedCounter();

    @DefaultInt(0)
    int currentCafeteria();

    @DefaultString("")
    String cafeteriasJson();

    @DefaultString("")
    String source();

    @DefaultString("")
    String adUnitIdBottom();

    @DefaultString("")
    String adUnitIdInline();

    Set<String> mealJsonCache();
    Set<String> mealDateCache();

}
