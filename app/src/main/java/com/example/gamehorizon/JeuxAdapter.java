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
    private OnItemClickListener listener; // Ajout du listener de clic

    // Interface pour gérer les clics sur les éléments
    public interface OnItemClickListener {
        void onItemClick(Jeu jeu);
    }

    // Modifiez le constructeur pour accepter le listener
    public JeuxAdapter(List<Jeu> jeuxList, OnItemClickListener listener) {
        this.jeuxList = jeuxList;
        this.listener = listener; // Initialisation du listener
    }

    @NonNull
    @Override
    public JeuxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.jeu_pour_liste, parent, false); // Assurez-vous que 'jeu_pour_liste' est le bon layout
        return new JeuxViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull JeuxViewHolder holder, int position) {
        Jeu jeu = jeuxList.get(position);
        holder.gameNameTextView.setText(jeu.getName());

        // Utilisez Glide pour charger l'image depuis l'URL dans l'ImageView
        if (jeu.getImage() != null && !jeu.getImage().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(jeu.getImage())
                    .into(holder.gameImageView);
        } else {
            holder.gameImageView.setImageResource(R.drawable.ic_launcher_foreground); // Image par défaut
        }

        // Gestion du clic sur l'élément
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(jeu); // Appelle la méthode onItemClick de l'interface
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
            gameNameTextView = itemView.findViewById(R.id.gameNameTextView); // Assurez-vous que l'ID correspond à votre layout jeu_pour_liste.xml
            gameImageView = itemView.findViewById(R.id.gameImageView); // Assurez-vous que l'ID correspond à votre layout jeu_pour_liste.xml
        }
    }
}