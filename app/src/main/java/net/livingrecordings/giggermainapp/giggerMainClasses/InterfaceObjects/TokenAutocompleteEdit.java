package net.livingrecordings.giggermainapp.giggerMainClasses.InterfaceObjects;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.libaml.android.view.chip.ChipLayout;

import net.livingrecordings.giggermainapp.giggerMainClasses.GiggerItemAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by Kraetzig Neu on 11.01.2017.
 */

// basiet auf MultiAutoCompleteTextView..
public class TokenAutocompleteEdit extends ChipLayout implements MultiAutoCompleteTextView.Tokenizer {

    public ArrayAdapter<String> adapter;
    public Context mContext;


//http://stackoverflow.com/questions/3482981/how-to-replace-the-comma-with-a-space-when-i-use-the-multiautocompletetextview
    public int findTokenStart(CharSequence text, int cursor) {
        int i = cursor;

        while (i > 0 && text.charAt(i - 1) != ' ') {
            i--;
        }
        while (i < cursor && text.charAt(i) == ' ') {
            i++;
        }

        return i;
    }

    public int findTokenEnd(CharSequence text, int cursor) {
        int i = cursor;
        int len = text.length();

        while (i < len) {
            if (text.charAt(i) == ' ') {
                return i;
            } else {
                i++;
            }
        }

        return len;
    }

    public CharSequence terminateToken(CharSequence text) {
        int i = text.length();

        while (i > 0 && text.charAt(i - 1) == ' ') {
            i--;
        }

        if (i > 0 && text.charAt(i - 1) == ' ') {
            return text;
        } else {
            if (text instanceof Spanned) {
                SpannableString sp = new SpannableString(text + " ");
                TextUtils.copySpansFrom((Spanned) text, 0, text.length(),
                        Object.class, sp, 0);
                return sp;
            } else {
                return text + " ";
            }
        }
    }

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
                //s.charAt(s.length()-1)
                final String search = s.toString().toUpperCase().trim();
                // neuen adapter setzen
                char c = '\uf8ff';
                GiggerItemAPI.getInstance().getTagsPublishedRef().orderByKey()
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
