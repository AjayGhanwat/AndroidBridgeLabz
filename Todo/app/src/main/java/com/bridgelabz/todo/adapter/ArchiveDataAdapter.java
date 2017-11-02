package com.bridgelabz.todo.adapter;

import android.graphics.Color;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bridgelabz.todo.R;
import com.bridgelabz.todo.model.DataModel;

import java.util.ArrayList;

public class ArchiveDataAdapter extends RecyclerView.Adapter<ArchiveDataAdapter.userViewHolder> {

    //public static String mkey;
    //public static String mDate;
    private ArrayList<DataModel> list;

    public ArchiveDataAdapter(ArrayList<DataModel> list) {
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

        //mkey = list.get(position).getKey();
        color = list.get(position).getColor();
        //mDate = list.get(position).getDate();

        String hexColor = String.format("#%06X", (0xFFFFFF & color));

        if(!list.get(position).getTitle().isEmpty()) {
            holder.user_Title.setVisibility(View.VISIBLE);
            holder.user_Title.setText(list.get(position).getTitle());
        }else{
            holder.user_Title.setVisibility(View.GONE);
        }
        if(!list.get(position).getDesc().isEmpty()) {
            holder.user_desc.setVisibility(View.VISIBLE);
            holder.user_desc.setText(list.get(position).getDesc());
        }else{
            holder.user_desc.setVisibility(View.GONE);
        }
        holder.card.setCardBackgroundColor(Color.parseColor(hexColor));
        if (list.get(position).getPin()) {
            holder.imageView.setVisibility(View.VISIBLE);
        }else
            holder.imageView.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class userViewHolder extends RecyclerView.ViewHolder {
        TextView user_Title;
        TextView user_desc;
        AppCompatImageView imageView;
        CardView card;

        public userViewHolder(final View itemView) {
            super(itemView);
            user_Title = (TextView) itemView.findViewById(R.id.titleTextView);
            user_desc = (TextView) itemView.findViewById(R.id.descTextView);
            card = (CardView) itemView.findViewById(R.id.cardView);
            imageView = (AppCompatImageView) itemView.findViewById(R.id.importantPin);
        }
    }
}
