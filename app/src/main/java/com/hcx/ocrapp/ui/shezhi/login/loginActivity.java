package com.hcx.ocrapp.ui.shezhi.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hcx.ocrapp.R;
import com.hcx.ocrapp.modle.User;
import com.hcx.ocrapp.sqlite.UserDB;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.unstoppable.submitbuttonview.SubmitButton;

import org.json.JSONException;
import org.json.JSONObject;

public class loginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "loginActivity";
    private SubmitButton loginBtn;
    private SubmitButton regisBtn;
    private SubmitButton QQBtn;
    private EditText usernameText;
    private EditText passwordText;
    private ImageView qqImg;
    private WebView webView;
    ProgressDialog pd;
    private String username;
    private String password;
    private String loginReg="^[\\S]{2,8}$";
    private String regisReg="^[\\w]{4,10}$";
    private Tencent tencent;
    private IUiListener userInfoListener;
    private IUiListener QQLoginCallBack;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what==1){
                UserInfo userInfo = new UserInfo(loginActivity.this,tencent.getQQToken());
                userInfo.getUserInfo(userInfoListener);
            }
            return false;
        }
    });
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //去出标题栏
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        //登录
        loginBtn = findViewById(R.id.login_login);
        regisBtn = findViewById(R.id.login_regis);
        QQBtn = findViewById(R.id.login_qq);
        usernameText = findViewById(R.id.login_text_user);
        passwordText = findViewById(R.id.login_text_pass);
        qqImg = findViewById(R.id.shezhi_loginImg);

        loginBtn.setOnClickListener(this);
        regisBtn.setOnClickListener(this);
        QQBtn.setOnClickListener(this);

        regisBtn.setOnResultEndListener(listenerRegis);
        loginBtn.setOnResultEndListener(listenerLogin);

        initLoginID();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_login:
                login(String.valueOf(usernameText.getText()),String.valueOf(passwordText.getText()));
                break;
            case R.id.login_regis:
                username = String.valueOf(usernameText.getText());
                password = String.valueOf(passwordText.getText());
                //Toast.makeText(this, "账号："+username+" 密码："+password, Toast.LENGTH_SHORT).show();
                if (!username.matches(loginReg)){
                    Toast.makeText(this, "账号必须是2-8位非空白字符", Toast.LENGTH_SHORT).show();
                    regisBtn.doResult(false);
                }else if (!password.matches(regisReg)){
                    Toast.makeText(this, "密码需要4-10位A-Za-z0-9_字符", Toast.LENGTH_SHORT).show();
                    regisBtn.doResult(false);
                }else{
                    regis(new User(username,password));
                }
                break;
            case  R.id.login_qq:
                qqLogin();
                break;
        }
    }



    private void regis(User user) {
        UserDB userDB = new UserDB(loginActivity.this);
        if (!userDB.login(user.getUsername(),null)){
            userDB.addUser(user);
            Toast.makeText(this, "注册成功！", Toast.LENGTH_SHORT).show();
            regisBtn.doResult(true);
        }else {
            Toast.makeText(this, "账号已存在！", Toast.LENGTH_SHORT).show();
            regisBtn.doResult(false);
        }

    }

    private void login(String name,String pass) {
        UserDB userDB = new UserDB(loginActivity.this);
        SharedPreferences sharedPreferences = this.getSharedPreferences("login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (userDB.login(name,pass)){
            Toast.makeText(this, "登录成功！", Toast.LENGTH_SHORT).show();
            loginBtn.doResult(true);
            editor.putString("isLogin",name);
            editor.commit();
            //startActivity(new Intent(loginActivity.this, ShezhiFragment.class));
            this.finish();
        }else{
            Toast.makeText(this, "登录失败！账号或密码错误！", Toast.LENGTH_SHORT).show();
            loginBtn.doResult(false);
            editor.putString("isLogin","");
            editor.commit();
        }
    }

    //按钮regisBtn动画结束回调
    SubmitButton.OnResultEndListener listenerRegis = new SubmitButton.OnResultEndListener() {
        @Override
        public void onResultEnd() {
            regisBtn.reset();
        }
    };
    SubmitButton.OnResultEndListener listenerLogin = new SubmitButton.OnResultEndListener() {
        @Override
        public void onResultEnd() {
            loginBtn.reset();
        }
    };
    private void qqLogin() {
        tencent = Tencent.createInstance("101947920",loginActivity.this);
        if (tencent == null) {
            Log.d(TAG, "onCreate: Tencent instance create fail!");
            finish();
        }
        if (tencent.isQQInstalled(loginActivity.this)) { // 判断QQ是否安装了
            tencent.login(loginActivity.this,"all",QQLoginCallBack);

        } else {
            Toast.makeText(this, "请先安装QQ", Toast.LENGTH_SHORT).show();
            QQBtn.doResult(false);

        }
    }

    //qq数据返回


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode,
                data, QQLoginCallBack);

        if (requestCode == Constants.REQUEST_API) {
            if (resultCode == Constants.REQUEST_LOGIN) {
                Tencent.handleResultData(data, QQLoginCallBack);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void initLoginID() {
        QQLoginCallBack = new IUiListener(){
            @Override
            public void onComplete(Object o) {
                Log.d("-----------","QQ登录成功：");
                if (o == null) {
                    return;
                }
                try {
                    JSONObject jo = (JSONObject) o;
                    int ret = jo.getInt("ret");
                    System.out.println("json=" + String.valueOf(jo));
                    if (ret == 0) {
                        String openID = jo.getString("openid");
                        String accessToken = jo.getString("access_token");
                        String expires = jo.getString("expires_in");
                        tencent.setOpenId(openID);
                        tencent.setAccessToken(accessToken, expires);
                        circle_dialog(loginActivity.this);//调用等待框
                        handler.sendEmptyMessage(1);
                    }

                } catch (Exception e) {
                    // TODO: handle exception
                }
                QQBtn.doResult(true);
            }

            @Override
            public void onError(UiError e) {
                Toast.makeText(loginActivity.this, "登录失败：", Toast.LENGTH_SHORT).show();
                Log.d("-----------","QQ登录失败：" + e.toString());
                QQBtn.doResult(false);
                QQBtn.reset();
            }

            @Override
            public void onCancel() {
                Toast.makeText(loginActivity.this, "登录取消：", Toast.LENGTH_SHORT).show();
                Log.d("-----------","QQ登录取消：");
                QQBtn.doResult(false);
                QQBtn.reset();
            }

            @Override
            public void onWarning(int i) {
                QQBtn.doResult(false);
                QQBtn.reset();
            }
        };
        userInfoListener = new IUiListener() {
            @Override
            public void onComplete(Object o) {
                if (o==null){
                    return;
                }
                JSONObject info = (JSONObject) o;
                try {
                    int ret = info.getInt("ret");
                    if (ret==-1){
                        Toast.makeText(loginActivity.this, "还未登录！", Toast.LENGTH_SHORT).show();
                    }else {
                        SharedPreferences sharedPreferences = loginActivity.this.getSharedPreferences("login", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        String nickName = info.getString("nickname");
                        String imgUrl=info.getString("figureurl_qq_2");
                        editor.putString("imgUrl",imgUrl);
                        editor.commit();
                        UserDB userDB = new UserDB(loginActivity.this);
                        if (!userDB.login(nickName,null)){
                            userDB.addUser(new User(nickName,tencent.getOpenId()));
                        }
                        pd.dismiss();
                        login(nickName,tencent.getOpenId());
                        QQBtn.doResult(true);
                        logout();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(UiError uiError) {
                QQBtn.doResult(false);
            }

            @Override
            public void onCancel() {
                QQBtn.doResult(false);
                QQBtn.reset();
            }

            @Override
            public void onWarning(int i) {
                QQBtn.doResult(false);
                QQBtn.reset();
            }
        };

    }
    public void logout()
    {
        tencent.logout(this);
    }
    //数据处理等待
    public void circle_dialog(loginActivity view){
        pd=new ProgressDialog(this);
        pd.setMessage("正在处理数据");
        pd.setCancelable(false);
        pd.show();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
