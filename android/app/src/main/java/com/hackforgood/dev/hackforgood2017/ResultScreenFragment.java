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
import com.hackforgood.dev.hackforgood2017.controllers.LeafletAPIController;
import com.hackforgood.dev.hackforgood2017.controllers.TextToSpeechController;
import com.hackforgood.dev.hackforgood2017.controllers.VolleyController;
import com.hackforgood.dev.hackforgood2017.model.Medicine;
import com.hackforgood.dev.hackforgood2017.utils.HistoricUtils;

import java.io.IOException;

/**
 * Created by LaQuay on 11/03/2017.
 */

public class ResultScreenFragment extends Fragment implements LeafletAPIController.LeafletAPICallback {
    public static final String TAG = ResultScreenFragment.class.getSimpleName();
    private static final String ARG_URL = "url";
    private static final String ARG_MEDICINE = "medicine";
    private static final String ARG_TEXTTOSEARCH = "user_text";
    private static final String ARG_ISNEWEVENT = "new_event";
    private View rootview;
    private LinearLayout imageLayout;
    private CardView nameLayout;
    private CardView codeLayout;
    private CardView leafletLayout;
    private ImageView imageView;
    private TextView medNameText;
    private TextView medCodeText;
    private TextView medLeafletText;
    private ImageView nameSpeakerImage;
    private ImageView codeSpeakerImage;
    private ImageView leafletSpeakerImage;

    private String imageUrl;
    private Medicine medicine;
    private String textToSearch;
    private boolean isNewEvent;
    private String medicineLeaflet;

    public static ResultScreenFragment newInstance(String imageUrl, Medicine medicine, String textToSearch, boolean isNewEvent) {
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
        args.putBoolean(ARG_ISNEWEVENT, isNewEvent);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.imageUrl = getArguments().getString(ARG_URL);
        if (getArguments().getByteArray(ARG_MEDICINE) != null) {
            try {
                this.medicine = Medicine.deserialize(getArguments().getByteArray(ARG_MEDICINE));
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            this.medicine = null;
        }

        this.textToSearch = getArguments().getString(ARG_TEXTTOSEARCH);

        this.isNewEvent = getArguments().getBoolean(ARG_ISNEWEVENT);
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

        if (MainActivity.USE_DUMMY_MODE_MEDS) {
            medNameText.setText(medicine.getName());
            medCodeText.setText("" + medicine.getCode());
            medicineLeaflet = "Paracetamol Pensa pertenece al grupo de medicamentos llamados analgésicos y antipiréticos. \n" +
                    " \n" +
                    "Este medicamento está indicado para el tratamiento sintomático del dolor de intensidad leve o moderada, y \n" +
                    "para reducir la fiebre. ";
            medLeafletText.setText(medicineLeaflet);
        }

        if (medicine != null) {
            medNameText.setText(medicine.getName());
            medCodeText.setText("" + medicine.getCode());
            medLeafletText.setText(medicineLeaflet);
        } else if (textToSearch != null) {
            medNameText.setText(textToSearch);
            medCodeText.setText("-----");
        }

        if (medicine != null) {
            if (isNewEvent) {
                saveInformationForHistoric(medicine.getCode(), medicine.getName());
            }
            sendRequestoToGetLeafletInformation(LeafletAPIController.SEARCH_BY_CODE, medicine.getCode() + "");
        } else {
            speakerStatus(View.GONE);
            sendRequestoToGetLeafletInformation(LeafletAPIController.SEARCH_BY_NAME, textToSearch + "");
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

        nameSpeakerImage = (ImageView) rootview.findViewById(R.id.result_screen_name_speaker);
        codeSpeakerImage = (ImageView) rootview.findViewById(R.id.result_screen_code_speaker);
        leafletSpeakerImage = (ImageView) rootview.findViewById(R.id.result_screen_leaflet_speaker);
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
                    TextToSpeechController.getInstance(getContext()).speak(medicineLeaflet, TextToSpeech.QUEUE_FLUSH);
                }
            }
        });
    }

    private void speakerStatus(int state) {
        if (state == View.GONE) {
            nameSpeakerImage.setVisibility(View.GONE);
            codeSpeakerImage.setVisibility(View.GONE);
            leafletSpeakerImage.setVisibility(View.GONE);
        } else {
            nameSpeakerImage.setVisibility(View.VISIBLE);
            codeSpeakerImage.setVisibility(View.VISIBLE);
            leafletSpeakerImage.setVisibility(View.VISIBLE);
        }
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

    private void saveInformationForHistoric(int code, String name) {
        HistoricUtils.saveInformationHistoric(getContext(), code, name);
    }

    private void sendRequestoToGetLeafletInformation(int searchMode, String info) {
        LeafletAPIController.leafletAPIRequest(searchMode, info, getContext(), this);
    }

    @Override
    public void onLeafletAPIResolved(int searchMode, String leafletText) {
        Log.e(TAG, "Response: " + leafletText);

        // TODO Construir medicamento definitivo

        if (searchMode == LeafletAPIController.SEARCH_BY_NAME && isNewEvent) {
            saveInformationForHistoric(medicine.getCode(), medicine.getName());
        }

        speakerStatus(View.VISIBLE);
    }
}
