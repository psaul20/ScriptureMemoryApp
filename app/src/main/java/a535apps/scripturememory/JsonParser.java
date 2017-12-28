package a535apps.scripturememory;

import org.json.JSONObject;

/**
 * Created by Patrick on 12/24/2017.
 * Skeleton for handling JsonParsing, see https://developer.android.com/reference/android/util/JsonReader.html for reference
 */

public class JsonParser {

    public static MemoryPassage readPassage(JSONObject response, MemoryPassage memoryPassage) {

        try {
            // Get passage jsonObject
            JSONObject passage = (JSONObject) response.getJSONObject("response").getJSONObject("search")
                    .getJSONObject("result").getJSONArray("passages").get(0);

            // Get text field
            String text = (String) passage.get("text");

            // Parse text field for all verses
            int currentStartBracketIndex = 0;
            int currentEndBracketIndex = 0;
            String finalPassage = "";
            text = text.substring(text.indexOf("\n"));
            while(true) {
                currentEndBracketIndex = text.indexOf(">", currentStartBracketIndex);
                text = text.substring(currentEndBracketIndex+1);
                currentStartBracketIndex = text.indexOf("<");
                if(currentStartBracketIndex == -1) {
                    break;
                }
                String currentVersePart = text.substring(0, currentStartBracketIndex).trim();
                try {
                    int h;
                    Integer.parseInt(currentVersePart);
                } catch(NumberFormatException e) {
                    if(currentVersePart.length() > 0) {
                        Character firstChar = currentVersePart.charAt(0);
                        // Check for special characters
                        if (firstChar.toString().matches("[^a-z A-Z0-9]")) {
                            finalPassage += currentVersePart;
                        } else {
                            finalPassage += " " + currentVersePart;
                        }
                    }
                }
            }

            System.out.println("Verses Passage: " + finalPassage.trim());
            memoryPassage.setText(finalPassage.trim());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return memoryPassage;
    }

}
