package net.livingrecordings.giggermainapp;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.livingrecordings.giggermainapp.giggerMainClasses.GiggerContactCollection;
import net.livingrecordings.giggermainapp.giggerMainClasses.helperClasses.GiggerIntentHelperClass;

public class FragmentMainContactListAdapter extends BaseExpandableListAdapter {

    public GiggerContactCollection.GiggerBandList bands;
    public LayoutInflater inflater;
    public FragmentActivity context;
    private GiggerIntentHelperClass gic;

    public FragmentMainContactListAdapter(FragmentActivity context, GiggerContactCollection.GiggerBandList bandsToShow) {
        this.context = context;
        this.bands = bandsToShow;
        inflater = context.getLayoutInflater();
        gic = new GiggerIntentHelperClass(context);
    }



    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return bands.get(groupPosition).bandMembers.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final GiggerContactCollection.GiggerContact gUser = (GiggerContactCollection.GiggerContact) getChild(groupPosition, childPosition);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.items_maincontactlist_userelements, null);
        }
        TextView textViewAndroid = (TextView) convertView.findViewById(R.id.contlist_textview_users);
        ImageView imageViewAndroid = (ImageView) convertView.findViewById(R.id.contlist_imageview_users);
        textViewAndroid.setText(gUser.contactName);
        imageViewAndroid.setImageBitmap(gUser.getimageSmall(context));

        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                gic.intentShowContact(gUser);
            }
        });

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return bands.get(groupPosition).bandMembers.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return bands.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return bands.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.items_maincontactlist_bands, null);
        }
        GiggerContactCollection.GiggerBand bandVar = (GiggerContactCollection.GiggerBand) getGroup(groupPosition);

        View myView = (View)convertView;
        TextView textViewAndroid = (TextView) myView.findViewById(R.id.contlist_textview_bands);
        ImageView imageViewAndroid = (ImageView) myView.findViewById(R.id.contlist_imageview_bands);
        textViewAndroid.setText(bandVar.contactName);
        imageViewAndroid.setImageBitmap(bandVar.getimageColumn(context));
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}