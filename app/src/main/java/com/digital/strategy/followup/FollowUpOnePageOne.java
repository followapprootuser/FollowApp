package com.digital.strategy.followup;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

/**
 * Created by arradazar on 22-09-2015.
 */
public class FollowUpOnePageOne extends Fragment {

    boolean is_answer_card_collapsed = false;
    boolean show_hint_revealed = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_follow_up_one_page_one,container,false);

        //AppCompatActivity activity = (AppCompatActivity) getActivity();
        //activity.getSupportActionBar().show();

        final PageAnimation page_anim = new PageAnimation();

        //final CardView cardView = (CardView) v.findViewById(R.id.card_view);
        //final CardView answerCardView = (CardView) v.findViewById(R.id.answer_card_view);
        final TextView verse = (TextView) v.findViewById(R.id.verse);
        //final ImageButton show_hint = (ImageButton) v.findViewById(R.id.show_hint);
        final TextInputLayout usernameWrapper = (TextInputLayout) v.findViewById(R.id.usernameWrapper);
        final EditText answer_field = (EditText) v.findViewById(R.id.answer_field);
        //final ShimmerTextView swipe_guide = (ShimmerTextView) v.findViewById(R.id.swipe_guide);
        //final TextView verse_details = (TextView) v.findViewById(R.id.verse_details);

        //Set EditText
        usernameWrapper.setHint("Write answer here");

        answer_field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer_field.setFocusableInTouchMode(true);
                usernameWrapper.setHint("Answer");
            }
        });

        /*show_hint.setOnClickListener(new View.OnClickListener() {

            String verse = verse_details.getText().toString();
            String verse_hint = "Son of God";

            @Override
            public void onClick(View v) {
                int start = verse.indexOf(verse_hint);
                int end = start + verse_hint.length();
                Spannable WordtoSpan = new SpannableString(verse);
                WordtoSpan.setSpan(new ForegroundColorSpan(Color.BLUE), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                verse_details.setText(WordtoSpan);

                page_anim.exitReveal(show_hint);
                show_hint_revealed = true;
            }
        });*/

        answer_field.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                MainActivity main_activity = (MainActivity) getActivity();
                if (!answer_field.getText().toString().isEmpty()) {
                    main_activity.view_pager.setPagingEnabled(true);
                } else {
                    main_activity.view_pager.setPagingEnabled(false);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                int minHeight = 390;
                int maxHeight = 570;

                if (answer_field.getText().toString().isEmpty()) {
                    if (is_answer_card_collapsed == true) {
                        //is_answer_card_collapsed = page_anim.cardAnimator(answerCardView, minHeight, maxHeight);
                    } else {

                    }

                } else {
                    if (is_answer_card_collapsed == false) {
                        //is_answer_card_collapsed = page_anim.cardAnimator(answerCardView, minHeight, maxHeight);
                    } else {

                    }
                }
            }
        });

        //show_hint.setVisibility(View.GONE);

        verse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String verse = getResources().getString(R.string.MSG_00004);
                String verse_hint = "Son of God";

                int start = verse.indexOf(verse_hint);
                int end = start + verse_hint.length();
                final Spannable WordtoSpan = new SpannableString(verse);
                WordtoSpan.setSpan(new ForegroundColorSpan(Color.BLUE), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                //verse_details.setText(WordtoSpan);

                new MaterialDialog.Builder(getContext())
                        .title(R.string.MSG_00003)
                        .content(R.string.MSG_00004)
                        .neutralText(R.string.MSG_00009)
                        .theme(Theme.LIGHT)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onNeutral(@NonNull MaterialDialog dialog) {
                                dialog.setContent(WordtoSpan);
                            }
                        })
                        .autoDismiss(false)
                        .show();

                /*MainActivity main_activity = (MainActivity) getActivity();

                int minHeight = 2000;
                int maxHeight = 2500;
                boolean iscollapsed = page_anim.cardAnimator(main_activity.cardview, minHeight, maxHeight);

                if(show_hint_revealed == true)
                    return;

                //page_anim.buttonAnimator(show_hint, iscollapsed);
                if (iscollapsed == true) {
                    page_anim.exitReveal(show_hint);
                } else {
                    page_anim.enterReveal(show_hint);
                }*/
            }

        });

        /*Shimmer shimmer = new Shimmer();
        shimmer.setDuration(2000);
        shimmer.start(swipe_guide);*/

        return v;
    }
}