package com.hcx.ocrapp.fanyi;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import static android.content.ContentValues.TAG;

public class HttpGet {
    protected static final int SOCKET_TIMEOUT = 10000; // 10S
    protected static final String GET = "GET";
    private static String mText;
    private String appid;
    private String securityKey;

    public HttpGet(String appid, String securityKey) {
        this.appid = appid;
        this.securityKey = securityKey;
    }

    public void get(String host, Map<String, String> params) {
        Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpURLConnection conn = null;
                    InputStream input = null;
                    BufferedReader reader = null;
                    String url = getUrlWithQueryString(host, params);
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
                        Log.d(TAG, "run: ----：" + text);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

        });
        thread.start();
    }
    public static void formatText(String text){
        try {
            JSONObject jsonObject = new JSONObject(text);
            JSONArray jsonArray = jsonObject.getJSONArray("trans_result");
            JSONObject data = jsonArray.getJSONObject(0);
            String fanyi = data.getString("dst");
            Log.d(TAG, "formatText: "+fanyi);
            Log.d(TAG, "getText: 翻译结果："+decode(fanyi));
            mText = decode(fanyi);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //将UniCode编码转换成汉字
    public static String decode(String unicode) {
        StringBuffer str = new StringBuffer();
        String[] hex = unicode.split("\\\\u");
        for (int i = 1; i < hex.length; i++) {
            int data = Integer.parseInt(hex[i], 16);
            str.append((char) data);
        }
        return str.length() > 0 ? str.toString() : unicode;
    }


    public static String getUrlWithQueryString(String url, Map<String, String> params) {
        if (params == null) {
            return url;
        }

        StringBuilder builder = new StringBuilder(url);
        if (url.contains("?")) {
            builder.append("&");
        } else {
            builder.append("?");
        }

        int i = 0;
        for (String key : params.keySet()) {
            String value = params.get(key);
            if (value == null) { // 过滤空的key
                continue;
            }

            if (i != 0) {
                builder.append('&');
            }

            builder.append(key);
            builder.append('=');
            builder.append(encode(value));

            i++;
        }

        return builder.toString();
    }

    protected static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 对输入的字符串进行URL编码, 即转换为%20这种形式
     *
     * @param input 原文
     * @return URL编码. 如果编码失败, 则返回原文
     */
    public static String encode(String input) {
        if (input == null) {
            return "";
        }

        try {
            return URLEncoder.encode(input, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return input;
    }
    public Map<String, String> buildParams(String query, String from, String to) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("q", query);
        params.put("from", from);
        params.put("to", to);

        params.put("appid", appid);

        // 随机数
        String salt = String.valueOf(System.currentTimeMillis());
        params.put("salt", salt);

        // 签名
        String src = appid + query + salt + securityKey; // 加密前的原文
        params.put("sign", MD5.md5(src));
        Log.d(TAG, "buildParams: "+params);
        return params;
    }

    private static TrustManager myX509TrustManager = new X509TrustManager() {

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }
    };

}
