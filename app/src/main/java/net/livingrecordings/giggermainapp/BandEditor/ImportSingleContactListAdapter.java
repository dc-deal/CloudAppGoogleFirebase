package net.livingrecordings.giggermainapp.BandEditor;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.livingrecordings.giggermainapp.R;
import net.livingrecordings.giggermainapp.giggerMainClasses.GiggerContactCollection;

/**
 * Created by Kraetzig Neu on 11.11.2016.
 */

public class ImportSingleContactListAdapter extends BaseAdapter {


    private Activity mContext;
    public GiggerContactCollection.giggerContactList contList;

    public ImportSingleContactListAdapter(Activity context, GiggerContactCollection.giggerContactList gl) {
        mContext = context;
        this.contList = gl;
    }

    @Override
    public int getCount() {
        return contList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View gridViewAndroid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.items_maincontactlist_userelements, null);
        }
        // vorsicht der teil muss hier hin, das muss immer geschehen!! ansosnten wird die view TOTAL durcheinandergeschmissen!!
        GiggerContactCollection.GiggerContact gUser = contList.get(i);
        TextView textViewAndroid = (TextView) convertView.findViewById(R.id.contlist_textview_users);
        ImageView imageViewAndroid = (ImageView) convertView.findViewById(R.id.contlist_imageview_users);
        textViewAndroid.setText(gUser.contactName);
        imageViewAndroid.setImageBitmap(gUser.getimageSmall(mContext));


        return convertView;
    }


}