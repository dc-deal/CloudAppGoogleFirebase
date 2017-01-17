package net.livingrecordings.giggermainapp.giggerMainClasses.InterfaceObjects;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.libaml.android.view.chip.ChipLayout;

import net.livingrecordings.giggermainapp.giggerMainClasses.GiggerMainAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by Kraetzig Neu on 11.01.2017.
 */

public class TokenAutocompleteEdit extends ChipLayout {

    public ArrayAdapter<String> adapter;
    public Context mContext;


    private void createMe(Context context){
        mContext = context;
        if (mContext != null) {
            setOnChipItemChangeListener(cListen);
            addLayoutTextChangedListener(tv);
        }
    }

    public void setInputSet(Set<String> inp){
        ArrayList<String> li = new ArrayList<>();
        li.addAll(inp);
        setText(li);
    }

    public HashMap<String,Boolean> getAsHashMap(){
        HashMap<String,Boolean> mp = new HashMap<>();
        for (String s : getText()){
            mp.put(s,true);
        }
        return mp;
    }

    public TokenAutocompleteEdit(Context context) {
        super(context);
        createMe(context);
    }

    public TokenAutocompleteEdit(Context context, AttributeSet attrs) {
        super(context, attrs);
        createMe(context);
    }

    public TokenAutocompleteEdit(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        createMe(context);
      //  Typeface face=Typeface.createFromAsset(context.getAssets(), "Helvetica_Neue.ttf");
    //    this.setTypeface(face);
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);
    }


    ChipLayout.ChipItemChangeListener cListen = new ChipLayout.ChipItemChangeListener() {
        @Override
        public void onChipAdded(int pos, String txt) {

        }

        @Override
        public void onChipRemoved(int pos, String txt) {

        }
    };

    TextWatcher tv = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() > 1){
                final String search = s.toString().toUpperCase().trim();
                // neuen adapter setzen
                char c = '\uf8ff';
                GiggerMainAPI.getInstance().getTagsPublishedRef().orderByKey()
                        .startAt(search).endAt(search+c).limitToFirst(10)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    ArrayList<String> data = new ArrayList<>();
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        if (ds.child("name") != null) {
                                            data.add((String) ds.child("name").getValue());
                                        }
                                    }
                                    adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, data);
                                    setAdapter(adapter);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


}
