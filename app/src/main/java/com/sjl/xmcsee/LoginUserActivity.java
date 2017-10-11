package com.sjl.xmcsee;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lib.EUIMSG;
import com.lib.FunSDK;
import com.sjl.xmcsee.base.BaseActivity;
import com.sjl.xmcsee.lib.support.FunError;
import com.sjl.xmcsee.util.ToastUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class LoginUserActivity extends BaseActivity {
private EditText etUsername;
private EditText etPassword;
    private Button btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);

        initView();
    }

    private void initView() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                FunSDK.SysLoginToXM(App.regUser,etUsername.getText().toString(),etPassword.getText().toString(),0);
                FunSDK.SysGetDevList(App.regUser,etUsername.getText().toString(),etPassword.getText().toString(),0);
            }
        });
    }

    private static final String TAG = "LoginUserActivity";
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loginResult(Message msg) {
        Log.i(TAG, msg.toString());
        if(msg.what== EUIMSG.SYS_GET_DEV_INFO_BY_USER) {
            ToastUtil.showToast(mContext, msg.arg1 >=0?"获取用户设备列表成功":"获取用户设备列表失败");
        }
        if(msg.what== EUIMSG.SYS_GET_DEV_INFO_BY_USER_XM) {
            ToastUtil.showToast(mContext, msg.arg1 == FunError.EE_OK?"登录成功":"登录失败");
        }
    }
}
