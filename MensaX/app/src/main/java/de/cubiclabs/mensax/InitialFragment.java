package de.cubiclabs.mensax;

import android.app.ActionBar;
import android.app.Fragment;
import android.view.View;
import android.view.ViewGroup;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import de.cubiclabs.mensax.util.Events;
import de.greenrobot.event.EventBus;

/**
 * Created by thimokluser on 6/3/15.
 */
@EFragment(R.layout.fragment_initial)
public class InitialFragment extends Fragment {

    @Bean
    protected CafeteriaManager mCafeteriaManager;

    @ViewById
    protected ViewGroup mErrorWrapper;

    @ViewById
    protected ViewGroup mLoadingWrapper;

    private enum ViewState {
        LOADING, ERROR, SUCCESS
    }

    @AfterViews
    protected void afterViewsInjected() {
        EventBus.getDefault().register(this);
        changeViewState(ViewState.LOADING);
        mCafeteriaManager.request();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void onEvent(Events.CafeteriasDownloadedEvent event) {
        int i=0;
    }

    public void onEvent(Events.CafeteriaDownloadFailedEvent event) {
        changeViewState(ViewState.ERROR);
    }

    public void onClick_reloadFromErrorState(View v) {
        changeViewState(ViewState.LOADING);
        mCafeteriaManager.request();
    }

    private void changeViewState(ViewState state) {
        switch (state) {
            case LOADING:
                mErrorWrapper.setVisibility(View.GONE);
                mLoadingWrapper.setVisibility(View.VISIBLE);
                break;
            case ERROR:
                mLoadingWrapper.setVisibility(View.GONE);
                mErrorWrapper.setVisibility(View.VISIBLE);
                break;
            case SUCCESS:
                mLoadingWrapper.setVisibility(View.GONE);
                mErrorWrapper.setVisibility(View.GONE);

        }
    }

}
