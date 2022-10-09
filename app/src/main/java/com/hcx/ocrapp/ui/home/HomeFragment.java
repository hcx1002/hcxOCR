package com.hcx.ocrapp.ui.home;

import android.animation.Animator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.GeneralResult;
import com.bumptech.glide.Glide;
import com.hcx.ocrapp.R;
import com.hcx.ocrapp.ui.camera.OCRManager;
import com.hcx.ocrapp.ui.camera.pictureResult;
import com.hcx.ocrapp.ui.utils.FileUtil;
import com.youth.banner.BannerConfig;
import com.youth.banner.loader.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private ImageButton cameraButton;
    private View mRoot;
    ProgressDialog pd;
    private com.youth.banner.Banner banner;
    private ImageView imageView;
    private Animation animator;
    private Timer timer;    //设置定时器
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what==1){
                imageView.startAnimation(animator);
                return true;
            }
            return false;
        }
    });

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        mRoot = root;
        cameraButton = root.findViewById(R.id.btn_saomiao);
        MainOnClickListener mainOnClickListener = new MainOnClickListener();
        cameraButton.setOnClickListener(mainOnClickListener);
        OCR.getInstance(getActivity()).initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken accessToken) {
                // 调用成功，返回AccessToken对象
                String token = accessToken.getAccessToken();
                Log.e(TAG,accessToken.toString()+"调用成功");
            }
            @Override
            public void onError(OCRError ocrError) {
                // 调用失败，返回OCRError子类SDKError对象
                Log.e(TAG,ocrError.toString()+"调用失败");
            }
        },getActivity());
        initView(root);

        //创建动画
        imageView = root.findViewById(R.id.imageView4);
        animator = AnimationUtils.loadAnimation(getActivity(),R.anim.rotate_anim);
        imageView.startAnimation(animator);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
            }
        },0,3500);
        return root;
    }
    private void initView(View view){
        banner = view.findViewById(R.id.home_banner);
        //图片资源
        int[] imageResour = new int[]{R.mipmap.banner1,R.mipmap.banner2,R.mipmap.banner3};
        List<Integer> imgeList = new ArrayList<>();
        //轮播标题
        String[] mtitle = new String[]{"图片1", "图片2","图片3"};
        List<String> titleList = new ArrayList<>();
        for (int i=0;i<imageResour.length;i++){
            imgeList.add(imageResour[i]);
            titleList.add(mtitle[i]);
            banner.setImageLoader(new ImageLoader() {
                @Override
                public void displayImage(Context context, Object path, ImageView imageView) {
                    Glide.with(HomeFragment.this).load(path).into(imageView);
                }
            });
        }
        //设置轮播的动画效果,里面有很多种特效,可以到GitHub上查看文档。
        //banner.setBannerAnimation(Transformer.Accordion);
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        banner.setImages(imgeList);
        banner.setBannerTitles(titleList);
        //设置指示器位置（即图片下面的那个小圆点）
        banner.setIndicatorGravity(BannerConfig.CENTER);
        banner.setDelayTime(3000);
        banner.start();
    }


    private class MainOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_saomiao:
                    // 生成intent对象
                    Intent intent = new Intent(getActivity(), com.baidu.ocr.ui.camera.CameraActivity.class);

                    // 设置临时存储
                    intent.putExtra(com.baidu.ocr.ui.camera.CameraActivity.KEY_OUTPUT_FILE_PATH, FileUtil.getSaveFile(getActivity()).getAbsolutePath());

                    // 调用除银行卡，身份证等识别的activity
                    intent.putExtra(com.baidu.ocr.ui.camera.CameraActivity.KEY_CONTENT_TYPE, com.baidu.ocr.ui.camera.CameraActivity.CONTENT_TYPE_GENERAL);

                    startActivityForResult(intent, 106);

                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 106 && resultCode == Activity.RESULT_OK) {
            //调用等待框
            circle_dialog(mRoot);
            // 获取调用参数
            String contentType = data.getStringExtra(com.baidu.ocr.ui.camera.CameraActivity.KEY_CONTENT_TYPE);
            // 通过临时文件获取拍摄的图片
            String filePath = FileUtil.getSaveFile(getActivity()).getAbsolutePath();

            //通用文字识别
            OCRManager.recognizeGeneralBasic(getActivity(), filePath, new OCRManager.OCRCallBack<GeneralResult>() {
                @Override
                public void succeed(GeneralResult data) {

                    // 调用成功，返回GeneralResult对象
                    String content = OCRManager.getResult(data);
                    Log.e(TAG, "成功信息："+content );
                    //将信息传递到pictureResult
                    Intent intent = new Intent(getActivity(), pictureResult.class);
                    intent.putExtra("word",fromatJson(content));
                    startActivity(intent);
                    //关闭数据等待框
                    pd.dismiss();
                }

                @Override
                public void failed(OCRError error) {
                    // 调用失败，返回OCRError对象
                    Log.e(TAG, "错误信息：" + error.getMessage());
                    pd.dismiss();
                    Toast.makeText(getActivity(), "调用失败！请检查网络或权限", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    //解析JSON数据
    public String fromatJson(String json){
        StringBuffer result = new StringBuffer();
        JSONObject data = null;
        try {
            data = new JSONObject(json);
            JSONArray dataArr = data.getJSONArray("words_result");

            for (int i = 0;i<dataArr.length();i++){
                JSONObject words =dataArr.getJSONObject(i);
                String word = words.getString("words");
                Log.d(TAG, "fromatJson: word："+word);
                result.append(word+"\n");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result.toString();
    }
    //数据处理等待
    public void circle_dialog(View view){
        pd=new ProgressDialog(getActivity());
        pd.setMessage("正在处理数据");
        pd.setCancelable(false);
        pd.show();
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}