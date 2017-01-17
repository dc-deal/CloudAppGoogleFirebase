package net.livingrecordings.giggermainapp.giggerMainClasses;


import android.app.Activity;
import android.graphics.Bitmap;

import net.livingrecordings.giggermainapp.R;
import net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses.BitmapConverterHelper;

import java.security.*;
import java.util.ArrayList;

/**
 * Created by Kraetzig Neu on 03.11.2016.
 */


public class GiggerContactCollection {

    // alle registrierten bands
    public GiggerBandList bands;
    // alle kontakte
    public giggerContactList allContacts;
    // LoginUser , der besitzer des gerätes...
    public GiggerContact loginUser;
    public BitmapConverterHelper convertHelper;
    // solange ich das nciht habe wird das im fenster angezeigt.
    public String[] bnames = {"NOmansWill", "The WEEZERS", "EPICs", "FIREHAWKS"};
    public String[] names = {"Max", "Toby", "ghero", "fire",
            "hendrix87", "3gnite", "manny", "loadgun77",
            "apple10", "logicone", "nils", "harry",
            "mike", "zyizz", "vanHalen101", "guitarChannel",
            "muse", "sid"};
    // ein giggeruser ist ein registrierter "Gigger", die die app mit seinem Handy nutzt
    // oder ein Kontakt, der nach gigger eingeladen wird und schonmal vom telefonbuch integriert wird.
    // man kann
    int[] bimages = {R.drawable.bandlogo1,
            R.drawable.bandlogo2,
            R.drawable.bandlogo3,
            R.drawable.bandlogo4};

    public GiggerContactCollection() {
        // ersmal ein paar dummy kontakte erstellen.
        // die kann ich dann erstmal darstellen.

        bands = new GiggerBandList();
        allContacts = new giggerContactList();
        GiggerBand currBand = null;
        GiggerContact currUser = null;
        int perBand = Math.round(names.length / 5);
        for (int i = 0; i < bnames.length; i++) {
            currBand = new GiggerBand(bnames[i]);
            currBand.contactID = "BAND" + Integer.toString(i);
            currBand.imgRef = bimages[i];

            for (int u = 0; u <= perBand; u++) {
                currUser = new GiggerContact();
                int u2 = u+i;
                currUser.contactName = names[(i * perBand) + u2];
                currUser.contactNumber = Long.toString(Math.round(Math.random()*10000) + i + u2+1000);
                currUser.contactID = "User" + Integer.toString((i * perBand) + u2);
                currBand.bandMembers.add(currUser);
                currUser.myBands.add(currBand);
                allContacts.add(currUser);
            }
            bands.add(currBand);
        }
        // max spielt in allen bands. nicht nur in no mans will.
        // mein login dummy.
        loginUser = new GiggerContact();
        loginUser.contactName = "FrankyFive";
        loginUser.contactID = "User" + 2324;
        loginUser.contactNumber = "1234123";
        loginUser.userLoginString = "dcdeal001";
        loginUser.imgRef = R.drawable.fry;
        String pwClean = "A1234!";
        try {
            loginUser.userPasswordHash = toHashStr(pwClean);
        } catch (Exception ie) {
            ie.printStackTrace();
        }
        allContacts.add(loginUser);
        bands.get(0).bandMembers.add(loginUser);
        loginUser.myBands.add(bands.get(0));
        bands.get(1).bandMembers.add(loginUser);
        loginUser.myBands.add(bands.get(1));
        bands.get(2).bandMembers.add(loginUser);
        loginUser.myBands.add(bands.get(2));

        // Daten in die Firebase Datenbank schreiben....

    }



    public static String toHashStr(String inp) throws Exception {
        String original = inp;
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(original.getBytes());
        byte[] digest = md.digest();
        StringBuffer sb = new StringBuffer();
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }

        return sb.toString();
    }

    public class giggerContactList extends ArrayList<GiggerContact> {
        public giggerContactList() {
        }

        public GiggerContact getContactById(String id) {
            GiggerContact gc = null;
            for (int i = 0; i < this.size(); i++) {
                if (this.get(i).contactID.equals(id)) {
                    gc = this.get(i);
                    break;
                }
            }

            return gc;
        }

        public ArrayList<String> getContactStringList(){
            ArrayList<String> slist = new ArrayList<>();
            for (int i = 0;i<size();i++){
                String halp = get(i).myBands.getBandListing();
                if (get(i).myBands.isEmpty()) {
                    halp = "keiner Band";
                }
                slist.add(get(i).contactName + " Spielt in "+halp);
            }
            return slist;
        }
    }

    public class GiggerBandList extends ArrayList<GiggerBand> {
        public GiggerBandList() {
        }

        public String getBandListing() {
            String res = "";
            for (GiggerBand inp : this) {
                res += " >'" + inp.contactName + "'";
            }

            return res;
        }

        public GiggerBand getBandById(String id) {
            GiggerBand gc = null;
            for (int i = 0; i < this.size(); i++) {
                if (this.get(i).contactName.equals(id)) {
                    gc = this.get(i);
                    break;
                }
            }
            return gc;
        }
    }

    // ein kontakt in der band oder ein Looser kontakt.
    // ein kontakt kann einer band angehören.-
    public class GiggerContactBasicItem {
        public String contactID;
        public String contactName;
        public String Color; // jede band und jeder kontakt hat eine Farbe. die Farbe wird aus dem querschnitt des bandphotos bzw. kontaktes errechnet.
        public int imgRef;
        public Bitmap getimageSmall(Activity context) {
            BitmapConverterHelper bh = new BitmapConverterHelper();
            return bh.resizeBitmap(context.getResources(),this.imgRef, Math.round(context.getResources().getDimension(R.dimen.BandList_smallSize)));
        }

        public Bitmap getimageColumn(Activity context) {
            BitmapConverterHelper bh = new BitmapConverterHelper();
            return bh.resizeBitmap(context.getResources(),this.imgRef,Math.round(context.getResources().getDimension(R.dimen.BandList_profileColumn)));
        }

        public Bitmap getimageBig(Activity context) {
            BitmapConverterHelper bh = new BitmapConverterHelper();
            return bh.resizeBitmap(context.getResources(),this.imgRef,Math.round(context.getResources().getDimension(R.dimen.BandList_largeSize)));
        }
    }

    public class GiggerContact extends GiggerContactBasicItem {
        public String contactNumber;
        public String userLoginString; // gigger online username .. wird beim ersten benutzen von GIGGER! direkt rgistriert.
        public String userPasswordHash;
        public ArrayList<String> playedInstruments; // was für instrumente man so spielt, und wie gut. soll später ne eigene klase werden.
        public GiggerBandList myBands; // bands, in den nich spiele;
        // ein recht könnte z.b. so aussehen. gegenstand für meine Bands erlauben, in dnen ich spiele.

        public boolean rightContactVisibleForCommunity; // man kann online nach angemeldeten Gigger! Kontakten suchen(wenn man sich suchbar stellt).

        public GiggerContact(){

            rightContactVisibleForCommunity = true;
            myBands = new GiggerBandList();
        }

    }

    // eine band, eine Liste von kontakten.
    public class GiggerBand extends GiggerContactBasicItem {
        public giggerContactList bandMembers;
        public String description;

        public GiggerBand(String bandName) {
            this.contactName = bandName;
            bandMembers = new giggerContactList();
        }
    }


}




