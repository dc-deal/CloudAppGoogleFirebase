package net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses;

import android.app.Activity;
import android.content.Context;
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
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.R.attr.data;
import static net.livingrecordings.giggermainapp.giggerMainClasses.GiggerMainAPI.MAX_GLOBALFILE_SIZE;

/**
 * Created by Kraetzig Neu on 15.11.2016.
 */

public class BitmapConverterHelper {

    float scaleWidth;
    float scaleHeight;

    public final static String TAG_BMPCONV = "TAG_BMPCONV";

    public static BitmapConverterHelper getInstance(){
        return new BitmapConverterHelper();
    }

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

        int outHeight = Math.round(targetHeightDP * scaleHeight);
        int outWidth = Math.round((inWidth / (inHeight / targetHeightDP)) * scaleWidth);

        // Decode with inSampleSize
        options.inSampleSize = calculateInSampleSize(options, outWidth, outHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap tmp_bmp = BitmapFactory.decodeResource(res, resId, options);
        return Bitmap.createScaledBitmap(tmp_bmp, outWidth, outHeight, true);
    }


    public Bitmap loadBitmap(Context mContext, Uri imageUri) throws Exception
    {
        return MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), imageUri);
    }


    public Bitmap resizeBitmapFromUri(Context mContext, Uri inpFile) {
        Uri uri = inpFile;
        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = MAX_GLOBALFILE_SIZE;
            in = mContext.getContentResolver().openInputStream(uri);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();


            int scale = 1;
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) >
                    IMAGE_MAX_SIZE) {
                scale++;
            }
            Log.d(TAG_BMPCONV, "scale = " + scale + ", orig-width: " + o.outWidth + ",orig-height: " + o.outHeight);

            Bitmap b = null;
            in = mContext.getContentResolver().openInputStream(uri);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                b = BitmapFactory.decodeStream(in, null, o);

                // resize to desired dimensions
                int height = b.getHeight();
                int width = b.getWidth();
                Log.d(TAG_BMPCONV, "1th scale operation dimenions - width: " + width + ", height: " + height);

                double y = Math.sqrt(IMAGE_MAX_SIZE
                        / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
                        (int) y, true);
                b.recycle();
                b = scaledBitmap;

                System.gc();
            } else {
                b = BitmapFactory.decodeStream(in);
            }
            in.close();

            Log.d(TAG_BMPCONV, "bitmap size - width: " + b.getWidth() + ", height: " +
                    b.getHeight());
            return b;
        } catch (IOException e) {
            Log.e(TAG_BMPCONV, e.getMessage(), e);
            return null;
        }
    }
}
