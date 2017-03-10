package com.hackforgood.dev.hackforgood2017;

import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ImageOCRController.ImageOCRResolvedCallback,
        WikiAPIController.WikiAPIResolvedCallback {

    private final String TAG = MainActivity.class.getSimpleName();

    private final ImageOCRController.ImageOCRResolvedCallback imageOCRResolvedCallback = this;
    private final WikiAPIController.WikiAPIResolvedCallback wikiAPIResolvedCallback = this;
    private ImageOCRController imageOCRController;
    private WikiAPIController wikiAPIController;

    private Medicine medicine = null;
    private int possibleNames = Integer.MAX_VALUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        String url = "http://omicrono.elespanol.com/wp-content/uploads/2015/05/ibuprofeno.jpg";
        //imageOCRController.imageOCRRequest(url, imageOCRResolvedCallback);

        url = "http://carolinayh.com/image/cache/finalizado/2014_01_28/19-98web-780x600.jpg";
        imageOCRController.imageOCRRequest(url, imageOCRResolvedCallback);

        url = "http://www.elcorreo.com/noticias/201407/24/media/cortadas/paracetamol--575x323.jpg";
        //imageOCRController.imageOCRRequest(url, imageOCRResolvedCallback);
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
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onImageOCRResolved(ImageOCR imageOCR) {
        //Log.e(TAG, "onImageOCRResolved");

        if (imageOCR == null) Log.e(TAG, "ImageOCR is null :(");
        else {
            String parsedText = imageOCR.getParsedText();

            medicine = new Medicine();
            medicine.parseInfo(parsedText);

            if (!medicine.hasACode()) {
                Log.e(TAG, parsedText.replace("\n",""));
                Log.e(TAG, medicine.toString());

                String text = medicine.getName();
                WikiAPIController wikiAPIController = new WikiAPIController(this);
                ArrayList<String> words = new ArrayList<String>(Arrays.asList(text.split(" ")));
                possibleNames = words.size();
                for (int i = 0; i < words.size(); ++i) {
                    String word = words.get(i);
                    Log.e(TAG, "Calling WikiAPI with " + word);
                    wikiAPIController.wikiAPIRequest(word, wikiAPIResolvedCallback);
                }
            }
        }
    }

    @Override
    public void onWikiAPIResolved(WikiContent wikiContent) {
        if (medicine != null) {
            if (wikiContent.isAMedicine()) {
                medicine.setName(wikiContent.getQueryText());
                //Log.e(TAG, wikiContent.getQueryText() + " is a medicine");
            }
            if (wikiContent.redirects()) {
                Log.e(TAG, "Redirection from " + wikiContent.getRedirectionText() + " to " + wikiContent.getRedirectionText());
                possibleNames++;
                wikiAPIController.wikiAPIRequest(wikiContent.getRedirectionText(), wikiAPIResolvedCallback);
            }
            //else Log.e(TAG, wikiContent.getQueryText() + " is NOT a medicine");
        }
        possibleNames--;
        if (possibleNames == 0) Log.e(TAG, medicine.toString());
    }
}
