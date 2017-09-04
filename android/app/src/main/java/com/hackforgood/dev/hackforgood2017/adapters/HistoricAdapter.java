package com.hackforgood.dev.hackforgood2017.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hackforgood.dev.hackforgood2017.MainActivity;
import com.hackforgood.dev.hackforgood2017.R;
import com.hackforgood.dev.hackforgood2017.ResultScreenFragment;
import com.hackforgood.dev.hackforgood2017.model.HistoricItem;
import com.hackforgood.dev.hackforgood2017.model.Medicine;

import java.util.List;

public class HistoricAdapter extends RecyclerView.Adapter<HistoricAdapter.ViewHolder> {
    private List<HistoricItem> data;
    private Context context;

    public HistoricAdapter(List<HistoricItem> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.historic_item_view, parent, false);
        return new HistoricAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HistoricItem cardModel = data.get(position);
        final int code = cardModel.getCode();
        final String name = cardModel.getName();

        holder.mHistoricItemCode.setText(code + "");
        holder.mHistoricItemName.setText(name);

        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMedicineFragment(code, name);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private void loadMedicineFragment(int code, String name) {
        Medicine medicine = new Medicine();
        medicine.setCode(code);
        medicine.setName(name);

        Fragment fragment = ResultScreenFragment.newInstance(null, medicine, null, false);
        MainActivity mainActivity = (MainActivity) context;
        FragmentTransaction ft = mainActivity.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_container, fragment, ResultScreenFragment.TAG);
        ft.addToBackStack(null);
        ft.commit();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView mCardView;
        private TextView mHistoricItemCode;
        private TextView mHistoricItemName;

        ViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.historic_item_layout);
            mHistoricItemCode = (TextView) itemView.findViewById(R.id.historic_item_code);
            mHistoricItemName = (TextView) itemView.findViewById(R.id.historic_item_name);
        }
    }
}
