package de.cubiclabs.mensax;


import android.app.Fragment;
import android.view.View;
import android.view.ViewGroup;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import de.cubiclabs.mensax.models.Meal;
import de.cubiclabs.mensax.util.Events;
import de.greenrobot.event.EventBus;

@EFragment(R.layout.fragment_cafeteria)
public class CafeteriaFragment extends Fragment {

    @Bean
    protected MealManager mMealManager;

    @FragmentArg
    protected int mCafeteriaId;

    @FragmentArg
    protected String mCafeteriaName;

    @ViewById
    protected ViewGroup mErrorWrapper;

    @ViewById
    protected ViewGroup mLoadingWrapper;

    @ViewById
    protected ViewGroup mContentWrapper;

    private List<Meal> mMeals;

    private enum ViewState {
        LOADING, ERROR, SUCCESS
    }

    @AfterViews
    protected void afterViewsInjected() {
        EventBus.getDefault().register(this);
        load();
    }

    @Click(R.id.mErrorWrapper)
    public void onErrorMessageClicked(View v) {
        load();
    }

    private void load() {
        changeViewState(ViewState.LOADING);
        mMealManager.request(mCafeteriaId);
    }

    public void onEventMainThread(Events.MealDownloadFailedEvent event) {
        changeViewState(ViewState.ERROR);
    }

    public void onEventMainThread(Events.MealsDownloadedEvent event) {
        changeViewState(ViewState.SUCCESS);
    }

    private void changeViewState(ViewState state) {
        switch (state) {
            case LOADING:
                mContentWrapper.setVisibility(View.GONE);
                mErrorWrapper.setVisibility(View.GONE);
                mLoadingWrapper.setVisibility(View.VISIBLE);
                break;
            case ERROR:
                mContentWrapper.setVisibility(View.GONE);
                mLoadingWrapper.setVisibility(View.GONE);
                mErrorWrapper.setVisibility(View.VISIBLE);
                break;
            case SUCCESS:
                mLoadingWrapper.setVisibility(View.GONE);
                mErrorWrapper.setVisibility(View.GONE);
                mContentWrapper.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }
}
