package com.bridgelabz.todo.adapter;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bridgelabz.todo.R;
import com.bridgelabz.todo.editnote.view.EditNote;
import com.bridgelabz.todo.model.DataModel;
import com.bridgelabz.todo.note.View.NoteFragment;
import com.bridgelabz.todo.note.interacter.NoteFragmentInteracter;

import java.util.ArrayList;
import java.util.Collections;

public class NoteDataAdapter extends RecyclerView.Adapter<NoteDataAdapter.userViewHolder> {

    private ArrayList<DataModel> list;

    public NoteDataAdapter(ArrayList<DataModel> list) {
        this.list = list;
    }

    @Override
    public userViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_card, parent, false);
        return new userViewHolder(inflatedView);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(userViewHolder holder, int position) {

        int color;

        color = list.get(position).getColor();

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
            imageView = (AppCompatImageView) itemView.findViewById(R.id.importantPin);
            card = (CardView) itemView.findViewById(R.id.cardView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String user_title = list.get(getAdapterPosition()).getTitle();
                    String user_desc = list.get(getAdapterPosition()).getDesc();
                    String user_key = list.get(getAdapterPosition()).getKey();
                    int user_color = list.get(getAdapterPosition()).getColor();
                    String user_date = list.get(getAdapterPosition()).getDate();
                    boolean user_reminder = list.get(getAdapterPosition()).getReminder();
                    String user_reminder_date = list.get(getAdapterPosition()).getReminderDate();
                    String user_reminder_time = list.get(getAdapterPosition()).getReminderTime();
                    boolean user_pin = list.get(getAdapterPosition()).getPin();
                    String noteId = list.get(getAdapterPosition()).getId();

                    Intent intent = new Intent(itemView.getContext(), EditNote.class);
                    intent.putExtra("Title", user_title);
                    intent.putExtra("Desc", user_desc);
                    intent.putExtra("Key", user_key);
                    intent.putExtra("Color", user_color);
                    intent.putExtra("Date", user_date);
                    intent.putExtra("reminderDate", user_reminder_date);
                    intent.putExtra("reminderTime", user_reminder_time);
                    intent.putExtra("reminder", user_reminder);
                    intent.putExtra("pin", user_pin);
                    intent.putExtra("noteID", noteId);
                    itemView.getContext().startActivity(intent);

                }
            });

        }
    }
}
