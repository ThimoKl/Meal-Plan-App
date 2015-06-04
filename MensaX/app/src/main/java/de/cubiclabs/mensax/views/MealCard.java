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
public class MealCard extends Card {

    private Meal mMeal;

    public MealCard(Context context, Meal meal) {
        super(context, R.layout.meal_card);
        mMeal = meal;
        initLayout();
    }

    public MealCard(Context context, int innerLayout, Meal meal) {
        super(context, innerLayout);
        mMeal = meal;
        initLayout();
    }

    private void initLayout() {

        CustomExpandCard expand = new CustomExpandCard(getContext(), mMeal);
        addCardExpand(expand);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        TextView txtCategory = (TextView) view.findViewById(R.id.category);
        TextView txtName = (TextView) view.findViewById(R.id.name);
        TextView txtPrice = (TextView) view.findViewById(R.id.price);

        txtCategory.setText(mMeal.category);
        txtName.setText(mMeal.name);
        txtPrice.setText(mMeal.price);

        ViewToClickToExpand viewToClickToExpand =
                ViewToClickToExpand.builder()
                        .setupView(view);
        setViewToClickToExpand(viewToClickToExpand);
    }


    /*


        //Create a Card
        Card card = new Card(getActivity());

        //Create a CardHeader
        CardHeader header = new CardHeader(getActivity());

        //Set the header title
        header.setTitle(meal.category);

        //Set visible the expand/collapse button
        header.setButtonExpandVisible(true);

        //Add Header to card
        card.addCardHeader(header);

        //This provides a simple (and useless) expand area
        CustomExpandCard expand = new CustomExpandCard(getActivity(), meal);
        //Add Expand Area to Card
        card.addCardExpand(expand);

        //Swipe
        card.setSwipeable(true);

        //Animator listener
        card.setOnExpandAnimatorEndListener(new Card.OnExpandAnimatorEndListener() {
            @Override
            public void onExpandEnd(Card card) {
                Toast.makeText(getActivity(), "Expand " + card.getCardHeader().getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        card.setOnCollapseAnimatorEndListener(new Card.OnCollapseAnimatorEndListener() {
            @Override
            public void onCollapseEnd(Card card) {
                Toast.makeText(getActivity(),"Collpase " +card.getCardHeader().getTitle(),Toast.LENGTH_SHORT).show();
            }
        });



     */
}
