package com.example.gamehorizon.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.example.gamehorizon.Categorie;
import com.example.gamehorizon.Jeu;
import com.example.gamehorizon.JeuxAdapter;
import com.example.gamehorizon.Plateform;
import com.example.gamehorizon.R;
import com.example.gamehorizon.RequeteAPI;
import com.example.gamehorizon.activity.MainActivity;
import com.example.gamehorizon.activity.Recommandation;
import com.example.gamehorizon.activity.ajoutJeux;
import com.example.gamehorizon.activity.connexion;
import com.google.android.material.slider.Slider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Recherche extends AppCompatActivity implements View.OnClickListener {

    RecyclerView items;
    View header, footer;
    RequeteAPI requeteAPI;
    TextView headerText, textNom;
    EditText textRechercheJeu,textRechercheDate;
    Slider sliderEtoile;
    Spinner spinnerPlateforme, spinnerCategorie;
    ImageView page_accueil, page_connexion, page_principal, page_ajoutJeu, page_jeu, imageJeu;
    private static final String TAG = "Recherche";

    private String rechercheJeu = "";
    private String dateSortie = "";
    private Float noteSpiner = 0f;
    private Plateform plateformeSelectionnee = null;
    private Categorie categorieSelectionnee = null;

    private JeuxAdapter jeuxAdapter; // Adapter pour le RecyclerView
    private List<Jeu> listeJeux = new ArrayList<>(); // Liste de jeux pour le RecyclerView
    private List<Plateform> listPlatform = new ArrayList<Plateform>();
    private List<Categorie> listCategorie = new ArrayList<Categorie>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recherche);

        header = findViewById(R.id.header_chaque_jeu);
        footer = findViewById(R.id.footer_chaque_jeu);

        headerText = header.findViewById(R.id.titre_page);

        headerText.setText(getString(R.string.Jeu));

        page_accueil = header.findViewById(R.id.page_accueil_horizon);
        page_connexion = header.findViewById(R.id.page_connexion);
        page_principal = footer.findViewById(R.id.icone_accueil);
        page_ajoutJeu = footer.findViewById(R.id.icone_page_ajoutJeux);
        page_jeu = footer.findViewById(R.id.icone_page_recherche);

        items = findViewById(R.id.recyclerView);
        textNom = items.findViewById(R.id.gameNameTextView);

        items.setLayoutManager(new LinearLayoutManager(this));
        jeuxAdapter = new JeuxAdapter(listeJeux);
        items.setAdapter(jeuxAdapter);
        imageJeu = items.findViewById(R.id.gameImageView);
        requeteAPI = new RequeteAPI(this);

        textRechercheJeu = findViewById(R.id.rechercheJeuTextView);
        textRechercheDate = findViewById(R.id.dateSortieRecherceTextView);
        sliderEtoile = findViewById(R.id.etoile_range_slider);
        spinnerPlateforme = findViewById(R.id.champ_plateforme);
        spinnerCategorie = findViewById(R.id.champ_genre);

        page_accueil.setOnClickListener(this);
        page_connexion.setOnClickListener(this);
        page_principal.setOnClickListener(this);
        page_ajoutJeu.setOnClickListener(this);
        page_jeu.setOnClickListener(this);

        remplirSpinner();
        setupListeners();
        rechercherJeux();
    }

    @Override
    public void onClick(View v) {
        if (v == page_accueil){
            Intent intent = new Intent(Recherche.this, MainActivity.class);
            startActivity(intent);
        }
        else if (v == page_connexion){
            Intent intent = new Intent(Recherche.this, connexion.class);
            startActivity(intent);
        }
        else if (v == page_principal){
            Intent intent = new Intent(Recherche.this, Recommandation.class);
            startActivity(intent);
        }
        else if (v == page_ajoutJeu){
            Intent intent = new Intent(Recherche.this, ajoutJeux.class);
            startActivity(intent);
        }
        else if (v == page_jeu){
            Intent intent = new Intent(Recherche.this, Recherche.class);
            startActivity(intent);
        }
    }

    public void remplirSpinner() {
        String urlRecupPlatform = "https://equipe100.tch099.ovh/api/platform";
        requeteAPI.getJSONArray(urlRecupPlatform, new RequeteAPI.RequeteJSONArrayCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                listPlatform.add(new Plateform(0, "Toutes"));
                Log.d(TAG,"Liste des plateformes" + response);
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject platformObject = response.getJSONObject(i);
                        int platformId = platformObject.getInt("id");
                        String platformName = platformObject.getString("name");
                        listPlatform.add(new Plateform(platformId, platformName));
                    }

                    ArrayAdapter<Plateform> spinnerAdapterPlatform = new ArrayAdapter<>(
                            Recherche.this, android.R.layout.simple_spinner_dropdown_item, listPlatform);

                    // Définir l'adaptateur sur le Spinner
                    spinnerPlateforme.setAdapter(spinnerAdapterPlatform);

                } catch (JSONException e) {
                    Log.e(TAG, "Erreur lors du parsing JSON", e);
                    Toast.makeText(Recherche.this, "Erreur lors du traitement des données", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG,"Erreur lors de la récupération des plateformes", error);
                Toast.makeText(Recherche.this, "Erreur de connexion au serveur", Toast.LENGTH_SHORT).show();
            }
        });

        String urlRecupCategories = "https://equipe100.tch099.ovh/api/categorie";
        requeteAPI.getJSONArray(urlRecupCategories, new RequeteAPI.RequeteJSONArrayCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                listCategorie.add(new Categorie(0, "Tous"));
                Log.d(TAG,"Liste des Categories" + response);
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject CategorieObject = response.getJSONObject(i);
                        int categorieId = CategorieObject.getInt("id");
                        String categorieName = CategorieObject.getString("name");
                        listCategorie.add(new Categorie(categorieId, categorieName));
                    }

                    ArrayAdapter<Categorie> spinnerAdapterCategorie = new ArrayAdapter<>(
                            Recherche.this, android.R.layout.simple_spinner_dropdown_item, listCategorie);

                    // Définir l'adaptateur sur le Spinner
                    spinnerCategorie.setAdapter(spinnerAdapterCategorie);

                } catch (JSONException e) {
                    Log.e(TAG, "Erreur lors du parsing JSON", e);
                    Toast.makeText(Recherche.this, "Erreur lors du traitement des données", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG,"Erreur lors de la récupération des plateformes", error);
                Toast.makeText(Recherche.this, "Erreur de connexion au serveur", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        // Listener pour le champ de recherche de jeu
        textRechercheJeu.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                rechercheJeu = charSequence.toString();
                rechercherJeux(); // Déclencher la recherche à chaque changement de texte
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        // Listener pour le champ de date de sortie
        textRechercheDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                dateSortie = charSequence.toString();
                if (dateSortie.length() == 4) {
                    rechercherJeux();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        // Listener pour le RangeSlider
        sliderEtoile.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(Slider slider, float value, boolean fromUser) {
                noteSpiner = value;

                rechercherJeux(); // Déclencher la recherche à chaque changement du slider
            }
        });

        // Listener pour le Spinner Plateforme
        spinnerPlateforme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Récupérer l'objet Plateform directement de la liste en utilisant la position
                if (position >= 0 && position < listPlatform.size()) {
                    plateformeSelectionnee = listPlatform.get(position);
                } else {
                    plateformeSelectionnee = null; // Gérer le cas où la position est invalide (optionnel)
                }
                rechercherJeux();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                plateformeSelectionnee = null;
                rechercherJeux();
            }
        });

        // Listener pour le Spinner Catégorie
        spinnerCategorie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Récupérer l'objet Categorie directement de la liste en utilisant la position
                if (position >= 0 && position < listCategorie.size()) {
                    categorieSelectionnee = listCategorie.get(position);
                } else {
                    categorieSelectionnee = null; // Gérer le cas où la position est invalide (optionnel)
                }
                rechercherJeux();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                categorieSelectionnee = null;
                rechercherJeux();
            }
        });
    }

    private String buildApiUrl() {
        String baseUrl = "https://equipe100.tch099.ovh/api/jeux/get/multiParams?";
        StringBuilder urlBuilder = new StringBuilder(baseUrl);

        if (!rechercheJeu.isEmpty()) {
            urlBuilder.append("recherche=").append(rechercheJeu).append("&");
        }
        if (!dateSortie.isEmpty()) {
            urlBuilder.append("date=").append(dateSortie).append("&");
        }
        urlBuilder.append("note=").append(noteSpiner).append("&");;

        if (plateformeSelectionnee != null) {
            if(plateformeSelectionnee.getId() != 0) {
                urlBuilder.append("id_plateforme=").append(plateformeSelectionnee.getId()).append("&");
            }
        }
        if (categorieSelectionnee != null) {
            if(categorieSelectionnee.getId() != 0) {
                urlBuilder.append("id_genre=").append(categorieSelectionnee.getId()).append("&");
            }
        }

        urlBuilder.append("limite=50").append("&");
        urlBuilder.append("page=1").append("&");

        // Supprimer le dernier "&" si il y en a un
        if (urlBuilder.charAt(urlBuilder.length() - 1) == '&') {
            urlBuilder.deleteCharAt(urlBuilder.length() - 1);
        }

        return urlBuilder.toString();
    }

    private void rechercherJeux() {
        String apiUrl = buildApiUrl();
        Log.d(TAG, "URL de recherche: " + apiUrl);

        requeteAPI.getJSONArray(apiUrl, new RequeteAPI.RequeteJSONArrayCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                listeJeux.clear();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jeuObject = response.getJSONObject(i);
                        String imageUrl = jeuObject.getString("image"); // Get the image URL

                        // Handle protocol-relative URLs here:
                        if (imageUrl != null && imageUrl.startsWith("//")) {
                            imageUrl = "https:" + imageUrl; // Prepend https:
                        }

                        Jeu jeu = new Jeu(
                                jeuObject.getInt("id"),
                                jeuObject.getString("name"),
                                imageUrl // Use the modified imageUrl
                        );
                        listeJeux.add(jeu);
                    }
                    jeuxAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e(TAG, "Erreur lors du parsing JSON", e);
                    Toast.makeText(Recherche.this, "Erreur lors du traitement des données des jeux", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Erreur lors de la requête API", error);
                Toast.makeText(Recherche.this, "Erreur de connexion au serveur lors de la recherche de jeux", Toast.LENGTH_SHORT).show();
            }
        });
    }
}