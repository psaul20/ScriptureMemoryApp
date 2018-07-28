package com.scripturememory.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

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

        private TextView txtPsgRef;
        private TextView txtExercMsg;
        //used for click event
        private View mView;

        private ViewHolder(View savedPsgView) {
            super(savedPsgView);

            //Link java to views inflated from view model XML
            txtPsgRef = savedPsgView.findViewById(R.id.txtPsgRef);
            txtExercMsg = savedPsgView.findViewById(R.id.txtExercMsg);

            //Sets clickbox to entire inflated view
            mView = savedPsgView;
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SavedPsgAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View savedPsgView = inflater.inflate(R.layout.saved_psg_view_model, parent, false);
        // option to set the view's size, margins, paddings and layout parameters
        //...
        return new ViewHolder(savedPsgView);
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        final MemoryPassage psg = lstSavedPsgs.get(position);
        // - replace the contents of the view with given passage
            holder.txtPsgRef.setText(psg.getPsgReference());
            holder.txtExercMsg.setText(psg.getExercMsg());

        holder.mView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v){
                //Open new exercise activity, pass psg along with intent
                //May need to calculate currentTimeMillis and pass along to standardize
                //calculations (not sure if this is necessary)
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

