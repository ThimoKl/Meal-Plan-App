package de.cubiclabs.mensax;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.List;

import de.cubiclabs.mensax.models.Cafeteria;
import de.cubiclabs.mensax.util.Events;
import de.cubiclabs.mensax.util.Preferences_;
import de.greenrobot.event.EventBus;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
@EFragment(R.layout.fragment_navigation_drawer)
public class NavigationDrawerFragment extends Fragment {

    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    //private ListView mDrawerListView;
    private View mFragmentContainerView;

    @InstanceState
    protected boolean mUserLearnedDrawer;

    @Pref
    protected Preferences_ mPreferences;

    @InstanceState
    protected int mCurrentSelectedPosition = 0;

    @Bean
    protected CafeteriaManager mCafeteriaManager;

    protected List<Cafeteria> mCafeterias;

    @ViewById
    protected ViewGroup mErrorWrapper;

    @ViewById
    protected ViewGroup mLoadingWrapper;

    @ViewById
    protected ListView mListView;

    private enum ViewState {
        LOADING, ERROR, SUCCESS
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @AfterViews
    void afterViewsInjected() {
        EventBus.getDefault().register(this);
        mCafeteriaManager.request();
        changeViewState(ViewState.LOADING);
    }

    public void onEventMainThread(Events.CafeteriasDownloadedEvent event) {
        changeViewState(ViewState.SUCCESS);
        mCafeterias = event.mCafeterias;

        initListView();
    }

    public void onEventMainThread(Events.CafeteriaDownloadFailedEvent event) {
        changeViewState(ViewState.ERROR);
    }

    private void changeViewState(ViewState state) {
        switch (state) {
            case LOADING:
                mListView.setVisibility(View.GONE);
                mErrorWrapper.setVisibility(View.GONE);
                mLoadingWrapper.setVisibility(View.VISIBLE);
                break;
            case ERROR:
                mListView.setVisibility(View.GONE);
                mLoadingWrapper.setVisibility(View.GONE);
                mErrorWrapper.setVisibility(View.VISIBLE);
                break;
            case SUCCESS:
                mLoadingWrapper.setVisibility(View.GONE);
                mErrorWrapper.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);

        }
    }

    private void initListView() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == mCafeterias.size()) {
                    launchMarket();
                    // "Rate app" shall not be the active item. Use the current cafeteria
                    if (mListView != null) {
                        mListView.setItemChecked(mCurrentSelectedPosition, true);
                    }
                } else if(position == mCafeterias.size() + 1) {
                    reportIssue();
                    // "Rate app" shall not be the active item. Use the current cafeteria
                    if (mListView != null) {
                        mListView.setItemChecked(mCurrentSelectedPosition, true);
                    }
                } else {
                    selectItem(position);
                }
            }
        });

        String[] cafTitles = new String[mCafeterias.size()];
        for(int i=0; i<mCafeterias.size(); i++) {
            cafTitles[i] = mCafeterias.get(i).name;
        }

        mListView.setAdapter(new NavigationDrawerAdapter(
                getActionBar().getThemedContext(),
                R.layout.navdrawer_list_item,
                mCafeterias));
        mListView.setItemChecked(mCurrentSelectedPosition, true);
        EventBus.getDefault().post(new Events.CafeteriaSelected(mCafeterias.get(mCurrentSelectedPosition)));
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:cafId of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        mUserLearnedDrawer = mPreferences.userLearnedDrawer().get();

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }


                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    mPreferences.edit()
                            .userLearnedDrawer()
                            .put(true)
                            .apply();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        if (!mUserLearnedDrawer) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mListView != null) {
            mListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        EventBus.getDefault().post(new Events.CafeteriaSelected(mCafeterias.get(position)));
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), getActivity().getString(R.string.error_open_play_store), Toast.LENGTH_LONG).show();
        }
    }

    private void reportIssue() {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","mensaspeiseplan@gmail.com", null));
        intent.putExtra(Intent.EXTRA_EMAIL, "");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Problem: " + getString(R.string.app_name));
        intent.putExtra(Intent.EXTRA_TEXT, "");

        startActivity(Intent.createChooser(intent, getString(R.string.report_issue)));
    }

}
