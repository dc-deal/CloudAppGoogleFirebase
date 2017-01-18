package net.livingrecordings.giggermainapp.giggerMainClasses;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.birbit.android.jobqueue.log.CustomLogger;
import com.birbit.android.jobqueue.scheduling.FrameworkJobSchedulerService;
import com.birbit.android.jobqueue.scheduling.GcmJobSchedulerService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


/**
 * Created by Kraetzig Neu on 15.12.2016.
 */


// items eintragen


//TODO Gigger gig historie. Gig abgesagt ist grau
// TODO Gigger gig historie..  Wenn du.in die band im feed hast kannst du.die.gig historie einsehen und dss ewuipment was sie benutzen. Auch.dss ausleihen das facebook der musiker
// TODO (1) (telefonbuch zugriff ( chips) band farben , benutzerfarben (einstellbar), bandlogo einstellbar
// TODO .. kann man die adapeterlisten aktualiesieren und dann aktualisiert sich auch die view?
// TODO man kann über einen Suchbildschirm nach Gigger Mitgleidern suchen, die kann man dann einladen, bzw. deirekt in eine deiner Bands einladen(band detail bildschirm).oder Kontakt eionladen activity, da kann man die band zum einladen wählen
// TODO Personen chips(wie gkalender personen einladen), bearbeiten selbes layout wie details!!Item bearbeiten layout total komisch
// TODO ( hier kann man Gigger Kontakte suchen.
// TODO ( man kann online nach angemeldeten Gigger! Kontakten suchen(wenn man sich suchbar stellt).
// TODO ( alle die die App installiert haben, besitzen automatishceinen Gigger account ( wird angelegt falsl nciht vorhanden oder google connected.)
// TODO ( man schickt dann einen Invite los, den der andere annehmen kann. dann ist man in der Band.
// TODO ( man kann den screen für eine Band launchen oder einfach nur einen losen kontakt hinzufügen, mit dem kann man dann chatten..
// TODO ( man kann auch einfach gigger leuten eine nachricht schreiben.
// TODO ( man kann auch telefonkontakte nach gigger einladen.
// TODO (2) Alles mit FireBase verknüpfen, online abspeichern.
// TODO (3) abspeichern : item list / details / editieren möglich machen - importieren,bans kontakten zu ordenen ,wegordnen.
// TODO (4) noch ein paar mehr wichtige eigenschaften übernehmen (ev. kamerabild zufügen / image von galerie...
// TODO - restliche eigenschaften mit übernehmen in die edit felder.
// TODO (5)  die bezezeichner sollen durch android mat. design icons ersetzt werden..(https://material.io/icons/)
// TODO (6) Kategorie manager treeview - alle cats, items mehrfachselektion mergen, das soll ein
// TODO fragment werden. ich kann entweder nur kategorien oder auch gegenstände anzeigen. Die suche funktioniert über den Manager
// TODO auch alle auswahlboxen könen ndiesen manager nutzen. Bei rechtevergaben wird diese Manager zu mauswählen benutzt
// TODO also ein vielschichtiges Übersichtstool. (siehe das treeview tool was ich runtergeladen hab!)
// TODO -> weitere features für gigs soll ein abhaken aller gegenstäne möglich sein, die gebraucht werden.
// TODO -> welche gegenstände gebraucht werden, kann den gegenständen angehangen weden. das wird in SCHEMAS abespecihert.
// TODO -> Beispiel: Marshall JVM AMP. MUSS IMMER MIT (root schema) - ITEM CAMSTAND -> schema "Kameraarufnahmen" adden -> AmpMIX -> schema Recording Adden. ->
// TODO (7) DERGROSSE WURF:
// TODO Gigger Online Login und sync zwischen registrierten geräten. Leute können sich bei gigger online anmelden. Eigenes profil bearbeitbar (z.b. meine Farbe)
// TODO Dann können Sie Ihre Bands mitenander verbinden, man kann so andere Giggermitgleider suchen, und einladen.(leute die gigger installiert haben
// TODO auf ihrem handy erscheinen dann (falls sie zu einer Band oder Kontakt hinzugefügt werden)
// TODO Später können auch leute mit webapps auf gigger zu greifen, dafür wird dann aber ein account benötigt.
// TODO d.h. alle leute mit einer Telefonnummer bekommen automatisch online einen dummy gigger account, mit dem tag der telefonnummer. so
// TODO ist schonmal eine grundsätzliche loginstruktur vorhanden. Man kann dann seinen Accoundnamen noch ändern etc. der name wird dann immer
// TODO automatisch vergeben (z.b. Name+TelefonModell+ende der TelNR.)
// TODO (8) Rechtesystem- PRINZIP: Keep it simple. was brauchen die leute wirklich. Sehr basisch halten
// TODO (8.1) Nur wenn man Mitgleid in der Band ist, kann man gegenseitig gegenstände hinzufügen und bearbeiten. (mitglied wird man wenn man eingeladen wurde).
// TODO (8.2) Der ersteller des gegenstandes muss das share-flag anhaken, damit andere die gegenstände bearbeiten können.
// TODO (8.2) Wird der Gegenstand als Band-gegenstand deklariert, darf jeder den gegenstand bearbeiten-löschen etc.
// TODO (8.3) Bandgegenstände bekommen eigene Farben, der Bandfarbe nach. Benutzter bekommen eigene Farben (wählbar), zur schnellen besitzerunterscheidung.
// TODO (9) Die ganzen kniffligen fragen. Was passiert wenn ein nutzer aussteigt, wird dann das equipment auf die restlichen Bandmitgleider automatisch übertragen!?!?
// TODO merge account fature. wenn man zwei accounts aht kann man die zusammenfügen damit die Gegenstände nicht weg sind. (dublikate ignorieren- bei dublikaten im name das lokale nehmen..)
// -----------------------------------------------------------------


