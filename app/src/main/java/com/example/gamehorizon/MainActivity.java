package com.example.gamehorizon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    View header, footer;

    TextView headerText;

    ImageView page_accueil, page_connexion, page_principal, page_ajoutJeu, page_jeu;

    Button connexion, inscription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        header = findViewById(R.id.header_main_page);
        footer = findViewById(R.id.footerMainPage);

        headerText = header.findViewById(R.id.titre_page);

        headerText.setText(getString(R.string.accueil));

        page_accueil = header.findViewById(R.id.imageView2);
        page_connexion = header.findViewById(R.id.imageView);
        page_principal = footer.findViewById(R.id.imageView5);
        page_ajoutJeu = footer.findViewById(R.id.imageView8);
        page_jeu = footer.findViewById(R.id.imageView7);

        connexion = findViewById(R.id.connexion);
        inscription = findViewById(R.id.inscription);

        page_accueil.setOnClickListener(this);
        page_connexion.setOnClickListener(this);
        page_principal.setOnClickListener(this);
        page_ajoutJeu.setOnClickListener(this);
        page_jeu.setOnClickListener(this);

        connexion.setOnClickListener(this);
        inscription.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == page_accueil){
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
        }
        else if (v == page_connexion){
            Intent intent = new Intent(MainActivity.this, connexion.class);
            startActivity(intent);
        }
        else if (v == page_principal){
            Intent intent = new Intent(MainActivity.this, pagePrincipal.class);
            startActivity(intent);
        }
        else if (v == page_ajoutJeu){
            Intent intent = new Intent(MainActivity.this, ajoutJeux.class);
            startActivity(intent);
        }
        else if (v == page_jeu){
            Intent intent = new Intent(MainActivity.this, Recherche.class);
            startActivity(intent);
        }
        else if (v == connexion){
            Intent intent = new Intent(MainActivity.this, connexion.class);
            startActivity(intent);
        }
        else if (v == inscription){
            Intent intent = new Intent(MainActivity.this, inscription.class);
            startActivity(intent);
        }
    }
}