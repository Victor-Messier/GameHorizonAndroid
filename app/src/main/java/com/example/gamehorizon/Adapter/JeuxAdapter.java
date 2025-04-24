package com.example.gamehorizon.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gamehorizon.entite.Jeu;
import com.example.gamehorizon.R;

import java.util.List;

public class JeuxAdapter extends RecyclerView.Adapter<JeuxAdapter.JeuxViewHolder> {

    private List<Jeu> jeuxList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Jeu jeu);
    }

    public JeuxAdapter(List<Jeu> jeuxList, OnItemClickListener listener) {
        this.jeuxList = jeuxList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public JeuxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.jeu_pour_liste, parent, false);
        return new JeuxViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull JeuxViewHolder holder, int position) {
        Jeu jeu = jeuxList.get(position);
        holder.gameNameTextView.setText(jeu.getName());

        //Image avec Glide
        if (jeu.getImage() != null && !jeu.getImage().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(jeu.getImage())
                    .into(holder.gameImageView);
        } else {
            //Image par défault
            holder.gameImageView.setImageResource(R.drawable.ic_launcher_foreground);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(jeu);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return jeuxList.size();
    }

    public static class JeuxViewHolder extends RecyclerView.ViewHolder {
        public TextView gameNameTextView;
        public ImageView gameImageView;

        public JeuxViewHolder(View itemView) {
            super(itemView);
            gameNameTextView = itemView.findViewById(R.id.gameNameTextView);
            gameImageView = itemView.findViewById(R.id.gameImageView);
        }
    }
}