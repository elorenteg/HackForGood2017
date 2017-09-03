package com.hackforgood.dev.hackforgood2017.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hackforgood.dev.hackforgood2017.R;
import com.hackforgood.dev.hackforgood2017.model.HistoricItem;

import org.json.JSONException;

import java.util.List;

public class HistoricAdapter extends RecyclerView.Adapter<HistoricAdapter.ViewHolder>{

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
        String name = cardModel.getName();

        holder.mHistoricItemName.setText(name);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView mCardView;
        private TextView mHistoricItemName;

        ViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.historic_item_layout);
            mHistoricItemName = (TextView) itemView.findViewById(R.id.historic_item_text);
        }
    }
}
