package com.sjl.xmcsee;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.basic.G;
import com.lib.EUIMSG;
import com.lib.FunSDK;
import com.lib.MsgContent;
import com.sjl.xmcsee.base.BaseActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class SearchDeviceActivity extends BaseActivity {
    private static final String TAG = "SearchDeviceActivity";
    private ListView lv;
    private ArrayAdapter adapter;
    private List<String> nameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_device);
        initView();
    }

    private void initView() {
        lv = findViewById(R.id.lv);
        nameList = new ArrayList<>();
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,nameList);
        lv.setAdapter(adapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getSearchResult(Message msg) {
        if (msg.what == EUIMSG.DEV_SEARCH_DEVICES) {
            nameList.add(G.ToString(((MsgContent)msg.obj).pData));
            adapter.notifyDataSetChanged();
        }
    }

    public void startSearch(View view) {
        nameList.clear();
        adapter.notifyDataSetChanged();
        int result = FunSDK.DevSearchDevice(App.regUser, 10000, 0);
        Log.i(TAG, "result=" + result);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
