package com.bridgelabz.note.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bridgelabz.note.R;
import com.bridgelabz.note.editnote.view.EditNote;
import com.bridgelabz.note.model.DataModel;

import java.util.ArrayList;

public class NoteDataAdapter extends RecyclerView.Adapter<NoteDataAdapter.userViewHolder>{

    public static String key;
    public static String date;
    private ArrayList<DataModel> list;

    public NoteDataAdapter(ArrayList<DataModel> list) {
        this.list = list;
    }

    @Override
    public NoteDataAdapter.userViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_card, parent, false);
        return new userViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(NoteDataAdapter.userViewHolder holder, int position) {

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

        CardView card;

        public userViewHolder(final View itemView) {
            super(itemView);
            user_Title = (TextView) itemView.findViewById(R.id.titleTextView);
            user_desc = (TextView) itemView.findViewById(R.id.descTextView);
            card = (CardView) itemView.findViewById(R.id.cardView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String user_title = list.get(getAdapterPosition()).getTitle();
                    String user_desc = list.get(getAdapterPosition()).getDesc();
                    String user_key = list.get(getAdapterPosition()).getKey();
                    int user_color = list.get(getAdapterPosition()).getColor();
                    String user_date = list.get(getAdapterPosition()).getDate();

                    Intent intent = new Intent(itemView.getContext(),EditNote.class);
                    intent.putExtra("Title", user_title);
                    intent.putExtra("Desc", user_desc);
                    intent.putExtra("Key", user_key);
                    intent.putExtra("Color", user_color);
                    intent.putExtra("Date", user_date);
                    itemView.getContext().startActivity(intent);

                }
            });
        }
    }
}
