package com.liteon.icampusguardian.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapUtils {  
	
    private static int calculateInSampleSize(BitmapFactory.Options options,  
            int reqWidth, int reqHeight) {  
        final int height = options.outHeight;  
        final int width = options.outWidth;  
        int inSampleSize = 1;  
        if (height > reqHeight || width > reqWidth) {  
            final int halfHeight = height / 2;  
            final int halfWidth = width / 2;  
            while ((halfHeight / inSampleSize) > reqHeight  
                    && (halfWidth / inSampleSize) > reqWidth) {  
                inSampleSize *= 2;  
            }  
        }  
        return inSampleSize;  
    }  
  
    private static Bitmap createScaleBitmap(Bitmap src, int dstWidth,  
            int dstHeight) {  
        Bitmap dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);  
        if (src != dst) { 
            src.recycle(); 
        }  
        return dst;  
    }  
  

    public static Bitmap decodeSampledBitmapFromResource(Resources res,  
            int resId, int reqWidth, int reqHeight) {  
        final BitmapFactory.Options options = new BitmapFactory.Options();  
        options.inJustDecodeBounds = true;  
        BitmapFactory.decodeResource(res, resId, options); // 讀取圖片長款  
        options.inSampleSize = calculateInSampleSize(options, reqWidth,  
                reqHeight); // 計算inSampleSize  
        options.inJustDecodeBounds = false;  
        Bitmap src = BitmapFactory.decodeResource(res, resId, options); // 載入一個稍大的縮略圖  
        return createScaleBitmap(src, reqWidth, reqHeight); // 進一步得到目標大小的縮略圖  
    }  
  

    public static Bitmap decodeSampledBitmapFromFd(String pathName,  
            int reqWidth, int reqHeight) {  
        final BitmapFactory.Options options = new BitmapFactory.Options();  
        options.inJustDecodeBounds = true;  
        BitmapFactory.decodeFile(pathName, options);  
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);  
        options.inJustDecodeBounds = false;  
        Bitmap src = BitmapFactory.decodeFile(pathName, options);  
        return createScaleBitmap(src, reqWidth, reqHeight);  
    }  
}  
