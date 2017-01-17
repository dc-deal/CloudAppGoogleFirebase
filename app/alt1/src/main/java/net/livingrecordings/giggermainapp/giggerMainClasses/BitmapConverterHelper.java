package net.livingrecordings.giggermainapp.giggerMainClasses;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.io.ByteArrayInputStream;

/**
 * Created by Kraetzig Neu on 15.11.2016.
 */

public class BitmapConverterHelper {

    float scaleWidth;
    float scaleHeight;

    public BitmapConverterHelper(){
       // DisplayMetrics metrics = new DisplayMetrics();
        //    context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        scaleWidth = 1;
        scaleHeight = 1;
//        scaleWidth = metrics.scaledDensity;
  //      scaleHeight = metrics.scaledDensity;
    }


    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public Bitmap resizeBitmap(Resources res, int resId, int targetHeightDP) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        float inHeight = options.outHeight;
        float inWidth = options.outWidth;

        int outHeight = Math.round(targetHeightDP*scaleHeight);
        int outWidth = Math.round((inWidth / (inHeight / targetHeightDP))*scaleWidth);

        // Decode with inSampleSize
        options.inSampleSize = calculateInSampleSize(options,outWidth,outHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap tmp_bmp =  BitmapFactory.decodeResource(res, resId, options);
        return Bitmap.createScaledBitmap(tmp_bmp,outWidth,outHeight, true);
    }

}
