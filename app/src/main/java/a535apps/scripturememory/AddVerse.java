package a535apps.scripturememory;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddVerse extends AppCompatActivity {

    private final String bibleApiKey = "2DHrfilG08rAibeYoRIHvKm3CyWxIo74NYtR7wus";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_verse);

        // Test API
        MemoryPassage memPassage1 = getVerse("eng-KJVA","john", "3", "16", "17");
        MemoryPassage memPassage2 = getVerse("eng-KJVA","1Samuel", "3", "1", "1");
    }

    public MemoryPassage getVerse(String translation, String book, String chapter, String startVerse, String endVerse){
        String query = "https://bibles.org/v2/search.js?query=" + book + "+" + chapter
                + ":" + startVerse + "-" + endVerse + "&version=" + translation;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        HttpURLConnection con = null;

        try {
            // Set HttpRequest Properties
            URL url = new URL(query);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            String userCredentials = bibleApiKey + ":X";
            String encodedCredentials = Base64.encodeToString(userCredentials.getBytes(), Base64.DEFAULT);
            con.setRequestProperty("Authorization", "Basic " + encodedCredentials);
            con.setRequestProperty("Content-Type", "application/json");

            // Connect and check for successful response
            con.connect();
            int responseCode = con.getResponseCode();
            InputStream inputStream;
            if (200 <= responseCode && responseCode <= 299) {
                inputStream = con.getInputStream();
            } else {
                inputStream = con.getErrorStream();
            }

            // Collect response as String
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder response = new StringBuilder();
            String currentLine;
            while ((currentLine = in.readLine()) != null) {
                response.append(currentLine);
            }

            in.close();

            System.out.println("Response: " + response.toString());

            // Convert response to JSON Object and parse for verse
            MemoryPassage memoryPassage = new MemoryPassage();
            memoryPassage.setStartVerse(Integer.parseInt(startVerse));
            memoryPassage.setEndVerse(Integer.parseInt(endVerse));
            memoryPassage.setTranslation(translation);
            memoryPassage.setChapter(Integer.parseInt(chapter));
            memoryPassage.setBook(book);
            memoryPassage = JsonParser.readPassage(new JSONObject(response.toString()), memoryPassage);
            return memoryPassage;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            con.disconnect();
        }
        return null;
    }
}
