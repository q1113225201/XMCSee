package com.sjl.xmcsee;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.os.Message;
import android.util.Log;

import com.basic.G;
import com.lib.EFUN_ATTR;
import com.lib.FunSDK;
import com.lib.IFunSDKResult;
import com.lib.MsgContent;
import com.sjl.xmcsee.lib.sdk.struct.SInitParam;

import org.greenrobot.eventbus.EventBus;

/**
 * App
 *
 * @author SJL
 * @date 2017/9/25
 */

public class App extends Application implements IFunSDKResult {
    private static final String TAG = "App";
    // 应用证书,请在开放平台注册应用之后替换以下4个字段
    private static final String APP_UUID = "e0534f3240274897821a126be19b6d46";
    private static final String APP_KEY = "0621ef206a1d4cafbe0c5545c3882ea8";
    private static final String APP_SECRET = "90f8bc17be2a425db6068c749dee4f5d";
    private static final int APP_MOVECARD = 2;

    public static final String BASE_PATH = Environment.getExternalStorageDirectory() + "/xiongmai/";
    public static final String UPDATE_PATH = Environment.getExternalStorageDirectory() + "/xiongmai/";

    // "42.96.197.189";223.4.33.127
    public static final String SERVER_IP = "223.4.33.127;54.84.132.236;112.124.0.188";
    public static final int SERVER_PORT = 15010; // 更新版本的服务器端口
    public static App app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        init(this);
    }

    public static int regUser;

    public void init(Context context) {
        int result = 0;

        // 库初始化1
        SInitParam param = new SInitParam();
        param.st_0_nAppType = SInitParam.LOGIN_TYPE_MOBILE;
        result = FunSDK.Init(0, G.ObjToBytes(param));
        Log.i(TAG, "Init result=" + result);
        // 库初始化2
        FunSDK.MyInitNetSDK();

        // 设置临时文件保存路径
        FunSDK.SetFunStrAttr(EFUN_ATTR.APP_PATH, BASE_PATH);
        // 设置设备更新文件保存路径
        FunSDK.SetFunStrAttr(EFUN_ATTR.UPDATE_FILE_PATH, UPDATE_PATH);

        // 设置以互联网的方式访问
        result = FunSDK.SysInitNet(SERVER_IP, SERVER_PORT);
        Log.i(TAG, "SysInitNet result=" + result);

        // 初始化APP证书(APP启动后调用一次即可)
        FunSDK.XMCloundPlatformInit(
                APP_UUID,        // uuid
                APP_KEY, // App Key
                APP_SECRET, // App Secret
                APP_MOVECARD); // moveCard

        // 创建/注册库接口操作句柄
        regUser = FunSDK.RegUser(this);
        Log.i(TAG, "FunSDK.RegUser : " + regUser);

    }

    @Override
    public int OnFunSDKResult(Message message, MsgContent msgContent) {
        Log.i(TAG, message.toString() + "--------" + msgContent.toString());
        EventBus.getDefault().post(message);
        return 0;
    }
}
