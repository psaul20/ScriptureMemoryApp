package com.scripturememory.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import com.scripturememory.Interfaces.ItemClickListener;
import com.scripturememory.models.MemoryPassage;
import com.scripturememory.R;

/**
 * Created by Patrick on 12/17/2017.
 * Modeled after David Gassner instructional video on Lynda.com
 * Code taken from https://gist.github.com/davidgassner/e2ca4aef5eb0adceae80790afeba0859
 */

public class SavedPsgAdapter extends RecyclerView.Adapter<SavedPsgAdapter.ViewHolder> {

    private List<MemoryPassage> lstSavedPsgs;
    private Context mContext;
    private static ItemClickListener clickListener;

    //set generic constant to store psg on click with intent.putExtra
    public static final String PSG_KEY = "psg_key";

    // Provide a suitable constructor (depends on the kind of dataset)
    public SavedPsgAdapter(Context context, List<MemoryPassage> savedPsgs) {
        mContext = context;
        lstSavedPsgs = savedPsgs;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SavedPsgAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View savedPsgView = inflater.inflate(R.layout.view_model_saved_psgs, parent, false);
        // option to set the view's size, margins, paddings and layout parameters
        //...
        return new ViewHolder(savedPsgView);
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // - get element from your dataset at this position
        final MemoryPassage psg = lstSavedPsgs.get(position);
        // - replace the contents of the view with given passage
        holder.txtPsgRef.setText(psg.getPsgReference());
        holder.txtExercMsg.setText(psg.getExercMsg());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return lstSavedPsgs.size();
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        clickListener = itemClickListener;
    }

    //Click Listener implementation from https://www.codexpedia.com/android/defining-item-click-listener-for-recyclerview-in-android/
    // Provide a reference to the views for each data item
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView txtPsgRef;
        public TextView txtExercMsg;
        public ImageButton btnDeletePsg;

        private ViewHolder(View savedPsgView) {
            super(savedPsgView);

            //Link java to views inflated from view model XML
            txtPsgRef = savedPsgView.findViewById(R.id.txtPsgRef);
            txtExercMsg = savedPsgView.findViewById(R.id.txtExercMsg);
            btnDeletePsg = savedPsgView.findViewById(R.id.btnDeletePsg);

            //Sets clickbox to entire inflated layout
            savedPsgView.setOnClickListener(this);
            savedPsgView.setOnLongClickListener(this);
            btnDeletePsg.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view){
            return clickListener.onLongClick(view, getAdapterPosition());
        }
    }

}

