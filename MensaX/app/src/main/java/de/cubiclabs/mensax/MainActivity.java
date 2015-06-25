package de.cubiclabs.mensax;

import android.app.FragmentManager;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.support.v4.widget.DrawerLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

import de.cubiclabs.mensax.util.Events;
import de.cubiclabs.mensax.views.FadeInSlideShow;
import de.greenrobot.event.EventBus;

@EActivity(R.layout.activity_main)
public class MainActivity extends ActionBarActivity {

    @Bean
    protected CafeteriaManager mCafeteriaManager;

    @FragmentById(R.id.navigation_drawer)
    protected NavigationDrawerFragment mNavigationDrawerFragment;

    private CharSequence mTitle;

    @ViewById(R.id.drawer_layout)
    protected DrawerLayout mNavDrawerLayout;

    @InstanceState
    protected boolean mHasSavedInstanceState = false;

    protected FadeInSlideShow mSlideShow;




    @AfterViews
    protected void afterViewsInjected() {
        EventBus.getDefault().register(this);
        mTitle = getTitle();
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));


        // Show initial fragment
        if(!mHasSavedInstanceState) {
            InitialFragment initialFragment = InitialFragment_.builder()
                    .build();

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, initialFragment)
                    .commit();

            if(mNavDrawerLayout != null) {
                mNavDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }
        }

        mHasSavedInstanceState = true;

        mSlideShow = new FadeInSlideShow(mNavDrawerLayout);
        mSlideShow.start();

        //mNavDrawerLayout.setBackgroundResource(R.drawable.background_slideshow);
        //mBackgroundSlideshow = (TransitionDrawable) mNavDrawerLayout.getBackground();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_drawer);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        mSlideShow.stop();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void onEventMainThread(Events.CafeteriasDownloadedEvent event) {
        // Only one cafeteria -> Disable navigation drawer
        DrawerLayout navDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(navDrawerLayout != null) {
            if (event.mCafeterias.size() == 1) {
                navDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            } else {
                navDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }
        }
    }

    public void onEventMainThread(Events.CafeteriaDownloadFailedEvent event) {

    }

    public void onEvent(Events.CafeteriaSelected event) {
        CafeteriaFragment cafeteriaFragment = CafeteriaFragment_.builder()
                .mCafeteriaId(event.mCafeteria.id)
                .mCafeteriaName(event.mCafeteria.name)
                .mCafeteriaRatingUid(event.mCafeteria.ratingUid)
                .build();

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, cafeteriaFragment)
                .commit();


        mTitle = event.mCafeteria.name;
        restoreActionBar();
    }
}

