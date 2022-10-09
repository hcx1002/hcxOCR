package com.hcx.ocrapp.ui.utils;

import android.content.Context;

import java.io.File;

/**
 * Created by baidu.ORC
 */
public class FileUtil {
    public static File getSaveFile(Context context) {
        File file = new File(context.getFilesDir(), "pic.jpg");
        return file;
    }
}
