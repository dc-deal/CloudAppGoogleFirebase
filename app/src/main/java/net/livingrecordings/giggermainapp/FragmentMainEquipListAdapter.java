package net.livingrecordings.giggermainapp;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Query;

import net.livingrecordings.giggermainapp.giggerMainClasses.models.ItemClass;

/**
 * Created by Kraetzig Neu on 03.11.2016.
 */


public class FragmentMainEquipListAdapter extends FirebaseListAdapter<ItemClass>  {
    private Context mContext;
 //   private String parCatFromPopView;

    public FragmentMainEquipListAdapter(Query ref, Activity activity, int layout) {
        super(activity, ItemClass.class, layout, ref);
  //      parCatFromPopView = ref.getRef().getKey();
        mContext = activity;
    }

    public String getParCat(){
        // TODO eigentlich muss hier ein callback rein..und interface .. später
    //    if (parCatFromPopView.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
    //        return "root";
     //   } else
    //      return parCatFromPopView;
        return "";
    }


    @Override
    protected void populateView(View view, ItemClass catClass, int index) {
        // Vies Finden und beschriften...
        TextView compText = (TextView) view.findViewById(R.id.maincategory_gridview_textview);
        compText.setText(catClass.getName());
       // TextView cmpdesc = (TextView) view.findViewById(R.id.maincategory_gridview_descview);
    //    if (cmpdesc != null) {
     //       cmpdesc.setText(catClass.getDesc());
      //  }
        ImageView img = (ImageView) view.findViewById(R.id.maincategory_gridview_itemimage);
      //  Uri catImgUri = Uri.parse(catClass.getImg());
     //   Picasso.with(mContext)
   //             .load(catImgUri)
     //           .placeholder(R.drawable.imgplaceholder)// //R.drawable.progress_animation
    //            .error(R.drawable.erroricon).into(img);
        // hier könnte ich noch ein desc feld einfügen...

  //      parCatFromPopView = catClass.getParCat();
    }

}