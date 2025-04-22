package com.example.gamehorizon.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gamehorizon.R;

public class jeuCommentaire extends AppCompatActivity implements View.OnClickListener {

    View header, footer;

    TextView headerText;
    private static final String TAG = "jeuCommentaire";
    private int idJeu;
    private int idUtilisateur;
    private String nomUtilisateur;

    ImageView page_accueil, page_connexion, page_principal, page_ajoutJeu, page_jeu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jeu);

        header = findViewById(R.id.header_jeu);
        footer = findViewById(R.id.footerJeu);

        headerText = header.findViewById(R.id.titre_page);

        headerText.setText(getString(R.string.Index));

        page_accueil = header.findViewById(R.id.page_accueil_horizon);
        page_connexion = header.findViewById(R.id.page_connexion);
        page_principal = footer.findViewById(R.id.icone_accueil);
        page_ajoutJeu = footer.findViewById(R.id.icone_page_ajoutJeux);
        page_jeu = footer.findViewById(R.id.icone_page_recherche);

        page_accueil.setOnClickListener(this);
        page_connexion.setOnClickListener(this);
        page_principal.setOnClickListener(this);
        page_ajoutJeu.setOnClickListener(this);
        page_jeu.setOnClickListener(this);

        Intent intent = getIntent();
        idJeu = intent.getIntExtra("ID_JEU",1);
        idUtilisateur = intent.getIntExtra("ID_UTILISATEUR", 1);
        nomUtilisateur = intent.getStringExtra("NAME_UTILISATEUR");
        Log.d(TAG,"L'id du jeu choisi est: " + idJeu);

    }

    @Override
    public void onClick(View v) {
        if (v == page_accueil){
            Intent intent = new Intent(jeuCommentaire.this, MainActivity.class);
            startActivity(intent);
        }
        else if (v == page_connexion){
            Intent intent = new Intent(jeuCommentaire.this, connexion.class);
            startActivity(intent);
        }
        else if (v == page_principal){
            Intent intent = new Intent(jeuCommentaire.this, jeuCommentaire.class);
            intent.putExtra("ID_UTILISATEUR", idUtilisateur);
            intent.putExtra("NAME_UTILISATEUR", nomUtilisateur);
            startActivity(intent);
        }
        else if (v == page_ajoutJeu){
            Intent intent = new Intent(jeuCommentaire.this, ajoutJeux.class);
            intent.putExtra("ID_UTILISATEUR", idUtilisateur);
            intent.putExtra("NAME_UTILISATEUR", nomUtilisateur);
            startActivity(intent);
        }
        else if (v == page_jeu){
            Intent intent = new Intent(jeuCommentaire.this, Recherche.class);
            intent.putExtra("ID_UTILISATEUR", idUtilisateur);
            intent.putExtra("NAME_UTILISATEUR", nomUtilisateur);
            startActivity(intent);
        }
    }
}