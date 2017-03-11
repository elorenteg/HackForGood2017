package com.hackforgood.dev.hackforgood2017;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.hackforgood.dev.hackforgood2017.controllers.ImageOCRController;
import com.hackforgood.dev.hackforgood2017.controllers.WikiAPIController;
import com.hackforgood.dev.hackforgood2017.model.ImageOCR;
import com.hackforgood.dev.hackforgood2017.model.Medicine;
import com.hackforgood.dev.hackforgood2017.model.WikiContent;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import com.beardedhen.androidbootstrap.TypefaceProvider;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ImageOCRController.ImageOCRResolvedCallback,
        WikiAPIController.WikiAPIResolvedCallback {

    private final String TAG = MainActivity.class.getSimpleName();

    public static final int CAMERA_PERMISSION_CODE = 200;
    public static final int WRITE_SD_PERMISSION_CODE = 201;
    private final ImageOCRController.ImageOCRResolvedCallback imageOCRResolvedCallback = this;
    private final WikiAPIController.WikiAPIResolvedCallback wikiAPIResolvedCallback = this;
    private ImageOCRController imageOCRController;
    private WikiAPIController wikiAPIController;

    private Medicine medicine = null;
    private int possibleNames = Integer.MAX_VALUE;
    private Map<String,String> medNameRedirections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TypefaceProvider.registerDefaultIconSets();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        imageOCRController = new ImageOCRController(this);
        wikiAPIController = new WikiAPIController(this);
        medNameRedirections = new HashMap<String,String>();

        String url = "";
        url= "http://omicrono.elespanol.com/wp-content/uploads/2015/05/ibuprofeno.jpg";
        url = "http://carolinayh.com/image/cache/finalizado/2014_01_28/19-98web-780x600.jpg";
        //url = "http://www.elcorreo.com/noticias/201407/24/media/cortadas/paracetamol--575x323.jpg";

        imageOCRController.imageOCRRequest(url, imageOCRResolvedCallback);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        Fragment fragment = null;
        String fragmentTAG = null;
        if (id == R.id.nav_home) {
            fragment = MainFragmentActivity.newInstance();
            fragmentTAG = MainFragmentActivity.TAG;
        } else if (id == R.id.nav_gallery) {
            fragment = PhotoSearchFragment.newInstance();
            fragmentTAG = PhotoSearchFragment.TAG;
        } else if (id == R.id.nav_keyboard) {
            fragment = KeyboardSearchFragment.newInstance();
            fragmentTAG = KeyboardSearchFragment.TAG;
        } else if (id == R.id.nav_manage) {

        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_container, fragment, fragmentTAG);
            if (id != R.id.nav_home)
                ft.addToBackStack(null);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PhotoSearchFragment fragment = (PhotoSearchFragment) getSupportFragmentManager().findFragmentByTag(PhotoSearchFragment.TAG);
                if (fragment != null) {
                    fragment.makePhotoCamera();
                }
            }
        } else if (requestCode == WRITE_SD_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PhotoSearchFragment fragment = (PhotoSearchFragment) getSupportFragmentManager().findFragmentByTag(PhotoSearchFragment.TAG);
                if (fragment != null) {
                    fragment.setUpPhotoCamera();
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

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
                WikiAPIController wikiAPIController = new WikiAPIController(this);
                ArrayList<String> words = new ArrayList<String>(Arrays.asList(text.split(" ")));

                possibleNames = words.size();
                for (int i = 0; i < words.size(); ++i) {
                    String word = words.get(i);
                    //Log.e(TAG, "Calling WikiAPI with " + word);
                    wikiAPIController.wikiAPIRequest(word, wikiAPIResolvedCallback);
                }
            }
            else Log.e(TAG, medicine.toString());
        }
    }

    @Override
    public void onWikiAPIResolved(WikiContent wikiContent) {
        if (medicine != null) {
            if (wikiContent.isAMedicine()) {
                medicine.setName(wikiContent.getQueryText());
            }
            else {
                if (wikiContent.redirects()) {
                    String queryText = Normalizer.normalize(wikiContent.getQueryText(), Normalizer.Form.NFD).replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");;
                    String redirText = Normalizer.normalize(wikiContent.getRedirectionText(), Normalizer.Form.NFD).replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");;
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
