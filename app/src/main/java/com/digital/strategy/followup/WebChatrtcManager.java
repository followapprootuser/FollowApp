package com.digital.strategy.followup;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBSignaling;
import com.quickblox.chat.QBWebRTCSignaling;
import com.quickblox.chat.listeners.QBVideoChatSignalingManagerListener;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCConfig;
import com.quickblox.videochat.webrtc.QBRTCException;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientConnectionCallbacks;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientSessionCallbacks;

import org.jivesoftware.smack.SmackException;
import org.webrtc.VideoCapturerAndroid;
import org.webrtc.VideoRenderer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by DS on 10/10/15.
 */
public class WebChatrtcManager extends Service implements QBRTCClientSessionCallbacks, QBRTCClientConnectionCallbacks{
    protected Context context;
    private static final String TAG = WebChatrtcManager.class.getSimpleName();
    private QBChatService chatService;
    private String login;
    private String password;
    private PendingIntent pendingIntent;
    private int startServiceVariant;
    private BroadcastReceiver connectionStateReceiver;
    private boolean needMaintainConnectivity;
    private boolean isConnectivityExists;

    /*public WebChatrtcManager(Context context) {
        this.context = context;
    }*/

    @Override
    public void onCreate() {
        super.onCreate();

        // App credentials from QB Admin Panel
        QBSettings.getInstance().fastConfigInit(Consts.APP_ID, Consts.AUTH_KEY, Consts.AUTH_SECRET);
        //QBSettings.getInstance().setTransferProtocol(TransferProtocol.HTTP);

        initConnectionManagerListener();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");

        if (!QBChatService.isInitialized()) {
            QBChatService.init(getApplicationContext());
        }

        chatService = QBChatService.getInstance();

        /* Get User information */
        if (intent != null && intent.getExtras()!= null) {
            pendingIntent = intent.getParcelableExtra(Consts.PARAM_PINTENT);
            parseIntentExtras(intent);
            if (TextUtils.isEmpty(login) && TextUtils.isEmpty(password)){
                getUserDataFromPreferences();
            }
        }

        if(!QBChatService.getInstance().isLoggedIn()){
            createSession(login, password);
        } else {
            startActionsOnSuccessLogin(login, password);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    private void initConnectionManagerListener() {
        connectionStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Connection state was changed");
                boolean isConnected = processConnectivityState(intent);
                updateStateIfNeed(isConnected);
            }

            private boolean processConnectivityState(Intent intent) {
                int connectivityType = intent.getIntExtra(ConnectivityManager.EXTRA_NETWORK_TYPE, -1);
                // Check does connectivity equal mobile or wifi types
                boolean connectivityState = false;
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();

                if (networkInfo != null){
                    if (connectivityType == ConnectivityManager.TYPE_MOBILE
                            || connectivityType == ConnectivityManager.TYPE_WIFI
                            || networkInfo.getTypeName().equals("WIFI")
                            || networkInfo.getTypeName().equals("MOBILE")) {
                        //should check null because in air plan mode it will be null
                        if (networkInfo.isConnected()) {
                            // Check does connectivity EXISTS for connectivity type wifi or mobile internet
                            // Pay attention on "!" symbol  in line below
                            connectivityState = true;
                        }
                    }
                }
                return connectivityState;
            }

            private void updateStateIfNeed(boolean connectionState) {
                if (isConnectivityExists != connectionState) {
                    processCurrentConnectionState(connectionState);
                }
            }

            private void processCurrentConnectionState(boolean isConnected) {
                if (!isConnected) {
                    Log.d(TAG, "Connection is turned off");
                } else {
                    if (needMaintainConnectivity) {
                        Log.d(TAG, "Connection is turned on");
                        if (!QBChatService.isInitialized()) {
                            QBChatService.init(getApplicationContext());
                        }
                        chatService = QBChatService.getInstance();
                        if (!QBChatService.getInstance().isLoggedIn()) {
                            SharedPreferences sharedPreferences = getSharedPreferences(Consts.SHARED_PREFERENCES, Context.MODE_PRIVATE);
                            String login = sharedPreferences.getString(Consts.USER_LOGIN, null);
                            String password = sharedPreferences.getString(Consts.USER_PASSWORD, null);
                            reloginToChat(login, password);
                        }
                    }
                }
            }

        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectionStateReceiver, intentFilter);
    }

