package net.livingrecordings.giggermainapp.giggerMainClasses.jobs;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import net.livingrecordings.giggermainapp.MainActivity;
import net.livingrecordings.giggermainapp.R;
import net.livingrecordings.giggermainapp.giggerMainClasses.Exceptions.GiggerPicUploadException;
import net.livingrecordings.giggermainapp.giggerMainClasses.GiggerMainAPI;
import net.livingrecordings.giggermainapp.giggerMainClasses.UploadImgTaskData;
import net.livingrecordings.giggermainapp.giggerMainClasses.models.ItemClass;

import java.io.File;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Franky on 17.12.2016.
 */

public class GiggerPicUploadJob extends Job {

    // kompletter UploadBlock für ein BILD!!!
    // kommuniziert mit einer NOtification, um die gerade laufenden uploads darzustellen.
    // die daten für einen Job sind im Objekt UploadImgTaskData festgehalten-.

    private static final String TAG = GiggerPicUploadJob.class.getCanonicalName();
    private static final int NOTIFICATION_ID = 109;
    private static final int MAXPROGRESSCOUNT = 100;
    private final CountDownLatch loginLatch = new CountDownLatch(1);
    // Objekt hält die bilderuploads...
    UploadImgTaskData thisTask;
    NotificationManager mNotificationManager;
    NotificationCompat.Builder mNotifyBuilder;
    Context mContext;
    UploadTask uploadTask;
    //-----------------------------------------------------
    // UPLOAD LISTENER BLOCK
    OnFailureListener failListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception exception) {
            failtureNotification();
            loginLatch.countDown();
        }
    };
    OnProgressListener progListener = new OnProgressListener<UploadTask.TaskSnapshot>() {

        @Override
        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            int currentprogress = (int) progress;
            updadteNotification(currentprogress);
        }
    };
    OnPausedListener pauseList = new OnPausedListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
            // TODO .. notification pausieren....
        }
    };
    OnSuccessListener succListenr = new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            Uri durl = taskSnapshot.getDownloadUrl();
            GiggerMainAPI.getInstance().addItemImage(thisTask,durl);
            completeNotification(mContext.getString(R.string.editItem_uploadsucsess));
            loginLatch.countDown();
        }
    };
    private String mAddress;

    public GiggerPicUploadJob(UploadImgTaskData taskImg) {
        super(new Params(1)
                .requireNetwork()
                //   .singleInstanceBy(TAG)
                .addTags(TAG)
        );
        this.thisTask = taskImg;
    }

    @Override
    public void onAdded() {
        mContext = getApplicationContext();
        //  start notification, if it ain't already present.
        startNotification();
    }

    @Override
    public void onRun() throws Throwable {
        // cashes the input file to local gigger dir and
        File tmpFile = new File(thisTask.getFileToUpload().toString());
        String fileName = tmpFile.getName();
        StorageReference sr = GiggerMainAPI.getInstance().getItemStorageRef(thisTask.getDbKey());
        uploadTask = sr.child(fileName).putFile(thisTask.getFileToUpload());
        uploadTask.addOnSuccessListener(succListenr)
                .addOnFailureListener(failListener)
                .addOnProgressListener(progListener)
                .addOnPausedListener(pauseList);
        loginLatch.await();
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
        failtureNotification();
        loginLatch.countDown();
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        if (throwable instanceof GiggerPicUploadException) {
            GiggerPicUploadException error = (GiggerPicUploadException) throwable;
            return RetryConstraint.CANCEL;
        }
        return RetryConstraint.RETRY;
    }

    private String getContextText() {
        return mContext.getString(R.string.pic_for_item1) + '"' + thisTask.getNameOfItem() + '"' +
                mContext.getString(R.string.pic_for_item2);
    }


    //-----------------------------------------------------
    // NOTIFICATION BLOCK
    private void startNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.drawable.ic_cloud_start)
                        .setContentTitle(mContext.getString(R.string.upload_starting))
                        .setProgress(MAXPROGRESSCOUNT, 0, true)
                        .setContentText(getContextText());
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(mContext, MainActivity.class);
// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }


    void updadteNotification(int progrssNow) {
        NotificationManager mNotifyManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder
                .setSmallIcon(R.drawable.ic_cloud_upload)
                .setContentTitle(mContext.getString(R.string.upload_running))
                .setProgress(MAXPROGRESSCOUNT, progrssNow, false)
                .setContentText(getContextText());
        // Displays the progress bar for the first time.
        mNotifyManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    void completeNotification(String note) {
        NotificationManager mNotifyManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setContentTitle(mContext.getString(R.string.upload_complete1))
                .setContentText(note)
                .setProgress(0, 0, false)
                .setSmallIcon(R.drawable.ic_cloud_done);
        mNotifyManager.notify(
                NOTIFICATION_ID,
                mBuilder.build());
        //     mNotifyManager.
//        Toast.makeText(mContext,mContext.getString(R.string.upload_complete1_text),Toast.LENGTH_SHORT);
    }

    void failtureNotification() {
        NotificationManager mNotifyManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setContentTitle(mContext.getString(R.string.upload_failture))
                .setProgress(0, 0, false)
                .setContentText(
                        mContext.getString(R.string.upload_failture_notep1) +
                                thisTask.getFileToUpload().getLastPathSegment().toString() +
                                mContext.getString(R.string.upload_failture_notep2)

                )
                .setSmallIcon(R.drawable.ic_sync_problem);
        mNotifyManager.notify(NOTIFICATION_ID, mBuilder.build());
        Toast.makeText(mContext, mContext.getString(R.string.upload_failture), Toast.LENGTH_SHORT);
    }


}
