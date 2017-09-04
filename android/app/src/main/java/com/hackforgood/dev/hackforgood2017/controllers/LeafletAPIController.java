package com.hackforgood.dev.hackforgood2017.controllers;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class LeafletAPIController {
    private static final String TAG = LeafletAPIController.class.getSimpleName();

    //TODO Request para el heroku
    public static void leafletAPIRequest(String leafletCode, Context context, final LeafletAPICallback leafletAPICallback) {
        Uri.Builder builder = new Uri.Builder();

        String url = builder.build().toString();

        Log.e(TAG, url);

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        leafletAPICallback.onLeafletAPIResolved("TEST");
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
        void onLeafletAPIResolved(String leaflet);
    }
}
