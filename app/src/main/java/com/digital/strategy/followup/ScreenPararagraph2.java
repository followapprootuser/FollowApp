package com.digital.strategy.followup;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

public class ScreenPararagraph2 extends Fragment implements ObservableScrollViewCallbacks {

    TextView title;
    AppCompatActivity actionbar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_screen_pararagraph2,container,false);

        ObservableScrollView observable_scrollview = (ObservableScrollView) v.findViewById(R.id.observable_scrollview);
        observable_scrollview.setScrollViewCallbacks(this);

        title = (TextView) v.findViewById(R.id.series_title);
        //content.setMovementMethod(new ScrollingMovementMethod());

        //actionbar = (AppCompatActivity) getActivity().getSupportActionBar();

        return v;
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll,
                                boolean dragging) {
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

        //title.setVisibility(View.VISIBLE);

        /*if (scrollState == ScrollState.UP) {
            if (ab.isShowing()) {
                ab.hide();
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (!ab.isShowing()) {
                ab.show();
            }
        }*/

    }

}
