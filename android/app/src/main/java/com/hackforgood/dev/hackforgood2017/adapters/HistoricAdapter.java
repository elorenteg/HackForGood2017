package com.hackforgood.dev.hackforgood2017.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hackforgood.dev.hackforgood2017.R;
import com.hackforgood.dev.hackforgood2017.model.HistoricItem;

import java.util.List;

public class HistoricAdapter extends RecyclerView.Adapter<HistoricAdapter.ViewHolder> {
    private List<HistoricItem> data;

    public HistoricAdapter(List<HistoricItem> data) {
        this.data = data;
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
        int code = cardModel.getCode();
        String name = cardModel.getName();

        holder.mHistoricItemCode.setText(code + "");
        holder.mHistoricItemName.setText(name);
    }

    @Override
    public int getItemCount() {
        return data.size();
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