public class GiggerMainAPI {

    //PFADE..
    public static final String ITEMPATH = "ITEMPATH";
    public static final String ITEMPATH_LOCAL = "ITEMPATH_LOCAL";
    public static final String ITEMPATH_GLOBAL = "ITEMPATH_GLOBAL";
    public static final String TAGS_PUBLISHED = "TAGS_PUBLISHED";
    public static final String BANDS_PUBLISHED = "BANDS_PUBLISHED";
    public static final String USERS_PUBLISHED = "USERS_PUBLISHED";
    // INDEX
    public static final String INDEXES = "INDEXES";
    public static final String PUBLISHED_NAMESEARCH_INDEX = "PUBLISHED_NAMESEARCH_INDEX";
    public static final String LOCAL_NAMESEARCH_INDEX = "LOCAL_NAMESEARCH_INDEX";
    public static final String TAGITEMS_LOCAL_ALLTAGS_INDEX = "TAGITEMS_LOCAL_ALLTAGS_INDEX";
    public static final String BANDS_GLOBAL_NAMESEARCH_INDEX = "BANDS_GLOBAL_NAMESEARCH_INDEX";
    FirebaseDatabase mDatabase;
    FirebaseStorage mStorage;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    Context mContext;
    private mainAPICallbacks apiCallbacks;

