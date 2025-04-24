package com.example.gamehorizon.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.example.gamehorizon.Adapter.JeuxAdapter;
import com.example.gamehorizon.R;
import com.example.gamehorizon.RequeteAPI;
import com.example.gamehorizon.entite.Jeu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Recommandation extends AppCompatActivity implements View.OnClickListener, JeuxAdapter.OnItemClickListener {

    private static final String TAG = "Recommandation";
    RequeteAPI requeteAPI;
    View header, footer;
    TextView headerText;
    ImageView page_accueil, page_connexion, page_principal, page_ajoutJeu, page_jeu, imageJeu;
    private RecyclerView gamesRV;
    private JeuxAdapter jeuxAdapter;
    private List<Jeu> listeJeux = new ArrayList<>();

    private int idUtilisateur;
    private String nomUtilisateur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommandation);

        header = findViewById(R.id.header_principal);
        footer = findViewById(R.id.footer_principal);

        headerText = header.findViewById(R.id.titre_page);

        headerText.setText(getString(R.string.recommandation));

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

        gamesRV = findViewById(R.id.gamesRecyclerView);

        gamesRV.setLayoutManager(new LinearLayoutManager(this));
        jeuxAdapter = new JeuxAdapter(listeJeux, this);
        gamesRV.setAdapter(jeuxAdapter);
        imageJeu = gamesRV.findViewById(R.id.gameImageView);
        requeteAPI = new RequeteAPI(this);

        //Récupération des infos de l'intent
        Intent intentionRecommandation = getIntent();
        idUtilisateur = intentionRecommandation.getIntExtra("ID_UTILISATEUR", 1);
        nomUtilisateur = intentionRecommandation.getStringExtra("NAME_UTILISATEUR");

        recommandationsJeux();
    }

    //Gère les clicks sur les icones du footer et du header
    @Override
    public void onClick(View v) {
        if (v == page_accueil){
            Intent intent = new Intent(Recommandation.this, MainActivity.class);
            startActivity(intent);
        }
        else if (v == page_connexion){
            Intent intent = new Intent(Recommandation.this, connexion.class);
            startActivity(intent);
        }
        else if (v == page_principal){
            Intent intent = new Intent(Recommandation.this, Recommandation.class);
            intent.putExtra("ID_UTILISATEUR", idUtilisateur);
            intent.putExtra("NAME_UTILISATEUR", nomUtilisateur);
            startActivity(intent);
        }
        else if (v == page_ajoutJeu){
            Intent intent = new Intent(Recommandation.this, ajoutJeux.class);
            intent.putExtra("ID_UTILISATEUR", idUtilisateur);
            intent.putExtra("NAME_UTILISATEUR", nomUtilisateur);
            startActivity(intent);
        }
        else if (v == page_jeu){
            Intent intent = new Intent(Recommandation.this, com.example.gamehorizon.activity.Recherche.class);
            intent.putExtra("ID_UTILISATEUR", idUtilisateur);
            intent.putExtra("NAME_UTILISATEUR", nomUtilisateur);
            startActivity(intent);
        }
    }

    //Quand on click sur un jeu de la liste
    @Override
    public void onItemClick(Jeu jeu) {
        Intent jeuInfo = new Intent(Recommandation.this, jeuCommentaire.class);
        jeuInfo.putExtra("ID_JEU", jeu.getId());
        jeuInfo.putExtra("ID_UTILISATEUR", idUtilisateur);
        jeuInfo.putExtra("NAME_UTILISATEUR", nomUtilisateur);
        startActivity(jeuInfo);
    }

    //Fait la requête pour récupérer les recommandations d'un utilisateur
    private void recommandationsJeux() {
        String baseUrl = "https://equipe100.tch099.ovh/api/Recommendations?";
        StringBuilder urlBuilder = new StringBuilder(baseUrl);
        urlBuilder.append("id_utilisateur=").append(idUtilisateur).append("&limite=20");

        String apiUrl = urlBuilder.toString();

        Log.d(TAG, "URL de recherche: " + apiUrl);

        requeteAPI.getJSONArray(apiUrl, new RequeteAPI.RequeteJSONArrayCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                listeJeux.clear();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jeuObject = response.getJSONObject(i);
                        String imageUrl = jeuObject.getString("image");

                        if (imageUrl != null && imageUrl.startsWith("//")) {
                            imageUrl = "https:" + imageUrl;
                        }

                        Jeu jeu = new Jeu(
                                jeuObject.getInt("id"),
                                jeuObject.getString("name"),
                                imageUrl
                        );
                        listeJeux.add(jeu);
                    }
                    jeuxAdapter = new JeuxAdapter(listeJeux, Recommandation.this);
                    gamesRV.setAdapter(jeuxAdapter);
                } catch (JSONException e) {
                    Log.e(TAG, "Erreur lors du parsing JSON", e);
                    Toast.makeText(Recommandation.this, "Erreur lors du traitement des données des jeux", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Erreur lors de la requête API", error);
                Toast.makeText(Recommandation.this, "Erreur de connexion au serveur lors de la recherche de jeux", Toast.LENGTH_SHORT).show();
            }
        });
    }
}