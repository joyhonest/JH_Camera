package com.joyhonest.jh_fly;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;

public class TextToImageConverter {

    // 将文字转换为图片并保存
    public static boolean saveTextAsImage(String text, String fileName) {
        try {
            // 1. 创建 Bitmap 和 Canvas
            int width = 800;  // 图片宽度
            int height = 600; // 图片高度
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            // 2. 绘制背景
            canvas.drawColor(Color.WHITE);

            // 3. 设置文字样式
            Paint textPaint = new Paint();
            textPaint.setColor(Color.BLACK);
            textPaint.setTextSize(40); // 文字大小
            textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)); // 字体
            textPaint.setAntiAlias(true); // 抗锯齿

            // 4. 计算文字位置（居中）
            float textWidth = textPaint.measureText(text);
            float x = (width - textWidth) / 2;
            float y = height / 2f; // 垂直居中

            // 5. 绘制文字
            canvas.drawText(text, x, y, textPaint);

            // 6. 保存图片
//            File storageDir = Environment.getExternalStoragePublicDirectory(
//                    Environment.DIRECTORY_PICTURES
//            );
            File imageFile = new File(fileName);

            try (FileOutputStream out = new FileOutputStream(imageFile)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // 100=最高质量
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
