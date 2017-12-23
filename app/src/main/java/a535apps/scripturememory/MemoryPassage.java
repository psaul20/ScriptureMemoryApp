package a535apps.scripturememory;

/**
 * Created by Patrick on 8/17/2017.
 */

public class MemoryPassage {
    private String strTranslation;
    private String strBook;
    private String strChapter;
    private String strStartVerse;
    private String strEndVerse;

    public MemoryPassage(){}

    public String getTranslation(){return strTranslation;}
    public void setTranslation(String Translation) {strTranslation = Translation;}

    public String getBook(){return strBook;}
    public void setBook(String Book) {strBook = Book;}

    public String getChapter(){return strChapter;}
    public void setChapter(String Chapter) {strChapter = Chapter;}

    public String getStartVerse(){return strStartVerse;}
    public void setStartVerse(String StartVerse) {strStartVerse = StartVerse;}

    public String getEndVerse(){return strEndVerse;}
    public void setEndVerse(String EndVerse) {strEndVerse = EndVerse;}
}
