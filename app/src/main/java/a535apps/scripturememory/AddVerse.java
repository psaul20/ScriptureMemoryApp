package a535apps.scripturememory;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONArray;

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
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import a535apps.scripturememory.database.DataSource;

public class AddVerse extends AppCompatActivity {

    private static final String BIBLE_API_KEY = "05f145575386971d2f9a4fafb4b27983";

    private DataSource mDataSource;

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
        mDataSource = new DataSource(this);
        mDataSource.open();

        MemoryPassage testPassage = getVerses("ENGESVN", "John", 4, 14, 15);
        if(testPassage != null) {
            System.out.println(testPassage.getTranslation() + ": " + testPassage.getBook() + " " + testPassage.getChapter() + ":" + testPassage.getStartVerse() + "-" + testPassage.getEndVerse() + " " + testPassage.getText());
            mDataSource.insertPsg(testPassage);
        } else {
            System.out.println("Null response...");
        }
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

    public MemoryPassage getVerses(String damId, String bookId, int chapter, int startVerse, int endVerse) {
        // Try with damId 1
        String text = "";
        String query = "https://dbt.io/text/verse?key=" + BIBLE_API_KEY
                + "&dam_id=" + damId + "1ET&book_id=" + bookId + "&chapter_id="
                + chapter + "&verse_start=" + startVerse + "&verse_end=" + endVerse + "&v=2";
        String response = makeRequest(query);
        if(response != null) {
            try {
                text = JsonParser.readVerses(new JSONArray(response));
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
                        text = JsonParser.readVerses(new JSONArray(response));
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
