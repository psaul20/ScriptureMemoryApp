package com.scripturememory.helpers;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.scripturememory.models.BibleVersion;
import com.scripturememory.models.Book;
import com.scripturememory.models.Language;

/**
 * Created by Tyler on 12/29/2017.
 * Skeleton for handling JsonParsing
 */

public class JsonParser {

    public static List<Language> readLanguages(JSONArray response) {
        List<Language> languages = new ArrayList<>();
        try {
            for(int i = 0; i < response.length(); ++i) {
                JSONObject currentObject = response.getJSONObject(i);
                Language currentLanguage = new Language();
                currentLanguage.setLngCode(currentObject.getString("language_family_code").trim());
                currentLanguage.setLngName(currentObject.getString("language_family_name").trim());
                languages.add(currentLanguage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return languages;
    }

    public static List<BibleVersion> readBibleVersions(JSONArray response) {
        List<BibleVersion> bibleVersions = new ArrayList<>();
        Set<String> encounteredVersions = new HashSet<>();
        try {
            for(int i = 0; i < response.length(); ++i) {
                JSONObject currentObject = response.getJSONObject(i);
                String versionCode = currentObject.getString("version_code").trim();
                if(!encounteredVersions.contains(versionCode)) {
                    BibleVersion currentBibleVersion = new BibleVersion();
                    currentBibleVersion.setVersionCode(versionCode);
                    currentBibleVersion.setLngCode(currentObject.getString("language_code").trim());
                    currentBibleVersion.setVersionName(currentObject.getString("version_name").trim());
                    bibleVersions.add(currentBibleVersion);
                    encounteredVersions.add(versionCode);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bibleVersions;
    }

    public static List<Book> readBooks(JSONArray response) {
        List<Book> books = new ArrayList<>();
        try {
            for(int i = 0; i < response.length(); ++i) {
                JSONObject currentObject = response.getJSONObject(i);
                Book currentBook = new Book();
                currentBook.setBookId(currentObject.getString("book_id").trim());
                currentBook.setBookName(currentObject.getString("book_name").trim());
                currentBook.setDamId(currentObject.getString("dam_id").trim());
                currentBook.setNumChapters(Integer.parseInt(currentObject.getString("number_of_chapters").trim()));
                books.add(currentBook);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return books;
    }

    public static List<Integer> readVerses(JSONObject response, String book_id, int chapter) {
        List<Integer> verses = new ArrayList<>();
        try {
            JSONObject bookObject = response.getJSONObject(book_id);
            JSONArray verseArray = bookObject.getJSONArray(Integer.toString(chapter));
            for(int i = 0; i < verseArray.length(); ++i) {
                verses.add(Integer.parseInt((String)verseArray.get(i)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return verses;
    }

    public static String readPassage(JSONArray response) {
        String verseText = "";
        try {
            for(int i = 0; i < response.length(); ++i) {
                JSONObject currentObject = response.getJSONObject(i);
                verseText += currentObject.getString("verse_text").trim() + " ";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return verseText.trim();
    }

}
