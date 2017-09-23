package com.bridgelabz.note.adapter;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bridgelabz.note.R;
import com.bridgelabz.note.model.DataModel;

import java.util.ArrayList;

public class ArchiveDataAdapter extends RecyclerView.Adapter<ArchiveDataAdapter.userViewHolder> {

    public static String key;
    public static String date;
    private ArrayList<DataModel> list;

    public ArchiveDataAdapter(ArrayList<DataModel> list) {
        this.list = list;
    }

    @Override
    public ArchiveDataAdapter.userViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_card, parent, false);
        return new userViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(ArchiveDataAdapter.userViewHolder holder, int position) {

        int color;

        key = list.get(position).getKey();
        color = list.get(position).getColor();
        date = list.get(position).getDate();

        String hexColor = String.format("#%06X", (0xFFFFFF & color));

        holder.user_Title.setText(list.get(position).getTitle());
        holder.user_desc.setText(list.get(position).getDesc());
        holder.card.setCardBackgroundColor(Color.parseColor(hexColor));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class userViewHolder extends RecyclerView.ViewHolder {
        TextView user_Title;
        TextView user_desc;

        CardView card;

        public userViewHolder(final View itemView) {
            super(itemView);
            user_Title = (TextView) itemView.findViewById(R.id.titleTextView);
            user_desc = (TextView) itemView.findViewById(R.id.descTextView);
            card = (CardView) itemView.findViewById(R.id.cardView);
        }
    }
}
