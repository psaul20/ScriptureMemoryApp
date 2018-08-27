package com.scripturememory.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.scripturememory.R;
import com.scripturememory.models.BibleVersion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SettingsBibleVersionAdapter extends RecyclerView.Adapter<SettingsBibleVersionAdapter.ViewHolder> {

    private int mSelectedItem;
    private Map<String, BibleVersion> mapBibleVersions;
    private List<BibleVersion> lstBibleVersions;
    private Context mContext;
    private static OnBibleVersionClickListener clickListener;

    // Provide a suitable constructor (depends on the kind of dataset)
    public SettingsBibleVersionAdapter(Context context, Map<String, BibleVersion> versions) {
        mContext = context;
        mapBibleVersions = versions;

        // Convert Map to List of Map
        List<Map.Entry<String, BibleVersion>> list = new LinkedList<Map.Entry<String, BibleVersion>>(mapBibleVersions.entrySet());

        // Sort list with Collections.sort(), provide a custom Comparator
        Collections.sort(list, new Comparator<Map.Entry<String, BibleVersion>>() {
            public int compare(Map.Entry<String, BibleVersion> o1, Map.Entry<String, BibleVersion> o2) {
                return (o1.getValue().compareTo(o2.getValue()));
            }
        });

        // Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<String, BibleVersion> mapSortedBibleVersions = new LinkedHashMap<>();
        for (Map.Entry<String, BibleVersion> entry : list) {
            mapSortedBibleVersions.put(entry.getKey(), entry.getValue());
        }

        lstBibleVersions = new ArrayList<>(mapSortedBibleVersions.values());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String bibleVersionPreference = preferences.getString("bible_version", "King James Version - KJV");
        int dashIndex = bibleVersionPreference.indexOf('-');
        String versionCode = bibleVersionPreference.substring(dashIndex+1, bibleVersionPreference.length()).trim();
        List<String> indexes = new ArrayList<>(mapSortedBibleVersions.keySet());
        mSelectedItem = indexes.indexOf(versionCode);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SettingsBibleVersionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View bibleVersionView = inflater.inflate(R.layout.view_model_longlist_selection, parent, false);
        return new SettingsBibleVersionAdapter.ViewHolder(bibleVersionView);
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final SettingsBibleVersionAdapter.ViewHolder holder, int position) {
        final BibleVersion bibleVersion = lstBibleVersions.get(position);
        holder.txtTitle.setText(bibleVersion.getVersionName() + " - " + bibleVersion.getVersionCode());
        holder.rbSelected.setChecked(position == mSelectedItem);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return lstBibleVersions.size();
    }

    //Click Listener implementation from https://www.codexpedia.com/android/defining-item-click-listener-for-recyclerview-in-android/
    // Provide a reference to the views for each data item
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txtTitle;
        public RadioButton rbSelected;

        private ViewHolder(View bibleVersionView) {
            super(bibleVersionView);

            //Link java to views inflated from view model XML
            txtTitle = bibleVersionView.findViewById(R.id.txtTitle);
            rbSelected = bibleVersionView.findViewById(R.id.rbSelected);

            //Sets clickbox to entire inflated layout
            rbSelected.setOnClickListener(this);
            bibleVersionView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mSelectedItem = getAdapterPosition();
            notifyDataSetChanged();
            String languageText = txtTitle.getText().toString();
            clickListener.onItemClick(languageText ,mSelectedItem);
        }

    }

    public interface OnBibleVersionClickListener {
        void onItemClick(String bibleVersion, int position);
    }

    public void setClickListener(OnBibleVersionClickListener itemClickListener) {
        clickListener = itemClickListener;
    }

}
