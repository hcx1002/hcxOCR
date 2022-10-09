package com.hcx.ocrapp.ui.start;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hcx.ocrapp.MainActivity;
import com.hcx.ocrapp.R;



public class StartActivity extends Activity {
    private Button btnTiaoguo;
    private int count = 5;
    private WebView webView;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if(msg.what==0){
                if (handler != null) {
                    btnTiaoguo.setText(getCount()+"");
                    handler.sendEmptyMessageDelayed(0,1000);
                    return true;
                }
            }
            return false;
        }
    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        colse();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        //状态栏透明
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        btnTiaoguo = findViewById(R.id.btn_tiaoguo);
        if (handler!=null){
            intView();
        }
        webView = findViewById(R.id.home_webview);
        webView.setWebViewClient(new WebViewClient()
//        {
//            //设置在webView点击打开的新网页在当前界面显示,而不跳转到新的浏览器中
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                view.loadUrl(url);
//                return true;
//            }
//        }
        );
        webView.getSettings().setJavaScriptEnabled(true);  //设置WebView属性,运行执行js脚本
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//支持通过Javascript打开新窗口
        webView.loadUrl("file:///android_asset/web/back.html");

    }
    public void intView(){
        btnTiaoguo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler=null;
                startActivity(new Intent(StartActivity.this, MainActivity.class));
                finish();
            }
        });
        handler.sendEmptyMessageDelayed(0,1000);
    }
    public int getCount() {
        count--;
        if (count == 0) {
            if (handler!=null){
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }

            finish();
        }
        return count;
    }
    public void colse(){
        SharedPreferences preferences = StartActivity.this.getSharedPreferences("setup", Context.MODE_PRIVATE);
        String start = preferences.getString("start","false");
        if ("false".equals(start)){
            startActivity(new Intent(StartActivity.this, MainActivity.class));
            handler.removeMessages(0);
            handler.removeCallbacksAndMessages(null);
            handler = null;
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        if (handler!=null){
            handler.removeMessages(0);
            handler.removeCallbacksAndMessages(null);
            handler=null;
        }
        super.onDestroy();
    }
}
