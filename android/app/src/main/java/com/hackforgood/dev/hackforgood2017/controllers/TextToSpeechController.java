package com.hackforgood.dev.hackforgood2017.controllers;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Created by LaQuay on 11/03/2017.
 */

public class TextToSpeechController {
    private static TextToSpeechController instance;
    Handler handler = new Handler();
    private TextToSpeech textToSpeech;
    private boolean textToSpeechInitialized;

    private TextToSpeechController(Context context) {
        textToSpeechInitialized = false;

        textToSpeech = new TextToSpeech(context.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    Locale loc = new Locale("es", "ES");
                    textToSpeech.setLanguage(loc);
                    textToSpeechInitialized = true;
                }
            }
        });
    }

    public static TextToSpeechController getInstance(Context ctx) {
        if (instance == null) {
            createInstance(ctx);
        }
        return instance;
    }

    private synchronized static void createInstance(Context ctx) {
        if (instance == null) {
            instance = new TextToSpeechController(ctx);
        }
    }

    public void speak(final CharSequence textToSpeak, final int QUEUE_MODE) {
        int waitTimeToSpeak = 1;

        if (!textToSpeechInitialized) {
            waitTimeToSpeak = 250;
        }

        final int finalWaitTimeToSpeak = waitTimeToSpeak;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (textToSpeechInitialized) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ttsGreaterAPI20(textToSpeak, QUEUE_MODE);
                    } else {
                        ttsUnderAPI21(textToSpeak, QUEUE_MODE);
                    }
                } else {
                    handler.postDelayed(this, finalWaitTimeToSpeak);
                }
            }
        }, waitTimeToSpeak);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreaterAPI20(CharSequence textToSpeak, int QUEUE_MODE) {
        textToSpeech.speak(textToSpeak, QUEUE_MODE, null, null);
    }

    @SuppressWarnings("deprecation")
    private void ttsUnderAPI21(CharSequence textToSpeak, int QUEUE_MODE) {
        textToSpeech.speak(textToSpeak.toString(), QUEUE_MODE, null);
    }

    public void stop() {
        textToSpeechInitialized = false;
        textToSpeech.stop();
    }

    public void shutdown() {
        textToSpeechInitialized = true;
        textToSpeech.shutdown();
    }
}
