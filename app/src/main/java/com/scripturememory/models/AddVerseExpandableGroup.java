package com.scripturememory.models;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class AddVerseExpandableGroup extends ExpandableGroup<AddVerseExpandableItem> {

    private String selection;

    public AddVerseExpandableGroup(String title, List<AddVerseExpandableItem> items) {
        super(title, items);
    }

    public String getSelection() {
        return selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

}
