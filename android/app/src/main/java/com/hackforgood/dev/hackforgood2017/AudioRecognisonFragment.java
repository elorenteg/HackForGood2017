package com.hackforgood.dev.hackforgood2017;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class AudioRecognisonFragment extends Fragment {
    public static final String TAG = AudioRecognisonFragment.class.getSimpleName();
    public final static int REQ_CODE_SPEECH_INPUT = 100;
    private View rootview;
    private Button buttonSearch;
    private EditText editText;

    public static AudioRecognisonFragment newInstance() {
        return new AudioRecognisonFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.audio_recognison_fragment, container, false);

        setUpElements();
        setUpListeners();

        return rootview;
    }

    private void setUpElements() {
        editText = (EditText) rootview.findViewById(R.id.audio_recognison_edit_text);
        buttonSearch = (Button) rootview.findViewById(R.id.audio_recognison_button);
    }

    private void setUpListeners() {
        editText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            }
        });

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            getActivity().startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void setSpeechText(ArrayList<String> result) {
        if (editText != null) {
            editText.setText(result.get(0));
        }
    }
}
