package com.sjl.xmcsee;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.basic.G;
import com.lib.EUIMSG;
import com.lib.FunSDK;
import com.lib.MsgContent;
import com.sjl.xmcsee.base.BaseActivity;
import com.sjl.xmcsee.lib.sdk.struct.SDK_CONFIG_NET_COMMON_V2;
import com.sjl.xmcsee.util.ToastUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class SettingDeviceActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "SettingDeviceActivity";
    private EditText etWifiSSID;
    private EditText etWifiPwd;
    private Button btnSettingWifi;
    private TextView tvSn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_device);

        initView();
    }

    private void initView() {
        etWifiSSID = findViewById(R.id.etWifiSSID);
        etWifiPwd = findViewById(R.id.etWifiPwd);
        btnSettingWifi = findViewById(R.id.btnSettingWifi);
        btnSettingWifi.setOnClickListener(this);
        tvSn = findViewById(R.id.tvSn);
        tvSn.setOnClickListener(this);

        WifiManager wifimanage=(WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        etWifiSSID.setText(wifimanage.getConnectionInfo().getSSID().replace("\"", ""));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSettingWifi:
                settingWifi();
                break;
            case R.id.tvSn:
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                clipboardManager.setPrimaryClip(ClipData.newPlainText("text", tvSn.getText().toString()));
                ToastUtil.showToast(mContext,"复制成功");
                break;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void result(Message msg) {
        if (msg.what == EUIMSG.DEV_AP_CONFIG) {
            ToastUtil.showToast(mContext,"配置完成");
            SDK_CONFIG_NET_COMMON_V2 commonV2 = new SDK_CONFIG_NET_COMMON_V2();
            G.BytesToObj(commonV2,((MsgContent)msg.obj).pData);
            String devSn = G.ToString(commonV2.st_14_sSn);
            tvSn.setText(devSn);
        }
    }
    /**
     * 设置wifi
     */
    private void settingWifi() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        String ssid = wifiInfo.getSSID().replace("\"", "");
        String pwd = etWifiPwd.getText().toString();
        wifiManager.startScan();
        ScanResult scanResult = null;
        List<ScanResult> list = wifiManager.getScanResults();
        if (list != null) {
            for (ScanResult item : list) {
                if (item.SSID.contains(ssid)) {
                    scanResult = item;
                    break;
                }
            }
        }
        int pwdType = getEncrypPasswordType(scanResult.capabilities);
        StringBuffer data = new StringBuffer();
        data.append("S:").append(ssid).append("P:").append(pwd).append("T:").append(pwdType);
        Log.i(TAG,"data="+data);
        String submask;
        if (dhcpInfo.netmask == 0) {
            submask = "255.255.255.0";
        } else {
            submask = formatIpAddress(dhcpInfo.netmask);
        }
        String mac = wifiInfo.getMacAddress();
        StringBuffer info = new StringBuffer();
        info.append("gateway:").append(formatIpAddress(dhcpInfo.gateway)).append(" ip:")
                .append(formatIpAddress(dhcpInfo.ipAddress)).append(" submask:").append(submask)
                .append(" dns1:").append(formatIpAddress(dhcpInfo.dns1)).append(" dns2:")
                .append(formatIpAddress(dhcpInfo.dns2)).append(" mac:").append(mac)
                .append(" ");
        Log.i(TAG,"info="+info);
        int nGetType = 2; // 1:外网;2:内网 0x3:一起
        FunSDK.DevStartAPConfig(App.regUser, nGetType, ssid, data.toString(), info.toString(), formatIpAddress(dhcpInfo.gateway), pwdType, 0, mac, -1);
        Log.i(TAG,nGetType+"---\n"+ssid+"---\n"+data.toString()+"---\n"+info.toString()+"---\n"+formatIpAddress(dhcpInfo.gateway)+"---\n"+pwdType+"---\n"+0+"---\n"+mac+"---\n"+(-1));
    }

    public static String formatIpAddress(int ip) {
        byte[] ipAddress = new byte[4];
        InetAddress myaddr;
        try {
            ipAddress[3] = (byte) ((ip >> 24) & 0xff);
            ipAddress[2] = (byte) ((ip >> 16) & 0xff);
            ipAddress[1] = (byte) ((ip >> 8) & 0xff);
            ipAddress[0] = (byte) (ip & 0xff);
            myaddr = InetAddress.getByAddress(ipAddress);
            String hostaddr = myaddr.getHostAddress();
            return hostaddr;
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    public static int getEncrypPasswordType(String capabilities) {
        if (capabilities.contains("WPA2") && capabilities.contains("CCMP")) {
            // sEncrypType = "AES";
            // sAuth = "WPA2";
            return 1;
        } else if (capabilities.contains("WPA2")
                && capabilities.contains("TKIP")) {
            // sEncrypType = "TKIP";
            // sAuth = "WPA2";
            return 2;
        } else if (capabilities.contains("WPA")
                && capabilities.contains("TKIP")) {
            // EncrypType = "TKIP";
            // sAuth = "WPA";
            return 2;
        } else if (capabilities.contains("WPA")
                && capabilities.contains("CCMP")) {
            // sEncrypType = "AES";
            // sAuth = "WPA";
            return 1;
        } else if (capabilities.contains("WEP")) {
            return 3;
        } else {
            // sEncrypType = "NONE";
            // sAuth = "OPEN";
            return 0;
        }
    }
}
