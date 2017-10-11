package com.sjl.xmcsee.demo;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.lib.EPTZCMD;
import com.lib.EUIMSG;
import com.lib.FunSDK;
import com.lib.IFunSDKResult;
import com.lib.MsgContent;
import com.sjl.xmcsee.R;
import com.sjl.xmcsee.lib.support.FunError;
import com.sjl.xmcsee.util.ToastUtil;
import com.video.opengl.GLSurfaceView20;

public class WatchDemoActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "WatchDemoActivity";
    private Context context;
    //停止播放
    public static final int STATE_STOPED = 21;
    //播放中
    public static final int STATE_PLAYING = 22;
    //暂停
    public static final int STATE_PAUSE = 23;
    private RelativeLayout rlVideo;
    private EditText etSn;
    private EditText etDevUsername;
    private EditText etDevPassword;
    private Button btnLogin;
    private Button btnPlay;
    private Button btnPause;
    private Button btnStop;
    private Button btnUp;
    private Button btnDown;
    private Button btnLeft;
    private Button btnRight;
    private Button btnStopRotate;

    private GLSurfaceView surfaceView;
    private int playHandler=-1;
    private int hUser;
    private int state = STATE_STOPED;
    private int control = 0;
    private int direction = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_demo);

        initView();
    }

    private void initView() {
        context = this;
        rlVideo = findViewById(R.id.rlVideo);
        etSn = findViewById(R.id.etSn);
        etDevUsername = findViewById(R.id.etDevUsername);
        etDevPassword = findViewById(R.id.etDevPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        btnPlay = findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(this);
        btnPause = findViewById(R.id.btnPause);
        btnPause.setOnClickListener(this);
        btnStop = findViewById(R.id.btnStop);
        btnStop.setOnClickListener(this);
        btnUp = findViewById(R.id.btnUp);
        btnUp.setOnClickListener(this);
        btnDown = findViewById(R.id.btnDown);
        btnDown.setOnClickListener(this);
        btnLeft = findViewById(R.id.btnLeft);
        btnLeft.setOnClickListener(this);
        btnRight = findViewById(R.id.btnRight);
        btnRight.setOnClickListener(this);
        btnStopRotate = findViewById(R.id.btnStopRotate);
        btnStopRotate.setOnClickListener(this);
        initSurfaceView();
    }

    private void initSurfaceView() {
        surfaceView = new GLSurfaceView20(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rlVideo.addView(surfaceView, layoutParams);

        hUser = FunSDK.RegUser(new IFunSDKResult() {
            @Override
            public int OnFunSDKResult(Message message, MsgContent msgContent) {
                Log.i(TAG, message.toString());
                if (message.what == EUIMSG.DEV_LOGIN) {
                    ToastUtil.showToast(context, message.arg1 == FunError.EE_OK ? "登录成功" : "登录失败");
                }
                return 0;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                FunSDK.DevLogin(hUser, etSn.getText().toString(),
                        etDevUsername.getText().toString(),
                        etDevPassword.getText().toString(),
                        0);
                break;
            case R.id.btnPlay:
                play();
                break;
            case R.id.btnPause:
                pause();
                break;
            case R.id.btnStop:
                stop();
                break;
            case R.id.btnUp:
                control(EPTZCMD.TILT_UP, 0);
                break;
            case R.id.btnDown:
                control(EPTZCMD.TILT_DOWN, 0);
                break;
            case R.id.btnLeft:
                control(EPTZCMD.PAN_LEFT, 0);
                break;
            case R.id.btnRight:
                control(EPTZCMD.PAN_RIGHT, 0);
                break;
            case R.id.btnStopRotate:
                control(direction, 1);
                break;
        }
    }

    /**
     * 控制方向
     *
     * @param direction
     */
    private void control(int direction, int isStop) {
        this.direction = direction;
        Log.i(TAG, String.format("direction:%d,isStop:%d", direction, isStop));
        FunSDK.DevPTZControl(hUser, etSn.getText().toString(), 0, direction, isStop, 4, control);
    }

    /**
     * 播放
     */
    public void play() {
        if (state == STATE_STOPED) {
            state = STATE_PLAYING;
            Log.i(TAG, "play");
            playHandler = FunSDK.MediaRealPlay(hUser, etSn.getText().toString(), 0, 0, surfaceView, control);
        }
    }

    /**
     * 暂停
     */
    public void pause() {
        if (state == STATE_PLAYING) {
            state = STATE_PAUSE;
            Log.i(TAG, "pause 1");
            FunSDK.MediaPause(playHandler, 1, control);
        } else if (state == STATE_PAUSE) {
            state = STATE_PLAYING;
            Log.i(TAG, "pause 0");
            FunSDK.MediaPause(playHandler, 0, control);
        }
    }

    /**
     * 停止
     */
    public void stop() {
        if (state != STATE_STOPED) {
            state = STATE_STOPED;
            Log.i(TAG, "stop");
            FunSDK.MediaStop(playHandler);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(playHandler!=-1) {
            FunSDK.MediaStop(playHandler);
        }
        FunSDK.UnRegUser(hUser);
    }
}