    public GiggerMainAPI(Context cont) {
        apiCallbacks = (mainAPICallbacks) cont;
        mContext = cont;
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    public static GiggerMainAPI getInstance(Context cont) {
        return new GiggerMainAPI(cont);
    }

    public DatabaseReference getGlobalItemRef() {
        return mDatabase.getReference()
                .child(ITEMPATH).child(ITEMPATH_GLOBAL);
    }

    public DatabaseReference getLocalItemRef() {
        return mDatabase.getReference()
                .child(ITEMPATH).child(ITEMPATH_LOCAL).child(mUser.getUid());
    }

    public StorageReference getItemStorageRef(String ItemKey) {
        return mStorage.getReference()
                .child(ITEMPATH).child(ITEMPATH_GLOBAL).child(ItemKey);
    }

    public DatabaseReference getTagsPublishedRef() {
        return mDatabase.getReference().child(TAGS_PUBLISHED);
    }


    public Intent saveItemService(ItemClass item) {
        Intent i = new Intent(mContext, GiggerMainAPI.class);
        i.putExtra("ItemClass", item);
        mContext.startActivity(i);
        return i;
    }

    public interface mainAPICallbacks {
        // man kann hierrüber callbacken, ob alles geklappt hat und wann es fertig ist.
        void onMainAPIDone(boolean error);
    }

    public class SaveItemService extends IntentService {
        // save it.
        ItemClass inpItem;
        DatabaseReference inpItemRef;
        Boolean isNewEntry;
        GiggerPicUploadJob mNotification;
        JobManager mJobManager;

        public SaveItemService() {
            super("GiggerItemAPI");
        }

        @Override
        protected void onHandleIntent(Intent workIntent) {
            //SET_UP:
            // Gets data from the incoming Intent

            if (workIntent.hasExtra("ItemClass")) {
                ItemClass item = (ItemClass) workIntent.getSerializableExtra("ItemClass");
                if (workIntent.hasExtra("ItemKey")) {
                    item.setDbKey(workIntent.getStringExtra("ItemKey"));
                }
                save();
            }
        }

        void createJobManager(){
            Configuration.Builder builder = new Configuration.Builder(mContext)
                    .minConsumerCount(1) // always keep at least one consumer alive
                    .maxConsumerCount(3) // up to 3 consumers at a time
                    .loadFactor(3) // 3 jobs per consumer
                    .consumerKeepAlive(120) // wait 2 minute
                    .customLogger(new CustomLogger() {
                        private static final String TAG = "JOBS";
                        @Override
                        public boolean isDebugEnabled() {
                            return true;
                        }

                        @Override
                        public void d(String text, Object... args) {
                            Log.d(TAG, String.format(text, args));
                        }

                        @Override
                        public void e(Throwable t, String text, Object... args) {
                            Log.e(TAG, String.format(text, args), t);
                        }

                        @Override
                        public void e(String text, Object... args) {
                            Log.e(TAG, String.format(text, args));
                        }

                        @Override
                        public void v(String text, Object... args) {

                        }
                    });

            // hiermit kann ich wohl machen ,das der job wierde aufgewekct wird, wenn die appliakiton gestartet wird etc..

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                builder.scheduler(FrameworkJobSchedulerService.createSchedulerFor(mContext,
//                        AppJobService.class), true);
//            } else {
//                int enableGcm = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext);
//                if (enableGcm == ConnectionResult.SUCCESS) {
//                    builder.scheduler(GcmJobSchedulerService.createSchedulerFor(mContext,
//                            AppGcmJobService.class), true);
//                }
//            }
            mJobManager = new JobManager(builder.build());
        }

        void save() {
            isNewEntry = false;
            if (inpItem.getDbKey() != null) {
                inpItem.setDbKey(getPublishedItemsRef().push().getKey());
                isNewEntry = true;
            }
            String key = inpItem.getDbKey();
            inpItemRef = getPublishedItemsRef().child(key);
            // gloabals..
            inpItemRef.child("name").setValue(inpItem.getName());
            //   inpItemRef.child("brand").setValue(inpItem.getBrand());
            inpItemRef.child("desc").setValue(inpItem.getDesc());

            //----------------------------------
            // dieser abschnitt vergleicht alle hochgeladenen bilder
            createJobManager();
            inpItemRef.child("imgs").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Boolean found;
                    for (int i = 0; i < inpItem.newImagesInput.size(); i++) {
                        found = false;
                        Boolean setToGallery = i== 0;
                        Uri inpImgUri = Uri.parse(inpItem.newImagesInput.get(i).toString());
                        for (DataSnapshot thisImgUrl : dataSnapshot.getChildren()) {
                            DatabaseReference ref = thisImgUrl.getRef();
                            ref.child("gallery").removeValue();
                            Uri dbImgUri = Uri.parse((String) thisImgUrl.child("imgUrl").getValue());
                            if (inpImgUri.toString().equals(dbImgUri.toString())) {
                                // gefunden!!
                                if (setToGallery){
                                    DatabaseReference refTemp1 = thisImgUrl.getRef();
                                    refTemp1.child("gallery").setValue(true);
                                }
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            // direkt als thumb und als image in die Uploadquepacken.. dann wird eine notification gemacht um den User auf den gegenstand hinzuweise
                            mJobManager.addJobInBackground(new GiggerPicUploadJob(new UploadImgTaskData(
                                inpItem.newImagesInput.get(i), // der link zum bild auf der festplatte
                                inpItemRef.child("imgs"), // wo es später gespeichert werden soll
                                getItemStorageRef(inpItem.getDbKey()),setToGallery))
                            ); // muss dann nach dem hochladen gesetzt werden.
                        }
                    }
                    // NUN ALLE NIcht gebrauchten wegschmissen / löschen.
                    // ----------------------------
                    for (DataSnapshot thisImgUrl : dataSnapshot.getChildren()) {
                        found = false;
                        String thisImgKey = thisImgUrl.getKey();

                        Uri dbImgUri = Uri.parse((String) thisImgUrl.child("imgUrl").getValue());
                        for (int i = 0; i < inpItem.newImagesInput.size(); i++) {
                            Uri inpImgUri = Uri.parse(inpItem.newImagesInput.get(i).toString());
                            if (inpImgUri.toString().equals(dbImgUri.toString())) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            // nicht in der nuene Liste wiedergefudnen.
                            inpItemRef.child("imgs").child(thisImgKey);
                            getItemStorageRef(inpItem.getDbKey()).child(thisImgKey).delete();
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });





        }



    }


}
