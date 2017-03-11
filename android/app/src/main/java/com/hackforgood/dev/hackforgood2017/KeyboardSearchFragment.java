package com.hackforgood.dev.hackforgood2017;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;

public class KeyboardSearchFragment extends Fragment {
    public static final String TAG = KeyboardSearchFragment.class.getSimpleName();
    private View rootview;
    private BootstrapButton buttonSearch;
    private BootstrapEditText editText;

    public static KeyboardSearchFragment newInstance() {
        return new KeyboardSearchFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.keyboard_search_fragment, container, false);

        setUpElements();
        setUpListeners();

        return rootview;
    }

    private void setUpElements() {
        editText = (BootstrapEditText) rootview.findViewById(R.id.keyboard_search_edit_text);
        buttonSearch = (BootstrapButton) rootview.findViewById(R.id.keyboard_search_button);
    }

    private void setUpListeners() {
        editText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Fragment fragment = ResultScreenFragment.newInstance(null, null, editText.getText().toString());
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.main_container, fragment, ResultScreenFragment.TAG);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
    }
}
