package com.example.gamehorizon.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamehorizon.R;
import com.example.gamehorizon.entite.Commentaire;

import java.util.List;

public class CommentaireAdapteur extends RecyclerView.Adapter<CommentaireAdapteur.CommentaireViewHolder> {
    private List<Commentaire> commentaireList;
    private OnCommentaireActionListener listener;
    private int loggedInUserId;

    public interface OnCommentaireActionListener {
        void onDeleteClick(int position);
    }

    public CommentaireAdapteur(List<Commentaire> commentaireList, int loggedInUserId, OnCommentaireActionListener listener) {
        this.commentaireList = commentaireList;
        this.loggedInUserId = loggedInUserId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CommentaireAdapteur.CommentaireViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.commentaire, parent, false);
        return new CommentaireViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentaireAdapteur.CommentaireViewHolder holder, int position) {
        Commentaire commentaire = commentaireList.get(position);
        holder.usernameView.setText(commentaire.getUsername());
        holder.commentaireView.setText(commentaire.getContenu());

        if (commentaire.getIdUser() == loggedInUserId) {
            holder.btnSupprimer.setVisibility(View.VISIBLE);
            holder.btnSupprimer.setEnabled(true);
            holder.btnSupprimer.setOnClickListener(v -> {
                if (listener != null) {
                    int currentPosition = holder.getAdapterPosition();
                    if (currentPosition != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(currentPosition);
                    }
                }
            });
        } else {
            holder.btnSupprimer.setVisibility(View.GONE);
            holder.btnSupprimer.setEnabled(false);
            holder.btnSupprimer.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return commentaireList.size();
    }

    public static class CommentaireViewHolder extends RecyclerView.ViewHolder {
        public TextView usernameView,commentaireView;
        public Button btnSupprimer;

        public CommentaireViewHolder(View itemView) {
            super(itemView);
            usernameView = itemView.findViewById(R.id.usernameTextView);
            btnSupprimer = itemView.findViewById(R.id.buttonSupprimerCom);
            commentaireView = itemView.findViewById(R.id.commentTextView);
        }
    }
}