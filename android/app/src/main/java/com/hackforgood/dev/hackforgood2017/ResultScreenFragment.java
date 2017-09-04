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
            medicine = new Medicine();
            medicine.setName("Amoxicilina/Ácido clavulánico Mylan 500 mg/125 mg comprimidos recubiertos con película EFG");
            medicine.setCode(694513);
            medicine.setQue("Amoxicilina/Ácido clavulánico Mylan está indicado para el tratamiento de las siguientes infecciones en\n" +
                    "adultos y niños:\n" +
                    "\uF02D Sinusitis bacteriana aguda.\n" +
                    "\uF02D Otitis media aguda.\n" +
                    "\uF02D Exacerbación aguda de bronquitis crónica.\n" +
                    "\uF02D Neumonía adquirida en la comunidad.\n" +
                    "\uF02D Cistitis.\n" +
                    "\uF02D Pielonefritis.\n" +
                    "\uF02D Infecciones de la piel y tejidos blandos, en particular celulitis, mordeduras de animales, abscesos\n" +
                    "dentales severos con celulitis diseminada.\n" +
                    "\uF02D Infecciones de huesos y articulaciones, en particular osteomielitis. ");
            medicine.setComo("Para adultos y niños ≥ 40 kg, esta formulación de amoxicilina/ácido clavulánico proporciona una dosis\n" +
                    "diaria total de 1.500 mg de amoxicilina/ 375 mg de ácido clavulánico, cuando se administra como se\n" +
                    "recomienda a continuación.\n" +
                    "Para niños < 40 kg esta formulación de amoxicilina/ácido clavulánico proporciona una dosis máxima diaria\n" +
                    "de 2.400 mg de amoxicilina/600 mg de ácido clavulánico, cuando se administra como se recomienda a\n" +
                    "continuación. Si se considera que es necesaria una mayor dosis diaria de amoxicilina se recomienda elegir\n" +
                    "otra formulación de amoxicilina/ácido clavulánico para evitar la administración innecesaria de dosis altas\n" +
                    "de ácido clavulánico (ver secciones 4.4 y 5.1). \n");
            medicine.setAntes("Antes de la administración de amoxicilina/ácido clavulánico, debe revisarse la existencia previa de\n" +
                    "reacciones de hipersensibilidad a penicilinas, cefalosporinas u otros agentes beta-lactámicos (ver secciones\n" +
                    "4.3 y 4.8).\n" +
                    "Se han notificado casos de reacciones de hipersensibilidad (anafilaxia) graves y a veces mortales, en\n" +
                    "pacientes tratados con penicilinas. Estas reacciones suelen ocurrir en individuos con antecedentes de\n" +
                    "hipersensibilidad a las penicilinas y en pacientes atópicos. Si ocurriera una reacción alérgica, se debe\n" +
                    "suprimir el tratamiento con amoxicilina/ácido clavulánico y utilizar una terapia alternativa.\n" +
                    "En caso de que se confirme que una infección es debida a un microorganismo sensible a amoxicilina debe\n" +
                    "considerarse cambiar de amoxicilina/ácido clavulánico a amoxicilina de acuerdo con las recomendaciones\n" +
                    "oficiales. ");
            medicine.setEfectos("Las reacciones adversas que se notificaron más frecuentemente fueron diarrea, náuseas y vómitos.\n" +
                    "\n" +
                    "Tras los ensayos clínicos y la experiencia post-comercialización con amoxicilina/ácido clavulánico se han\n" +
                    "notificado las reacciones adversas listadas a continuación, clasificadas en base al Sistema MedDRA.\n" +
                    "Para clasificar la frecuencia de reacciones adversas se han utilizado los siguientes términos:\n" +
                    "Muy frecuentes (≥1/10)\n" +
                    "Frecuentes (≥1/100 a <1/10)\n" +
                    "Poco frecuentes (≥1/1.000 a <1/100)\n" +
                    "Raras (≥1/10.000 a <1/1.000)\n" +
                    "Muy raras (<1/10.000)\n" +
                    "No conocida (no puede estimarse a partir de los datos disponibles)");
            medicine.setConservacion("No conservar a temperatura superior a 25° C. Conservar en el embalaje original.");
            medicine.setInformacion("La información detallada y actualizada de este medicamento está disponible en la página Web de la\n" +
                    "Agencia Española de Medicamentos y Productos Sanitarios (AEMPS) http://www.aemps.gob.es/");

            imageUrl = "https://image.prntscr.com/image/1LI4UdV7TG_0ebx1O4vSGA.png";
            loadImage(imageUrl);

            medNameText.setText(medicine.getName());
            medCodeText.setText("" + medicine.getCode());
            medQueText.setText(medicine.getQue());
            medComoText.setText(medicine.getComo());
            medAntesText.setText(medicine.getAntes());
            medEfectosText.setText(medicine.getEfectos());
            medConservacionText.setText(medicine.getConservacion());
            medInformacionText.setText(medicine.getInformacion());

            speakerStatus(View.VISIBLE);
        } else {
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
