package com.sjl.xmcsee;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.lib.EUIMSG;
import com.lib.FunSDK;
import com.sjl.xmcsee.base.BaseActivity;
import com.sjl.xmcsee.lib.support.FunError;
import com.sjl.xmcsee.util.ToastUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";
    private EditText etSn;
    private EditText etUsername;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
    }

    private void initView() {
        etSn = findViewById(R.id.etSn);
        etUsername = findViewById(R.id.etUsername);
        etUsername.setText("admin");
        etPassword = findViewById(R.id.etPassword);
        etPassword.setText("qazwsx");
        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        findViewById(R.id.btnLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
    }

    private void logout() {
        FunSDK.DevLogout(App.regUser,etSn.getText().toString(),
                (etSn.getText().toString()+etSn.getText().toString()).hashCode());
        Log.i(TAG,(etSn.getText().toString()+etSn.getText().toString()).hashCode()+"");
    }

    private void login() {
        FunSDK.DevLogin(App.regUser,etSn.getText().toString(),
                etUsername.getText().toString(),
                etPassword.getText().toString(),
                (etSn.getText().toString()+etSn.getText().toString()).hashCode());
        Log.i(TAG,(etSn.getText().toString()+etSn.getText().toString()).hashCode()+"");
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loginResult(Message msg) {
        Log.i(TAG, msg.toString());
        if(msg.what== EUIMSG.DEV_LOGIN) {
            ToastUtil.showToast(mContext, msg.arg1 == FunError.EE_OK?"登录成功":"登录失败");
        }
    }

}
