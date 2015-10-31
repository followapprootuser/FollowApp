package com.digital.strategy.followup;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.firebase.client.Firebase;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientVideoTracksCallbacks;
import com.quickblox.videochat.webrtc.view.QBGLVideoView;
import com.quickblox.videochat.webrtc.view.QBRTCVideoTrack;
import com.quickblox.videochat.webrtc.view.VideoCallBacks;

import org.webrtc.VideoRenderer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements QBRTCClientVideoTracksCallbacks{

    // Declaring Your View and Variables

    private static final String TAG = MainActivity.class.getSimpleName();

    //Toolbar Variables
    Toolbar toolbar;
    ViewPager pager;
    ViewPagerAdapter adapter;

    //ViewPager Indicator Variables
    private ImageButton _btn1,_btn2;
    private ProgressBar page_progress;

    CustomViewPager view_pager;

    CardView cardview;

    private QBGLVideoView remoteVideoView;
    private QBGLVideoView localVideoView;

    private RelativeLayout bgViewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        PageAnimation pageAnimation = new PageAnimation();

        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);
        pageAnimation.setToolbar(toolbar);

        pageAnimation.setActivity(this);

        bgViewGroup = (RelativeLayout) findViewById(R.id.reveal_root);
        pageAnimation.setBgViewGroup(bgViewGroup);

        pageAnimation.setupWindowAnimations();

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter =  new ViewPagerAdapter(getSupportFragmentManager());

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setLogo(R.drawable.ic_follow_app);

        //Set Progressbar
        page_progress = (ProgressBar) findViewById(R.id.page_progress);

        //Set Default properties
        page_progress.setMax(100);
        page_progress.setProgress(10);

        view_pager = (CustomViewPager) findViewById(R.id.pager);
        pageAnimation.setViewPager(view_pager);

        cardview = (CardView) findViewById(R.id.card_view);
        pageAnimation.setCardView(cardview);

        this.view_pager.setPagingEnabled(false);

        //remoteVideoView = (QBGLVideoView) findViewById(R.id.remoteVideoView);
        //remoteVideoView.setVisibility(View.VISIBLE);
        //QBRTCClient.getInstance().addVideoTrackCallbacksListener(this);
        //localVideoView = (QBGLVideoView) findViewById(R.id.localVideoVidew);
        //localVideoView.setVisibility(View.VISIBLE);

        String login = Consts.USER_LOGIN;
        String password = Consts.USER_PASSWORD;
        startIncomeCallListenerService(login, password, Consts.LOGIN);

        Firebase.setAndroidContext(this);
        Firebase firebaseref = new Firebase("https://glaring-fire-5320.firebaseio.com/");

        Firebase sampledata = firebaseref.child("comment");

        Map<String,String> commentone = new HashMap<String, String>();
        commentone.put("comment_string", "Sample");
        sampledata.push().setValue(commentone);

        Map<String,String> commenttwo = new HashMap<String, String>();
        commenttwo.put("comment_string", "SampleTtwo");
        sampledata.push().setValue(commenttwo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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

    private void setPage(final Activity activity){
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int position) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int position) {
                // TODO Auto-generated method stub
                btnAction(position, page_progress);
                /*switch (position) {
                    case 0:
                        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        }
                        //toolbar.setPadding(0, 24, 0, 0);
                        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        break;
                    case 1:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Window window = getWindow();
                            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                            window.setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
                            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.ColorPrimaryDark)));
                            //toolbar.setPadding(0, 0, 0, 0);
                        }

                        break;
                    default:
                        break;
                }*/

            }

        });

    }

    private void btnAction(int action, ProgressBar page_progress){
        switch(action){
            case 0:
                //Set Progressbar
                page_progress.setMax(100);
                page_progress.setProgress(10);
                break;

            case 1:
                //Set Progressbar
                page_progress.setMax(100);
                page_progress.setProgress(20);
                break;
        }
    }



    // A method to find height of the status bar
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void onLocalVideoTrackReceive(QBRTCSession qbrtcSession, QBRTCVideoTrack qbrtcVideoTrack) {
        Log.d(TAG, "localVideoView is " + localVideoView);
        if (localVideoView != null) {
            qbrtcVideoTrack.addRenderer(new VideoRenderer(new VideoCallBacks(localVideoView, QBGLVideoView.Endpoint.LOCAL)));
            localVideoView.setVideoTrack(qbrtcVideoTrack, QBGLVideoView.Endpoint.LOCAL);
            Log.d(TAG, "onLocalVideoTrackReceive() is running");
        }
    }

    @Override
    public void onRemoteVideoTrackReceive(QBRTCSession qbrtcSession, QBRTCVideoTrack qbrtcVideoTrack, Integer integer) {
        /*Log.d(TAG, "remoteVideoView is " + remoteVideoView);
        if (remoteVideoView != null) {
            VideoRenderer remoteRenderer = new VideoRenderer(new VideoCallBacks(remoteVideoView, QBGLVideoView.Endpoint.REMOTE));
            qbrtcVideoTrack.addRenderer(remoteRenderer);
            remoteVideoView.setVideoTrack(qbrtcVideoTrack, QBGLVideoView.Endpoint.REMOTE);
            Log.d(TAG, "onRemoteVideoTrackReceive() is running");
        }*/
    }

    public void startIncomeCallListenerService(String login, String password, int startServiceVariant){
        Intent tempIntent = new Intent(this, WebChatrtcManager.class);
        PendingIntent pendingIntent = createPendingResult(Consts.LOGIN_TASK_CODE, tempIntent, 0);
        Intent intent = new Intent(this, WebChatrtcManager.class);
        intent.putExtra(Consts.USER_LOGIN, login);
        intent.putExtra(Consts.USER_PASSWORD, password);
        intent.putExtra(Consts.START_SERVICE_VARIANT, startServiceVariant);
        intent.putExtra(Consts.PARAM_PINTENT, pendingIntent);
        startService(intent);
    }
}
