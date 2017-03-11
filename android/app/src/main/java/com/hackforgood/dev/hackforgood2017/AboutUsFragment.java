package com.hackforgood.dev.hackforgood2017;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class AboutUsFragment extends Fragment {
    public static final String TAG = AboutUsFragment.class.getSimpleName();
    private View rootview;
    private LinearLayout marcLayout;
    private LinearLayout esterLayout;
    private LinearLayout juanLayout;
    private LinearLayout oriolLayout;

    public static AboutUsFragment newInstance() {
        return new AboutUsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.about_us_fragment, container, false);

        setUpElements();
        setUpListeners();

        return rootview;
    }

    private void setUpElements() {
        marcLayout = (LinearLayout) rootview.findViewById(R.id.dialog_about_us_marc_dialog);
        esterLayout = (LinearLayout) rootview.findViewById(R.id.dialog_about_us_ester_dialog);
        juanLayout = (LinearLayout) rootview.findViewById(R.id.dialog_about_us_juan_dialog);
        oriolLayout = (LinearLayout) rootview.findViewById(R.id.dialog_about_us_oriol_dialog);
    }

    private void setUpListeners() {
        marcLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://es.linkedin.com/in/marcvilagomez")));
            }
        });

        esterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://es.linkedin.com/in/elorenteg")));
            }
        });

        juanLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://es.linkedin.com/in/juansalmeronmoya")));
            }
        });

        oriolLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://es.linkedin.com/in/oriolserchmuni")));
            }
        });
    }
}
