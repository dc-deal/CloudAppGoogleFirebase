package net.livingrecordings.giggermainapp.giggerMainClasses;

import android.content.ClipData;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import net.livingrecordings.giggermainapp.R;
import net.livingrecordings.giggermainapp.giggerMainClasses.Exceptions.Gigger_NoCurrentUserException;
import net.livingrecordings.giggermainapp.giggerMainClasses.Exceptions.Gigger_NoItemInputException;
import net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses.ItemImageCasheHelper;
import net.livingrecordings.giggermainapp.giggerMainClasses.jobs.GiggerPicUploadJob;
import net.livingrecordings.giggermainapp.giggerMainClasses.models.ItemClass;
import net.livingrecordings.giggermainapp.giggerMainClasses.models.ItemClassLocal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


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
    public static final String ITEMPATH_PRIVATE = "ITEMPATH_PRIVATE";
    public static final String TAGS_PUBLISHED = "TAGS";
    public static final String BANDS_PUBLISHED = "BANDS_PUBLISHED";
    public static final String USERS_PUBLISHED = "USERS_PUBLISHED";
    // INDEX
    public static final String INDEXES = "INDEXES";
    public static final String TAGITEMS_LOCAL_ALLTAGS_INDEX = "TAGITEMS_LOCAL_ALLTAGS_INDEX";
    public static final String BANDS_GLOBAL_NAMESEARCH_INDEX = "BANDS_GLOBAL_NAMESEARCH_INDEX";
    public static final String JOB_MANAGER_UPLOADS = "UploadJobsGiggerAPI";
    // LOGS
    public static final String GIGGERMAINAPI_SAVE_ERROR = "GIGGER_SAVEI_ERROR";
    // GLOBALS
    public static final int MAX_GLOBALFILE_SIZE = 1000000; //1200000; // 1.2MP
    // local vars
    FirebaseDatabase mDatabase;
    FirebaseStorage mStorage;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    // Für das save
    Boolean isNewEntry;
    GiggerPicUploadJob mNotification;
    AppJobManager_ForPicUploads mJobManager;
    private mainAPICallbacks apiCallbacks;
    public GiggerMainAPI() {
        // apiCallbacks = (mainAPICallbacks) cont;
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    public static GiggerMainAPI getInstance() {
        return new GiggerMainAPI();
    }

    private DatabaseReference getPublishedItemsRef() {
        return mDatabase.getReference()
                .child(ITEMPATH).child(ITEMPATH_GLOBAL);
    }

    public DatabaseReference getPublishedItemsRef(String itemKey) {
        return getPublishedItemsRef().child(itemKey);
    }

    private DatabaseReference getPrivateItemRef() {
        return mDatabase.getReference()
                .child(ITEMPATH).child(ITEMPATH_PRIVATE).child(getCurrentUser().getUid());
    }

    public DatabaseReference getPrivateItemRef(String itemKey) {
        return getPrivateItemRef().child(itemKey);
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    private String removeForbiddenChars(String inp) {
        // chekck if chars correct..
        // check if dublicate..
        String posString = "";
        final char[] ReservedChars = {'/', '.', '#', '$', '[', ']'};
        Boolean found;
        for (char c : inp.toCharArray()) {
            found = false;
            for (char ckChar : ReservedChars) {
                if (ckChar == c) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                posString = posString + c;
            }
        }
        return posString;
    }

    public void removeItem(final String key) {
        getPublishedItemsRef().child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ItemClass thisItem = dataSnapshot.getValue(ItemClass.class);
                // tags löschen?? aber wie .. wenn sie doch global genutzt werden...
                // INDEXE!!
                getItemStorageRef(key).delete();
                // remove all storage items!!
                getPrivateItemRef(key).removeValue();
                getPublishedItemsRef(key).removeValue();
                getLocalItemRef(key).removeValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private DatabaseReference getLocalItemRef() {
        return mDatabase.getReference()
                .child(ITEMPATH).child(ITEMPATH_LOCAL).child(mUser.getUid());
    }

    public DatabaseReference getLocalItemRef(String itemKey) {
        return getLocalItemRef().child(itemKey);
    }

    public StorageReference getItemStorageRef(String itemKey) {
        return mStorage.getReference()
                .child(ITEMPATH).child(ITEMPATH_GLOBAL).child(itemKey);
    }

    public DatabaseReference getTagsPublishedRef() {
        return mDatabase.getReference().child(TAGS_PUBLISHED);
    }

    public Query getDublicateNameSearchQuery(String nameStr) {
        return getLocalItemRef()
                .orderByChild("searchName").equalTo(nameStr.toUpperCase());
    }

    public Query getCreatedByUserQuery() {
        return getPrivateItemRef().orderByChild("createdBy").equalTo(
                getCurrentUser().getUid()
        );
    }

    private void saveItemEssential(ItemClass item, String key) {
        if (item.isPublished()) {
            getPublishedItemsRef(key).setValue(item);
            getPrivateItemRef(key).setValue(item);
        } else {
            getPublishedItemsRef(key).removeValue();
            getPrivateItemRef(key).setValue(item);
        }
    }

    private void deleteImageFromItem(final String itemKey, String imgKey) {
        getPrivateItemRef(itemKey).child("imgs").child(imgKey).removeValue();
        getPublishedItemsRef(itemKey).child("imgs").child(imgKey).removeValue();
        getPublishedItemsRef(itemKey).child("imgs").child(imgKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    StorageReference sr = mStorage.getReferenceFromUrl((String) dataSnapshot.getValue());
                    if (sr != null)
                        getItemStorageRef(itemKey).child(sr.getName()).delete();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private ArrayList<String> getUploadPics(ItemClass refItem, ItemClass inpItem) {
        ArrayList<String> uniquevalues = new ArrayList(inpItem.getImgs().values());
        uniquevalues.removeAll(refItem.getImgs().values()); // retainAll .. alle behalten..dublikate finden.
        // gallery.... hinzufügen ()
        if (!uniquevalues.contains(inpItem.getGalleryPic()) &&
                (inpItem.getGalleryPic() != null)) {
            uniquevalues.add(inpItem.getGalleryPic());
        }
        return uniquevalues;
    }

    private ItemClass mergeImage(ItemClass inpItem, String image, Boolean isgallery) {
        HashMap<String, String> hm = inpItem.getImgs();
        if (!inpItem.getImgs().containsValue(image)) {
            hm.put(ItemImageCasheHelper.getInstance().generateUniqueIdentifier(), inpItem.getGalleryPic());
        }
        if (isgallery) {
            inpItem.setGalleryPic(image);
        }
        inpItem.setImgs(hm);
        return inpItem;
    }

    public void saveItem(final Context mContext, ItemClass item, ItemClassLocal itemLocal) {
        // save it.
        try {
            final ItemClass inpItem = item;
            if (inpItem == null) {
                // error kein user
                throw new Gigger_NoItemInputException();
            }
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                // error kein user
                throw new Gigger_NoCurrentUserException();
            }

            inpItem.setCreatedBy(user.getUid());
            isNewEntry = false;
            if (inpItem.getDbKey() == null) {
                inpItem.setDbKey(getPublishedItemsRef().push().getKey());
                isNewEntry = true;

            }
            final String key = inpItem.getDbKey();

            inpItem.setSearchName(inpItem.getName().toUpperCase());

            // dublicate check and forbiddenchars
            HashMap<String, Boolean> map = new HashMap<>();
            for (String s : inpItem.getTags().keySet()) {
                String afterCheck = removeForbiddenChars(s);
                if (afterCheck.length() > 0) {
                    map.put(afterCheck, true);
                }
            }
            inpItem.setTags(map);
            saveItemEssential(inpItem, key);
            // noch die lokalen...
            // muss ich recht manuell machen.
            itemLocal.setSearchName(inpItem.getName().toUpperCase());

            itemLocal.setTags(inpItem.getTags());
            // TODO TAG global index .. zum löschen s. obewn.
            for (String s : inpItem.getTags().keySet()) {
                getTagsPublishedRef().child(s.toUpperCase()).child("name").setValue(s);
                getTagsPublishedRef().child(s.toUpperCase()).child("searchName").setValue(s.toUpperCase());
            }
            getLocalItemRef(key).setValue(itemLocal);
            //----------------------------------
            // dieser abschnitt vergleicht alle hochgeladenen bilder
            getPrivateItemRef(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ItemClass refItem = dataSnapshot.getValue(ItemClass.class);
                    // erst  alle bilder "populaten..."
                    // Variable names edited for readability
                    ArrayList<String> toUploadList = getUploadPics(refItem, inpItem);
                    for (String s : toUploadList) { // immer value set
                        Boolean setToGallery = false;
                        if (s == inpItem.getGalleryPic()) {
                            setToGallery = true;
                        }
                        Uri localUploadFile = ItemImageCasheHelper.getInstance().casheImageFromUri(mContext, inpItem.getDbKey(), Uri.parse(s));
                        // ist ja schon alles im cashe..
                        mJobManager.getJobManager(mContext).addJobInBackground(new GiggerPicUploadJob(new UploadImgTaskData(
                                inpItem.getName(),
                                inpItem.getDbKey(),
                                localUploadFile, // der link zum bild auf der festplatte
                                setToGallery))
                        ); // muss dann nach dem hochladen gesetzt werden.
                    }
                    for (String s : inpItem.deleteList.keySet()) { // nur keys
                        deleteImageFromItem(key, s);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            // noch einen toast, das die Aufgabe eingetragen wurde und der upload beginnt.
            Toast.makeText(mContext, mContext.getString(R.string.upload_ongoing_toast), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(GIGGERMAINAPI_SAVE_ERROR, e.getMessage());
        }
    }

    public void addItemImage(final UploadImgTaskData task, final Uri imageUri) {
        GiggerMainAPI.getInstance()
                .getPrivateItemRef(task.getDbKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ItemClass ic = dataSnapshot.getValue(ItemClass.class);
                if (ic != null) {
                    ic = mergeImage(ic, imageUri.toString(), task.isGlleryPic());
                    saveItemEssential(ic, task.getDbKey()); //immer benutzen..
                } else {
                    // wow zugrundeliegendenr gegenstand nicht gefunden...
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public interface mainAPICallbacks {
        // man kann hierrüber callbacken, ob alles geklappt hat und wann es fertig ist.
        void onMainAPIDone(boolean error);
    }


}
