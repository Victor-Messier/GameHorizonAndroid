package com.example.gamehorizon;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class JeuxAdapter extends RecyclerView.Adapter<JeuxAdapter.JeuxViewHolder> {

    private List<Jeu> jeuxList;

    public JeuxAdapter(List<Jeu> jeuxList) {
        this.jeuxList = jeuxList;
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

        // Utilisez Picasso pour charger l'image depuis l'URL dans l'ImageView
        if (jeu.getImage() != null && !jeu.getImage().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(jeu.getImage())
                    .into(holder.gameImageView);
        } else {
            holder.gameImageView.setImageResource(R.drawable.ic_launcher_foreground); // On pourrait changer l'image par défaut ici
        }
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
            gameNameTextView = itemView.findViewById(R.id.gameNameTextView); // Assurez-vous que l'ID correspond à votre layout item_jeu.xml
            gameImageView = itemView.findViewById(R.id.gameImageView); // Assurez-vous que l'ID correspond à votre layout item_jeu.xml
        }
    }
}