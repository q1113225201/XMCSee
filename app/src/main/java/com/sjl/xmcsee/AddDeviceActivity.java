package com.sjl.xmcsee;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.basic.G;
import com.lib.EUIMSG;
import com.lib.FunSDK;
import com.sjl.xmcsee.base.BaseActivity;
import com.sjl.xmcsee.bean.SDBDeviceInfo;
import com.sjl.xmcsee.lib.support.FunError;
import com.sjl.xmcsee.util.ToastUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class AddDeviceActivity extends BaseActivity {
    private static final String TAG = "AddDeviceActivity";
    private EditText etSn;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etDevUsername;
    private EditText etDevPassword;
    private Button btnLogin;
    private Button btnAddDevice;
    private Button btnUpdateDevice;
    private Button btnDeleteDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        initView();
    }

    private void initView() {
        etSn = findViewById(R.id.etSn);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etDevUsername = findViewById(R.id.etDevUsername);
        etDevPassword = findViewById(R.id.etDevPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        btnAddDevice = findViewById(R.id.btnAddDevice);
        btnAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDevice();
            }
        });
        btnUpdateDevice = findViewById(R.id.btnUpdateDevice);
        btnUpdateDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               updateDevice();
            }
        });
        btnDeleteDevice = findViewById(R.id.btnDeleteDevice);
        btnDeleteDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDevice();
            }
        });
    }

    /**
     * 登陆
     */
    private void login() {
        FunSDK.DevLogin(App.regUser,etSn.getText().toString(),
                etDevUsername.getText().toString(),
                etDevPassword.getText().toString(),
                0);
    }

    /**
     * 删除设备
     */
    private void deleteDevice() {
        FunSDK.SysDeleteDev(App.regUser, etSn.getText().toString(), etUsername.getText().toString(), etPassword.getText().toString(),
                0);
//                (etSn.getText().toString()+etSn.getText().toString()).hashCode());
    }

    /**
     * 添加设备
     */
    private void addDevice() {
        SDBDeviceInfo sdbDeviceInfo = new SDBDeviceInfo();
        // 7, 雄迈摇头机
        sdbDeviceInfo.st_7_nType = 7;
        G.SetValue(sdbDeviceInfo.st_0_Devmac, etSn.getText().toString());
        G.SetValue(sdbDeviceInfo.st_1_Devname, "xm");
        // 设备端口,默认34567
        sdbDeviceInfo.st_6_nDMZTcpPort = 34567;
        G.SetValue(sdbDeviceInfo.st_4_loginName, etDevUsername.getText().toString());
        G.SetValue(sdbDeviceInfo.st_5_loginPsw, etDevPassword.getText().toString());
        Log.i(TAG,"************"+sdbDeviceInfo.toString());
        Log.i(TAG,"************etUsername="+etUsername.getText().toString()+",etPassword="+etPassword.getText().toString());
        FunSDK.SysAddDevice(App.regUser, G.ObjToBytes(sdbDeviceInfo), etUsername.getText().toString(), etPassword.getText().toString(),
                0);
//                (etSn.getText().toString()+etSn.getText().toString()).hashCode());
    }

    /**
     * 修改设备
     */
    private void updateDevice() {
        SDBDeviceInfo sdbDeviceInfo = new SDBDeviceInfo();
        // 7, 雄迈摇头机
        sdbDeviceInfo.st_7_nType = 7;
        G.SetValue(sdbDeviceInfo.st_0_Devmac, etSn.getText().toString());
        G.SetValue(sdbDeviceInfo.st_1_Devname, "xm_update");
        // 设备端口,默认34567
        sdbDeviceInfo.st_6_nDMZTcpPort = 34567;
        G.SetValue(sdbDeviceInfo.st_4_loginName, etDevUsername.getText().toString());
        G.SetValue(sdbDeviceInfo.st_5_loginPsw, etDevPassword.getText().toString());
        Log.i(TAG,"************"+sdbDeviceInfo.toString());
        Log.i(TAG,"************etUsername="+etUsername.getText().toString()+",etPassword="+etPassword.getText().toString());
        FunSDK.SysChangeDevInfo(App.regUser, G.ObjToBytes(sdbDeviceInfo), etUsername.getText().toString(), etPassword.getText().toString(),
                0);
//                (etSn.getText().toString()+etSn.getText().toString()).hashCode());
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getResult(Message msg) {
        Log.i(TAG, msg.toString());
        if (msg.what == EUIMSG.SYS_ADD_DEVICE) {
            ToastUtil.showToast(mContext, msg.arg1 == FunError.EE_OK ? "添加成功" : "添加失败");
        }else if (msg.what == EUIMSG.SYS_DELETE_DEV) {
            ToastUtil.showToast(mContext, msg.arg1 == FunError.EE_OK ? "删除成功" : "删除失败");
        }else if(msg.what== EUIMSG.DEV_LOGIN) {
            ToastUtil.showToast(mContext, msg.arg1 == FunError.EE_OK?"登录成功":"登录失败");
        }else if(msg.what== EUIMSG.SYS_CHANGEDEVINFO) {
            ToastUtil.showToast(mContext, msg.arg1 == FunError.EE_OK?"更新成功":"更新失败");
        }
    }
}
