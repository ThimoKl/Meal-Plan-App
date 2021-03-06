package de.cubiclabs.mensax;


import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.android.gms.analytics.HitBuilders;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.cubiclabs.mensax.models.Day;
import de.cubiclabs.mensax.models.Meal;
import de.cubiclabs.mensax.util.Events;
import de.cubiclabs.mensax.util.Preferences_;
import de.cubiclabs.mensax.views.EmptyCard;
import de.cubiclabs.mensax.views.MealCard;
import de.greenrobot.event.EventBus;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.prototypes.CardSection;
import it.gmariotti.cardslib.library.prototypes.SectionedCardAdapter;
import it.gmariotti.cardslib.library.view.CardListView;

@EFragment(R.layout.fragment_cafeteria)
public class CafeteriaFragment extends Fragment {

    @Bean
    protected MealManager mMealManager;

    @FragmentArg
    protected int mCafeteriaId;

    @FragmentArg
    protected String mCafeteriaName;

    @FragmentArg
    protected String mCafeteriaRatingUid;

    @ViewById
    protected ViewGroup mErrorWrapper;

    @ViewById
    protected ViewGroup mLoadingWrapper;

    @ViewById
    protected ViewGroup mContentWrapper;

    @Pref
    protected Preferences_ mPreferences;

    MyApplication mApplication;

    protected boolean mShowAds;

    private List<Meal> mMeals;

    private enum ViewState {
        LOADING, ERROR, SUCCESS
    }

    @AfterViews
    protected void afterViewsInjected() {
        EventBus.getDefault().register(this);
        mApplication = (MyApplication)getActivity().getApplication();
        load();
    }

    @Click(R.id.mErrorWrapper)
    public void onErrorMessageClicked(View v) {
        load();
    }

    private void load() {
        changeViewState(ViewState.LOADING);
        mMealManager.request(mCafeteriaId, mCafeteriaRatingUid, mApplication);
    }

    public void onEventMainThread(Events.MealDownloadFailedEvent event) {
        changeViewState(ViewState.ERROR);
    }

