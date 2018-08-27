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
import com.scripturememory.models.Language;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SettingsLanguageAdapter extends RecyclerView.Adapter<SettingsLanguageAdapter.ViewHolder> {

    private int mSelectedItem;
    private Map<String, Language> mapLanguages;
    private List<Language> lstLanguages;
    private Context mContext;
    private static OnLanguageClickListener clickListener;

    // Provide a suitable constructor (depends on the kind of dataset)
    public SettingsLanguageAdapter(Context context, Map<String, Language> languages) {
        mContext = context;
        mapLanguages = languages;

        // Convert Map to List of Map
        List<Map.Entry<String, Language>> list = new LinkedList<Map.Entry<String, Language>>(mapLanguages.entrySet());

        // Sort list with Collections.sort(), provide a custom Comparator
        Collections.sort(list, new Comparator<Map.Entry<String, Language>>() {
            public int compare(Map.Entry<String, Language> o1, Map.Entry<String, Language> o2) {
                return (o1.getValue().compareTo(o2.getValue()));
            }
        });

        // Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<String, Language> mapSortedLanguages = new LinkedHashMap<>();
        for (Map.Entry<String, Language> entry : list) {
            mapSortedLanguages.put(entry.getKey(), entry.getValue());
        }

        lstLanguages = new ArrayList<>(mapSortedLanguages.values());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String languagePreference = preferences.getString("language", "English - ENG");
        int dashIndex = languagePreference.indexOf('-');
        String lngCode = languagePreference.substring(dashIndex+1, languagePreference.length()).trim();
        List<String> indexes = new ArrayList<>(mapSortedLanguages.keySet());
        mSelectedItem = indexes.indexOf(lngCode);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SettingsLanguageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View languageView = inflater.inflate(R.layout.view_model_longlist_selection, parent, false);
        return new SettingsLanguageAdapter.ViewHolder(languageView);
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final SettingsLanguageAdapter.ViewHolder holder, int position) {
        final Language language = lstLanguages.get(position);
        holder.txtTitle.setText(language.getLngName() + " - " + language.getLngCode());
        holder.rbSelected.setChecked(position == mSelectedItem);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return lstLanguages.size();
    }

    //Click Listener implementation from https://www.codexpedia.com/android/defining-item-click-listener-for-recyclerview-in-android/
    // Provide a reference to the views for each data item
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txtTitle;
        public RadioButton rbSelected;

        private ViewHolder(View languageView) {
            super(languageView);

            //Link java to views inflated from view model XML
            txtTitle = languageView.findViewById(R.id.txtTitle);
            rbSelected = languageView.findViewById(R.id.rbSelected);

            //Sets clickbox to entire inflated layout
            rbSelected.setOnClickListener(this);
            languageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mSelectedItem = getAdapterPosition();
            notifyDataSetChanged();
            String languageText = txtTitle.getText().toString();
            clickListener.onItemClick(languageText ,mSelectedItem);
        }

    }

    public interface OnLanguageClickListener {
        void onItemClick(String language, int position);
    }

    public void setClickListener(OnLanguageClickListener itemClickListener) {
        clickListener = itemClickListener;
    }

}
