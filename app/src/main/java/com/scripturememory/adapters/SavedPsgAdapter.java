package com.scripturememory.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.scripturememory.models.MemoryPassage;
import com.scripturememory.R;
import com.scripturememory.activities.MemoryExercise;

/**
 * Created by Patrick on 12/17/2017.
 * Modeled after David Gassner instructional video on Lynda.com
 * Code taken from https://gist.github.com/davidgassner/e2ca4aef5eb0adceae80790afeba0859
 */

public class SavedPsgAdapter extends RecyclerView.Adapter<SavedPsgAdapter.ViewHolder> {

    private List<MemoryPassage> lstSavedPsgs;
    private Context mContext;

    //set generic constant to store psg on click with intent.putExtra
    public static final String PSG_KEY = "psg_key";

    // Provide a suitable constructor (depends on the kind of dataset)
    public SavedPsgAdapter(Context context, List<MemoryPassage> savedPsgs) {
        mContext = context;
        lstSavedPsgs = savedPsgs;
    }

    // Provide a reference to the views for each data item
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView passageTitle;

        private ViewHolder(View savedPsgView) {
            super(savedPsgView);
            passageTitle = savedPsgView.findViewById(R.id.passageTitle);
        }

        public TextView getPassageTitle() {
            return passageTitle;
        }

        public void setPassageTitle(String title) {
            passageTitle.setText(title);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SavedPsgAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View savedPsgView = inflater.inflate(R.layout.row_saved_verses, parent, false);
        return new ViewHolder(savedPsgView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        final MemoryPassage psg = lstSavedPsgs.get(position);
        holder.setPassageTitle(psg.getPsgReference());
        holder.getPassageTitle().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //Open new exercise activity, display text
                Intent intent = new Intent(mContext, MemoryExercise.class);
                intent.putExtra(PSG_KEY, psg);
                mContext.startActivity(intent);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return lstSavedPsgs.size();
    }
}