    public void onEventMainThread(Events.MealsDownloadedEvent event) {
        changeViewState(ViewState.SUCCESS);
        showMeals(event.mDays);
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
    public void onResume() {

        int appOpenedCounter = mPreferences.appOpenedCounter().get();
        appOpenedCounter++;
        if(appOpenedCounter <= 4) {
            mShowAds = false;
            mPreferences.edit().appOpenedCounter().put(appOpenedCounter).apply();
            super.onResume();
            return;
        }
        mShowAds = true;

        ViewGroup adWrapper = (ViewGroup) getView().findViewById(R.id.adWrapper);
        if (adWrapper.getChildCount() > 0) {
            adWrapper.removeAllViews();
        }

        final PublisherAdView adView = new PublisherAdView(getActivity());//(PublisherAdView) view.findViewById(R.id.adView);
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        adView.setAdUnitId(mPreferences.adUnitIdBottom().get());
        //adView.setAdUnitId(getString(R.string.ad_unit_id_inline));
        adView.setAdSizes(AdSize.MEDIUM_RECTANGLE, AdSize.BANNER, AdSize.FULL_BANNER, AdSize.LARGE_BANNER, AdSize.LEADERBOARD);
        adView.setVisibility(View.GONE);

        adWrapper.addView(adView);




        /*
        final AdView adView = (AdView) getView().findViewById(R.id.adView);
        adView.setVisibility(View.GONE);
        AdRequest adRequest = new AdRequest.Builder().build();*/
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                try {
                    adView.setVisibility(View.VISIBLE);
                    mApplication.mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Ads")
                            .setAction(getString(R.string.app_name))
                            .setLabel("filled")
                            .setValue(1)
                            .build());
                } catch (Exception e) {

                }
                super.onAdLoaded();
            }
        });

        mApplication.mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Ads")
                .setAction(getString(R.string.app_name))
                .setLabel("requested")
                .setValue(1)
                .build());

        super.onResume();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        mMealManager.close();
        super.onDestroyView();
    }

    private void showMeals(List<Day> days) {
        List<Card> cards = new ArrayList<Card>();
        for(Day day : days) {
            if(day.mMeals.size() == 0) {
                cards.add(new EmptyCard(getActivity().getBaseContext()));
            }
            for (Meal meal : day.mMeals) {
                Card card = new MealCard(getActivity().getBaseContext(), meal, mCafeteriaId, mCafeteriaRatingUid);
                cards.add(card);
            }
        }

        CardArrayAdapter cardArrayAdapter = new CardArrayAdapter(getActivity(), cards);

        // Sections code.
        // Add the card sections
        List<CustomCardSection> sections =
                new ArrayList<CustomCardSection>();

        int itemCountTillSelectedDate = 0;
        boolean todayFound = false;
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String nowFormatted = format.format(now);

        int mealCount = 0;
        for(Day day : days) {
            sections.add(new CustomCardSection(mealCount, day.getFormattedDate()));

            if(!todayFound && day.mDatum.equals(nowFormatted)) {
                todayFound = true;
            }

            if(day.mMeals.size() == 0) {
                mealCount++;
                if(!todayFound) itemCountTillSelectedDate++;
            }

            for (Meal meal : day.mMeals) {
                mealCount++;
                if(!todayFound) itemCountTillSelectedDate++;

            }

            if(!todayFound) itemCountTillSelectedDate++;
        }

        CustomCardSection[] dummy = new CustomCardSection[sections.size()];

        //Sectioned adapter
        CustomSectionedAdapter sectionAdapter = new CustomSectionedAdapter(getActivity(),
                cardArrayAdapter);
        sectionAdapter.setCardSections(sections.toArray(dummy));

        CardListView listView = (CardListView) getActivity().findViewById(R.id.carddemo_list_expand);
        if (listView!=null){
            listView.setExternalAdapter(sectionAdapter, cardArrayAdapter);

            if(listView.getFooterViewsCount() == 0) {
                View footerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.cafeteria_list_view_footer, null, false);
                TextView txtSource = (TextView)footerView.findViewById(R.id.source);
                String source = mPreferences.source().get();
                txtSource.setText(source);
                listView.addFooterView(footerView);
            }
        }

        if(todayFound) {
            listView.setSelection(itemCountTillSelectedDate);
        }
    }

    /**
     * Sectioned adapter
     */
    public class CustomSectionedAdapter extends SectionedCardAdapter {

        public CustomSectionedAdapter(Context context, CardArrayAdapter cardArrayAdapter) {
            super(context, R.layout.card_section_layout,cardArrayAdapter);
        }

        @Override
        protected View getSectionView(int position, View view, ViewGroup parent) {
            CustomCardSection section = (CustomCardSection) getCardSections().get(position);

            if (section != null ) {
                //Set the title
                TextView title = (TextView) view.findViewById(R.id.carddemo_section_gplay_title);
                if (title != null)
                    title.setText(section.getTitle());


                if(mShowAds) {

                    ViewGroup adWrapper = (ViewGroup) view.findViewById(R.id.adWrapper);
                    if (adWrapper.getChildCount() > 0) {
                        adWrapper.removeAllViews();
                    }

                    final PublisherAdView adView = new PublisherAdView(getActivity());//(PublisherAdView) view.findViewById(R.id.adView);
                    PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
                    adView.setAdUnitId(mPreferences.adUnitIdInline().get());
                    //adView.setAdUnitId(getString(R.string.ad_unit_id_inline));
                    adView.setAdSizes(AdSize.MEDIUM_RECTANGLE, AdSize.BANNER, AdSize.FULL_BANNER, AdSize.LARGE_BANNER, AdSize.LEADERBOARD);
                    adView.setVisibility(View.GONE);

                    adWrapper.addView(adView);
                    adView.loadAd(adRequest);

                    adView.setAdListener(new AdListener() {
                        @Override
                        public void onAdLoaded() {
                            try {
                                adView.setVisibility(View.VISIBLE);

                                mApplication.mTracker.send(new HitBuilders.EventBuilder()
                                        .setCategory("Ads")
                                        .setAction(getString(R.string.app_name))
                                        .setLabel("filled")
                                        .setValue(1)
                                        .build());
                            } catch (Exception e) {

                            }
                            super.onAdLoaded();
                        }
                    });

                    mApplication.mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Ads")
                            .setAction(getString(R.string.app_name))
                            .setLabel("requested")
                            .setValue(1)
                            .build());
                }

            }

            return view;
        }
    }

    public class CustomCardSection extends CardSection {
        public CustomCardSection(int firstPosition, CharSequence title) {
            super(firstPosition, title);
        }
    }
}
