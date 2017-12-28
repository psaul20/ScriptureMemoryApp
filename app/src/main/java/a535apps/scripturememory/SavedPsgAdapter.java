package a535apps.scripturememory;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Patrick on 12/17/2017.
 * Modeled after David Gassner instructional video on Lynda.com
 * Code taken from https://gist.github.com/davidgassner/e2ca4aef5eb0adceae80790afeba0859
 */

public class SavedPsgAdapter extends RecyclerView.Adapter<SavedPsgAdapter.ViewHolder> {

    private List<MemoryPassage> lstSavedPsgs = new ArrayList<>();
    private Context mContext;
    public static final String PSG_KEY = "psg_key";

    // Provide a suitable constructor (depends on the kind of dataset)
    public SavedPsgAdapter(Context context, List<MemoryPassage> DataSet) {
        lstSavedPsgs = DataSet;
        mContext = context;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtPsgRef;
        //used for click event
        public View mView;

        public ViewHolder(View savedPsgView) {
            super(savedPsgView);

            txtPsgRef = (TextView) savedPsgView.findViewById(R.id.txtPsgRefText);
            mView = savedPsgView;
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SavedPsgAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View savedPsgView = inflater.inflate(R.layout.saved_verse_view_model, parent, false);
        // set the view's size, margins, paddings and layout parameters
        //...
        return new ViewHolder(savedPsgView);

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        final MemoryPassage psg = lstSavedPsgs.get(position);
        // - replace the contents of the view with that element
        try {
            holder.txtPsgRef.setText(psg.getPsgReference());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
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

