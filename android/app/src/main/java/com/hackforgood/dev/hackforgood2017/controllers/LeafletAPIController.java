package com.hackforgood.dev.hackforgood2017.controllers;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hackforgood.dev.hackforgood2017.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class LeafletAPIController {
    public static final int SEARCH_BY_CODE = 1;
    public static final int SEARCH_BY_NAME = 2;
    private static final String TAG = LeafletAPIController.class.getSimpleName();

    public static void leafletAPIRequest(final int searchMode, String leafletCode, Context context, final LeafletAPICallback leafletAPICallback) {
        if (searchMode == SEARCH_BY_CODE) {

        } else if (searchMode == SEARCH_BY_NAME) {

        }

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("hackforgoodbcn2017app.herokuapp.com")
                .appendPath("getprospecto")
                .appendPath("bycode")
                .appendPath(leafletCode);
        String url = builder.build().toString();

        Log.e(TAG, url);

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String que = response.getString("que");
                            String antes = response.getString("antes");
                            String como = response.getString("como");
                            String efectos = response.getString("efectos");
                            String informacion = response.getString("informacion");
                            String conservacion = response.getString("conservacion");

                            leafletAPICallback.onLeafletAPIResolved(searchMode, que, antes, como, efectos, informacion, conservacion);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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

    public interface LeafletAPICallback {
        void onLeafletAPIResolved(int searchMode, String que, String antes, String como, String efectos, String informacion, String conservacion);
    }
}
