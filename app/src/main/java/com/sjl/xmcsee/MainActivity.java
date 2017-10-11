package com.sjl.xmcsee;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.sjl.xmcsee.demo.DemoActivity;
import com.sjl.xmcsee.demo.WatchDemoActivity;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void toDemo(View view) {
        startActivity(new Intent(this, DemoActivity.class));
    }

    public void toLoginUser(View view) {
        startActivity(new Intent(this, LoginUserActivity.class));
    }

    public void toSearch(View view) {
        startActivity(new Intent(this, SearchDeviceActivity.class));
    }

    public void toAddDevice(View view) {
        startActivity(new Intent(this, AddDeviceActivity.class));
    }

    public void toSettingWifi(View view) {
        startActivity(new Intent(this, SettingDeviceActivity.class));
    }

    public void toLogin(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void toWatch(View view) {
        startActivity(new Intent(this, WatchActivity.class));
    }

    public void toWatchCustom(View view) {
        startActivity(new Intent(this, WatchDemoActivity.class));
    }
}
