package com.scripturememory.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

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
import com.scripturememory.adapters.AddPsgAdapter;
import com.scripturememory.adapters.AddPsgAdapter.OnChildClickListener;
import com.scripturememory.data.SavedPsgsService;
import com.scripturememory.models.AddVerseExpandableGroup;
import com.scripturememory.models.AddVerseExpandableItem;
import com.scripturememory.models.BibleVersion;
import com.scripturememory.models.Book;
import com.scripturememory.helpers.JsonParser;
import com.scripturememory.models.Language;
import com.scripturememory.models.MemoryPassage;
import com.scripturememory.R;
import com.scripturememory.network.ScriptureClient;

public class AddPsg extends AppCompatActivity implements OnChildClickListener {

    private Logger logger = Logger.getLogger(getClass().toString());

    private SavedPsgsService mSavedPsgsService;
    private ScriptureClient mScriptureClient;
    private String selectedLngCode;
    private String selectedVersionCode;
    private String selectedBookName;
    private Integer selectedChapter;
    private MemoryPassage foundPassage;
    private Map<String, Book> mapNameToBook;
    private RecyclerView recyclerView;
    private AddPsgAdapter addPsgAdapter;
    private List<AddVerseExpandableGroup> groupList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_psg);

        mSavedPsgsService = new SavedPsgsService(this);
        mSavedPsgsService.openDb();

        mScriptureClient = ScriptureClient.getInstance(this);

        recyclerView = findViewById(R.id.rcvAddVerse);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        groupList.add(new AddVerseExpandableGroup("Language", new ArrayList<AddVerseExpandableItem>()));
        groupList.add(new AddVerseExpandableGroup("Bible Version", new ArrayList<AddVerseExpandableItem>()));
        groupList.add(new AddVerseExpandableGroup("Book", new ArrayList<AddVerseExpandableItem>()));
        groupList.add(new AddVerseExpandableGroup("Chapter", new ArrayList<AddVerseExpandableItem>()));
        groupList.add(new AddVerseExpandableGroup("Verse", new ArrayList<AddVerseExpandableItem>()));
        addPsgAdapter = new AddPsgAdapter(groupList, AddPsg.this);
        recyclerView.setAdapter(addPsgAdapter);
        populateLanguages();
        mapNameToBook = new HashMap<>();
    }

    public void populateLanguages() {
        String url = "https://dbt.io/library/volumelanguagefamily?key=" + ScriptureClient.BIBLE_API_KEY + "&media=text&v=2";
        JsonArrayRequest languageRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                System.out.println("HELLO!");
                logger.info(response.toString());
                List<Language> availableLanguages;
                List<AddVerseExpandableItem> languageExpandableItems = new ArrayList<>();
                availableLanguages = JsonParser.readLanguages(response);
                List<String> languages = new ArrayList<>();
                for (Language language : availableLanguages) {
                    String languageTitle = language.getLngName() + " - " + language.getLngCode();
                    languageExpandableItems.add(new AddVerseExpandableItem(languageTitle));
                }
                groupList.set(0, new AddVerseExpandableGroup("Language", languageExpandableItems));
                addPsgAdapter = new AddPsgAdapter(groupList, AddPsg.this);
                recyclerView.setAdapter(addPsgAdapter);
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
                List<AddVerseExpandableItem> versionsExpandableItems = new ArrayList<>();
                availableBibleVersions = JsonParser.readBibleVersions(response);
                List<String> bibleVersions = new ArrayList<>();
                for (BibleVersion bibleVersion : availableBibleVersions) {
                    String bibleVersionTitle = bibleVersion.getVersionName() + " - " + bibleVersion.getVersionCode();
                    versionsExpandableItems.add(new AddVerseExpandableItem(bibleVersionTitle));
                }
                groupList.set(1, new AddVerseExpandableGroup("Bible Version", versionsExpandableItems));
                addPsgAdapter = new AddPsgAdapter(groupList, AddPsg.this);
                recyclerView.setAdapter(addPsgAdapter);
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
                List<AddVerseExpandableItem> booksExpandableItems = new ArrayList<>();
                availableBooks = JsonParser.readBooks(response);
                List<String> books = new ArrayList<>();
                mapNameToBook.clear();
                for (Book book : availableBooks) {
                    mapNameToBook.put(book.getBookName(), book);
                    booksExpandableItems.add(new AddVerseExpandableItem(book.getBookName()));
                }
                groupList.set(2, new AddVerseExpandableGroup("Book", booksExpandableItems));
                addPsgAdapter = new AddPsgAdapter(groupList, AddPsg.this);
                recyclerView.setAdapter(addPsgAdapter);
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
                    List<AddVerseExpandableItem> versesExpandableItems = new ArrayList<>();
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
                                    List<AddVerseExpandableItem> versesExpandableItems = new ArrayList<>();
                                    if (!response.startsWith("[")) {
                                        availableVerses = JsonParser.readVerses(new JSONObject(response), bookId, chapter);
                                    }
                                    for (int verseNumber : availableVerses) {
                                        versesExpandableItems.add(new AddVerseExpandableItem(Integer.toString(verseNumber)));
                                    }
                                    groupList.set(4, new AddVerseExpandableGroup("Verse", versesExpandableItems));
                                    addPsgAdapter = new AddPsgAdapter(groupList, AddPsg.this);
                                    recyclerView.setAdapter(addPsgAdapter);
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
                    for (int verseNumber : availableVerses) {
                        versesExpandableItems.add(new AddVerseExpandableItem(Integer.toString(verseNumber)));
                    }
                    groupList.set(4, new AddVerseExpandableGroup("Verse", versesExpandableItems));
                    addPsgAdapter = new AddPsgAdapter(groupList, AddPsg.this);
                    recyclerView.setAdapter(addPsgAdapter);
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

    @Override
    public void onItemClick(AddVerseExpandableGroup group) {
        if ((group).getTitle().equals("Language")) {
            String selectedLng = group.getSelection();
            selectedLngCode = selectedLng.substring(selectedLng.indexOf('-')+1).trim();
            populateBibleVersions(selectedLngCode);
        } else if (group.getTitle().equals("Bible Version")) {
            String selectedVersion = group.getSelection();
            selectedVersionCode = selectedVersion.substring(selectedVersion.indexOf('-')+1).trim();
            populateBooks(selectedLngCode, selectedVersionCode);
        } else if (group.getTitle().equals("Book")) {
            selectedBookName = group.getSelection();
            int numChaptersAvailable = mapNameToBook.get(selectedBookName).getNumChapters();
            List<AddVerseExpandableItem> chaptersExpandableItems = new ArrayList<>();
            for (int i = 1; i <= numChaptersAvailable; ++i) {
                chaptersExpandableItems.add(new AddVerseExpandableItem(Integer.toString(i)));
            }
            groupList.set(3, new AddVerseExpandableGroup("Chapter", chaptersExpandableItems));
            addPsgAdapter = new AddPsgAdapter(groupList, AddPsg.this);
            recyclerView.setAdapter(addPsgAdapter);
        } else if (group.getTitle().equals("Chapter")) {
            selectedChapter = Integer.parseInt(group.getSelection());
            String selectedBookId = mapNameToBook.get(selectedBookName).getBookId();
            String damId = mapNameToBook.get(selectedBookName).getDamId();
            populateVerses(damId, selectedBookId, selectedChapter);
        } else if (group.getTitle().equals("Verse")) {
            int selectedVerse = Integer.parseInt(group.getSelection());
            String selectedBookId = mapNameToBook.get(selectedBookName).getBookId();
            String damId = mapNameToBook.get(selectedBookName).getDamId();
            getPassage(damId, selectedBookId, selectedBookName, selectedChapter, selectedVerse,selectedVerse);
        }
    }
}
