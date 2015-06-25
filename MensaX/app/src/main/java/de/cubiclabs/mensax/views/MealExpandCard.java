/*
 * ******************************************************************************
 *   Copyright (c) 2013-2014 Gabriele Mariotti.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *  *****************************************************************************
 */

package de.cubiclabs.mensax.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import de.cubiclabs.mensax.R;
import de.cubiclabs.mensax.RatingManager_;
import de.cubiclabs.mensax.models.Meal;
import it.gmariotti.cardslib.library.internal.CardExpand;

/**
 * This class provides an example of custom expand/collapse area.
 * It uses carddemo_example_inner_expand layout.
 * <p/>
 * You have to override the {@link #setupInnerViewElements(ViewGroup, View)});
 *
 * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
 */
public class MealExpandCard extends CardExpand {

    private Meal mMeal;
    private MealCard mMealCard;
    private int mCafeteriaId;
    private String mCafeteriaRatingUid;

    /*public CustomExpandCard(Context context) {
        super(context, R.layout.expandable_meal_card);
    }*/

    public MealExpandCard(Context context, Meal meal, MealCard card, int cafeteriaId, String cafeteriaRatingUid) {
        super(context, R.layout.expandable_meal_card);
        mMeal = meal;
        mMealCard = card;
        mCafeteriaId = cafeteriaId;
        mCafeteriaRatingUid = cafeteriaRatingUid;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, final View view) {

        if(view == null) return;
        if(mMeal == null) return;

        final RatingBar rating = (RatingBar)view.findViewById(R.id.rating);
        final Button btnRate = (Button)view.findViewById(R.id.btnRate);
        View txtMyRating = view.findViewById(R.id.txtMyRating);
        if(mMeal.rating == null) {
            rating.setVisibility(View.GONE);
            btnRate.setVisibility(View.GONE);
            btnRate.setEnabled(false);
            txtMyRating.setVisibility(View.GONE);
        }
        else {
            if(mMeal.rating.mMyRateInformation == null) {
                rating.setRating(0);
                rating.setIsIndicator(false);
                btnRate.setVisibility(View.VISIBLE);
                btnRate.setEnabled(true);
                txtMyRating.setVisibility(View.GONE);
            } else {
                rating.setRating(mMeal.rating.mMyRateInformation.mStarsRated);
                rating.setIsIndicator(true);
                btnRate.setVisibility(View.GONE);
                btnRate.setEnabled(false);
                txtMyRating.setVisibility(View.VISIBLE);
            }

        }

        btnRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRate.setEnabled(false);
                btnRate.setVisibility(View.GONE);
                rating.setIsIndicator(true);
                try {
                    mMeal.rating.addToCount(1);
                    mMeal.rating.addToRating((int) rating.getRating());
                    mMeal.rating.calcStars();
                    mMeal.rating.mMyRateInformation = RatingManager_.sendRating(mMeal.uid,
                            mCafeteriaRatingUid, mCafeteriaId, (int) rating.getRating());

                    if(mMealCard != null) {
                        mMealCard.updateRatingIndicator(mMeal.rating);
                    }

                } catch(Exception e) {

                }



            }
        });


    }

}
