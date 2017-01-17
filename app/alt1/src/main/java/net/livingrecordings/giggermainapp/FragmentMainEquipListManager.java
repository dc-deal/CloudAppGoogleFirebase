package net.livingrecordings.giggermainapp;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.database.DataSetObserver;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.firebase.ui.database.FirebaseListAdapter;
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

import net.livingrecordings.giggermainapp.giggerMainClasses.ItemClass;


/**
 * Created by Kraetzig Neu on 12.12.2016.
 */

public class FragmentMainEquipListManager {

    static DatabaseReference userCatsData,cserCatsData2;
    static StorageReference userCatsImages;
    Query fbQuery;
    String actCatKey;
    Activity mContext;
    AbsListView mView;
    FragmentMainEquipListAdapter adapter;

    catManagerCallbacks managerCallbacks;
    public interface catManagerCallbacks {
        void onLastLevelClicked(String actCatKey); // kommt auf, wenn einer das letzt level geklickt hat!
    }

    private void refreshAdapter() {
        // 2 ideen. a) setReference nue machen..
        // b) adapeter erst später der globalen zuweisen
        fbQuery = userCatsData.orderByChild("parCat").equalTo(actCatKey);
        int itemLayoutReference;
        if(mView.getClass().equals(GridView.class)){
            itemLayoutReference = R.layout.items_maincategorygrid;
        } else {
            itemLayoutReference = R.layout.items_maincategorylist;
        }
        FragmentMainEquipListAdapter myadapter = new FragmentMainEquipListAdapter(fbQuery,mContext,itemLayoutReference);
        mView.setAdapter(myadapter);
        adapter = myadapter;
    }


    public ItemClass getItem(int i){
        return adapter.getItem(i);
    }
    public DatabaseReference getRef(int i){
        return adapter.getRef(i);
    }

    public String getCurrentRootKey(){
        return actCatKey;
    }


    public FragmentMainEquipListManager(Activity activity,Object listenerCarrier, AbsListView listView, String startCategory) {
        try {
            managerCallbacks = (catManagerCallbacks)listenerCarrier;
        } catch (ClassCastException e) {
            throw new ClassCastException("Input Parameter listenerCarrier must implement catManagerCallbacks");
        }
        mView = listView; // kann ngrid oder Liste sein.
        mContext = activity;
        actCatKey = startCategory;
        // mein context, also die activity.
        FirebaseDatabase fDB = FirebaseDatabase.getInstance();
        FirebaseStorage fStorage = FirebaseStorage.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userCatsData = fDB.getReference("").child(user.getUid());
        cserCatsData2 = fDB.getReference("").child(user.getUid());
        userCatsImages = fStorage.getReference("").child(user.getUid());

        fbQuery = userCatsData.orderByChild("parCat").equalTo(actCatKey);

        fbQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fbQuery.removeEventListener(this);
                if (dataSnapshot.getChildrenCount() <= 0) {
                    pushBack();
                };
            }
            @Override public void onCancelled(DatabaseError databaseError) {}
        });
        // klick EVENT!!!
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int i, long id) {
                final String clickedCategoryKey = adapter.getRef(i).getKey();
                userCatsData.orderByChild("parCat").equalTo(clickedCategoryKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() > 0) {
                            actCatKey = clickedCategoryKey;
                            refreshAdapter();
                        } else {
                            // keine unterkategorie...
                            if (managerCallbacks != null){
                                managerCallbacks.onLastLevelClicked(clickedCategoryKey);
                            }


                            //Toast.makeText(getActivity(), getString(R.string.no_child_cat), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override public void onCancelled(DatabaseError databaseError) {}
                });

            }
        });
        refreshAdapter();
    }

    public Boolean pushBack() {
        if (!actCatKey.equals("root")) {
            // ich gehe zur hbauptkategore..
            if (adapter.getParCat().equals("root")) {
                actCatKey = "root";
                refreshAdapter();
            } else {
                // ich gehe in eine reele kategorie zturück..
                cserCatsData2.child(adapter.getParCat()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        cserCatsData2.removeEventListener(this);
                        actCatKey = (String) dataSnapshot.child("parCat").getValue();
                        refreshAdapter();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        userCatsData.removeEventListener(this);
                    }
                });
                return true;
            }
        }
        return false;
    }


}
