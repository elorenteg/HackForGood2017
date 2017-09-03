package com.hackforgood.dev.hackforgood2017;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hackforgood.dev.hackforgood2017.adapters.HistoricAdapter;
import com.hackforgood.dev.hackforgood2017.model.HistoricItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HistoricFragment extends Fragment {
    public static final String TAG = HistoricFragment.class.getSimpleName();
    private View rootview;

    RecyclerView mRecyclerView;
    private List<HistoricItem> mData;

    public static HistoricFragment newInstance() {
        return new HistoricFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.historic_fragment, container, false);

        mData = new ArrayList<HistoricItem>();
        for (int i = 1; i < 15; i++) {
            mData.add(new HistoricItem("Elem " + i));
        }

        setUpElements();
        setUpListeners();

        return rootview;
    }

    private void setUpElements() {
        mRecyclerView = (RecyclerView) rootview.findViewById(R.id.historic_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(new HistoricAdapter(mData, getContext()));
    }

    private void setUpListeners() {
    }
}
