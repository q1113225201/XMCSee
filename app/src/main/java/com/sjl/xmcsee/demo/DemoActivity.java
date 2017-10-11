package com.sjl.xmcsee.demo;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.basic.G;
import com.lib.EPTZCMD;
import com.lib.EUIMSG;
import com.lib.FunSDK;
import com.lib.IFunSDKResult;
import com.lib.MsgContent;
import com.sjl.xmcsee.App;
import com.sjl.xmcsee.R;
import com.sjl.xmcsee.base.BaseActivity;
import com.sjl.xmcsee.bean.SDBDeviceInfo;
import com.sjl.xmcsee.lib.sdk.struct.SDK_CONFIG_NET_COMMON_V2;
import com.sjl.xmcsee.lib.support.FunError;
import com.sjl.xmcsee.util.PermisstionUtil;
import com.sjl.xmcsee.util.ToastUtil;
import com.video.opengl.GLSurfaceView20;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DemoActivity extends BaseActivity implements View.OnClickListener, IFunSDKResult {
    private static final String TAG = "DemoActivity";
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnLogout;

    private EditText etWifiSSID;
    private EditText etWifiPwd;
    private Button btnSettingWifi;

    private EditText etSn;
    private EditText etDevName;
    private EditText etDevUsername;
    private EditText etDevPassword;
    private EditText etDevOldPassword;
    private Button btnDevAdd;
    private Button btnDevUpdate;
    private Button btnDevUpdatePwd;
    private Button btnDevDelete;
    private Button btnDevLogin;
    private Button btnDevLogout;
    private Button btnDevState;

    private RelativeLayout rlVideo;
    private TextView tvVideoState;
    private Button btnPlay;
    private Button btnPause;
    private Button btnStop;
    private Button btnSnap;
    private Button btnUp;
    private Button btnDown;
    private Button btnLeft;
    private Button btnRight;
    private Button btnStopRotate;

    private Button btnStartTalk;
    private Button btnStopTalk;

    private int hUser;
    private int hPlayer = -1;
    private int hTalker = 0;
    private int control = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        initView();
    }

    private void initView() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);

        etWifiSSID = findViewById(R.id.etWifiSSID);
        etWifiPwd = findViewById(R.id.etWifiPwd);
        btnSettingWifi = findViewById(R.id.btnSettingWifi);
        btnSettingWifi.setOnClickListener(this);

        etSn = findViewById(R.id.etSn);
        etDevName = findViewById(R.id.etDevName);
        etDevUsername = findViewById(R.id.etDevUsername);
        etDevPassword = findViewById(R.id.etDevPassword);
        etDevOldPassword = findViewById(R.id.etDevOldPassword);
        btnDevAdd = findViewById(R.id.btnDevAdd);
        btnDevAdd.setOnClickListener(this);
        btnDevUpdatePwd = findViewById(R.id.btnDevUpdatePwd);
        btnDevUpdatePwd.setOnClickListener(this);
        btnDevUpdate = findViewById(R.id.btnDevUpdate);
        btnDevUpdate.setOnClickListener(this);
        btnDevDelete = findViewById(R.id.btnDevDelete);
        btnDevDelete.setOnClickListener(this);
        btnDevLogin = findViewById(R.id.btnDevLogin);
        btnDevLogin.setOnClickListener(this);
        btnDevLogout = findViewById(R.id.btnDevLogout);
        btnDevLogout.setOnClickListener(this);
        btnDevState = findViewById(R.id.btnDevState);
        btnDevState.setOnClickListener(this);

        rlVideo = findViewById(R.id.rlVideo);
        tvVideoState = findViewById(R.id.tvVideoState);
        btnPlay = findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(this);
        btnPause = findViewById(R.id.btnPause);
        btnPause.setOnClickListener(this);
        btnStop = findViewById(R.id.btnStop);
        btnStop.setOnClickListener(this);
        btnSnap = findViewById(R.id.btnSnap);
        btnSnap.setOnClickListener(this);
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

        btnStartTalk = findViewById(R.id.btnStartTalk);
        btnStartTalk.setOnClickListener(this);
        btnStopTalk = findViewById(R.id.btnStopTalk);
        btnStopTalk.setOnClickListener(this);

        initVideo();
        initWifi();
        PermisstionUtil.requestPermissions(mContext, PermisstionUtil.STORAGE, 100, "", new PermisstionUtil.OnPermissionResult() {
            @Override
            public void granted(int requestCode) {
                Log.i(TAG, "granted");
            }

            @Override
            public void denied(int requestCode) {
                Log.i(TAG, "denied");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermisstionUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void initWifi() {
        WifiManager wifimanage = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        etWifiSSID.setText(wifimanage.getConnectionInfo().getSSID().replace("\"", ""));
    }

    private GLSurfaceView surfaceView;

    private void initVideo() {
        hUser = FunSDK.RegUser(this);
        surfaceView = new GLSurfaceView20(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rlVideo.addView(surfaceView, layoutParams);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                login();
                break;
            case R.id.btnLogout:
                logout();
                break;
            case R.id.btnSettingWifi:
                settingWifi();
                break;
            case R.id.btnDevAdd:
                devAdd();
                break;
            case R.id.btnDevUpdatePwd:
                devUpdatePwd();
                break;
            case R.id.btnDevUpdate:
                devUpdate();
                break;
            case R.id.btnDevDelete:
                devDelete();
                break;
            case R.id.btnDevLogin:
                devLogin();
                break;
            case R.id.btnDevLogout:
                devLogout();
                break;
            case R.id.btnDevState:
                devState();
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
            case R.id.btnSnap:
                snap();
                break;
            case R.id.btnUp:
                directionControl(EPTZCMD.TILT_UP, 0);
                break;
            case R.id.btnDown:
                directionControl(EPTZCMD.TILT_DOWN, 0);
                break;
            case R.id.btnLeft:
                directionControl(EPTZCMD.PAN_LEFT, 0);
                break;
            case R.id.btnRight:
                directionControl(EPTZCMD.PAN_RIGHT, 0);
                break;
            case R.id.btnStopRotate:
                directionControl(direction, 1);
                break;
            case R.id.btnStartTalk:
                startTalk();
                break;
            case R.id.btnStopTalk:
                stopTalk();
                break;
        }
    }

    /**
     * 结束对讲
     */
    private void stopTalk() {
        recoding = false;
        setSound(true);
        if (hTalker != 0) {
            FunSDK.DevStopTalk(hTalker);
            hTalker = 0;
        }
    }

    /**
     * 开始对讲
     */
    private void startTalk() {
        String sn = etSn.getText().toString();
        setSound(false);
        if (hTalker == 0) {
            hTalker = FunSDK.DevStarTalk(hUser, sn, 0);
        }
        sendRecord(sn);
    }

    /**
     * 设置声音
     *
     * @param open
     */
    private void setSound(boolean open) {
        FunSDK.MediaSetSound(hPlayer, open ? 100 : 0, 0);
    }

    private AudioRecord audioRecord;
    private boolean recoding;

    /**
     * 发送音频
     *
     * @param sn
     */
    private void sendRecord(final String sn) {
        recoding = true;
        int minBufferSize = AudioRecord.getMinBufferSize(8000,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        if (audioRecord == null) {
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, minBufferSize);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                audioRecord.startRecording();
                int bufferSizeInBytes = 640;
                byte[] audiodata = new byte[bufferSizeInBytes];
                int readsize = 0;
                while (recoding) {
                    readsize = audioRecord.read(audiodata, 0, bufferSizeInBytes);
                    if (AudioRecord.ERROR_INVALID_OPERATION != readsize && readsize > 0 && recoding) {
                        FunSDK.DevSendTalkData(sn, audiodata, readsize);
                    } else {
                        SystemClock.sleep(5);
                    }
                    Log.i(TAG, "*****sendTalk");
                }
                if (audioRecord.getState() == AudioRecord.RECORDSTATE_RECORDING) {
                    audioRecord.stop();
                }
                audioRecord.release();
                audioRecord = null;
                Log.i(TAG, "*****sendTalk end");
            }
        }).start();
    }


    /**
     * 设备状态
     */
    private void devState() {
        String sn = etSn.getText().toString();
        FunSDK.SysGetDevState(App.regUser, sn, 0);
    }

    private int direction;

    /**
     * 控制方向
     *
     * @param direction
     * @param isStop
     */
    private void directionControl(int direction, int isStop) {
        this.direction = direction;
        //isStop 1:停止
        FunSDK.DevPTZControl(hUser, etSn.getText().toString(), 0, direction, isStop, 4, control);
    }

    /**
     * 抓拍
     */
    private void snap() {
        String path = App.BASE_PATH + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".jpg";
        int result = FunSDK.MediaSnapImage(hPlayer, path, 0);
        Log.i(TAG, "result=" + result + path);
    }

    /**
     * 停止
     */
    private void stop() {
        FunSDK.MediaStop(hPlayer);
    }

    /**
     * 暂停
     */
    private void pause() {
        //-1暂停播放切换
        FunSDK.MediaPause(hPlayer, -1, control);
    }

    /**
     * 播放
     */
    private void play() {
        hPlayer = FunSDK.MediaRealPlay(hUser, etSn.getText().toString(), 0, 0, surfaceView, control);
        FunSDK.MediaSetSound(hPlayer, 100, control);
    }

    /**
     * 登出设备
     */
    private void devLogout() {
        String sn = etSn.getText().toString();
        FunSDK.DevLogout(App.regUser, sn,
                0);
    }

    /**
     * 登陆设备
     */
    private void devLogin() {
        String sn = etSn.getText().toString();
        String username = etDevUsername.getText().toString();
        String password = etDevPassword.getText().toString();
        FunSDK.DevLogin(App.regUser, sn,
                username,
                password,
                0);
    }

    /**
     * 删除设备
     */
    private void devDelete() {
        String sn = etSn.getText().toString();
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        FunSDK.SysDeleteDev(App.regUser, sn, username, password,
                0);
    }

    /**
     * 修改设备
     */
    private void devUpdate() {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String name = etDevName.getText().toString();
        String sn = etSn.getText().toString();
        String devUsername = etDevUsername.getText().toString();
        String devPassword = etDevPassword.getText().toString();
        SDBDeviceInfo sdbDeviceInfo = new SDBDeviceInfo();
        sdbDeviceInfo.st_7_nType = 0;
        G.SetValue(sdbDeviceInfo.st_0_Devmac, sn);
        G.SetValue(sdbDeviceInfo.st_1_Devname, name);
        // 设备端口,默认34567
        sdbDeviceInfo.st_6_nDMZTcpPort = 34567;
        G.SetValue(sdbDeviceInfo.st_4_loginName, devUsername);
        G.SetValue(sdbDeviceInfo.st_5_loginPsw, devPassword);
        Log.i(TAG, "************" + sdbDeviceInfo.toString());
        Log.i(TAG, "************username=" + username + ",password=" + password);
        FunSDK.SysChangeDevInfo(App.regUser, G.ObjToBytes(sdbDeviceInfo), username, password,
                0);
    }

    /**
     * 修改密码
     */
    private void devUpdatePwd() {
        String sn = etSn.getText().toString();
        String devOldPassword = etDevOldPassword.getText().toString();
        String devPassword = etDevPassword.getText().toString();
        JSONObject pswJson = new JSONObject();

        // 修改密码,传输时需要加密处理
        try {
            String new_pwd = FunSDK.DevMD5Encrypt(devPassword);
            String old_pwd = FunSDK.DevMD5Encrypt(devOldPassword);
            pswJson.put("EncryptType", "MD5");
            pswJson.put("NewPassWord", new_pwd);
            pswJson.put("PassWord", old_pwd);
            pswJson.put("UserName", "admin");
            pswJson.put("SessionID", "0x6E472E78");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        FunSDK.DevSetConfigByJson(App.regUser, sn, "ModifyPassword", pswJson.toString(), 0, 60000, 0);
    }

    /**
     * 添加设备
     */
    private void devAdd() {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String name = etDevName.getText().toString();
        String sn = etSn.getText().toString();
        String devUsername = etDevUsername.getText().toString();
        String devPassword = etDevPassword.getText().toString();
        SDBDeviceInfo sdbDeviceInfo = new SDBDeviceInfo();
        sdbDeviceInfo.st_7_nType = 0;
        G.SetValue(sdbDeviceInfo.st_0_Devmac, sn);
        G.SetValue(sdbDeviceInfo.st_1_Devname, name);
        // 设备端口,默认34567
        sdbDeviceInfo.st_6_nDMZTcpPort = 34567;
        G.SetValue(sdbDeviceInfo.st_4_loginName, devUsername);
        G.SetValue(sdbDeviceInfo.st_5_loginPsw, devPassword);
        Log.i(TAG, "************" + sdbDeviceInfo.toString());
        Log.i(TAG, "************etUsername=" + username + ",etPassword=" + password);
        FunSDK.SysAddDevice(App.regUser, G.ObjToBytes(sdbDeviceInfo), username, password,
                0);
    }

    /**
     * 设置Wifi
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
        Log.i(TAG, "data=" + data);
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
        Log.i(TAG, "info=" + info);
        int nGetType = 2; // 1:外网;2:内网 0x3:一起
        FunSDK.DevStartAPConfig(App.regUser, nGetType, ssid, data.toString(), info.toString(), formatIpAddress(dhcpInfo.gateway), pwdType, 0, mac, -1);
        Log.i(TAG, nGetType + "---\n" + ssid + "---\n" + data.toString() + "---\n" + info.toString() + "---\n" + formatIpAddress(dhcpInfo.gateway) + "---\n" + pwdType + "---\n" + 0 + "---\n" + mac + "---\n" + (-1));
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

    /**
     * 登出
     */
    private void logout() {

    }

    /**
     * 登陆
     */
    private void login() {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        FunSDK.SysGetDevList(App.regUser, username, password, 0);
    }

    @Override
    public int OnFunSDKResult(Message message, MsgContent msgContent) {
        Log.i(TAG, "******" + message.toString());
        if (message.what == EUIMSG.START_PLAY) {
            //视频播放
            tvVideoState.setText("播放" + (message.arg1 == FunError.EE_OK ? "成功" : "失败"));
        }
        if (message.what == EUIMSG.ON_PLAY_BUFFER_BEGIN) {
            //视频缓冲
            tvVideoState.setText("缓冲。。。");
        }
        if (message.what == EUIMSG.ON_PLAY_BUFFER_END) {
            //视频缓冲结束
            tvVideoState.setText("缓冲结束");
        }
        if (message.what == EUIMSG.ON_PLAY_INFO) {
            //视频播放中
            tvVideoState.setText("播放中");
        }
        return 0;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getResult(Message message) {
        Log.i(TAG, "******----" + message.toString());
        if (message.what == EUIMSG.DEV_AP_CONFIG) {
            if(message.arg1>=0) {
                //Wifi配置
                ToastUtil.showToast(mContext, "配置完成");
                SDK_CONFIG_NET_COMMON_V2 commonV2 = new SDK_CONFIG_NET_COMMON_V2();
                G.BytesToObj(commonV2, ((MsgContent) message.obj).pData);
                String devSn = G.ToString(commonV2.st_14_sSn);
                etSn.setText(devSn);
            }else{
                ToastUtil.showToast(mContext, "配置失败");
            }
        }
        if (message.what == EUIMSG.SYS_GET_DEV_INFO_BY_USER) {
            MsgContent msgContent = (MsgContent) message.obj;
            SDBDeviceInfo sdbDeviceInfo = new SDBDeviceInfo();
            int itemLen = G.Sizeof(sdbDeviceInfo);
            int count = msgContent.pData.length / itemLen;
            SDBDeviceInfo[] infos = new SDBDeviceInfo[itemLen];
            for (int i = 0; i < itemLen; i++) {
                infos[i] = new SDBDeviceInfo();
            }
            G.BytesToObj(infos, msgContent.pData);
            for (int i = 0; i < count; i++) {
                Log.i(TAG, "******----" + i + "---" + infos[i]);
            }
            //获取用户设备列表成功
            ToastUtil.showToast(mContext, message.arg1 >= 0 ? "获取用户设备列表成功" : "获取用户设备列表失败");
        }
        if (message.what == EUIMSG.DEV_LOGIN) {
            ToastUtil.showToast(mContext, message.arg1 == FunError.EE_OK ? "登录成功" : "登录失败");
        }
        if (message.what == EUIMSG.SYS_ADD_DEVICE) {
            ToastUtil.showToast(mContext, message.arg1 == FunError.EE_OK ? "添加成功" : "添加失败");
        }
        if (message.what == EUIMSG.SYS_DELETE_DEV) {
            ToastUtil.showToast(mContext, message.arg1 == FunError.EE_OK ? "删除成功" : "删除失败");
        }
        if (message.what == EUIMSG.DEV_LOGIN) {
            ToastUtil.showToast(mContext, message.arg1 == FunError.EE_OK ? "登录成功" : "登录失败");
        }
        if (message.what == EUIMSG.SYS_CHANGEDEVINFO) {
            ToastUtil.showToast(mContext, message.arg1 == FunError.EE_OK ? "更新成功" : "更新失败");
        }
        if (message.what == EUIMSG.DEV_SET_JSON) {
            ToastUtil.showToast(mContext, message.arg1 >= 0 ? "密码修改成功" : "密码修改失败");
        }
        if (message.what == EUIMSG.SYS_GET_DEV_STATE) {
            btnDevState.setText("设备状态" + (message.arg1 > 0 ? "在线" : "离线"));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        directionControl(direction, 1);
        stopTalk();
        stop();
        devLogout();
        FunSDK.UnRegUser(hUser);
    }
}
