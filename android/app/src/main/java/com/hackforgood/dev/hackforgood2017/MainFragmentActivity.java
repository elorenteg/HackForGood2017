package com.hackforgood.dev.hackforgood2017;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by LaQuay on 10/03/2017.
 */

public class MainFragmentActivity extends Fragment {
    public static final String TAG = MainFragmentActivity.class.getSimpleName();

    public static MainFragmentActivity newInstance() {
        return new MainFragmentActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);


    }
}
