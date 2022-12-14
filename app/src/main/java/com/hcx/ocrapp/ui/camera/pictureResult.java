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
                textFanyi.setText("???????????????\n"+mText);
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
        mSpeechSynthesizer.setParam(com.baidu.tts.client.SpeechSynthesizer.PARAM_SPEAKER, "0"); // ???????????????????????????????????????
        // ????????????????????????0-15 ????????? 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "9");
        // ????????????????????????0-15 ????????? 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5");
        // ????????????????????????0-15 ????????? 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5");
        int i = mSpeechSynthesizer.initTts(TtsMode.ONLINE); // ?????????????????????
        Log.d(TAG, "onCreate: ????????????????????????"+i);
        SharedPreferences loginSP = context.getSharedPreferences("login",Context.MODE_PRIVATE);
        isLogin = loginSP.getString("isLogin","");
        if (isT==null&&!"".equals(isLogin)){
            zidongSave();
        }
        //???????????????
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
                this.finish();//??????
                break;
            case R.id.title_share:  //??????
                allShare();
                break;
            case R.id.btn_copy: //??????
                copy();
                break;
            case R.id.btn_save: //??????
                if ("".equals(isLogin)){
                    Toast.makeText(pictureResult.this, "?????????????????????????????????", Toast.LENGTH_SHORT).show();
                    btnSave.doResult(false);
                }else {
                    saveWord();
                }

                break;
            case R.id.btn_reader:   //??????
                words = textWords.getText().toString();
                Pattern p = Pattern.compile("\t|\r|\n");
                Matcher m = p.matcher(words);
                words = m.replaceAll("");
                ReadWord(words);
                btnReader.doResult(true);

                break;
            case R.id.btn_fanyi:    //??????
                if ("".equals(isLogin)){
                    Toast.makeText(pictureResult.this, "?????????????????????????????????", Toast.LENGTH_SHORT).show();
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
     * Android??????????????????
     * ???????????????????????????????????????APP
     */
    public void allShare() {
        String newWord = textWords.getText().toString();
        if (!newWord.equals("")){
            Intent share_intent = new Intent();
            share_intent.setAction(Intent.ACTION_SEND);//??????????????????
            share_intent.setType("text/plain");//???????????????????????????
            share_intent.putExtra(Intent.EXTRA_SUBJECT, "?????????-????????????");//????????????????????????
            share_intent.putExtra(Intent.EXTRA_TEXT, "????????????:\n" + newWord);//??????????????????
            //???????????????Dialog
            share_intent = Intent.createChooser(share_intent, "share");
            startActivity(share_intent);
        }else{
            Toast.makeText(context, "????????????????????????????????????", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Android??????????????????
     *
     * @param appName:??????????????????????????????
     */
    private void share(String appName) {
        Intent share_intent = new Intent();
        share_intent.setAction(Intent.ACTION_SEND);
        share_intent.setType("text/plain");
        share_intent.putExtra(Intent.EXTRA_SUBJECT, "??????");
        share_intent.putExtra(Intent.EXTRA_TEXT, "???????????????????????????:" + appName);
        share_intent = Intent.createChooser(share_intent, "??????");
        startActivity(share_intent);
    }

    private void copy() {
        String newWord = textWords.getText().toString();
        if (!newWord.equals("")){
            //???????????????????????????
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            // ?????????????????????ClipData
            ClipData mClipData = ClipData.newPlainText("Label", newWord);
            // ???ClipData?????????????????????????????????
            cm.setPrimaryClip(mClipData);
            Toast.makeText(context, "???????????????", Toast.LENGTH_SHORT).show();
            btnCopy.doResult(true);
        }else {
            Toast.makeText(context, "????????????????????????????????????", Toast.LENGTH_SHORT).show();
            btnCopy.doResult(false);
        }

    }

    //????????????
    private void saveWord() {
        String newWord = textWords.getText().toString().trim();
        Log.d(TAG, "saveWord: newWord"+newWord);
        if (!words.equals(newWord)) {
            words = newWord;
            WordDB worddb = new WordDB(pictureResult.this);
            if (!words.equals("")) {
                WordData word = new WordData(worddb.getMaxId() + 1, words, worddb.getTime(), isLogin);
                worddb.addWord(word);
                Toast.makeText(pictureResult.this, "?????????????????????",
                        Toast.LENGTH_SHORT).show();
                btnSave.doResult(true);
            } else {
                Toast.makeText(pictureResult.this, "?????????????????????",
                        Toast.LENGTH_SHORT).show();
                btnSave.doResult(false);
            }

        }else{
            Toast.makeText(pictureResult.this, "?????????????????????????????????", Toast.LENGTH_SHORT).show();
            btnSave.doResult(false);
        }


    }
    private void ReadWord(String words){
        Log.d(TAG, "ReadWord: ?????????"+words.length());
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
                Log.d(TAG, "run: url???"+url);
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
                    Log.d(TAG, "run: ----???" + text);
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
        //?????????????????????text?????????????????????120???GBK?????????
        speechSynthesizeBag.setText(text);
        //speechSynthesizeBag.setUtteranceId(utteranceId);
        return speechSynthesizeBag;
    }
    //?????????json
    public void formatText(String text){
        try {
            JSONObject jsonObject = new JSONObject(text);
            JSONArray jsonArray = jsonObject.getJSONArray("trans_result");
            String fanyi="";
            for (int i=0;i<jsonArray.length();i++){
                JSONObject data = jsonArray.getJSONObject(i);
                fanyi += data.getString("dst")+"\n";
            }
            Log.d(TAG, "getText: ???????????????"+decode(fanyi));
            mText = decode(fanyi);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //??????????????????????????????
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

    //??????????????????????????????
    public SubmitButton.OnResultEndListener getListenerReader(SubmitButton s){
        SubmitButton.OnResultEndListener listener = new SubmitButton.OnResultEndListener() {
            @Override
            public void onResultEnd() {
                s.reset();
            }
        };
        return listener;
    }




    //???UniCode?????????????????????
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
            Log.d(TAG, "onDestroy: "+"??????????????????");
        }
        super.onDestroy();
    }

}

