package net.livingrecordings.giggermainapp.EquipEditor;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.ListView;
import android.widget.TextView;

import net.livingrecordings.giggermainapp.FragmentMainEquipListManager;
import net.livingrecordings.giggermainapp.R;

/**
 * Created by Kraetzig Neu on 12.12.2016.
 */

public class CategoryDlg extends DialogFragment
implements FragmentMainEquipListManager.catManagerCallbacks {

    categoryDLGEvents catEventsListener;
    FragmentMainEquipListManager adapter;
    View rootView;
    String inputKey;
    Activity context;
    public CategoryDlg() {

    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        context = (Activity)activity;
        if (isAdded()) {
            try {
                if (getTargetFragment() != null) {
                    catEventsListener = (categoryDLGEvents) getTargetFragment();
                } else
                    catEventsListener = (categoryDLGEvents) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString() + " must implement categoryDLGEvents");
            }
        }
    }

    android.content.DialogInterface.OnClickListener oc = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            catEventsListener.onChooseCategory(adapter.getCurrentRootKey());
        }
    };
    android.content.DialogInterface.OnClickListener ocNegative = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            catEventsListener.onChooseCategory(inputKey);
        }
    };

    android.content.DialogInterface.OnCancelListener ocCancel = new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            catEventsListener.onChooseCategory(inputKey);
        }
    };

    @Override
    public void onLastLevelClicked(String actCatKey) {
        catEventsListener.onChooseCategory(actCatKey);
        dismiss();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        rootView = inflater.inflate(R.layout.fragment_maincategorylist, null);
        builder.setView(rootView);

        builder.setTitle(R.string.choose_cat);
   //     builder.setPositiveButton(getString(R.string.choose_cat_add),oc);
        builder.setNegativeButton(getString(R.string.abort),ocNegative);
        builder.setOnCancelListener(ocCancel);
        inputKey = getArguments().getString("startCategory");


        ListView listView = (ListView) rootView.findViewById(R.id.maincategorylist);
        //   TODO .. ich kann über die shared preferences die letzte position abfahren das wäre "ganz nett"
        adapter = new FragmentMainEquipListManager(getActivity(),this, listView, inputKey);

        TextView noCatsThere = (TextView) rootView.findViewById(R.id.noCatsThere);
        noCatsThere.setVisibility(View.VISIBLE);

        return builder.create();
    }

    public interface categoryDLGEvents {
        void onChooseCategory(String chooseKey);
    }


}