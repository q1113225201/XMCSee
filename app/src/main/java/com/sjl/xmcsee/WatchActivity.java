package com.sjl.xmcsee;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lib.EUIMSG;
import com.lib.FunSDK;
import com.lib.IFunSDKResult;
import com.lib.MsgContent;
import com.sjl.xmcsee.base.BaseActivity;
import com.sjl.xmcsee.lib.support.FunError;
import com.sjl.xmcsee.lib.support.models.FunStreamType;
import com.sjl.xmcsee.lib.support.widget.FunVideoView;
import com.sjl.xmcsee.util.ToastUtil;

public class WatchActivity extends BaseActivity implements View.OnClickListener, IFunSDKResult {
    private static final String TAG = "WatchActivity";
    private RelativeLayout llVideo;
    private FunVideoView funVideoView;
    private Button btnChannel;
    private Button btnPlay;
    private Button btnGetState;
    private TextView tvState;
    private EditText etSn;
    private EditText etDevUsername;
    private EditText etDevPassword;
    private Button btnLogin;

    private int regUser;
    private boolean isPlaying = false;
    private int playHandler;
    private int channel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch);

        initView();
    }

    private void initView() {
        llVideo = findViewById(R.id.llVideo);
        btnPlay = findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(this);
        btnGetState = findViewById(R.id.btnGetState);
        btnGetState.setOnClickListener(this);
        btnChannel = findViewById(R.id.btnChannel);
        btnChannel.setOnClickListener(this);
        etSn = findViewById(R.id.etSn);
        etDevUsername = findViewById(R.id.etDevUsername);
        etDevPassword = findViewById(R.id.etDevPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        tvState = findViewById(R.id.tvState);

        regUser = FunSDK.RegUser(this);
        isPlaying = false;
        initVideoView();
    }

    private void initVideoView() {
        funVideoView = findViewById(R.id.funVideoView);
        funVideoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.i(TAG,"onTouch");
                return false;
            }
        });
        funVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Log.i(TAG,"onPrepared");
            }
        });
        funVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
                Log.i(TAG,"onError---"+what+"---"+extra);
                if ( FunError.EE_TPS_NOT_SUP_MAIN == extra
                        || FunError.EE_DSS_NOT_SUP_MAIN == extra ) {
                    // 不支持高清码流,设置为标清码流重新播放
                    if (null != funVideoView) {
                        funVideoView.setStreamType(FunStreamType.STREAM_SECONDARY);
                        btnPlay.performClick();
                    }
                }
                return true;
            }
        });
        funVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
                Log.i(TAG,"onInfo");
                return true;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                FunSDK.DevLogin(App.regUser,etSn.getText().toString(),
                        etDevUsername.getText().toString(),
                        etDevPassword.getText().toString(),
                        0);
                break;
            case R.id.btnGetState:
                FunSDK.SysGetDevState(regUser,etSn.getText().toString(),(etSn.getText().toString()+etSn.getText().toString()).hashCode());
                break;
            case R.id.btnChannel:
                FunSDK.DevGetChnName(regUser,etSn.getText().toString(),"","",(etSn.getText().toString()+etSn.getText().toString()).hashCode());
                break;
            case R.id.btnPlay:
                funVideoView.setRealDevice(etSn.getText().toString(), 0);
                break;
        }
    }

    @Override
    public int OnFunSDKResult(Message message, MsgContent msgContent) {
        Log.i(TAG, message.toString() + "--------" + msgContent.toString());
        if(message.what== EUIMSG.SYS_GET_DEV_STATE){
            tvState.setText(msgContent.str+"---"+(message.arg1>0?"在线":"离线"));
        }else if(message.what == EUIMSG.DEV_GET_CHN_NAME){
            channel = message.arg1;
        }else if(message.what == EUIMSG.DEV_LOGIN){
            ToastUtil.showToast(mContext, message.arg1 == FunError.EE_OK?"登录成功":"登录失败");
        }
        return 0;
    }
}
