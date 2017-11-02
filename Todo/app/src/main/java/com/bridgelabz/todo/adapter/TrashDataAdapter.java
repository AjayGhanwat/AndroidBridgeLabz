package com.bridgelabz.todo.adapter;

import android.graphics.Color;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bridgelabz.todo.R;
import com.bridgelabz.todo.model.DataModel;
import com.bridgelabz.todo.MainPanelActivity;

import java.util.ArrayList;

import static com.bridgelabz.todo.MainPanelActivity.index;
import static com.bridgelabz.todo.MainPanelActivity.isOnClickEnable;

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
    public void onBindViewHolder(final userViewHolder holder, final int position) {

        int color;

        key = list.get(position).getKey();
        color = list.get(position).getColor();
        //date = list.get(position).getDate();

        String hexColor = String.format("#%06X", (0xFFFFFF & color));

        if (!list.get(position).getTitle().isEmpty()) {
            holder.user_Title.setVisibility(View.VISIBLE);
            holder.user_Title.setText(list.get(position).getTitle());
        } else {
            holder.user_Title.setVisibility(View.GONE);
        }
        if (!list.get(position).getDesc().isEmpty()) {
            holder.user_desc.setVisibility(View.VISIBLE);
            holder.user_desc.setText(list.get(position).getDesc());
        } else {
            holder.user_desc.setVisibility(View.GONE);
        }
        holder.card.setCardBackgroundColor(Color.parseColor(hexColor));
        if (list.get(position).getPin()) {
            holder.imageView.setVisibility(View.VISIBLE);
        }else
            holder.imageView.setVisibility(View.GONE);

//        holder.user_Title.setText(list.get(position).getTitle());
//        holder.user_desc.setText(list.get(position).getDesc());
//        holder.card.setCardBackgroundColor(Color.parseColor(hexColor));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class userViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener{
        public CardView card;
        TextView user_Title;
        TextView user_desc;
        AppCompatImageView imageView;

        boolean isClicked = true;

        public userViewHolder(final View itemView) {
            super(itemView);
            user_Title = (TextView) itemView.findViewById(R.id.titleTextView);
            user_desc = (TextView) itemView.findViewById(R.id.descTextView);
            card = (CardView) itemView.findViewById(R.id.cardView);
            imageView = (AppCompatImageView) itemView.findViewById(R.id.importantPin);

            index.clear();

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    MainPanelActivity.change();
                    isOnClickEnable = true;
                    return false;
                }
            });
            itemView.setOnTouchListener(this);
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (isOnClickEnable) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isClicked) {
                            card.setCardBackgroundColor(itemView.getResources().getColor(R.color.cardview_shadow_start_color));
                            MainPanelActivity.getToDataAdd(Integer.parseInt(list.get(getAdapterPosition()).getKey()));
                            isClicked = false;
                        } else {
                            MainPanelActivity.getToDataDelete(Integer.parseInt(list.get(getAdapterPosition()).getKey()));
                            int color = list.get(getAdapterPosition()).getColor();
                            String hexColor = String.format("#%06X", (0xFFFFFF & color));
                            card.setCardBackgroundColor(Color.parseColor(hexColor));
                            isClicked = true;
                        }
                    }
                });
            }
            return false;
        }

        public static void refreshListData(ArrayList<Integer> index){

            for (int i = 0; i < index.size(); i++){

                for (int j = 0; j < list.size(); j++) {

                    if (index.get(i) == Integer.parseInt(list.get(j).getKey())) {

                        list.remove(j);
                    }
                }
            }
        }
    }
}
