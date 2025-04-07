package com.example.gamehorizon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ajoutJeux extends AppCompatActivity implements View.OnClickListener {

    View header, footer;

    TextView headerText;

    ImageView page_accueil, page_connexion, page_principal, page_ajoutJeu, page_jeu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajoutjeux);

        header = findViewById(R.id.header_ajout_jeux);
        footer = findViewById(R.id.footerAjoutJeux);

        headerText = header.findViewById(R.id.titre_page);

        headerText.setText(getString(R.string.Ajout_Jeu));

        page_accueil = header.findViewById(R.id.imageView2);
        page_connexion = header.findViewById(R.id.imageView);
        page_principal = footer.findViewById(R.id.imageView5);
        page_ajoutJeu = footer.findViewById(R.id.imageView8);
        page_jeu = footer.findViewById(R.id.imageView7);


        page_accueil.setOnClickListener(this);
        page_connexion.setOnClickListener(this);
        page_principal.setOnClickListener(this);
        page_ajoutJeu.setOnClickListener(this);
        page_jeu.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == page_accueil){
            Intent intent = new Intent(ajoutJeux.this, MainActivity.class);
            startActivity(intent);
        }
        else if (v == page_connexion){
            Intent intent = new Intent(ajoutJeux.this, connexion.class);
            startActivity(intent);
        }
        else if (v == page_principal){
            Intent intent = new Intent(ajoutJeux.this, pagePrincipal.class);
            startActivity(intent);
        }
        else if (v == page_ajoutJeu){
            Intent intent = new Intent(ajoutJeux.this, ajoutJeux.class);
            startActivity(intent);
        }
        else if (v == page_jeu){
            Intent intent = new Intent(ajoutJeux.this, jeu.class);
            startActivity(intent);
        }
    }
}