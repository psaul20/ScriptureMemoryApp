package com.scripturememory.Interfaces;

import android.view.View;

public interface ItemClickListener {
    void onClick(View view, int position);
    boolean onLongClick (View view, int position);
}
