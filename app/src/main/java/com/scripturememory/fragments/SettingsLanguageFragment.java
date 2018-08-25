package com.scripturememory.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
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
import com.scripturememory.adapters.SettingsLanguageAdapter;
import com.scripturememory.adapters.SettingsLanguageAdapter.OnLanguageClickListener;
import com.scripturememory.helpers.JsonParser;
import com.scripturememory.models.AddVerseExpandableGroup;
import com.scripturememory.models.AddVerseExpandableItem;
import com.scripturememory.models.Language;
import com.scripturememory.network.ScriptureClient;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SettingsLanguageFragment extends Fragment implements OnLanguageClickListener {

    private RecyclerView rcvSettingsLanguage;
    private Map<String, Language> mapLanguages = new HashMap<>();
    private ScriptureClient mScriptureClient;
    private SettingsLanguageAdapter settingsLanguageAdapter;

    private static Logger logger = Logger.getLogger(SettingsLanguageFragment.class.toString());

    private OnLanguageSelectedListener mListener;

    public SettingsLanguageFragment() {

    }

    public static SettingsLanguageFragment newInstance(Bundle args) {
        SettingsLanguageFragment fragment = new SettingsLanguageFragment();
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
        final View rootView = inflater.inflate(R.layout.settings_language_fragment, container, false);

        mScriptureClient = ScriptureClient.getInstance(getActivity());
        populateLanguages(rootView);

        return rootView;
    }

    public void populateLanguages(final View rootView) {
        String url = "https://dbt.io/library/volumelanguagefamily?key=" + ScriptureClient.BIBLE_API_KEY + "&media=text&v=2";
        JsonArrayRequest languageRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                logger.info(response.toString());
                List<Language> availableLanguages;
                availableLanguages = JsonParser.readLanguages(response);
                List<String> languages = new ArrayList<>();
                for (Language language : availableLanguages) {
                    mapLanguages.put(language.getLngCode(), language);
                }

                //Create RecyclerView to be populated
                rcvSettingsLanguage = rootView.findViewById(R.id.rcvSettingsLanguage);

                //Used for performance gains if list item layout will not change based on addition of new items
                rcvSettingsLanguage.setHasFixedSize(true);

                //Declare and specify layout manager for RecyclerView
                rcvSettingsLanguage.setLayoutManager(new LinearLayoutManager(getActivity()));

                //Declare and specify adapter for RecyclerView. See SavedPsgAdapter Class
                settingsLanguageAdapter = new SettingsLanguageAdapter(getActivity(), mapLanguages);
                rcvSettingsLanguage.setAdapter(settingsLanguageAdapter);
                settingsLanguageAdapter.setClickListener(SettingsLanguageFragment.this);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mScriptureClient.addToRequestQueue(languageRequest);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLanguageSelectedListener) {
            mListener = (OnLanguageSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLanguageSelectedListener");
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
                Map<String, Language> filter = new HashMap<>();
                for (Map.Entry<String, Language> entry : mapLanguages.entrySet()) {
                    if (entry.getValue().getLngName().toString().toLowerCase().startsWith(newText.toLowerCase())) {
                        filter.put(entry.getKey(), entry.getValue());
                    }
                }

                settingsLanguageAdapter = new SettingsLanguageAdapter(getActivity(), filter);
                rcvSettingsLanguage.setAdapter(settingsLanguageAdapter);
                return false;
            }
        });
    }

    // Recylcerview on item clicked
    @Override
    public void onItemClick(String language, int position) {
        if (mListener != null) {
            mListener.onLanguageSelected(language, position);
        }
    }

    public interface OnLanguageSelectedListener {
        void onLanguageSelected(String language, int position);
    }
}