    private void initQBRTCClient() {
        Log.d(TAG, "initQBRTCClient()");
        try {
            QBChatService.getInstance().startAutoSendPresence(60);
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
        }

        // Add signalling manager
        QBChatService.getInstance().getVideoChatWebRTCSignalingManager().addSignalingManagerListener(new QBVideoChatSignalingManagerListener() {
            @Override
            public void signalingCreated(QBSignaling qbSignaling, boolean createdLocally) {
                if (!createdLocally) {
                    QBRTCClient.getInstance().addSignaling((QBWebRTCSignaling) qbSignaling);
                }
            }
        });

        QBRTCClient.getInstance().setCameraErrorHendler(new VideoCapturerAndroid.CameraErrorHandler() {
            @Override
            public void onCameraError(final String s) {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }
        });

        QBRTCConfig.setAnswerTimeInterval(Consts.ANSWER_TIME_INTERVAL);

        // Add activity as callback to RTCClient
        QBRTCClient.getInstance().addSessionCallbacksListener(this);
        QBRTCClient.getInstance().addConnectionCallbacksListener(this);

        // Start mange QBRTCSessions according to VideoCall parser's callbacks
        QBRTCClient.getInstance().prepareToProcessCalls(getApplicationContext());
    }

    private void parseIntentExtras(Intent intent) {
        login = intent.getStringExtra(Consts.USER_LOGIN);
        password = intent.getStringExtra(Consts.USER_PASSWORD);
        startServiceVariant = intent.getIntExtra(Consts.START_SERVICE_VARIANT, Consts.AUTOSTART);
    }

    protected void getUserDataFromPreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences(Consts.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        login = sharedPreferences.getString(Consts.USER_LOGIN, null);
        password = sharedPreferences.getString(Consts.USER_PASSWORD, null);
    }

