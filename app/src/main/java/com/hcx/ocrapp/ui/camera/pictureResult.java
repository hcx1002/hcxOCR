package com.hcx.ocrapp.ui.camera;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizeBag;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.hcx.ocrapp.R;
import com.hcx.ocrapp.fanyi.HttpGet;
import com.hcx.ocrapp.modle.WordData;
import com.hcx.ocrapp.sqlite.WordDB;
import com.unstoppable.submitbuttonview.SubmitButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class pictureResult extends Activity implements View.OnClickListener {
    private static final String TAG = "pictureResult";
    private static final String APP_ID = "20210323000738945";
    private static final String SECURITY_KEY = "2kHxnPhm_XtjKyFii6dW";
    private static final String TRANS_API_HOST = "http://api.fanyi.baidu.com/api/trans/vip/translate";
    private String words = "";
    private String isT;
    private String time;
    private String mText;
    private String isLogin;
    private TextView textWords;
    private TextView textTime;
    private TextView textFanyi;
    private Button btnReturn;
    private Button btnShare;
    private SeekBar seekBar;
    private SubmitButton btnCopy;
    private SubmitButton btnSave;
    private SubmitButton btnReader;
    private SubmitButton btnFanyi;
    private Context context = this;
    SpeechSynthesizer mSpeechSynthesizer= com.baidu.tts.client.SpeechSynthesizer.getInstance();
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what==0){
                textFanyi.setText("翻译结果：\n"+mText);
                btnFanyi.doResult(true);
            }
            return false;
        }
    });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_camera);
        textWords = findViewById(R.id.text_picture);
        textTime = findViewById(R.id.text_time);
        textFanyi = findViewById(R.id.text_fanyi);
        Intent intent = getIntent();
        words = intent.getStringExtra("word");
        isT = intent.getStringExtra("isT");
        time = intent.getStringExtra("time");
        textWords.setText(words);
        textTime.setText(time);

        btnReturn = (Button) findViewById(R.id.title_return);
        btnShare = (Button) findViewById(R.id.title_share);
        btnCopy = findViewById(R.id.btn_copy);
        btnSave = findViewById(R.id.btn_save);
        btnReader = findViewById(R.id.btn_reader);
        btnFanyi = findViewById(R.id.btn_fanyi);
        seekBar = findViewById(R.id.text_seekbar);

        btnReturn.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnCopy.setOnClickListener(this);
        btnReader.setOnClickListener(this);
        btnFanyi.setOnClickListener(this);



        mSpeechSynthesizer.setContext(context);
        mSpeechSynthesizer.setSpeechSynthesizerListener(speechLis);
        mSpeechSynthesizer.setAppId("23826455");
        mSpeechSynthesizer.setApiKey("dEA6hG2IMaeNPELD6NKcuadX","clonxvDjzSrSQ5fKRUa6TeXFF4IDpviG");
        mSpeechSynthesizer.setParam(com.baidu.tts.client.SpeechSynthesizer.PARAM_SPEAKER, "0"); // 设置发声的人声音，在线生效
        // 设置合成的音量，0-15 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "9");
        // 设置合成的语速，0-15 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5");
        // 设置合成的语调，0-15 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5");
        int i = mSpeechSynthesizer.initTts(TtsMode.ONLINE); // 初始化在线模式
        Log.d(TAG, "onCreate: 初始化在线模式："+i);
        SharedPreferences loginSP = context.getSharedPreferences("login",Context.MODE_PRIVATE);
        isLogin = loginSP.getString("isLogin","");
        if (isT==null&&!"".equals(isLogin)){
            zidongSave();
        }
        //控制进度条
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textWords.setTextSize(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        textWords.addTextChangedListener(new TextWatcher() {
            String oldText = "";
            String newText = "";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                oldText = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                newText = s.toString();
                if (!oldText.equals(newText)){
                    btnCopy.reset();
                    btnFanyi.reset();
                    btnReader.reset();
                    btnSave.reset();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_return:
                this.finish();//返回
                break;
            case R.id.title_share:  //分享
                allShare();
                break;
            case R.id.btn_copy: //复制
                copy();
                break;
            case R.id.btn_save: //保存
                if ("".equals(isLogin)){
                    Toast.makeText(pictureResult.this, "数据保存失败，未登录！", Toast.LENGTH_SHORT).show();
                    btnSave.doResult(false);
                }else {
                    saveWord();
                }

                break;
            case R.id.btn_reader:   //朗读
                words = textWords.getText().toString();
                Pattern p = Pattern.compile("\t|\r|\n");
                Matcher m = p.matcher(words);
                words = m.replaceAll("");
                ReadWord(words);
                btnReader.doResult(true);

                break;
            case R.id.btn_fanyi:    //翻译
                if ("".equals(isLogin)){
                    Toast.makeText(pictureResult.this, "数据翻译失败，未登录！", Toast.LENGTH_SHORT).show();
                    btnFanyi.doResult(false);
                }else {
                    fanyi();
                }
                break;
            default:
                break;
        }
    }

    /**
     * Android原生分享功能
     * 默认选取手机所有可以分享的APP
     */
    public void allShare() {
        String newWord = textWords.getText().toString();
        if (!newWord.equals("")){
            Intent share_intent = new Intent();
            share_intent.setAction(Intent.ACTION_SEND);//设置分享行为
            share_intent.setType("text/plain");//设置分享内容的类型
            share_intent.putExtra(Intent.EXTRA_SUBJECT, "幻彩希-文字识别");//添加分享内容标题
            share_intent.putExtra(Intent.EXTRA_TEXT, "识别文字:\n" + newWord);//添加分享内容
            //创建分享的Dialog
            share_intent = Intent.createChooser(share_intent, "share");
            startActivity(share_intent);
        }else{
            Toast.makeText(context, "分享失败，内容不能为空！", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Android原生分享功能
     *
     * @param appName:要分享的应用程序名称
     */
    private void share(String appName) {
        Intent share_intent = new Intent();
        share_intent.setAction(Intent.ACTION_SEND);
        share_intent.setType("text/plain");
        share_intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        share_intent.putExtra(Intent.EXTRA_TEXT, "推荐您使用一款软件:" + appName);
        share_intent = Intent.createChooser(share_intent, "分享");
        startActivity(share_intent);
    }

    private void copy() {
        String newWord = textWords.getText().toString();
        if (!newWord.equals("")){
            //获取剪贴板管理器：
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("Label", newWord);
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);
            Toast.makeText(context, "复制成功！", Toast.LENGTH_SHORT).show();
            btnCopy.doResult(true);
        }else {
            Toast.makeText(context, "复制失败，内容不能为空！", Toast.LENGTH_SHORT).show();
            btnCopy.doResult(false);
        }

    }

    //保存操作
    private void saveWord() {
        String newWord = textWords.getText().toString().trim();
        Log.d(TAG, "saveWord: newWord"+newWord);
        if (!words.equals(newWord)) {
            words = newWord;
            WordDB worddb = new WordDB(pictureResult.this);
            if (!words.equals("")) {
                WordData word = new WordData(worddb.getMaxId() + 1, words, worddb.getTime(), isLogin);
                worddb.addWord(word);
                Toast.makeText(pictureResult.this, "数据保存成功！",
                        Toast.LENGTH_SHORT).show();
                btnSave.doResult(true);
            } else {
                Toast.makeText(pictureResult.this, "数据保存失败！",
                        Toast.LENGTH_SHORT).show();
                btnSave.doResult(false);
            }

        }else{
            Toast.makeText(pictureResult.this, "数据保存失败，已存在！", Toast.LENGTH_SHORT).show();
            btnSave.doResult(false);
        }


    }
    private void ReadWord(String words){
        Log.d(TAG, "ReadWord: 长度："+words.length());
        if (words.length()>=60){
            List<SpeechSynthesizeBag> bags = new ArrayList<SpeechSynthesizeBag>();
            char[] chars = words.toCharArray();
            StringBuilder sb = new StringBuilder();
            for (int i=0;i<words.length();i++){
                sb.append(chars[i]);
                if (sb.length()>=50){
                    bags.add(getSpeechSynthesizeBag(new String(sb)));
                    sb.delete(0,sb.length());
                }
            }
            bags.add(getSpeechSynthesizeBag(new String(sb)));
            mSpeechSynthesizer.batchSpeak(bags);
        }else{
            mSpeechSynthesizer.speak(words);
        }

    }
    private void zidongSave(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("setup", Context.MODE_PRIVATE);
        String save = sharedPreferences.getString("save","false");
        if ("true".equals(save)){
            saveWord();
        }

    }
    private void fanyi(){
        words = textWords.getText().toString();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                InputStream input = null;
                BufferedReader reader = null;
                HttpGet httpGet = new HttpGet(APP_ID,SECURITY_KEY);
                Map<String, String> params = httpGet.buildParams(words, "en", "zh");
                String url = httpGet.getUrlWithQueryString(TRANS_API_HOST, params);
                Log.d(TAG, "run: url："+url);
                try {
                    conn = (HttpURLConnection) new URL(url).openConnection();
                    conn.setReadTimeout(5000);
                    input = conn.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder builder = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    String text = builder.toString();
                    formatText(text);
                    handler.sendEmptyMessage(0);
                    Log.d(TAG, "run: ----：" + text);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

    }
    private SpeechSynthesizeBag getSpeechSynthesizeBag(String text) {
        SpeechSynthesizeBag speechSynthesizeBag = new SpeechSynthesizeBag();
        //需要合成的文本text的长度不能超过120个GBK字节。
        speechSynthesizeBag.setText(text);
        //speechSynthesizeBag.setUtteranceId(utteranceId);
        return speechSynthesizeBag;
    }
    //格式化json
    public void formatText(String text){
        try {
            JSONObject jsonObject = new JSONObject(text);
            JSONArray jsonArray = jsonObject.getJSONArray("trans_result");
            String fanyi="";
            for (int i=0;i<jsonArray.length();i++){
                JSONObject data = jsonArray.getJSONObject(i);
                fanyi += data.getString("dst")+"\n";
            }
            Log.d(TAG, "getText: 翻译结果："+decode(fanyi));
            mText = decode(fanyi);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //语音合成结束回调接口
    SpeechSynthesizerListener speechLis = new SpeechSynthesizerListener() {
        @Override
        public void onSynthesizeStart(String s) {

        }

        @Override
        public void onSynthesizeDataArrived(String s, byte[] bytes, int i, int i1) {

        }

        @Override
        public void onSynthesizeFinish(String s) {
        }

        @Override
        public void onSpeechStart(String s) {

        }

        @Override
        public void onSpeechProgressChanged(String s, int i) {

        }

        @Override
        public void onSpeechFinish(String s) {
        }

        @Override
        public void onError(String s, SpeechError speechError) {
        }
    };

    //按钮动画结束回调接口
    public SubmitButton.OnResultEndListener getListenerReader(SubmitButton s){
        SubmitButton.OnResultEndListener listener = new SubmitButton.OnResultEndListener() {
            @Override
            public void onResultEnd() {
                s.reset();
            }
        };
        return listener;
    }




    //将UniCode编码转换成汉字
    public String decode(String unicode) {
        StringBuffer str = new StringBuffer();
        String[] hex = unicode.split("\\\\u");
        for (int i = 1; i < hex.length; i++) {
            int data = Integer.parseInt(hex[i], 16);
            str.append((char) data);
        }
        return str.length() > 0 ? str.toString() : unicode;
    }

    @Override
    protected void onDestroy() {
        if (mSpeechSynthesizer!=null){
            mSpeechSynthesizer.stop();
            mSpeechSynthesizer.release();
            mSpeechSynthesizer = null;
            Log.d(TAG, "onDestroy: "+"释放资源成功");
        }
        super.onDestroy();
    }

}

