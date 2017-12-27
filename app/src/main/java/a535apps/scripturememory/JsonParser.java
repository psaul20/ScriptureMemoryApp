package a535apps.scripturememory;

import android.util.JsonReader;
import android.util.JsonToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Patrick on 12/24/2017.
 * Skeleton for handling JsonParsing, see https://developer.android.com/reference/android/util/JsonReader.html for reference
 */

public class JsonParser {

    public static MemoryPassage readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readPassage(reader);
        } finally {
            reader.close();
        }
    }


    public static MemoryPassage readPassage(JsonReader reader) throws IOException {
        MemoryPassage passage = new MemoryPassage();

        reader.beginObject();
        while (reader.hasNext()) {
            //Change these to reflect bible search API JSON structure
/*            String name = reader.nextName();
            if (name.equals("translation")) {
                passage.setTranslation(reader.nextString());
            } else if (name.equals("book")) {
                passage.setBook(reader.nextString());
            } else if (name.equals("chapter")) {
                passage.setChapter(reader.nextString());
            } else if (name.equals("startverse")) {
                passage.setStartVerse(reader.nextString());
            } else if (name.equals("endverse")) {
                passage.setEndVerse(reader.nextString());
            } else if (name.equals("text")) {
                passage.setText(reader.nextString());
            } else {
                reader.skipValue();
            }*/
        }
        reader.endObject();
        return passage;
    }

}
