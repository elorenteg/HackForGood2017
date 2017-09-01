package com.hackforgood.dev.hackforgood2017;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.hackforgood.dev.hackforgood2017.controllers.ImageSearchController;
import com.hackforgood.dev.hackforgood2017.controllers.PhotoToServerController;
import com.hackforgood.dev.hackforgood2017.controllers.WikiAPIController;
import com.hackforgood.dev.hackforgood2017.model.ImageOCR;
import com.hackforgood.dev.hackforgood2017.model.Medicine;
import com.hackforgood.dev.hackforgood2017.model.WikiContent;
import com.hackforgood.dev.hackforgood2017.utils.ImageUtils;
import com.hackforgood.dev.hackforgood2017.utils.UriUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivityFragment extends Fragment implements PhotoToServerController.PhotoToServerCallback,
        ImageSearchController.ImageOCRResolvedCallback, WikiAPIController.WikiAPIResolvedCallback {
    public static final String TAG = MainActivityFragment.class.getSimpleName();
    private static final int GALLERY_PHOTO_CODE = 100;
    private static final int CAMERA_PHOTO_CODE = 101;
    private final ImageSearchController.ImageOCRResolvedCallback imageOCRResolvedCallback = this;
    private final WikiAPIController.WikiAPIResolvedCallback wikiAPIResolvedCallback = this;
    private View rootview;
    private Button buttonCamera;
    private Button buttonGallery;
    private Button buttonKeyboard;
    private Button buttonMicrophone;
    private ImageSearchController imageSearchController;
    private WikiAPIController wikiAPIController;

    private Medicine medicine = null;
    private int possibleNames = Integer.MAX_VALUE;
    private Map<String, String> medNameRedirections;
    private String imageURL;
    private int PHOTO_SCALED_WIDTH = 854;
    private int PHOTO_SCALED_HEIGHT = 480;
    private Uri outputFileUri;
    private String cameraDirectory;

    public static MainActivityFragment newInstance() {
        return new MainActivityFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.main_activity_fragment, container, false);

        setUpElements();
        setUpListeners();

        setUpPhotoCamera();

        imageSearchController = new ImageSearchController(getContext());
        wikiAPIController = new WikiAPIController(getContext());

        return rootview;
    }

    private void setUpElements() {
        buttonCamera = (Button) rootview.findViewById(R.id.main_fragment_camera_button);
        buttonGallery = (Button) rootview.findViewById(R.id.main_fragment_gallery_button);
        buttonKeyboard = (Button) rootview.findViewById(R.id.main_fragment_keyboard_button);
        buttonMicrophone = (Button) rootview.findViewById(R.id.main_fragment_microphone_button);
    }

    private void setUpListeners() {
        buttonCamera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                makePhotoCamera();
            }
        });

        buttonGallery.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openImageChooser();
            }
        });

        buttonKeyboard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Fragment fragment = KeyboardSearchFragment.newInstance();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.main_container, fragment, KeyboardSearchFragment.TAG);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        buttonMicrophone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            }
        });
    }

    public void makePhotoCamera() {
        outputFileUri = imageSearchController.getUriCameraPhoto(cameraDirectory);
        if (outputFileUri != null) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

            startActivityForResult(cameraIntent, CAMERA_PHOTO_CODE);
        }
    }

    public void setUpPhotoCamera() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MainActivity.WRITE_SD_PERMISSION_CODE);
        } else {
            cameraDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/tellmedleaflet/";
            File newdir = new File(cameraDirectory);
            if (!newdir.isDirectory()) {
                if (!newdir.mkdirs()) {
                    Toast.makeText(getContext(), "Problem creating IMAGES FOLDER", Toast.LENGTH_SHORT).show();
                }
            }
        }

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, MainActivity.CAMERA_PERMISSION_CODE);
        }
    }

    private void openImageChooser() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_PHOTO_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY_PHOTO_CODE) {
                try {
                    Uri imageUri = data.getData();
                    String realUri = UriUtils.getRealPathFromUri(imageUri, getContext());
                    InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);

                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                    Bitmap scaledBitmap = ImageUtils.scaleBitmap(selectedImage, PHOTO_SCALED_WIDTH, PHOTO_SCALED_HEIGHT);
                    ImageUtils.compressBitmap(new File(realUri), scaledBitmap);

                    uploadImageToAPI(realUri);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
                }
            } else if (requestCode == CAMERA_PHOTO_CODE) {
                try {
                    Bitmap selectedImage = readImageFromResources(outputFileUri);

                    Bitmap scaledBitmap = ImageUtils.scaleBitmap(selectedImage, PHOTO_SCALED_WIDTH, PHOTO_SCALED_HEIGHT);
                    ImageUtils.compressBitmap(new File(outputFileUri.getPath()), scaledBitmap);

                    uploadImageToAPI(outputFileUri.getPath());
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            Toast.makeText(getActivity(), "You haven't picked Image", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap readImageFromResources(Uri uriToRead) throws IOException {
        return MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uriToRead);
    }

    public void uploadImageToAPI(String uriToUpload) {
        Toast.makeText(getActivity(), "Uploading photo", Toast.LENGTH_SHORT).show();

        PhotoToServerController.sendPhotoToServer(uriToUpload, this);
    }

    @Override
    public void onPhotoToServerSent(String message) {
        Log.e(TAG, "FINALIZANDO ACK RECIBIDO; " + message);
        imageURL = message;
        medNameRedirections = new HashMap<>();

        Toast.makeText(getActivity(), "Doing OCR", Toast.LENGTH_SHORT).show();
        imageSearchController.imageOCRRequest(imageURL, imageOCRResolvedCallback);
    }

    public void onImageOCRResolved(ImageOCR imageOCR) {
        //Log.e(TAG, "onImageOCRResolved");

        if (imageOCR == null) Log.e(TAG, "ImageOCR is null :(");
        else {
            //Toast.makeText(getActivity(), "Doing WIKI", Toast.LENGTH_SHORT).show();
            String parsedText = imageOCR.getParsedText();

            medicine = new Medicine();
            medicine.parseInfo(parsedText);

            if (!medicine.hasACode()) {
                //Log.e(TAG, parsedText.replace("\n",""));
                //Log.e(TAG, medicine.toString());

                String text = medicine.getName();
                WikiAPIController wikiAPIController = new WikiAPIController(getContext());
                ArrayList<String> words = new ArrayList<>(Arrays.asList(text.split(" ")));

                possibleNames = words.size();
                for (int i = 0; i < words.size(); ++i) {
                    String word = words.get(i);
                    //Log.e(TAG, "Calling WikiAPI with " + word);
                    wikiAPIController.wikiAPIRequest(word, wikiAPIResolvedCallback);
                }
            } else {
                loadMedicineFragment(medicine);
            }
        }
    }

    @Override
    public void onWikiAPIResolved(WikiContent wikiContent) {
        if (medicine != null) {
            if (wikiContent.isAMedicine()) {
                medicine.setName(wikiContent.getQueryText());
            } else {
                if (wikiContent.redirects()) {
                    String queryText = Normalizer.normalize(wikiContent.getQueryText(), Normalizer.Form.NFD).replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
                    String redirText = Normalizer.normalize(wikiContent.getRedirectionText(), Normalizer.Form.NFD).replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
                    if (!queryText.contains(redirText) && !redirText.contains(queryText)) {
                        //Log.e(TAG, "Redirection from " + queryText + " to " + redirText);
                        possibleNames++;
                        medNameRedirections.put(wikiContent.getRedirectionText(), wikiContent.getQueryText());
                        wikiAPIController.wikiAPIRequest(wikiContent.getRedirectionText(), wikiAPIResolvedCallback);
                    }
                }
            }
        }
        possibleNames--;
        if (possibleNames == 0) {
            String name = medicine.getName();

            String prevName = medNameRedirections.get(name);
            while (prevName != null) {
                Log.e(TAG, name + "->" + prevName);
                name = prevName;
                prevName = medNameRedirections.get(name);
            }
            medicine.setName(name);

            loadMedicineFragment(medicine);
        }
    }

    private void loadMedicineFragment(Medicine medicine) {
        Log.e(TAG, "Sending: " + medicine.toString());
        Fragment fragment = ResultScreenFragment.newInstance(imageURL, medicine, null);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_container, fragment, ResultScreenFragment.TAG);
        ft.addToBackStack(null);
        ft.commit();
    }
}
