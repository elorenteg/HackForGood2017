package com.hackforgood.dev.hackforgood2017.controllers;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hackforgood.dev.hackforgood2017.model.ImageOCR;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ImageOCRController {
    private final String TAG = ImageOCRController.class.getSimpleName();
    private final Context context;

    public ImageOCRController(Context context) {
        this.context = context;
    }

    public void imageOCRRequest(String imageURL, final ImageOCRResolvedCallback imageOCRResolvedCallback) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("api.ocr.space")
                .appendPath("parse")
                .appendPath("imageurl")
                .appendQueryParameter("apikey", "ab70658b5888957")
                .appendQueryParameter("url", imageURL);
        String url = builder.build().toString();

        Log.e(TAG, url);

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        ImageOCR imageOCRArray = parseImageOCRJSON(response);
                        imageOCRResolvedCallback.onImageOCRResolved(imageOCRArray);
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

    private ImageOCR parseImageOCRJSON(JSONObject imageOCRJSONObject) {
        try {
            ImageOCR imageOCR = new ImageOCR();

            JSONArray parsedResultsArray = imageOCRJSONObject.getJSONArray("ParsedResults");
            for (int i = 0; i < parsedResultsArray.length(); ++i) {
                JSONObject parsedResultObject = parsedResultsArray.getJSONObject(i);
                imageOCR.setParsedText(parsedResultObject.getString("ParsedText"));
            }

            return imageOCR;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public interface ImageOCRResolvedCallback {
        void onImageOCRResolved(ImageOCR imageOCRArray);
    }
}
