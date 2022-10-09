package com.hcx.ocrapp.ui.shezhi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.hcx.ocrapp.R;
import com.hcx.ocrapp.ui.shezhi.author.ShezhiAuthorActivity;
import com.hcx.ocrapp.ui.shezhi.login.loginActivity;
import com.suke.widget.SwitchButton;
import com.unstoppable.submitbuttonview.SubmitButton;

import static com.baidu.tts.tools.cuid.util_GP.Util.TAG;

public class ShezhiFragment extends Fragment {

    private Button authorBtn;
    private Button updateBtn;
    private ImageView loginBun;
    private SwitchButton startSwitch;
    private SwitchButton tuijianSwitch;
    private SwitchButton saveSwitch;
    private TextView loginText;
    private WebView webView;

    @Override
    public void onResume() {
        SharedPreferences loginSP = getActivity().getSharedPreferences("login",Context.MODE_PRIVATE);
        String isLogin = loginSP.getString("isLogin","");
        String url = loginSP.getString("imgUrl","");
        Log.d(TAG, "onResume: "+isLogin);
        if (isLogin!=""){
            loginText.setText(isLogin+"");
            if(webView!=null){
                webView.setWebViewClient(new WebViewClient());
                webView.getSettings().setJavaScriptEnabled(true);  //设置WebView属性,运行执行js脚本
                webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//支持通过Javascript打开新窗口
                webView.loadUrl("file:///android_asset/web_1/xuehua.html");
            }
        }
        if (!"".equals(url)){
            Glide.with(getActivity()).load(url).into(loginBun);
        }

        super.onResume();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_shezhi, container, false);

        //登录
        SharedPreferences loginSP = getActivity().getSharedPreferences("login",Context.MODE_PRIVATE);
        String isLogin = loginSP.getString("isLogin","");
        String url = loginSP.getString("imgUrl","");
        Log.d(TAG, "onCreateView: isLongin："+isLogin);

        loginText = root.findViewById(R.id.shezhi_loginName);
        loginBun = root.findViewById(R.id.shezhi_loginImg);
        webView = root.findViewById(R.id.web_shezhi);
        if (isLogin!=""){
            loginText.setText(isLogin+"");
            webView.setWebViewClient(new WebViewClient());
            webView.getSettings().setJavaScriptEnabled(true);  //设置WebView属性,运行执行js脚本
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//支持通过Javascript打开新窗口
            webView.loadUrl("file:///android_asset/web_1/xuehua.html");
        }
        if (!"".equals(url)){
            Glide.with(getActivity()).load(url).into(loginBun);
        }

        loginBun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences loginSP = getActivity().getSharedPreferences("login",Context.MODE_PRIVATE);
                String isLogin = loginSP.getString("isLogin","");
                if ("".equals(isLogin)){
                    Intent intent = new Intent(getActivity(), loginActivity.class);
                    startActivity(intent);
                }else{
                    //ImageLoader.getInstance().displayImage("http://qzapp.qlogo.cn/qzapp/101947920/2BD9E057709C9D7098B740047D724EC2/100",loginBun);
                   showDialog(getActivity());
                }
            }
        });
        //开发者界面
        authorBtn = root.findViewById(R.id.btn_author);
        authorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShezhiAuthorActivity.class);
                startActivity(intent);
            }
        });
        updateBtn = root.findViewById(R.id.btn_update);
        //版本更新
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "当前版本："+packageName(getActivity()), Toast.LENGTH_SHORT).show();
            }
        });
        startSwitch = root.findViewById(R.id.switchBtn_2);
        tuijianSwitch = root.findViewById(R.id.switchBtn_3);
        saveSwitch = root.findViewById(R.id.switchBtn_4);
        //使用SharedPreferences存储数据 数据只能被本应用程序读、写。
        SharedPreferences preferences = getActivity().getSharedPreferences("setup", Context.MODE_PRIVATE);
         String start = preferences.getString("start","false");
        String tuijian = preferences.getString("tuijian","false");
        String save = preferences.getString("save","false");
        if ("false".equals(start)){
            startSwitch.setChecked(false);
        }else{
            startSwitch.setChecked(true);
        }
        if ("false".equals(tuijian)){
            tuijianSwitch.setChecked(false);
        }else{
            tuijianSwitch.setChecked(true);
        }
        if ("false".equals(save)){
            saveSwitch.setChecked(false);
        }else{
            saveSwitch.setChecked(true);
        }


        startSwitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                SharedPreferences preferences1 =getActivity().getSharedPreferences("setup",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences1.edit();
                if (isChecked){
                    editor.putString("start","true");
                }else {
                    editor.putString("start","false");
                }
                editor.commit();
            }
        });
        tuijianSwitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                SharedPreferences preferences1 =getActivity().getSharedPreferences("setup",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences1.edit();
                if (isChecked){
                    editor.putString("tuijian","true");
                }else {
                    editor.putString("tuijian","false");
                }
                editor.commit();
            }
        });
        saveSwitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                SharedPreferences preferences1 =getActivity().getSharedPreferences("setup",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences1.edit();
                if (isChecked){
                    editor.putString("save","true");
                }else {
                    editor.putString("save","false");
                }
                editor.commit();
            }
        });

        return root;
    }

    //获取软件版本
    public static String packageName(Context content) {
        PackageManager manager = content.getPackageManager();
        String name = null;
        try {
            PackageInfo info = manager.getPackageInfo(content.getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return name;
    }
    //底部选择框
    public void showDialog(final Context context){
        final BottomSheetDialog dialog = new BottomSheetDialog(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.layout_bottomsheetdialog,null);
        SharedPreferences preferences = context.getSharedPreferences("login",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        SubmitButton exitBtn = dialogView.findViewById(R.id.dialog_exit_login);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: -----------------------------");
                editor.putString("isLogin","");
                editor.putString("imgUrl","");
                editor.commit();
                exitBtn.doResult(true);
                dialog.dismiss();
                loginText.setText("登录");
                loginBun.setImageResource(R.drawable.icon_people);
                if (webView!=null){//把webview页面设置为空白
                    webView.loadDataWithBaseURL(null, "","text/html", "utf-8",null);
                }
            }
        });
        dialog.setContentView(dialogView);
        dialog.show();
    }
}