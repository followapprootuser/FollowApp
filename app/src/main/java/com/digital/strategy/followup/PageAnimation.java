package com.digital.strategy.followup;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

/**
 * Created by tech on 9/25/15.
 */
public class PageAnimation extends Activity {

    private static final int DELAY = 100;

    private CustomViewPager viewPager ;
    private CardView cardview ;

    private Interpolator interpolator;

    private RelativeLayout bgViewGroup;

    private AppCompatActivity activity;

    public void setActivity(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void setBgViewGroup(RelativeLayout bgViewGroup) {
        this.bgViewGroup = bgViewGroup;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
    }

    private Toolbar toolbar;

    public boolean cardAnimator (final CardView cardView, int minHeight, int maxHeight){
        ValueAnimator anim;
        boolean isCollapsed;

        if (cardView.getHeight() == minHeight) {
            // collapse
            anim = ValueAnimator.ofInt(cardView.getMeasuredHeightAndState(),
                    maxHeight);


            isCollapsed = true;
        } else {
            // expand
            anim = ValueAnimator.ofInt(cardView.getMeasuredHeightAndState(),
                    minHeight);

            isCollapsed = false;
        }

        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = cardView.getLayoutParams();
                layoutParams.height = val;
                cardView.setLayoutParams(layoutParams);
            }
        });
        anim.start();

        return isCollapsed;
    }

    public void buttonAnimator (final ImageButton button, boolean card_state_collapsed){

        if(card_state_collapsed == true){
            //Card is collapsed
            AlphaAnimation fade_out = new AlphaAnimation(1.0f, 0.0f);
            fade_out.setDuration(500);
            fade_out.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation arg0) {
                    button.setVisibility(View.GONE);
                }

                public void onAnimationRepeat(Animation arg0) {
                }

                public void onAnimationEnd(Animation arg0) {

                }
            });
            button.startAnimation(fade_out);
        }
        else{
            AlphaAnimation fade_in = new AlphaAnimation(0.0f, 1.0f);
            fade_in.setDuration(500);
            fade_in.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation arg0) {
                    button.setVisibility(View.VISIBLE);
                }

                public void onAnimationRepeat(Animation arg0) {
                }

                public void onAnimationEnd(Animation arg0) {

                }
            });
            button.startAnimation(fade_in);
        }
    }

    public void setViewPager (final CustomViewPager viewPager){
        this.viewPager = viewPager;
    }

    public CustomViewPager getViewPager (){
        return viewPager;
    }

    public void setCardView (final CardView cardview){
        this.cardview = cardview;
    }

    public CardView getCardView (){
        return cardview;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void enterReveal(final View button, final Transition.TransitionListener mEnterTransitionListener) {

        // get the center for the clipping circle
        int cx = button.getMeasuredWidth() / 2;
        int cy = button.getMeasuredHeight() / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(button.getWidth(), button.getHeight()) / 2;

        // create the animator for this view (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(button, cx, cy, 0, finalRadius);

        // make the view visible and start the animation
        button.setVisibility(View.VISIBLE);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //getWindow().getEnterTransition().removeListener(mEnterTransitionListener);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void exitReveal(final View button) {

        // get the center for the clipping circle
        int cx = button.getMeasuredWidth() / 2;
        int cy = button.getMeasuredHeight() / 2;

        // get the initial radius for the clipping circle
        int initialRadius = button.getWidth() / 2;

        // create the animation (the final radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(button, cx, cy, initialRadius, 0);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                button.setVisibility(View.INVISIBLE);
            }
        });

        // start the animation
        anim.start();
    }

    /** Called when the user touches the button */
    public void changeButtonNormal(Button button, Resources res) {
        button.setBackgroundDrawable(res.getDrawable(R.drawable.rounded_drawable));
        button.setTextColor(res.getColor(R.color.QuestionTextColor));

        button.setCompoundDrawablePadding(0);
        button.setPadding(100, 0, 100, 0);
        button.setTypeface(null, Typeface.NORMAL);
        button.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }

    /** Called when the user touches the button */
    public void changeButtonPressed(Button button, Resources res) {
        button.setBackgroundDrawable(res.getDrawable(R.drawable.rounded_drawable_pressed));
        button.setTextColor(Color.WHITE);
        //show icon to the left of text

        // Read your drawable from somewhere
        Drawable dr = res.getDrawable(R.drawable.ic_checked);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        // Scale it to 50 x 50
        Drawable d = new BitmapDrawable(res, Bitmap.createScaledBitmap(bitmap, 60, 60, true));
        // Set your new, scaled drawable "d"
        button.setCompoundDrawablePadding(0);
        button.setPadding(100, 0, 100, 0);
        button.setTypeface(button.getTypeface(), Typeface.BOLD);
        button.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setupWindowAnimations() {
        interpolator = AnimationUtils.loadInterpolator(activity, android.R.interpolator.linear_out_slow_in);
        setupEnterAnimations();
        setupExitAnimations();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupEnterAnimations() {
        Fade startTransition = new Fade();
        activity.getWindow().setEnterTransition(startTransition);
        startTransition.setDuration(500);
        startTransition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                transition.removeListener(this);
                animateButtonsIn();
                animateRevealShow(bgViewGroup);
            }

            @Override
            public void onTransitionEnd(Transition transition) {

            }

            @Override
            public void onTransitionCancel(Transition transition) {
            }

            @Override
            public void onTransitionPause(Transition transition) {
            }

            @Override
            public void onTransitionResume(Transition transition) {
            }
        });
    }

    private void hideTarget() {
        //findViewById(R.id.shared_target).setVisibility(View.GONE);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupExitAnimations() {
        Fade returnTransition = new Fade();
        activity.getWindow().setReturnTransition(returnTransition);
        returnTransition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                transition.removeListener(this);
                animateButtonsOut();
                animateRevealHide(bgViewGroup);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
            }

            @Override
            public void onTransitionCancel(Transition transition) {
            }

            @Override
            public void onTransitionPause(Transition transition) {
            }

            @Override
            public void onTransitionResume(Transition transition) {
            }
        });
    }

    private void animateButtonsIn() {
        for (int i = 0; i < bgViewGroup.getChildCount(); i++) {
            View child = bgViewGroup.getChildAt(i);
            child.animate()
                    .setStartDelay(100 + i * DELAY)
                    .setInterpolator(interpolator)
                    .alpha(1)
                    .scaleX(1)
                    .scaleY(1);
        }
    }

    private void animateButtonsOut() {
        for (int i = 0; i < bgViewGroup.getChildCount(); i++) {
            View child = bgViewGroup.getChildAt(i);
            child.animate()
                    .setStartDelay(i)
                    .setInterpolator(interpolator)
                    .alpha(0)
                    .scaleX(0f)
                    .scaleY(0f);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void animateRevealShow(View viewRoot) {
        int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
        int cy = (viewRoot.getTop() + viewRoot.getBottom()) / 2;
        int finalRadius = Math.max(viewRoot.getWidth(), viewRoot.getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, 0, finalRadius);
        viewRoot.setVisibility(View.VISIBLE);
        anim.setDuration(500);
        anim.setInterpolator(new AccelerateInterpolator());
        anim.start();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void animateRevealHide(final View viewRoot) {
        int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
        int cy = (viewRoot.getTop() + viewRoot.getBottom()) / 2;
        int initialRadius = viewRoot.getWidth();

        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, initialRadius, 0);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                viewRoot.setVisibility(View.INVISIBLE);
            }
        });
        anim.setDuration(300);
        anim.start();
    }
}
