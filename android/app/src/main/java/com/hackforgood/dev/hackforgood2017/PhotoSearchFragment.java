package com.hackforgood.dev.hackforgood2017;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.hackforgood.dev.hackforgood2017.controllers.ImageOCRController;
import com.hackforgood.dev.hackforgood2017.controllers.PhotoToServerController;
import com.hackforgood.dev.hackforgood2017.controllers.WikiAPIController;
import com.hackforgood.dev.hackforgood2017.model.ImageOCR;
import com.hackforgood.dev.hackforgood2017.model.Medicine;
import com.hackforgood.dev.hackforgood2017.model.WikiContent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PhotoSearchFragment extends Fragment implements PhotoToServerController.PhotoToServerCallback,
        ImageOCRController.ImageOCRResolvedCallback,WikiAPIController.WikiAPIResolvedCallback {
    public static final String TAG = PhotoSearchFragment.class.getSimpleName();
    private static final int GALLERY_PHOTO_CODE = 100;
    private static final int CAMERA_PHOTO_CODE = 101;
    private final ImageOCRController.ImageOCRResolvedCallback imageOCRResolvedCallback = this;
    private final WikiAPIController.WikiAPIResolvedCallback wikiAPIResolvedCallback = this;
    private View rootview;
    private Button buttonCamera;
    private Button buttonGallery;
    private String cameraDir;
    private Uri outputFileUri;
    private ImageOCRController imageOCRController;
    private WikiAPIController wikiAPIController;

    private Medicine medicine = null;
    private int possibleNames = Integer.MAX_VALUE;
    private Map<String, String> medNameRedirections;

    public static PhotoSearchFragment newInstance() {
        return new PhotoSearchFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.camera_search_fragment, container, false);

        setUpElements();
        setUpListeners();

        setUpPhotoCamera();

        imageOCRController = new ImageOCRController(getContext());
        wikiAPIController = new WikiAPIController(getContext());

        return rootview;
    }

    private void setUpElements() {
        buttonCamera = (Button) rootview.findViewById(R.id.camera_search_camera_button);
        buttonGallery = (Button) rootview.findViewById(R.id.camera_search_gallery_button);
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
    }

    public void setUpPhotoCamera() {
        //buttonCamera.setBootstrapBrand();

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MainActivity.WRITE_SD_PERMISSION_CODE);
        } else {
            cameraDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/tellmedleaflet/";
            File newdir = new File(cameraDir);
            if (!newdir.isDirectory()) {
                if (!newdir.mkdirs()) {
                    Toast.makeText(getContext(), "Problem creating IMAGES FOLDER", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void makePhotoCamera() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, MainActivity.CAMERA_PERMISSION_CODE);
        } else {
            String file = cameraDir + System.currentTimeMillis() + ".jpg";
            File newfile = new File(file);
            try {
                if (!newfile.createNewFile()) {
                    Toast.makeText(getContext(), "Problem creating IMAGE", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            outputFileUri = getUrifromFile(newfile);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

            startActivityForResult(cameraIntent, CAMERA_PHOTO_CODE);
        }
    }

    private Uri getUrifromFile(File newfile) {
        Uri uri;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            uri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".provider", newfile);
        } else {
            uri = Uri.fromFile(newfile);
        }

        return uri;
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
                    String realUri = getRealPathFromUri(imageUri);
                    InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                    Bitmap scaledBitmap = scaleBitmap(selectedImage, 640, 360);
                    Bitmap compressedBitmap = compressBitmap(new File(realUri), scaledBitmap);

                    uploadImageToAPI(compressedBitmap, realUri);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
                }
            } else if (requestCode == CAMERA_PHOTO_CODE) {
                try {
                    Bitmap selectedImage = readImageFromResources(outputFileUri);

                    Bitmap scaledBitmap = scaleBitmap(selectedImage, 640, 360);
                    Bitmap compressedBitmap = compressBitmap(new File(outputFileUri.getPath()), scaledBitmap);

                    uploadImageToAPI(compressedBitmap, outputFileUri.getPath());
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            Toast.makeText(getActivity(), "You haven't picked Image", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap scaleBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            if (height > width) {
                int aux = maxHeight;
                maxHeight = maxWidth;
                maxWidth = aux;
            }

            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > 1) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxWidth / ratioBitmap);
            }
            return Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true);
        }
        return bitmap;
    }

    private Bitmap compressBitmap(File file, Bitmap bitmap) throws FileNotFoundException {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    private Bitmap readImageFromResources(Uri uriToRead) throws IOException {
        return MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uriToRead);
    }

    public void uploadImageToAPI(Bitmap imageToUpload, String uriToUpload) {
        Toast.makeText(getActivity(), "Uploading PHOTO", Toast.LENGTH_SHORT).show();

        PhotoToServerController.sendPhotoToServer(uriToUpload, this);
    }

    private String getRealPathFromUri(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getContext().getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void onPhotoToServerSent(String message) {
        Log.e(TAG, "FINALIZANDO ACK RECIBIDO; " + message);
        medNameRedirections = new HashMap<String, String>();

        String url = "";
        url = "http://omicrono.elespanol.com/wp-content/uploads/2015/05/ibuprofeno.jpg";
        url = "http://carolinayh.com/image/cache/finalizado/2014_01_28/19-98web-780x600.jpg";
        //url = "http://www.elcorreo.com/noticias/201407/24/media/cortadas/paracetamol--575x323.jpg";

        //TextToSpeechController.getInstance(this).speak("Hola Mundo!", TextToSpeech.QUEUE_FLUSH);

        url = message;

        imageOCRController.imageOCRRequest(url, imageOCRResolvedCallback);
    }

    public void onImageOCRResolved(ImageOCR imageOCR) {
        //Log.e(TAG, "onImageOCRResolved");

        if (imageOCR == null) Log.e(TAG, "ImageOCR is null :(");
        else {
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
            } else Log.e(TAG, medicine.toString());
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
            Log.e(TAG, medicine.toString());
        }
    }
}
