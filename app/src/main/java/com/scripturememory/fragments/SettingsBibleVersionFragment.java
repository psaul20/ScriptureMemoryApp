package com.scripturememory.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.scripturememory.R;
import com.scripturememory.activities.AddPsg;
import com.scripturememory.adapters.AddPsgAdapter;
import com.scripturememory.adapters.SettingsBibleVersionAdapter;
import com.scripturememory.adapters.SettingsLanguageAdapter;
import com.scripturememory.adapters.SettingsBibleVersionAdapter.OnBibleVersionClickListener;
import com.scripturememory.helpers.JsonParser;
import com.scripturememory.models.AddVerseExpandableGroup;
import com.scripturememory.models.AddVerseExpandableItem;
import com.scripturememory.models.BibleVersion;
import com.scripturememory.models.Language;
import com.scripturememory.network.ScriptureClient;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class SettingsBibleVersionFragment extends Fragment implements OnBibleVersionClickListener {

    private RecyclerView rcvSettingsBibleVersion;
    private Map<String, BibleVersion> mapBibleVersions = new HashMap<>();
    private ScriptureClient mScriptureClient;
    private SettingsBibleVersionAdapter settingsBibleVersionAdapter;

    private static Logger logger = Logger.getLogger(SettingsBibleVersionFragment.class.toString());

    private OnBibleVersionSelectedListener mListener;

    public SettingsBibleVersionFragment() {

    }

    public static SettingsBibleVersionFragment newInstance(Bundle args) {
        SettingsBibleVersionFragment fragment = new SettingsBibleVersionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.settings_longlist_fragment, container, false);

        mScriptureClient = ScriptureClient.getInstance(getActivity());
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String languagePreference = preferences.getString("language", "");
        int dashIndex = languagePreference.indexOf('-');
        String lngCode = languagePreference.substring(dashIndex+1, languagePreference.length()).trim();
        populateBibleVersions(rootView, lngCode);

        return rootView;
    }

    public void populateBibleVersions(final View rootView, String lngCode) {
        String url = "https://dbt.io/library/volume?key=" + ScriptureClient.BIBLE_API_KEY + "&media=text&language_family_code=" + lngCode + "&v=2";
        JsonArrayRequest bibleVersionRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                logger.info(response.toString());
                List<BibleVersion> availableBibleVersions;
                availableBibleVersions = JsonParser.readBibleVersions(response);
                for (BibleVersion bibleVersion : availableBibleVersions) {
                    mapBibleVersions.put(bibleVersion.getVersionCode(), bibleVersion);
                }

                //Create RecyclerView to be populated
                rcvSettingsBibleVersion = rootView.findViewById(R.id.rcvSettingsLongList);

                //Used for performance gains if list item layout will not change based on addition of new items
                rcvSettingsBibleVersion.setHasFixedSize(true);

                //Declare and specify layout manager for RecyclerView
                rcvSettingsBibleVersion.setLayoutManager(new LinearLayoutManager(getActivity()));

                //Declare and specify adapter for RecyclerView. See SavedPsgAdapter Class
                settingsBibleVersionAdapter = new SettingsBibleVersionAdapter(getActivity(), mapBibleVersions);
                rcvSettingsBibleVersion.setAdapter(settingsBibleVersionAdapter);
                settingsBibleVersionAdapter.setClickListener(SettingsBibleVersionFragment.this);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mScriptureClient.addToRequestQueue(bibleVersionRequest);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBibleVersionSelectedListener) {
            mListener = (OnBibleVersionSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnBibleVersionSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search_bar, menu);
        super.onCreateOptionsMenu(menu,inflater);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Map<String, BibleVersion> filter = new HashMap<>();
                for (Map.Entry<String, BibleVersion> entry : mapBibleVersions.entrySet()) {
                    if (entry.getValue().getVersionName().toString().toLowerCase().startsWith(newText.toLowerCase())) {
                        filter.put(entry.getKey(), entry.getValue());
                    }
                }

                settingsBibleVersionAdapter = new SettingsBibleVersionAdapter(getActivity(), filter);
                rcvSettingsBibleVersion.setAdapter(settingsBibleVersionAdapter);
                return false;
            }
        });
    }

    // Recylcerview on item clicked
    @Override
    public void onItemClick(String bibleVersion, int position) {
        if (mListener != null) {
            mListener.onBibleVersionSelected(bibleVersion, position);
        }
    }

    public interface OnBibleVersionSelectedListener {
        void onBibleVersionSelected(String bibleVersion, int position);
    }
}
