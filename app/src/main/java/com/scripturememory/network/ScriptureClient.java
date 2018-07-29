package com.scripturememory.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class ScriptureClient {

    public static final String BIBLE_API_KEY = "05f145575386971d2f9a4fafb4b27983";
    private static ScriptureClient mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private ScriptureClient(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized ScriptureClient getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ScriptureClient(context);
        }
        return mInstance;
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

}