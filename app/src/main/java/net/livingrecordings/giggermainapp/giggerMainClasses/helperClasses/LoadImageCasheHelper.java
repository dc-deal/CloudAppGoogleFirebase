package net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import net.livingrecordings.giggermainapp.R;
import net.livingrecordings.giggermainapp.giggerMainClasses.GiggerItemAPI;
import net.livingrecordings.giggermainapp.giggerMainClasses.models.ImagesClass;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Kraetzig Neu on 05.01.2017.
 */

public class LoadImageCasheHelper {

    private final static String CASHEUNIT_CONST = "CASHEUNIT_CONST";
    public Uri mOnlineUri;
    public Uri mCashedUri;
    private ImageView mImgView;
    loadImageCasheHelperCallbacks callbacks;

    public interface loadImageCasheHelperCallbacks{
        void provideAllImages_asOnlineUri(ArrayList<ImagesClass> imgList);
    }

    LoadImageCasheHelper() {

    }

    public static LoadImageCasheHelper getInstance() {
        return new LoadImageCasheHelper();
    }

    public void loadGalleryImage_Cashed(final Context mContext, final ImageView imgView, final String itemKey) {
        this.mImgView = imgView;
        GiggerItemAPI.getInstance().getGalleryImageQuery(itemKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ImagesClass thisImage = ds.getValue(ImagesClass.class);
                    if (thisImage != null) {
                        mOnlineUri = Uri.parse(thisImage.getImgUri()); // noch online link
                        // offlinne link ermitteln.
                        writeImageToLocalCashe(mContext, itemKey, Uri.parse(thisImage.getImgUri()));
                        ArrayList<ImagesClass> imgList = new ArrayList<>();
                        imgList.add(thisImage);
                        if (callbacks != null) {
                            callbacks.provideAllImages_asOnlineUri(imgList);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private File getLocalPath(String addFileName) {
        // TODO Überlegen, ob images aus einer globalen suche nicht lieber im cahse verzeihnis gelagert werden sollen.
        // TODO mussi ch antürlich imemr beides nachgucken..
        return new File(Environment.getExternalStorageDirectory() +
                    File.separator + "GiggerFiles" + File.separator + "GiggerImages" + File.separator
                    +  addFileName);
    }

    private File getLocalCasheDir(String addFileName) {
        // TODO Überlegen, ob images aus einer globalen suche nicht lieber im cahse verzeihnis gelagert werden sollen.
        // TODO mussi ch antürlich imemr beides nachgucken..
        return new File(Environment.getDownloadCacheDirectory() +
                File.separator + "GiggerFiles" + File.separator + "GiggerImages" + File.separator + addFileName);
    }


    public Uri casheImageFromUri(Context mContext, String thisItemKey, Uri localInputFile) {
        File outputFile = null;
        try {
            final File dir = getLocalPath("");// ... & cashe file to the right directory, wich is GiggerStore ;)
            outputFile = new File(dir, generateUniqueIdentifier() + ".JPEG");
            outputFile.getParentFile().mkdirs();
            Bitmap bmp = BitmapConverterHelper.getInstance().resizeBitmapFromUri(mContext,localInputFile);
            FileOutputStream fos = new FileOutputStream(outputFile);
            bmp.compress(Bitmap.CompressFormat.JPEG, 99, fos);// convert file to nessecary conditions...
            fos.close();
        } catch (Exception e) {
            return localInputFile;
        }
        return Uri.fromFile(outputFile);
    }


    // schreibt bilder in einen lokalen cashe, der imemr wieder ausgelesen wird, sobald das Bild heruntergeladen ist.
    public void writeImageToLocalCashe(final Context mContext, final String ItemKey, final Uri fileUri) {
        // check if file exists...
        mCashedUri = null;
        if (URLUtil.isValidUrl(fileUri.toString())) {
            // its an url... a storage reference...
            // download file if it doesn't exist in cashe...
            StorageReference storRef = FirebaseStorage.getInstance().getReferenceFromUrl(fileUri.toString());
            if (storRef == null) {
                Log.e(CASHEUNIT_CONST, "couldnt find storage reference in writeImageToLocalCashe");
            } else {
                // ref existes...
                final String filename = storRef.getName(); // get file name...
                // now check if file is in local cashe to prevent download task.
                File locaFile = getLocalPath(filename);
           /*     File locaCashedFile = getLocalCasheDir(filename);*/ // noch nicht aktiv.
                if ((locaFile.exists())/*||(locaCashedFile.exists())*/) {
                    setAndShowImage(mContext,locaFile);
                } else {
                    final long ONE_MEGABYTE = 1024 * 1024;
                    storRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            // Local temp file has been created
                            // show if nessecary - ex. called from loadGalleryImage_Cashed...
                            try {
                                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                File outputFile = getLocalPath(filename);
                                outputFile.mkdirs();
                                FileOutputStream fos = new FileOutputStream(outputFile);
                                bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
                                fos.close(); // http://stackoverflow.com/questions/15662258/how-to-save-a-bitmap-on-internal-storage
                                setAndShowImage(mContext,outputFile);
                                //  TODO Note: It might be wise to use Environment.getExternalStorageDirectory()
                                //  TODO for getting the "SD Card" directory as this might change if a phone
                                //  TODO comes along which has something other than an SD Card (such as built-in flash, a'la the iPhone). Either way you should keep in mind that you need to check to make sure it's actually there as the SD Card may be removed.
                            } catch (Exception e) {
                                // Log your error
                                Log.e(CASHEUNIT_CONST, "file error due gigger cashe FX1123");
                            }


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // FILE NOT FOUND!!!
                            Log.e(CASHEUNIT_CONST, "couldnt store / download file in writeImageToLocalCashe");
                            // TODO .. DELETE LOCAL CASHE:.?
                        }
                    });
                }
            }
        } // TODO noch kein else zweig. aber eigentlich werde ich die bielder heir auslesen und abgleichen mit der online verion..
    }


    public String generateUniqueIdentifier() {
        String filename = "";
        long millis = System.currentTimeMillis();
        String datetime = new Date().toGMTString();
        datetime = datetime.replace(" ", "");
        datetime = datetime.replace(":", "");
        filename = datetime + "_" + millis;
        return filename;
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    private Uri setAndShowImage(Context cont, File inp) {
        mCashedUri = Uri.fromFile(inp);
        if ((mImgView != null)){
            Picasso.with(cont)
                    .load(mCashedUri)
                    .placeholder(R.drawable.imgplaceholder)// //R.drawable.progress_animation
                    .error(R.drawable.ic_error_outline_black_24dp)
                    .into(mImgView);
        }
        return mCashedUri;
    }


}
