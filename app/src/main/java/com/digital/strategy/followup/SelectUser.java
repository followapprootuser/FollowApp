package com.digital.strategy.followup;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.transition.Slide;
import android.transition.Transition;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import fr.castorflex.android.circularprogressbar.CircularProgressDrawable;

public class SelectUser extends AppCompatActivity{

    RelativeLayout bgViewGroup;
    private Transition.TransitionListener mEnterTransitionListener;
    static boolean flag = false;

    CircularProgressDrawable circle_loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user);

        final PageAnimation page_anim = new PageAnimation();

        page_anim.setActivity(this);

        bgViewGroup = (RelativeLayout) findViewById(R.id.root_view);
        page_anim.setBgViewGroup(bgViewGroup);

        page_anim.setupWindowAnimations();

        final Button student_button = (Button) findViewById(R.id.student_button);
        final Button leader_button = (Button) findViewById(R.id.leader_button);

        final FloatingActionButton next_button = (FloatingActionButton) findViewById(R.id.next_button);
        //page_anim.setShared_target(next_button);
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(v.getContext(), MainActivity.class);
                //startActivity(intent);
                transitionToActivity(MainActivity.class, next_button, R.string.transition_reveal1);
            }

        });

        ViewTreeObserver observer = bgViewGroup.getViewTreeObserver();

        if (android.os.Build.VERSION_CODES.LOLLIPOP <= android.os.Build.VERSION.SDK_INT ){
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onGlobalLayout() {
                    //in here, place the code that requires you to know the dimensions.
                    if (flag == false) {
                        page_anim.enterReveal(next_button, mEnterTransitionListener);
                        flag = true;
                    }
                    //this will be called as the layout is finished, prior to displaying.
                }
            });
        }
        else{}

        student_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page_anim.changeButtonPressed(student_button, getResources());
                page_anim.changeButtonNormal(leader_button, getResources());

            }

        });

        leader_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page_anim.changeButtonPressed(leader_button, getResources());
                page_anim.changeButtonNormal(student_button, getResources());

            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void changeText (){

    }

    private void transitionToActivity(Class target, FloatingActionButton shared_button, int transitionName) {
        final Pair<View, String>[] pairs = TransitionHelper.createSafeTransitionParticipants(this, false,
                new Pair<>(shared_button, this.getString(transitionName)));
        startActivity(target, pairs);
    }

    private void startActivity(Class target, Pair<View, String>[] pairs) {
        Intent i = new Intent(this, target);
        ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pairs);
        this.startActivity(i, transitionActivityOptions.toBundle());
    }
}
