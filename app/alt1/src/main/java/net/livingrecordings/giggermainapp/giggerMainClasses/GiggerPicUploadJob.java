package net.livingrecordings.giggermainapp.giggerMainClasses;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import net.livingrecordings.giggermainapp.MainActivity;

/**
 * Created by Franky on 17.12.2016.
 */

public class GiggerPicUploadJob extends Job {

    // kompletter UploadBlock für ein BILD!!!
    // kommuniziert mit einer NOtification, um die gerade laufenden uploads darzustellen.
    // die daten für einen Job sind im Objekt UploadImgTaskData festgehalten-.

    private String mAddress;
    public static final String TAG = GiggerPicUploadJob.class.getCanonicalName();

    // Objekt hält die bilderuploads...
    UploadImgTaskData thisTask;
    NotificationManager mNotificationManager;
    NotificationCompat.Builder mNotifyBuilder;
    Context mContext;
    int mID;


    public GiggerPicUploadJob(UploadImgTaskData taskImg) {
        super(new Params(JobConstants.PRIORITY_NORMAL)
                .requireNetwork()
                .singleInstanceBy(TAG)
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
        UploadTask uploadTask = thisTask.getStorageToWriteTo().putFile(thisTask.getFileToUpload());
        uploadTask.addOnSuccessListener(succListenr)
                .addOnFailureListener(failListener)
                .addOnProgressListener(progListener)
                .addOnPausedListener(pauseList);
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
        failtureNotification();
    }

    @Override protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        if (throwable instanceof GiggerPicUploadException) {
            GiggerPicUploadException error = (GiggerPicUploadException) throwable;
            return RetryConstraint.CANCEL;
        }
        return RetryConstraint.RETRY;
    }


    private void startNotification(){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.drawable.ic_cloud_upload)
                        .setContentTitle(mContext.getString(R.string.upload_running))
                        .setProgress(max,0,true)
                        .setContentText(mContext.getString(R.string.upload_running_countpics)+thisTask.getFileToUpload().getLastPathSegment().toString());
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
        mNotificationManager.notify(mID, mBuilder.build());
    }

    //-----------------------------------------------------
    // NOTIFICATION BLOCK
    void updadteNotification(){
        // auf die notification zugreifen, updaten
        // a) wievielter uplad.
        // b) welcehs file
        mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        // nur den context text updaten.
        mNotifyBuilder = new NotificationCompat.Builder(mContext)
                .setProgress(max,1,true)
                .setContentText(this.getCurrentRunCount() + " / "+
                        +mContext.getString(R.string.upload_running)
                        +thisTask.getFileToUpload().getLastPathSegment().toString());
        // Because the ID remains unchanged, the existing notification is
        // updated.
        mNotificationManager.notify(
                mID,
                mNotifyBuilder.build());
    }
    void completeNotification(String note){
        mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyBuilder = new NotificationCompat.Builder(mContext)
                .setContentTitle(mContext.getString(R.string.upload_complete1))
                .setContentText(note)
                .setProgress(max,1,true)
                .setSmallIcon(R.drawable.ic_cloud_done);
        // Because the ID remains unchanged, the existing notification is
        // updated.
        mNotificationManager.notify(
                mID,
                mNotifyBuilder.build());
    }

    void failtureNotification(){
        mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyBuilder = new NotificationCompat.Builder(mContext)
                .setContentTitle(mContext.getString(R.string.upload_failture))
                .setContentText(
                        mContext.getString(R.string.upload_failture_notep1)+
                                thisTask.getFileToUpload().getLastPathSegment().toString()+
                                        mContext.getString(R.string.upload_failture_notep2)

                )
                .setSmallIcon(R.drawable.ic_sync_problem);
        // Because the ID remains unchanged, the existing notification is
        // updated.
        mNotificationManager.notify(
                mID,
                mNotifyBuilder.build());
    }

    static final int max = 1;
    void setProgress(int progress){
        mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyBuilder = new NotificationCompat.Builder(mContext)
             .setProgress(max,progress,true);
        // Because the ID remains unchanged, the existing notification is
        // updated.
        mNotificationManager.notify(
                mID,
                mNotifyBuilder.build());
    }


    //-----------------------------------------------------
    // UPLOAD LISTENER BLOCK
    OnFailureListener failListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception exception) {
            failtureNotification(mContext.getString(R.string.editcat_fail_input_upload));
        }
    };
    OnProgressListener progListener = new OnProgressListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            int currentprogress = (int) progress;
            setProgress(currentprogress);
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
            DatabaseReference thisImgRef = thisTask.getReferenceToWriteTo().push().getRef();
            thisImgRef.child("imgUrl").setValue(durl.toString());
            if (thisTask.isGlleryPic()) {
                thisImgRef.child("gallery").setValue(true);
            }
            completeNotification(mContext.getString(R.string.editCat_createsuccess));
        }
    };






}
