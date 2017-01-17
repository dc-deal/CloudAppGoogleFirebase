package net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.net.Uri;
import android.provider.OpenableColumns;

/**
 * Created by Kraetzig Neu on 04.01.2017.
 */

public class UriHelper {

    public UriHelper(){
    }

    public static UriHelper getInstance(){
        return new UriHelper();
    }

    public String getFileName(Uri uri,Context mContext) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


}
