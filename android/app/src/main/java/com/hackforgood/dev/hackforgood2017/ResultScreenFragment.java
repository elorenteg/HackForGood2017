package com.hackforgood.dev.hackforgood2017;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.hackforgood.dev.hackforgood2017.controllers.TextToSpeechController;
import com.hackforgood.dev.hackforgood2017.controllers.VolleyController;
import com.hackforgood.dev.hackforgood2017.model.Medicine;

import java.io.IOException;

/**
 * Created by LaQuay on 11/03/2017.
 */

public class ResultScreenFragment extends Fragment {
    public static final String TAG = ResultScreenFragment.class.getSimpleName();
    private static final String ARG_URL = "url";
    private static final String ARG_MEDICINE = "medicine";
    private static final String ARG_TEXTTOSEARCH = "user_text";
    private View rootview;
    private LinearLayout imageLayout;
    private CardView nameLayout;
    private CardView codeLayout;
    private CardView leafletLayout;
    private ImageView imageView;
    private TextView medNameText;
    private TextView medCodeText;
    private TextView medLeafletText;

    private String imageUrl;
    private Medicine medicine;
    private String textToSearch;
    private String medicineLeaflet;

    public static ResultScreenFragment newInstance(String imageUrl, Medicine medicine, String textToSearch) {
        ResultScreenFragment fragment = new ResultScreenFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URL, imageUrl);
        if (medicine != null) {
            try {
                args.putSerializable(ARG_MEDICINE, medicine.serialize());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        args.putString(ARG_TEXTTOSEARCH, textToSearch);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.imageUrl = getArguments().getString(ARG_URL);
        try {
            this.medicine = Medicine.deserialize(getArguments().getByteArray(ARG_MEDICINE));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.textToSearch = getArguments().getString(ARG_TEXTTOSEARCH);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.result_screen_fragment, container, false);

        setUpElements();
        setUpListeners();

        Log.e(TAG, "IMAGEURL: " + imageUrl);
        Log.e(TAG, "MEDICINE: " + medicine);
        Log.e(TAG, "TEXTTOSEARCH: " + textToSearch);

        if (imageUrl != null) {
            loadImage(imageUrl);
        } else {
            imageLayout.setVisibility(View.GONE);
        }

        if (medicine != null) {
            //TODO Send Volley to load the med XML
            medNameText.setText(medicine.getName());
            medCodeText.setText("" + medicine.getCode());
        } else if (textToSearch != null) {
            //TODO Send Volley to load the med XML
            medNameText.setText(textToSearch);
            medCodeText.setText("-----");
        }

        return rootview;
    }

    private void setUpElements() {
        imageLayout = (LinearLayout) rootview.findViewById(R.id.linear_result_image_screen);
        imageView = (ImageView) rootview.findViewById(R.id.result_screen_med_result_image);

        medNameText = (TextView) rootview.findViewById(R.id.result_screen_med_name_text);
        medCodeText = (TextView) rootview.findViewById(R.id.result_screen_med_code_text);
        medLeafletText = (TextView) rootview.findViewById(R.id.result_screen_med_leaflet_text);

        nameLayout = (CardView) rootview.findViewById(R.id.result_screen_name_layout);
        codeLayout = (CardView) rootview.findViewById(R.id.result_screen_code_layout);
        leafletLayout = (CardView) rootview.findViewById(R.id.result_screen_leaflet_layout);
    }

    private void setUpListeners() {
        nameLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (medicine != null) {
                    TextToSpeechController.getInstance(getContext()).speak(medicine.getName(), TextToSpeech.QUEUE_FLUSH);
                }
            }
        });

        codeLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (medicine != null) {
                    TextToSpeechController.getInstance(getContext()).speak("" + medicine.getCode(), TextToSpeech.QUEUE_FLUSH);
                }
            }
        });

        leafletLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (medicine != null) {
                    String leaflet = "PROSPECTO";
                    TextToSpeechController.getInstance(getContext()).speak(leaflet, TextToSpeech.QUEUE_FLUSH);
                }
            }
        });
    }

    private void loadImage(String url) {
        final ImageRequest request = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                imageLayout.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(response);
            }
        }, 0, 0, ImageView.ScaleType.CENTER_INSIDE, null, null);

        VolleyController.getInstance(getActivity()).addToQueue(request);
    }
}
