package com.scripturememory.activities;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import com.scripturememory.models.BibleVersion;
import com.scripturememory.models.Book;
import com.scripturememory.helpers.JsonParser;
import com.scripturememory.models.Language;
import com.scripturememory.models.MemoryPassage;
import com.scripturememory.R;
import com.scripturememory.data.SavedPsgsDao;

public class AddVerse extends AppCompatActivity {

    private static final String BIBLE_API_KEY = "05f145575386971d2f9a4fafb4b27983";

    private SavedPsgsDao mSavedPsgsDao;

    private String selectedLngCode;
    private String selectedVersionCode;
    private String selectedBookName;
    private Integer selectedChapter;
    private Map<String, Book> mapNameToBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_verse);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Test API
//        List<Language> testLanguages = getAvailableLanguages();
//        for(Language lng : testLanguages) {
//            System.out.println(lng.getLngCode() + "-" + lng.getLngName());
//        }
//        List<BibleVersion> testVersions = getAvailableBibleVersions("ENG");
//        for(BibleVersion bibVersion : testVersions) {
//            System.out.println(bibVersion.getLngCode() + ": " + bibVersion.getVersionCode() + "-" + bibVersion.getVersionName());
//        }
//        List<Book> testBooks = getAvailableBooks("ENG", "ESV");
//        for(Book book : testBooks) {
//            System.out.println(book.getDamId() + ": " + book.getBookName() + "(" + book.getBookId() + ") - " + book.getNumChapters());
//        }

        // This inserts a test verse every time the '+' fab is pressed
        mSavedPsgsDao = new SavedPsgsDao(this);
        mSavedPsgsDao.open();

        // Set selectors
        final AutoCompleteTextView languageSelector = (AutoCompleteTextView) findViewById(R.id.languageSelect);
        final AutoCompleteTextView versionSelector = (AutoCompleteTextView) findViewById(R.id.versionSelect);
        final AutoCompleteTextView bookSelector = (AutoCompleteTextView) findViewById(R.id.bookSelect);
        final AutoCompleteTextView chapterSelector = (AutoCompleteTextView) findViewById(R.id.chapterSelect);
        final AutoCompleteTextView verseSelector = (AutoCompleteTextView) findViewById(R.id.verseSelect);

        languageSelector.setThreshold(1);
        versionSelector.setThreshold(1);
        bookSelector.setThreshold(1);
        chapterSelector.setThreshold(1);
        verseSelector.setThreshold(1);

        mapNameToBook = new HashMap<>();

        // language adapter and pre-filled options
        List<Language> availableLanguages = getAvailableLanguages();
        List<String> languages = new ArrayList<>();
        for (Language language : availableLanguages) {
            languages.add(language.getLngName() + " - " + language.getLngCode());
        }
        ArrayAdapter languageAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, languages);
        languageSelector.setAdapter(languageAdapter);

        // set bible version options when language is set
        languageSelector.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedLang = (String)parent.getItemAtPosition(position);
                selectedLngCode = selectedLang.substring(selectedLang.indexOf('-')+1).trim();
                List<BibleVersion> availableVersions = getAvailableBibleVersions(selectedLngCode);
                List<String> versions = new ArrayList<>();
                for (BibleVersion version : availableVersions) {
                    versions.add(version.getVersionName() + " - " + version.getVersionCode());
                }
                ArrayAdapter versionAdapter = new ArrayAdapter(AddVerse.this ,android.R.layout.simple_list_item_1, versions);
                versionSelector.setAdapter(versionAdapter);
            }
        });

        versionSelector.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedVersion = (String)parent.getItemAtPosition(position);
                selectedVersionCode = selectedVersion.substring(selectedVersion.indexOf('-')+1).trim();
                List<Book> availableBooks = getAvailableBooks(selectedLngCode, selectedVersionCode);
                mapNameToBook.clear();
                for (Book book : availableBooks) {
                    mapNameToBook.put(book.getBookName(), book);
                }
                ArrayAdapter bookAdapter = new ArrayAdapter(AddVerse.this ,android.R.layout.simple_list_item_1, mapNameToBook.keySet().toArray());
                bookSelector.setAdapter(bookAdapter);
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
                List<Integer> verses = getAvailableVerses(damId, selectedBookId, selectedChapter);

                ArrayAdapter verseAdapter = new ArrayAdapter(AddVerse.this ,android.R.layout.simple_list_item_1, verses);
                verseSelector.setAdapter(verseAdapter);
            }
        });

        verseSelector.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int selectedVerse = (Integer) parent.getItemAtPosition(position);
                String selectedBookId = mapNameToBook.get(selectedBookName).getBookId();
                String damId = mapNameToBook.get(selectedBookName).getDamId();
                MemoryPassage passage = getPassage(damId, selectedBookId, selectedChapter, selectedVerse,selectedVerse);

                if(passage != null) {
                    mSavedPsgsDao.insertPsg(passage);
                }
            }
        });

    }

    public List<Language> getAvailableLanguages() {
        List<Language> languages = new ArrayList<>();
        String query = "https://dbt.io/library/volumelanguagefamily?key=" + BIBLE_API_KEY + "&media=text&v=2";
        String response = makeRequest(query);
        if(response != null) {
            try {
                languages = JsonParser.readLanguages(new JSONArray(response));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return languages;
    }

    public List<BibleVersion> getAvailableBibleVersions(String lngCode) {
        List<BibleVersion> bibleVersions = new ArrayList<>();
        String query = "https://dbt.io/library/volume?key=" + BIBLE_API_KEY
                + "&media=text&language_family_code=" + lngCode + "&v=2";
        String response = makeRequest(query);
        if(response != null) {
            try {
                bibleVersions = JsonParser.readBibleVersions(new JSONArray(response));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bibleVersions;
    }

    public List<Book> getAvailableBooks(String lngCode, String versionCode) {
        List<Book> books = new ArrayList<>();
        String query = "https://dbt.io/library/book?key=" + BIBLE_API_KEY
                + "&dam_id=" + lngCode + versionCode + "&v=2";
        String response = makeRequest(query);
        if(response != null) {
            try {
                books = JsonParser.readBooks(new JSONArray(response));
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return books;
    }

    public List<Integer> getAvailableVerses(String damId, String bookId, int chapter) {
        List<Integer> verses = new ArrayList<>();
        String query = "https://dbt.io/library/verseinfo?key=" + BIBLE_API_KEY
                + "&dam_id=" + damId + "1ET&book_id=" + bookId + "&chapter_id=" + chapter + "&v=2";
        String response = makeRequest(query);
        if(response != null) {
            try {
                if (!response.startsWith("[")) {
                    verses = JsonParser.readVerses(new JSONObject(response), bookId, chapter);
                }
                if (verses.isEmpty()) {
                    // // Could not find any verses. Try with damId 2
                    query = "https://dbt.io/library/verseinfo?key=" + BIBLE_API_KEY
                            + "&dam_id=" + damId + "2ET&book_id=" + bookId + "&chapter_id=" + chapter + "&v=2";
                    response = makeRequest(query);
                    if (response != null && !response.startsWith("[")) {
                        verses = JsonParser.readVerses(new JSONObject(response), bookId, chapter);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return verses;
    }

    public MemoryPassage getPassage(String damId, String bookId, int chapter, int startVerse, int endVerse) {
        // Try with damId 1
        String text = "";
        String query = "https://dbt.io/text/verse?key=" + BIBLE_API_KEY
                + "&dam_id=" + damId + "1ET&book_id=" + bookId + "&chapter_id="
                + chapter + "&verse_start=" + startVerse + "&verse_end=" + endVerse + "&v=2";
        String response = makeRequest(query);
        if(response != null) {
            try {
                text = JsonParser.readPassage(new JSONArray(response));
                if(!text.isEmpty()) {
                    // Found verses. Create passage
                    return new MemoryPassage(damId.substring(0, damId.length()-1),
                            bookId, chapter, startVerse, endVerse, text);
                } else {
                    // // Could not find any verses. Try with damId 2
                    query = "https://dbt.io/text/verse?key=" + BIBLE_API_KEY
                            + "&dam_id=" + damId + "2ET&book_id=" + bookId + "&chapter_id="
                            + chapter + "&verse_start=" + startVerse + "&verse_end=" + endVerse + "&v=2";
                    response = makeRequest(query);
                    if(response != null) {
                        text = JsonParser.readPassage(new JSONArray(response));
                        if (!text.isEmpty()) {
                            // Found verses. Create passage
                            return new MemoryPassage(damId.substring(0, damId.length() - 1),
                                    bookId, chapter, startVerse, endVerse, text);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String makeRequest(String query) {
        HttpsURLConnection con = null;
        boolean success = false;
        StringBuilder response = new StringBuilder();

        try {

            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            // From https://www.washington.edu/itconnect/security/ca/load-der.crt
            InputStream caInput = new BufferedInputStream(getAssets().open("bible.crt"));
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
                System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
            } finally {
                caInput.close();
            }

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);


            // Set HttpsRequest Properties
            URL url = new URL(query);
            con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setSSLSocketFactory(context.getSocketFactory());

            // Connect and check for successful response
            con.connect();
            int responseCode = con.getResponseCode();
            InputStream inputStream;

            if (200 <= responseCode && responseCode <= 299) {
                inputStream = con.getInputStream();
                success = true;
            } else {
                inputStream = con.getErrorStream();
            }

            // Collect response as String
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

            String currentLine;
            while ((currentLine = in.readLine()) != null) {
                response.append(currentLine);
            }

            in.close();

            System.out.println("Response: " + response.toString());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            con.disconnect();
        }

        if(success) {
            return response.toString();
        } else {
            return null;
        }
    }

}
