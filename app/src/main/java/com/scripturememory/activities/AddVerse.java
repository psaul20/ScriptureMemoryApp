package com.scripturememory.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.scripturememory.data.SavedPsgsService;
import com.scripturememory.models.BibleVersion;
import com.scripturememory.models.Book;
import com.scripturememory.helpers.JsonParser;
import com.scripturememory.models.Language;
import com.scripturememory.models.MemoryPassage;
import com.scripturememory.R;
import com.scripturememory.network.ScriptureClient;

public class AddVerse extends AppCompatActivity {

    private Logger logger = Logger.getLogger(getClass().toString());

    private SavedPsgsService mSavedPsgsService;
    private ScriptureClient mScriptureClient;
    private String selectedLngCode;
    private String selectedVersionCode;
    private String selectedBookName;
    private Integer selectedChapter;
    private MemoryPassage foundPassage;
    private Map<String, Book> mapNameToBook;
    private AutoCompleteTextView languageSelector;
    private AutoCompleteTextView versionSelector;
    private AutoCompleteTextView bookSelector;
    private AutoCompleteTextView chapterSelector;
    private AutoCompleteTextView verseSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_verse);

        mSavedPsgsService = new SavedPsgsService(this);
        mSavedPsgsService.openDb();

        mScriptureClient = ScriptureClient.getInstance(this);

        // Set selectors
        languageSelector = (AutoCompleteTextView) findViewById(R.id.languageSelect);
        versionSelector = (AutoCompleteTextView) findViewById(R.id.versionSelect);
        bookSelector = (AutoCompleteTextView) findViewById(R.id.bookSelect);
        verseSelector = (AutoCompleteTextView) findViewById(R.id.verseSelect);
        chapterSelector = (AutoCompleteTextView) findViewById(R.id.chapterSelect);

        languageSelector.setThreshold(1);
        versionSelector.setThreshold(1);
        bookSelector.setThreshold(1);
        chapterSelector.setThreshold(1);
        verseSelector.setThreshold(1);

        mapNameToBook = new HashMap<>();

        populateLanguages();
        languageSelector.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedLang = (String)parent.getItemAtPosition(position);
                selectedLngCode = selectedLang.substring(selectedLang.indexOf('-')+1).trim();
                populateBibleVersions(selectedLngCode);
            }
        });

        versionSelector.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedVersion = (String)parent.getItemAtPosition(position);
                selectedVersionCode = selectedVersion.substring(selectedVersion.indexOf('-')+1).trim();
                populateBooks(selectedLngCode, selectedVersionCode);
            }
        });

        bookSelector.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedBookName = (String)parent.getItemAtPosition(position);
                int numChaptersAvailable = mapNameToBook.get(selectedBookName).getNumChapters();
                List<Integer> chapters = new ArrayList<>();
                for (int i = 1; i <= numChaptersAvailable; ++i) {
                    chapters.add(i);
                }
                ArrayAdapter chapterAdapter = new ArrayAdapter(AddVerse.this ,android.R.layout.simple_list_item_1, chapters);
                chapterSelector.setAdapter(chapterAdapter);
            }
        });

        chapterSelector.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedChapter = (Integer) parent.getItemAtPosition(position);
                String selectedBookId = mapNameToBook.get(selectedBookName).getBookId();
                String damId = mapNameToBook.get(selectedBookName).getDamId();
                populateVerses(damId, selectedBookId, selectedChapter);
            }
        });

        verseSelector.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int selectedVerse = (Integer) parent.getItemAtPosition(position);
                String selectedBookId = mapNameToBook.get(selectedBookName).getBookId();
                String damId = mapNameToBook.get(selectedBookName).getDamId();
                getPassage(damId, selectedBookId, selectedBookName, selectedChapter, selectedVerse,selectedVerse);
            }
        });

    }

    public void populateLanguages() {
        String url = "https://dbt.io/library/volumelanguagefamily?key=" + ScriptureClient.BIBLE_API_KEY + "&media=text&v=2";
        JsonArrayRequest languageRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                logger.info(response.toString());
                List<Language> availableLanguages;
                availableLanguages = JsonParser.readLanguages(response);
                List<String> languages = new ArrayList<>();
                for (Language language : availableLanguages) {
                    languages.add(language.getLngName() + " - " + language.getLngCode());
                }
                ArrayAdapter languageAdapter = new ArrayAdapter(AddVerse.this,android.R.layout.simple_list_item_1, languages);
                languageSelector.setAdapter(languageAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mScriptureClient.addToRequestQueue(languageRequest);
    }

    public void populateBibleVersions(String lngCode) {
        String url = "https://dbt.io/library/volume?key=" + ScriptureClient.BIBLE_API_KEY + "&media=text&language_family_code=" + lngCode + "&v=2";
        JsonArrayRequest bibleVersionRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                logger.info(response.toString());
                List<BibleVersion> availableBibleVersions;
                availableBibleVersions = JsonParser.readBibleVersions(response);
                List<String> bibleVersions = new ArrayList<>();
                for (BibleVersion bibleVersion : availableBibleVersions) {
                    bibleVersions.add(bibleVersion.getVersionName() + " - " + bibleVersion.getVersionCode());
                }
                ArrayAdapter versionAdapter = new ArrayAdapter(AddVerse.this ,android.R.layout.simple_list_item_1, bibleVersions);
                versionSelector.setAdapter(versionAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mScriptureClient.addToRequestQueue(bibleVersionRequest);
    }

    public void populateBooks(String lngCode, String versionCode) {
        String url = "https://dbt.io/library/book?key=" + ScriptureClient.BIBLE_API_KEY + "&dam_id=" + lngCode + versionCode + "&v=2";
        JsonArrayRequest bookRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                logger.info(response.toString());
                List<Book> availableBooks;
                availableBooks = JsonParser.readBooks(response);
                List<String> books = new ArrayList<>();
                mapNameToBook.clear();
                for (Book book : availableBooks) {
                    mapNameToBook.put(book.getBookName(), book);
                }
                ArrayAdapter bookAdapter = new ArrayAdapter(AddVerse.this ,android.R.layout.simple_list_item_1, mapNameToBook.keySet().toArray());
                bookSelector.setAdapter(bookAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mScriptureClient.addToRequestQueue(bookRequest);
    }

    public void populateVerses(final String damId, final String bookId, final int chapter) {
        String url = "https://dbt.io/library/verseinfo?key=" + ScriptureClient.BIBLE_API_KEY
                + "&dam_id=" + damId + "1ET&book_id=" + bookId + "&chapter_id=" + chapter + "&v=2";
        StringRequest verseRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    logger.info(response);
                    List<Integer> availableVerses = new ArrayList<>();
                    if (!response.startsWith("[")) {
                        availableVerses = JsonParser.readVerses(new JSONObject(response), bookId, chapter);
                    }
                    if (availableVerses.isEmpty()) {
                        // // Could not find any verses. Try with damId 2
                        String url = "https://dbt.io/library/verseinfo?key=" + ScriptureClient.BIBLE_API_KEY
                                + "&dam_id=" + damId + "2ET&book_id=" + bookId + "&chapter_id=" + chapter + "&v=2";
                        StringRequest verseRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    logger.info(response);
                                    List<Integer> availableVerses = new ArrayList<>();
                                    if (!response.startsWith("[")) {
                                        availableVerses = JsonParser.readVerses(new JSONObject(response), bookId, chapter);
                                    }
                                    ArrayAdapter verseAdapter = new ArrayAdapter(AddVerse.this, android.R.layout.simple_list_item_1, availableVerses);
                                    verseSelector.setAdapter(verseAdapter);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                            }
                        });
                        mScriptureClient.addToRequestQueue(verseRequest);
                    }
                    ArrayAdapter verseAdapter = new ArrayAdapter(AddVerse.this ,android.R.layout.simple_list_item_1, availableVerses);
                    verseSelector.setAdapter(verseAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mScriptureClient.addToRequestQueue(verseRequest);
    }

    public void getPassage(final String damId, final String bookId, final String bookName, final int chapter, final int startVerse, final int endVerse) {
        String url = "https://dbt.io/text/verse?key=" + ScriptureClient.BIBLE_API_KEY
                + "&dam_id=" + damId + "1ET&book_id=" + bookId + "&chapter_id="
                + chapter + "&verse_start=" + startVerse + "&verse_end=" + endVerse + "&v=2";
        JsonArrayRequest passageRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                logger.info(response.toString());
                String passageText = "";
                passageText = JsonParser.readPassage(response);
                if (!passageText.isEmpty()) {
                    // Found verses. Create passage
                    foundPassage = new MemoryPassage(damId.substring(0, damId.length() - 1),
                            bookName, chapter, startVerse, endVerse, passageText);
                    if(foundPassage != null) {
                        mSavedPsgsService.insertPsg(foundPassage);
                    }
                } else {
                    // Could not find any verses. Try with damId 2
                    String url = "https://dbt.io/text/verse?key=" + ScriptureClient.BIBLE_API_KEY
                            + "&dam_id=" + damId + "2ET&book_id=" + bookId + "&chapter_id="
                            + chapter + "&verse_start=" + startVerse + "&verse_end=" + endVerse + "&v=2";
                    JsonArrayRequest passageRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            logger.info(response.toString());
                            String passageText = "";
                            passageText = JsonParser.readPassage(response);
                            if (!passageText.isEmpty()) {
                                // Found verses. Create passage
                                foundPassage = new MemoryPassage(damId.substring(0, damId.length() - 1),
                                        bookName, chapter, startVerse, endVerse, passageText);
                                if(foundPassage != null) {
                                    mSavedPsgsService.insertPsg(foundPassage);
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
                    mScriptureClient.addToRequestQueue(passageRequest);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mScriptureClient.addToRequestQueue(passageRequest);
    }

}
