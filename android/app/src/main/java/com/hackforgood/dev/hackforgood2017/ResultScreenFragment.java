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
import com.hackforgood.dev.hackforgood2017.utils.FakeMedsUtils;
import com.hackforgood.dev.hackforgood2017.utils.HistoricUtils;

import java.io.IOException;

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
    private ImageView imageView;
    private TextView medNameText;
    private TextView medCodeText;
    private ImageView nameSpeakerImage;
    private ImageView codeSpeakerImage;

    private CardView queLayout;
    private TextView medQueText;
    private ImageView queSpeakerImage;
    private CardView antesLayout;
    private TextView medAntesText;
    private ImageView antesSpeakerImage;
    private CardView comoLayout;
    private TextView medComoText;
    private ImageView comoSpeakerImage;
    private CardView efectosLayout;
    private TextView medEfectosText;
    private ImageView efectosSpeakerImage;
    private CardView informacionLayout;
    private TextView medInformacionText;
    private ImageView informacionSpeakerImage;
    private CardView conservacionLayout;
    private TextView medConservacionText;
    private ImageView conservacionSpeakerImage;

    private String imageUrl;
    private Medicine medicine;
    private String textToSearch;
    private boolean isNewEvent;

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
            if (MainActivity.USE_DUMMY_MODE_NO_MEDS) {
                this.medicine = FakeMedsUtils.getDummyMedicine(FakeMedsUtils.DUMMY_AMOXICILINA);
                //this.medicine = FakeMedsUtils.getDummyMedicine(FakeMedsUtils.DUMMY_BUDESONIDA_ALCON);
                //this.medicine = FakeMedsUtils.getDummyMedicine(FakeMedsUtils.DUMMY_CETRAXAL);
            } else {
                this.medicine = null;
            }
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
        } else if (medicine != null && medicine.getImageURL() != null && !medicine.getImageURL().equals("")) {
            loadImage(medicine.getImageURL());
        } else {
            imageLayout.setVisibility(View.GONE);
        }

        speakerStatus(View.GONE);
        if (medicine != null) {
            medNameText.setText(medicine.getName());
            medCodeText.setText("" + medicine.getCode());
        } else if (textToSearch != null) {
            medNameText.setText(textToSearch);
            medCodeText.setText("Cargando...");
        }

        medQueText.setText("Cargando...");
        medComoText.setText("Cargando...");
        medAntesText.setText("Cargando...");
        medEfectosText.setText("Cargando...");
        medConservacionText.setText("Cargando...");
        medInformacionText.setText("Cargando...");

        if (medicine != null) {
            if (isNewEvent) {
                saveInformationForHistoric(medicine.getCode(), medicine.getName());
            }
            sendRequestoToGetLeafletInformation(LeafletAPIController.SEARCH_BY_CODE, medicine.getCode() + "");
        } else {
            sendRequestoToGetLeafletInformation(LeafletAPIController.SEARCH_BY_NAME, textToSearch + "");
        }

        return rootview;
    }

    private void setUpElements() {
        imageLayout = (LinearLayout) rootview.findViewById(R.id.linear_result_image_screen);
        imageView = (ImageView) rootview.findViewById(R.id.result_screen_med_result_image);

        medNameText = (TextView) rootview.findViewById(R.id.result_screen_med_name_text);
        medCodeText = (TextView) rootview.findViewById(R.id.result_screen_med_code_text);
        medQueText = (TextView) rootview.findViewById(R.id.result_screen_med_que_text);
        medAntesText = (TextView) rootview.findViewById(R.id.result_screen_med_antes_text);
        medComoText = (TextView) rootview.findViewById(R.id.result_screen_med_como_text);
        medEfectosText = (TextView) rootview.findViewById(R.id.result_screen_med_efectos_text);
        medConservacionText = (TextView) rootview.findViewById(R.id.result_screen_med_conservacion_text);
        medInformacionText = (TextView) rootview.findViewById(R.id.result_screen_med_informacion_text);

        nameLayout = (CardView) rootview.findViewById(R.id.result_screen_name_layout);
        codeLayout = (CardView) rootview.findViewById(R.id.result_screen_code_layout);
        queLayout = (CardView) rootview.findViewById(R.id.result_screen_que_layout);
        antesLayout = (CardView) rootview.findViewById(R.id.result_screen_antes_layout);
        comoLayout = (CardView) rootview.findViewById(R.id.result_screen_como_layout);
        efectosLayout = (CardView) rootview.findViewById(R.id.result_screen_efectos_layout);
        conservacionLayout = (CardView) rootview.findViewById(R.id.result_screen_conservacion_layout);
        informacionLayout = (CardView) rootview.findViewById(R.id.result_screen_informacion_layout);

        nameSpeakerImage = (ImageView) rootview.findViewById(R.id.result_screen_name_speaker);
        codeSpeakerImage = (ImageView) rootview.findViewById(R.id.result_screen_code_speaker);
        queSpeakerImage = (ImageView) rootview.findViewById(R.id.result_screen_que_speaker);
        antesSpeakerImage = (ImageView) rootview.findViewById(R.id.result_screen_antes_speaker);
        comoSpeakerImage = (ImageView) rootview.findViewById(R.id.result_screen_como_speaker);
        efectosSpeakerImage = (ImageView) rootview.findViewById(R.id.result_screen_efectos_speaker);
        conservacionSpeakerImage = (ImageView) rootview.findViewById(R.id.result_screen_conservacion_speaker);
        informacionSpeakerImage = (ImageView) rootview.findViewById(R.id.result_screen_informacion_speaker);
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

        queLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (medicine != null) {
                    TextToSpeechController.getInstance(getContext()).speak(medicine.getQue(), TextToSpeech.QUEUE_FLUSH);
                }
            }
        });

        antesLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (medicine != null) {
                    TextToSpeechController.getInstance(getContext()).speak(medicine.getAntes(), TextToSpeech.QUEUE_FLUSH);
                }
            }
        });

        comoLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (medicine != null) {
                    TextToSpeechController.getInstance(getContext()).speak(medicine.getComo(), TextToSpeech.QUEUE_FLUSH);
                }
            }
        });

        efectosLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (medicine != null) {
                    TextToSpeechController.getInstance(getContext()).speak(medicine.getEfectos(), TextToSpeech.QUEUE_FLUSH);
                }
            }
        });

        conservacionLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (medicine != null) {
                    TextToSpeechController.getInstance(getContext()).speak(medicine.getConservacion(), TextToSpeech.QUEUE_FLUSH);
                }
            }
        });

        informacionLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (medicine != null) {
                    TextToSpeechController.getInstance(getContext()).speak(medicine.getInformacion(), TextToSpeech.QUEUE_FLUSH);
                }
            }
        });
    }

    private void speakerStatus(int state) {
        if (state == View.GONE) {
            nameSpeakerImage.setVisibility(View.GONE);
            codeSpeakerImage.setVisibility(View.GONE);
            queSpeakerImage.setVisibility(View.GONE);
            antesSpeakerImage.setVisibility(View.GONE);
            comoSpeakerImage.setVisibility(View.GONE);
            efectosSpeakerImage.setVisibility(View.GONE);
            conservacionSpeakerImage.setVisibility(View.GONE);
            informacionSpeakerImage.setVisibility(View.GONE);
        } else {
            nameSpeakerImage.setVisibility(View.VISIBLE);
            codeSpeakerImage.setVisibility(View.VISIBLE);
            queSpeakerImage.setVisibility(View.VISIBLE);
            antesSpeakerImage.setVisibility(View.VISIBLE);
            comoSpeakerImage.setVisibility(View.VISIBLE);
            efectosSpeakerImage.setVisibility(View.VISIBLE);
            conservacionSpeakerImage.setVisibility(View.VISIBLE);
            informacionSpeakerImage.setVisibility(View.VISIBLE);
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
    public void onLeafletAPIResolved(int searchMode, String que, String antes, String como, String efectos, String informacion, String conservacion) {
        medicine.setQue(que);
        medicine.setAntes(antes);
        medicine.setComo(antes);
        medicine.setEfectos(efectos);
        medicine.setConservacion(conservacion);
        medicine.setInformacion(informacion);

        medQueText.setText(medicine.getQue());
        medAntesText.setText(medicine.getAntes());
        medComoText.setText(medicine.getComo());
        medEfectosText.setText(medicine.getEfectos());
        medConservacionText.setText(medicine.getConservacion());
        medInformacionText.setText(medicine.getInformacion());

        if (searchMode == LeafletAPIController.SEARCH_BY_NAME && isNewEvent) {
            saveInformationForHistoric(medicine.getCode(), medicine.getName());
        }

        speakerStatus(View.VISIBLE);
    }
}
