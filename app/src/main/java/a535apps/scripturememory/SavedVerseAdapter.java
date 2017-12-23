package a535apps.scripturememory;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Patrick on 12/17/2017.
 */

public class SavedVerseAdapter extends RecyclerView.Adapter<SavedVerseAdapter.ViewHolder> {
    private String[] VerseRefData;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtVerseRef;
        public ViewHolder(TextView v) {
            super(v);
            txtVerseRef = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public SavedVerseAdapter(String[] DataSet) {
        VerseRefData = DataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SavedVerseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        TextView v = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_verse_textview, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.txtVerseRef.setText(VerseRefData[position]);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return VerseRefData.length;
    }
}

