package com.digital.strategy.followup;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerButton;

/**
 * Created by hp1 on 21-01-2015.
 */
public class FollowUpOnePageTwo extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_follow_up_one_page_two, container, false);

        final ShimmerButton lets_get_started_button = (ShimmerButton) v.findViewById(R.id.lets_get_started_button);

        Shimmer shimmer = new Shimmer();
        shimmer.setDuration(2000);
        shimmer.start(lets_get_started_button);

        lets_get_started_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity main_activity = (MainActivity) getActivity();
                //main_activity.view_pager.setPagingEnabled(true);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.pageone, new FollowUpOnePageOne());
                transaction.commit();
            }

        });
        return v;
    }
}