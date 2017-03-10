package com.hackforgood.dev.hackforgood2017;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.hackforgood.dev.hackforgood2017.model.MultipartUtility;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by LaQuay on 10/03/2017.
 */

public class PhotoSearchFragment extends Fragment {
    public static final String TAG = PhotoSearchFragment.class.getSimpleName();
    private static final int GALLERY_PHOTO_CODE = 100;
    private static final int CAMERA_PHOTO_CODE = 101;
    private View rootview;
    private BootstrapButton buttonCamera;
    private BootstrapButton buttonGallery;
    private String cameraDir;
    private Uri outputFileUri;

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

        return rootview;
    }

    private void setUpElements() {
        buttonCamera = (BootstrapButton) rootview.findViewById(R.id.camera_search_camera_button);
        buttonGallery = (BootstrapButton) rootview.findViewById(R.id.camera_search_gallery_button);
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

            outputFileUri = Uri.fromFile(newfile);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

            startActivityForResult(cameraIntent, CAMERA_PHOTO_CODE);
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
                    InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                    uploadImageToAPI(selectedImage, imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
                }
            } else if (requestCode == CAMERA_PHOTO_CODE) {
                try {
                    Bitmap selectedImage = readImageFromResources(outputFileUri);

                    uploadImageToAPI(selectedImage, outputFileUri);
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

    public void uploadImageToAPI(Bitmap imageToUpload, Uri uriToUpload) {
        Toast.makeText(getActivity(), "Uploading PHOTO", Toast.LENGTH_SHORT).show();

        //TODO Marc: Upload commit

        MultipartAsync multipartAsync = new MultipartAsync();
        multipartAsync.execute(uriToUpload.toString());
    }

    private class MultipartAsync extends AsyncTask<String, Void, List<String>> {
        @Override
        protected List<String> doInBackground(String... params) {
            try {
                String uriName = params[0];
                Log.e(TAG, "FileURIName: " + uriName);
                MultipartUtility multipart = new MultipartUtility("URL", "UTF-8");

                multipart.addFilePart("PHOTO", new File(uriName));

                return multipart.finish();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<String> response) {
            Log.e(TAG, "SERVER REPLIED:");
            for (String line : response) {
                Log.e(TAG, "Upload Files Response: " + line);
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
