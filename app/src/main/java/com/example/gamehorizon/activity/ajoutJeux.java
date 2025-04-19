package com.example.gamehorizon.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gamehorizon.R;

public class ajoutJeux extends AppCompatActivity implements View.OnClickListener {

    View header, footer;

    TextView headerText;

    ImageView page_accueil, page_connexion, page_principal, page_ajoutJeu, page_jeu;

    EditText champDescription, champNomJeux, champDateParution, champGenre, champImage;

    Spinner champPlateforme;

    Button boutonAjouterJeux;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajoutjeux);

        // Header et footer connexion aux autres pages
        header = findViewById(R.id.header_ajout_jeux);
        footer = findViewById(R.id.footerAjoutJeux);

        headerText = header.findViewById(R.id.titre_page);

        headerText.setText(getString(R.string.Ajout_Jeu));

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

        // Récupération des items de la page
        champNomJeux = findViewById(R.id.champ_nom_jeux);
        champDescription = findViewById(R.id.champ_description);
        champDateParution = findViewById(R.id.champ_date_parution);
        champPlateforme = findViewById(R.id.champ_plateforme);
        champGenre = findViewById(R.id.champ_genre);
        champImage = findViewById(R.id.champ_image);
        boutonAjouterJeux = findViewById(R.id.bouton_ajouter_jeux);
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
            Intent intent = new Intent(ajoutJeux.this, Recommandation.class);
            startActivity(intent);
        }
        else if (v == page_ajoutJeu){
            Intent intent = new Intent(ajoutJeux.this, ajoutJeux.class);
            startActivity(intent);
        }
        else if (v == page_jeu){
            Intent intent = new Intent(ajoutJeux.this, Recherche.class);
            startActivity(intent);
        }
        else if (v == boutonAjouterJeux){
            if (!champNomJeux.getText().toString().isEmpty()) {

            }
            else {
                Toast.makeText(ajoutJeux.this, "Vous devez remplire tous les champs de texte", Toast.LENGTH_SHORT).show();
            }
        }
    }
}