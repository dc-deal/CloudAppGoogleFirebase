package net.livingrecordings.giggermainapp.giggerMainClasses;

import android.content.ContentProvider;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

import static java.security.AccessController.getContext;

/**
 * Created by Franky on 11.12.2016.
 */

//http://stackoverflow.com/questions/10042695/how-to-get-camera-result-as-a-uri-in-data-folder
public class MyFileContentProvider /* extends ContentProvider*/ {
    /*
    public static final Uri CONTENT_URI = Uri.parse
            ("content://com.example.camerademo/");
    private static final HashMap<String, String> MIME_TYPES =
            new HashMap<String, String>();

    static {
        MIME_TYPES.put(".jpg", "image/jpeg");
        MIME_TYPES.put(".jpeg", "image/jpeg");
    }

    @Override
    public boolean onCreate() {

        try {
            File mFile = new File(getContext().getFilesDir(), "newImage.jpg");
            if(!mFile.exists()) {
                mFile.createNewFile();
            }
            getContext().getContentResolver().notifyChange(CONTENT_URI, null);
            return (true);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public String getType(Uri uri) {
        String path = uri.toString();

        for (String extension : MIME_TYPES.keySet()) {
            if (path.endsWith(extension)) {
                return (MIME_TYPES.get(extension));
            }
        }
        return (null);
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode)
            throws FileNotFoundException {

        File f = new File(getContext().getFilesDir(), "newImage.jpg");
        if (f.exists()) {
            return (ParcelFileDescriptor.open(f,
                    ParcelFileDescriptor.MODE_READ_WRITE));
        }
        throw new FileNotFoundException(uri.getPath());
    }
    */
}