    private void createSession(final String login, final String password) {
        Log.d(TAG, "Creating session");
        if (!TextUtils.isEmpty(login) && !TextUtils.isEmpty(password)) {
            // Register new user
            final QBUser user = new QBUser(login, password);

            /*QBUsers.signUp(user, new QBEntityCallbackImpl<QBUser>() {
                @Override
                public void onSuccess(QBUser user, Bundle args) {
                    Log.d(TAG, "User Created");
                    // success
                }

                @Override
                public void onError(List<String> errors) {
                    // error
                }
            });*/


            QBAuth.createSession(user, new QBEntityCallbackImpl<QBSession>() {
                @Override
                public void onSuccess(QBSession session, Bundle bundle) {
                    Log.d(TAG, "onSuccess create session with params");
                    user.setId(session.getUserId());

                    if (chatService.isLoggedIn()) {
                        Log.d(TAG, "chatService.isLoggedIn()");
                        //SessionManager.getCurrentSession().setVideoEnabled(true);
                        startActionsOnSuccessLogin(login, password);
                    } else {
                        Log.d(TAG, "!chatService.isLoggedIn()");

                        chatService.login(user, new QBEntityCallbackImpl<QBUser>() {

                            @Override
                            public void onSuccess(QBUser result, Bundle params) {
                                Log.d(TAG, "onSuccess login to chat with params");
                                startActionsOnSuccessLogin(login, password);

                            }

                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "onSuccess login to chat");
                                startActionsOnSuccessLogin(login, password);
                            }

                            @Override
                            public void onError(List errors) {
                            }
                        });
                    }
                }

                @Override
                public void onSuccess() {
                    super.onSuccess();
                    Log.d(TAG, "onSuccess create session");
                    SessionManager.getCurrentSession().setVideoEnabled(true);
                }

                @Override
                public void onError(List<String> errors) {
                    for (String s : errors) {
                        Log.d(TAG, s);
                    }
                    Log.d(TAG, "Session NOT CREATED");
                    //SessionManager.getCurrentSession().setVideoEnabled(true);
                }
            });
        } else {
            /*sendResultToActivity(false);
            stopForeground(true);
            stopService(new Intent(getApplicationContext(), IncomeCallListenerService.class));*/
        }
    }

    private void reloginToChat(String login, String password) {
        final QBUser user = new QBUser(login, password);
        QBAuth.createSession(login, password, new QBEntityCallbackImpl<QBSession>() {
            @Override
            public void onSuccess(QBSession session, Bundle bundle) {
                Log.d(TAG, "onSuccess create session with params");
                user.setId(session.getUserId());
                chatService.login(user, new QBEntityCallbackImpl<QBUser>() {

                    @Override
                    public void onSuccess(QBUser result, Bundle params) {
                        Log.d(TAG, "onSuccess login to chat with params");
                    }

                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "onSuccess login to chat");
                    }

                    @Override
                    public void onError(List errors) {
                        Toast.makeText(WebChatrtcManager.this, "Error when login", Toast.LENGTH_SHORT).show();
                        for (Object error : errors) {
                            Log.d(TAG, error.toString());
                        }
                    }
                });
            }

            @Override
            public void onSuccess() {
                super.onSuccess();
                Log.d(TAG, "onSuccess create session");
            }

            @Override
            public void onError(List<String> errors) {
                for (String s : errors) {
                    Log.d(TAG, s);
                }
            }
        });
    }


    private void startActionsOnSuccessLogin(String login, String password) {
        initQBRTCClient();
        sendResultToActivity(true);
        //startOpponentsActivity();
        //startForeground(Consts.NOTIFICATION_FORAGROUND, createNotification());
        saveUserDataToPreferences(login, password);
        needMaintainConnectivity = true;
    }

    private void saveUserDataToPreferences(String login, String password){
        Log.d(TAG, "saveUserDataToPreferences()");
        SharedPreferences sharedPreferences = getSharedPreferences(Consts.SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putString(Consts.USER_LOGIN, login);
        ed.putString(Consts.USER_PASSWORD, password);
        ed.commit();
    }

    private void sendResultToActivity (boolean isSuccess){
        Log.d(TAG, "sendResultToActivity()");
        if (startServiceVariant == Consts.LOGIN) {
            try {
                Intent intent = new Intent().putExtra(Consts.LOGIN_RESULT, isSuccess);
                pendingIntent.send(WebChatrtcManager.this, Consts.LOGIN_RESULT_CODE, intent);
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendBroadcastMessages(int callbackAction, Integer usedID,
                                       Map<String, String> userInfo, QBRTCException exception){
        Intent intent = new Intent();
        intent.setAction(Consts.CALL_RESULT);
        intent.putExtra(Consts.CALL_ACTION_VALUE, callbackAction);
        intent.putExtra(Consts.USER_ID, usedID);
        intent.putExtra(Consts.USER_INFO_EXTRAS, (Serializable) userInfo);
        intent.putExtra(Consts.QB_EXCEPTION_EXTRAS, exception);
        sendBroadcast(intent);
    }

    //========== Implement methods ==========//

    @Override
    public void onReceiveNewSession(QBRTCSession qbrtcSession) {
        if (SessionManager.getCurrentSession() == null){
            SessionManager.setCurrentSession(qbrtcSession);
            //CallActivity.start(this,
            //        qbrtcSession.getConferenceType(),
            //        qbrtcSession.getOpponents(),
            //        qbrtcSession.getUserInfo(),
            //        Consts.CALL_DIRECTION_TYPE.INCOMING);
        } else if (SessionManager.getCurrentSession() != null && !qbrtcSession.equals(SessionManager.getCurrentSession())){
            qbrtcSession.rejectCall(new HashMap<String, String>());
        }
    }

    @Override
    public void onUserNotAnswer(QBRTCSession qbrtcSession, Integer integer) {
        sendBroadcastMessages(Consts.USER_NOT_ANSWER, integer, null, null);
    }

    @Override
    public void onCallRejectByUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {
        sendBroadcastMessages(Consts.CALL_REJECT_BY_USER, integer, map, null);
    }

    @Override
    public void onReceiveHangUpFromUser(QBRTCSession qbrtcSession, Integer integer) {
        if (qbrtcSession.equals(SessionManager.getCurrentSession())) {
            sendBroadcastMessages(Consts.RECEIVE_HANG_UP_FROM_USER, integer, null, null);
        }
    }

    @Override
    public void onSessionClosed(QBRTCSession qbrtcSession) {
        if (qbrtcSession.equals(SessionManager.getCurrentSession())) {
            sendBroadcastMessages(Consts.SESSION_CLOSED, null, null, null);
        }
    }

    @Override
    public void onSessionStartClose(QBRTCSession qbrtcSession) {
        sendBroadcastMessages(Consts.SESSION_START_CLOSE, null, null, null);
    }

    @Override
    public void onStartConnectToUser(QBRTCSession qbrtcSession, Integer integer) {
        sendBroadcastMessages(Consts.START_CONNECT_TO_USER, integer, null, null);
    }

    @Override
    public void onConnectedToUser(QBRTCSession qbrtcSession, Integer integer) {
        sendBroadcastMessages(Consts.CONNECTED_TO_USER, integer, null, null);
    }

    @Override
    public void onConnectionClosedForUser(QBRTCSession qbrtcSession, Integer integer) {
        sendBroadcastMessages(Consts.CONNECTION_CLOSED_FOR_USER, integer, null, null);
    }

    @Override
    public void onDisconnectedFromUser(QBRTCSession qbrtcSession, Integer integer) {
        sendBroadcastMessages(Consts.DISCONNECTED_FROM_USER, integer, null, null);
    }

    @Override
    public void onDisconnectedTimeoutFromUser(QBRTCSession qbrtcSession, Integer integer) {
        sendBroadcastMessages(Consts.DISCONNECTED_TIMEOUT_FROM_USER, integer, null, null);
    }

    @Override
    public void onConnectionFailedWithUser(QBRTCSession qbrtcSession, Integer integer) {
        sendBroadcastMessages(Consts.CONNECTION_FAILED_WITH_USER, integer, null, null);
    }

    @Override
    public void onError(QBRTCSession qbrtcSession, QBRTCException e) {
        sendBroadcastMessages(Consts.ERROR, null, null, null);
    }

}
