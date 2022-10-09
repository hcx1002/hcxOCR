package com.hcx.ocrapp.ui.shezhi.author;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.hcx.ocrapp.R;
import com.hcx.ocrapp.ui.utils.SavePhoto;

import java.text.ParseException;

public class ShezhiAuthorActivity extends Activity {
    private LinearLayout linearLayout;
    private Button returnBtn;

    private ImageView img2;
    private ImageView img3;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author);
        linearLayout = findViewById(R.id.Shezhi_layout);
        returnBtn = findViewById(R.id.title_return);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        img2 = findViewById(R.id.author_img2);
        img2.setImageResource(R.drawable.wx_number);
        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SavePhoto savePhoto = new SavePhoto(ShezhiAuthorActivity.this);
                try {
                    savePhoto.SaveBitmapFromView(img2);
                    img2.setImageResource(R.drawable.wx_number);
                    linearLayout.removeView(img2);
                    Toast.makeText(ShezhiAuthorActivity.this, "保存成功！", Toast.LENGTH_SHORT).show();
                } catch (ParseException e) {
                    Toast.makeText(ShezhiAuthorActivity.this, "保存失败！", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
        img3 = findViewById(R.id.author_img3);
        img3.setImageResource(R.mipmap.icon_1);
    }
}
