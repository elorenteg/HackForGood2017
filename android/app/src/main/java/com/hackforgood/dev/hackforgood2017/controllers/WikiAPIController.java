package com.hackforgood.dev.hackforgood2017.controllers;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hackforgood.dev.hackforgood2017.model.ImageOCR;
import com.hackforgood.dev.hackforgood2017.model.WikiContent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WikiAPIController {
    private final String TAG = WikiAPIController.class.getSimpleName();
    private final Context context;

    public WikiAPIController(Context context) {
        this.context = context;
    }

    public void wikiAPIRequest(final String name, final WikiAPIController.WikiAPIResolvedCallback wikiAPIResolvedCallback) {
        Uri.Builder builder = new Uri.Builder();

        builder.scheme("https")
                .authority("es.wikipedia.org")
                .appendPath("w")
                .appendPath("api.php")
                .appendQueryParameter("action", "query")
                .appendQueryParameter("prop", "revisions")
                .appendQueryParameter("rvprop", "content")
                .appendQueryParameter("rvsection", "0")
                .appendQueryParameter("titles", name)
                .appendQueryParameter("format", "json");
        String url = builder.build().toString();

        Log.e(TAG, url);

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        WikiContent wikiContent = parseWikiAPIContent(response);
                        wikiContent.setQueryText(name);
                        wikiAPIResolvedCallback.onWikiAPIResolved(wikiContent);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "That didn't work!");
            }
        });

        // Add the request to the RequestQueue.
        VolleyController.getInstance(context).addToQueue(jsonObjectRequest);
    }

    private WikiContent parseWikiAPIContent(JSONObject wikiContentJSONObject) {
        WikiContent wikiContent = new WikiContent();
        wikiContent.setJsonResponse(wikiContentJSONObject);
        return wikiContent;
    }

    public interface WikiAPIResolvedCallback {
        void onWikiAPIResolved(WikiContent wikiContent);
    }
}
