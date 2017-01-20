package net.livingrecordings.giggermainapp.BandEditor;

import android.app.Activity;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import net.livingrecordings.giggermainapp.R;
import net.livingrecordings.giggermainapp.giggerMainClasses.MainAPI.GiggerMainAPI;
import net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses.GiggerIntentHelperClass;
import net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses.LoadImageCasheHelper;
import net.livingrecordings.giggermainapp.giggerMainClasses.models.BandClass;

/**
 * Created by Kraetzig Neu on 20.01.2017.
 */

// zeigt eine band liste...
    // kann sowohl nur die bands anzegien, die der user selbst spielt
    // oder auch alles, was an bands und anfragen so rumliegt.{USERS_BANDCONTACTSTATES}
public class BandList {


    public void setBandListCallbacks(BandListCallbacks bandListCallbacks) {
        this.bandListCallbacks = bandListCallbacks;
    }

    public interface BandListCallbacks{
        void onEmptyDatabase();// keine daten!!
        void onReadyLoading();
    }

    Activity mContext;
    ListView iList;
    FirebaseListAdapter<BandClass> listAdapter;
    private  BandListCallbacks bandListCallbacks;
    Boolean showCOntactStates;

    public void startBandList(Activity context, ListView list, Boolean showCOntactStates){
        mContext = context;
        iList = list;
        this.showCOntactStates = showCOntactStates;
        GiggerMainAPI gi = GiggerMainAPI.getInstance();
        Query fbQ;
        if (showCOntactStates) {
            fbQ = gi.getQueryAllBandprocessesOfUser(gi.getCurrentUserUID());
        } else {
            fbQ = gi.getQueryAllBandsOfUser(gi.getCurrentUserUID());
        }
        // check if empty..
        fbQ.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    // callback
                    bandListCallbacks.onEmptyDatabase();
                }
                bandListCallbacks.onReadyLoading();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        listAdapter = new FirebaseListAdapter<BandClass>
                (mContext, BandClass.class, R.layout.items_itemlist_item,
                        fbQ)
        {
            @Override
            protected void populateView(View view, BandClass object,int i) {
                // vorauswahl treffen ...
                if (bandListCallbacks != null){
                    ((TextView) view.findViewById(R.id.itemlist_textview_name)).setText(object.getName());
                    ((TextView) view.findViewById(R.id.itemlist_textview_desc)).setText(object.getStyle());
                    // noch das bild... dafür den cashe anzapfen ( managed die unit automatisch)
                    // SO EINFACH GEHTS NICHT""" ich muss den imageschlüssel holen
                    LoadImageCasheHelper.getInstance()
                            .getImageAndCashe(mContext, Uri.parse(object.getImgUrl()),(ImageView) view.findViewById(R.id.itemlist_textview_image));
                }
            }
        };
        iList.setAdapter(listAdapter);


        // klick event
        iList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                new GiggerIntentHelperClass(mContext)
                        .intentShowBand(listAdapter.getRef(position).getKey());
            }
        });
    }



}
