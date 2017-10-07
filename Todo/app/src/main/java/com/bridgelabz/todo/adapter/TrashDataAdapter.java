package com.bridgelabz.todo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bridgelabz.todo.R;
import com.bridgelabz.todo.model.DataModel;
import com.bridgelabz.todo.view.MainPanelActivity;

import java.util.ArrayList;

public class TrashDataAdapter extends RecyclerView.Adapter<TrashDataAdapter.userViewHolder> {

    public static String key;
    public static String date;
    public static ArrayList<DataModel> list;

    public TrashDataAdapter(ArrayList<DataModel> list) {
        this.list = list;
    }

    @Override
    public userViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_card, parent, false);
        return new userViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(userViewHolder holder, int position) {

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

    public class userViewHolder extends RecyclerView.ViewHolder{
        TextView user_Title;
        TextView user_desc;
        MainPanelActivity mainPanelActivity;

        public CardView card;

        public userViewHolder(final View itemView) {
            super(itemView);
            user_Title = (TextView) itemView.findViewById(R.id.titleTextView);
            user_desc = (TextView) itemView.findViewById(R.id.descTextView);
            card = (CardView) itemView.findViewById(R.id.cardView);

        }

    }
}
