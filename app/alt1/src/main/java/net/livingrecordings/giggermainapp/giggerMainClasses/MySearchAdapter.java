package net.livingrecordings.giggermainapp.giggerMainClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.livingrecordings.giggermainapp.R;

import java.util.ArrayList;

/**
 * Created by Franky on 17.12.2016.
 */

public class MySearchAdapter extends BaseAdapter {
    LayoutInflater inflater;
    Context context;


    public MySearchAdapter(Context context, ArrayList myList) {
    //    this.myList = myList;
        this.context = context;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        //return myList.size();
        return 0;
    }

    @Override
    public ItemClass getItem(int position) {
  //      return myList.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;

        if (convertView == null) {
          //  convertView = inflater.inflate(R.layout.layout_list_item, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

      //  ListData currentListData = getItem(position);
//
//        mViewHolder.tvTitle.setText(currentListData.getTitle());
//        mViewHolder.tvDesc.setText(currentListData.getDescription());
//        mViewHolder.ivIcon.setImageResource(currentListData.getImgResId());

        return convertView;
    }

    private class MyViewHolder {
        TextView tvTitle, tvDesc;
        ImageView ivIcon;

        public MyViewHolder(View item) {
//            tvTitle = (TextView) item.findViewById(R.id.tvTitle);
//            tvDesc = (TextView) item.findViewById(R.id.tvDesc);
//            ivIcon = (ImageView) item.findViewById(R.id.ivIcon);
        }
    }


}
