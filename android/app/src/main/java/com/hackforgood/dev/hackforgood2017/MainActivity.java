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
import com.hackforgood.dev.hackforgood2017.model.ImageOCR;
import com.hackforgood.dev.hackforgood2017.model.Medicine;

import com.beardedhen.androidbootstrap.TypefaceProvider;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ImageOCRController.ImageOCRResolvedCallback {
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int CAMERA_PERMISSION_CODE = 200;
    public static final int WRITE_SD_PERMISSION_CODE = 201;
    private final ImageOCRController.ImageOCRResolvedCallback imageOCRResolvedCallback = this;
    private ImageOCRController imageOCRController;

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

        String url = "http://omicrono.elespanol.com/wp-content/uploads/2015/05/ibuprofeno.jpg";
        imageOCRController.imageOCRRequest(url, imageOCRResolvedCallback);

        url = "http://elfarmaceutico.es/images/stories/546/Ibuprofeno_400mg_cinfa.jpg";
        imageOCRController.imageOCRRequest(url, imageOCRResolvedCallback);

        url = "http://www.elcorreo.com/noticias/201407/24/media/cortadas/paracetamol--575x323.jpg";
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
        Log.e(TAG, "onImageOCRResolved");

        if (imageOCR == null) Log.e(TAG, "ImageOCR is null :(");
        else {
            String parsedText = imageOCR.getParsedText();
            //Log.e(TAG, parsedText.replace("\n",""));

            Medicine medicine = new Medicine();
            medicine.parseInfo(parsedText);
            Log.e(TAG, medicine.toString());
        }
    }
}
