package com.sjl.xmcsee.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * BaseActivity
 *
 * @author SJL
 * @date 2017/9/25
 */

public class BaseActivity extends Activity {
    private static final String TAG = "BaseActivity";
    public Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mContext = this;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void funsdkResult(Message msg) {
        Log.i(TAG, msg.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }
}
