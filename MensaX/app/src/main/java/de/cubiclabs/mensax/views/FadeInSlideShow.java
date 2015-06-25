package de.cubiclabs.mensax.views;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.view.View;

import de.cubiclabs.mensax.R;

/**
 * Created by thimokluser on 6/24/15.
 */
public class FadeInSlideShow {

    private static final int DELAY = 9000;
    private static final int TRANSITION_DURATION = 900;

    private View mView;
    private int[] mDrawableIds = {
            R.drawable.bg_1,
            R.drawable.bg_2,
            R.drawable.bg_3,
            R.drawable.bg_4,
            R.drawable.bg_5,
            R.drawable.bg_6
    };
    private Drawable[] mDrawables;
    private int mCurrentDrawableIndex = 0;
    private Resources mResources;
    private boolean mDontUseSlideShow = false;

    private boolean mIsRunning = false;

    private Handler mHandler;

    private Runnable mTransitionEvent = new Runnable() {
        public void run() {
            mHandler.removeCallbacks(mTransitionEvent);
            if(mDontUseSlideShow) return;

            TransitionDrawable transitionDrawable = new TransitionDrawable(getNextDrawables());
            transitionDrawable.setCrossFadeEnabled(true);
            mView.setBackgroundDrawable(transitionDrawable);
            transitionDrawable.startTransition(TRANSITION_DURATION);

            mHandler.postDelayed(mTransitionEvent, DELAY + TRANSITION_DURATION);
        }
    };

    public FadeInSlideShow(View view) {
        mView = view;
        mHandler = new Handler();
        mResources = view.getResources();
        loadDrawables();
    }

    public void start() {
        if(mDontUseSlideShow) return;
        if(mIsRunning) return;
        mHandler.postDelayed(mTransitionEvent, DELAY);
        mIsRunning = true;
    }

    public void stop() {
        mIsRunning = false;
        mHandler.removeCallbacks(mTransitionEvent);
    }

    private void loadDrawables() {
        try {
            mDrawables = new Drawable[mDrawableIds.length];
            for (int i = 0; i < mDrawableIds.length; i++) {
                mDrawables[i] = mResources.getDrawable(mDrawableIds[i]);
            }
        } catch(OutOfMemoryError e) {
            mDontUseSlideShow = true;
            mDrawables = null;
        }
    }

    private Drawable[] getNextDrawables() {
        Drawable[] set = {
                mDrawables[mCurrentDrawableIndex % mDrawableIds.length],
                mDrawables[(mCurrentDrawableIndex+1) % mDrawableIds.length]
        };
        mCurrentDrawableIndex++;
        return set;
    }

}
