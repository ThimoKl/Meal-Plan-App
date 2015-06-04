package de.cubiclabs.mensax.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.cubiclabs.mensax.R;
import de.cubiclabs.mensax.models.Meal;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.ViewToClickToExpand;

/**
 * Created by thimokluser on 6/4/15.
 */
public class EmptyCard extends Card {

    public EmptyCard(Context context) {
        super(context, R.layout.empty_card);
    }

    public EmptyCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }

}
