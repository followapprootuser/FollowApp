package com.digital.strategy.followup;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.transition.Transition;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import org.w3c.dom.Text;

public class SessionActivity extends AppCompatActivity{

    RelativeLayout bgViewGroup;
    private Transition.TransitionListener mEnterTransitionListener;
    static boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        bgViewGroup = (RelativeLayout) findViewById(R.id.root_view);

        final PageAnimation page_anim = new PageAnimation();

        final FloatingActionButton next_button = (FloatingActionButton) findViewById(R.id.next_button);
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SelectUser.class);
                startActivity(intent);
            }

        });

        ViewTreeObserver observer = bgViewGroup.getViewTreeObserver();

        if (android.os.Build.VERSION_CODES.LOLLIPOP <= android.os.Build.VERSION.SDK_INT ) {
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

        final Button practice_button = (Button) findViewById(R.id.practice_button);
        final Button actual_button = (Button) findViewById(R.id.actual_button);

        final TextView select_session_title = (TextView) findViewById(R.id.session_title);
        final TextView select_session_content = (TextView) findViewById(R.id.session_content);

        practice_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                page_anim.changeButtonPressed(practice_button, getResources());
                page_anim.changeButtonNormal(actual_button, getResources());
                changeText(select_session_title, select_session_content, practice_button);

            }

        });

        actual_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page_anim.changeButtonPressed(actual_button, getResources());
                page_anim.changeButtonNormal(practice_button, getResources());
                changeText(select_session_title, select_session_content, actual_button);

            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_session, menu);
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

    public void changeText (final TextView title, final TextView content, final Button button){
        if(button.getId() == R.id.practice_button){
            title.setText(getResources().getString(R.string.MSG_0028));
            content.setText(getResources().getString(R.string.MSG_0029));
        }
        else{
            title.setText(getResources().getString(R.string.MSG_0030));
            content.setText(getResources().getString(R.string.MSG_0031));
        }

        //button.setCol
    }

}
