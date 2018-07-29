package com.scripturememory.models;

import android.os.Parcel;
import android.os.Parcelable;

public class AddVerseExpandableItem implements Parcelable {

    private String title;


    public AddVerseExpandableItem(String title) {
        this.title = title;
    }

    private AddVerseExpandableItem(Parcel in) {
        title = in.readString();
    }

    public final Creator<AddVerseExpandableItem> CREATOR = new Creator<AddVerseExpandableItem>() {
        @Override
        public AddVerseExpandableItem createFromParcel(Parcel in) {
            return new AddVerseExpandableItem(in);
        }

        @Override
        public AddVerseExpandableItem[] newArray(int size) {
            return new AddVerseExpandableItem[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String Title) {
        this.title = Title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
    }

}
