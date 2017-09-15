package com.hackforgood.dev.hackforgood2017;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.hackforgood.dev.hackforgood2017.adapters.HistoricAdapter;
import com.hackforgood.dev.hackforgood2017.model.HistoricItem;
import com.hackforgood.dev.hackforgood2017.model.Medicine;
import com.hackforgood.dev.hackforgood2017.utils.FakeMedsUtils;
import com.hackforgood.dev.hackforgood2017.utils.HistoricUtils;

import java.util.ArrayList;
import java.util.List;

public class HistoricFragment extends Fragment {
    public static final String TAG = HistoricFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private View rootview;
    private List<HistoricItem> mData;
    private AwesomeTextView noDataText;

    public static HistoricFragment newInstance() {
        return new HistoricFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.historic_fragment, container, false);

        setUpElements();
        setUpListeners();

        if (MainActivity.USE_DUMMY_MODE_NO_MEDS) {
            mData = getInformationHistoricFake();
        } else {
            mData = HistoricUtils.getInformationHistoric(getContext());
        }

        if (mData == null || mData.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            noDataText.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            noDataText.setVisibility(View.GONE);
        }

        mRecyclerView.setAdapter(new HistoricAdapter(mData, getContext()));

        return rootview;
    }

    private void setUpElements() {
        mRecyclerView = (RecyclerView) rootview.findViewById(R.id.historic_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        noDataText = (AwesomeTextView) rootview.findViewById(R.id.historic_no_result_text);
    }

    private void setUpListeners() {
    }

    private ArrayList<HistoricItem> getInformationHistoricFake() {
        ArrayList<HistoricItem> arrayHistoric = new ArrayList<>();

        Medicine amoxicilina = FakeMedsUtils.getDummyMedicine(FakeMedsUtils.DUMMY_AMOXICILINA);
        Medicine cetraxal = FakeMedsUtils.getDummyMedicine(FakeMedsUtils.DUMMY_CETRAXAL);
        Medicine budesonida = FakeMedsUtils.getDummyMedicine(FakeMedsUtils.DUMMY_BUDESONIDA_ALCON);

        HistoricItem item = new HistoricItem(amoxicilina.getCode(), amoxicilina.getName());
        HistoricItem item1 = new HistoricItem(cetraxal.getCode(), cetraxal.getName());
        HistoricItem item2 = new HistoricItem(budesonida.getCode(), budesonida.getName());
        arrayHistoric.add(item);
        arrayHistoric.add(item1);
        arrayHistoric.add(item2);

        return arrayHistoric;
    }
}
