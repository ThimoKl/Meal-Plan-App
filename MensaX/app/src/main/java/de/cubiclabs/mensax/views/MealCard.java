package de.cubiclabs.mensax.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import de.cubiclabs.mensax.R;
import de.cubiclabs.mensax.models.Meal;
import de.cubiclabs.mensax.models.Rating;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.ViewToClickToExpand;

/**
 * Created by thimokluser on 6/4/15.
 */
public class MealCard extends Card {

    private Meal mMeal;
    private RatingBar mRatingIndicator;
    private int mCafeteriaId;
    private String mCafeteriaRatingUid;
    private ImageView mIvExpandIndicator;

    public MealCard(Context context, Meal meal, int cafeteriaId, String cafeteriaRatingUid) {
        super(context, R.layout.meal_card);
        mMeal = meal;
        mCafeteriaId = cafeteriaId;
        mCafeteriaRatingUid = cafeteriaRatingUid;
        initLayout();
    }

    public MealCard(Context context, int innerLayout, Meal meal) {
        super(context, innerLayout);
        mMeal = meal;
        initLayout();
    }

    private void initLayout() {
        MealExpandCard expand = new MealExpandCard(getContext(), mMeal, this, mCafeteriaId, mCafeteriaRatingUid);
        addCardExpand(expand);
    }

    public void updateRatingIndicator(Rating rating) {
        if(mRatingIndicator == null) return;
        mMeal.rating = rating;
        mRatingIndicator.setVisibility(View.VISIBLE);
        mRatingIndicator.setRating(mMeal.rating.calcStars());
        doCollapse();
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        TextView txtCategory = (TextView) view.findViewById(R.id.category);
        TextView txtName = (TextView) view.findViewById(R.id.name);
        TextView txtPrice = (TextView) view.findViewById(R.id.price);
        mIvExpandIndicator = (ImageView) view.findViewById(R.id.ivExpandIndicator);

        txtCategory.setText(mMeal.category);
        txtName.setText(mMeal.name);
        txtPrice.setText(mMeal.price.replaceAll("EUR", "€"));

        boolean showExpandCard = true;
        mRatingIndicator = (RatingBar)view.findViewById(R.id.ratingIndicator);
        if(mMeal.rating == null) {
            mRatingIndicator.setVisibility(View.GONE);
            showExpandCard = false;
        }
        else {
            mRatingIndicator.setVisibility(View.VISIBLE);
            mRatingIndicator.setRating(mMeal.rating.getStars());
                //mRatingIndicator.setVisibility(View.GONE);
                //showExpandCard = false;

            if(mMeal.rating.count.equals("0")) mRatingIndicator.setVisibility(View.GONE);
            //if(mMeal.rating.hasBeenRated) showExpandCard = false;
        }

        if(showExpandCard) {
            ViewToClickToExpand viewToClickToExpand =
                    ViewToClickToExpand.builder()
                            .setupView(view);
            setViewToClickToExpand(viewToClickToExpand);
        }

        mIvExpandIndicator.setVisibility(showExpandCard ? View.VISIBLE : View.INVISIBLE);

        this.setOnExpandAnimatorEndListener(new OnExpandAnimatorEndListener() {
            @Override
            public void onExpandEnd(Card card) {
                mIvExpandIndicator.setImageResource(R.drawable.ic_expand_less_grey600_24dp);
            }
        });

        this.setOnCollapseAnimatorEndListener(new OnCollapseAnimatorEndListener() {
            @Override
            public void onCollapseEnd(Card card) {
                mIvExpandIndicator.setImageResource(R.drawable.ic_expand_more_grey600_24dp);
            }
        });
    }
}
