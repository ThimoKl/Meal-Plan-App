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
import android.widget.TextView;

import de.cubiclabs.mensax.R;
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
public class CustomExpandCard extends CardExpand {

    private Meal mMeal;

    /*public CustomExpandCard(Context context) {
        super(context, R.layout.expandable_meal_card);
    }*/

    public CustomExpandCard(Context context, Meal meal) {
        super(context, R.layout.expandable_meal_card);
        mMeal = meal;
    }

    //You can set you properties here (example buttons visibility)

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        if (view == null) return;
        if(mMeal == null) return;

        //Retrieve TextView elements
        TextView tx1 = (TextView) view.findViewById(R.id.carddemo_expand_text1);
        TextView tx2 = (TextView) view.findViewById(R.id.carddemo_expand_text2);

        //Set value in text views
        if (tx1 != null) {
            tx1.setText("Likes: " + mMeal.like);
        }

        if (tx2 != null) {
            tx2.setText("Dislikes: " + mMeal.dislike);
        }
    }

}
