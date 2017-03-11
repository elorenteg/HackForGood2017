package com.hackforgood.dev.hackforgood2017.controllers;

import android.os.AsyncTask;

import com.hackforgood.dev.hackforgood2017.model.MultipartUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PhotoToServerController {
    public static final String TAG = PhotoToServerController.class.getSimpleName();
    private static final String URL = "http://c3cce9a9.ngrok.io/upload";

    public static void sendPhotoToServer(String uriToUpload, PhotoToServerCallback photoToServerCallback) {
        PhotoToServerAsyncTask photoToServerAsyncTask = new PhotoToServerAsyncTask();
        photoToServerAsyncTask.execute(uriToUpload, photoToServerCallback);
    }

    public interface PhotoToServerCallback {
        void onPhotoToServerSent(String message);
    }

    private static class PhotoToServerAsyncTask extends AsyncTask<Object, Void, List<String>> {
        private static final String URL = "http://c3cce9a9.ngrok.io/upload";
        private PhotoToServerCallback callback;

        @Override
        protected List<String> doInBackground(Object... params) {
            try {
                String uriName = params[0].toString();
                callback = (PhotoToServerCallback) params[1];

                MultipartUtility multipart = new MultipartUtility(URL, "UTF-8");
                multipart.addFilePart("file", new File(uriName));

                return multipart.finish();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<String> response) {
            if (!response.isEmpty()) {
                try {
                    JSONObject jsonObj = new JSONObject(response.get(0));
                    callback.onPhotoToServerSent(jsonObj.getString("url"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}
