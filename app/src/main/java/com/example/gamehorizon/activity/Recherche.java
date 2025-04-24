package com.example.gamehorizon.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.example.gamehorizon.entite.Categorie;
import com.example.gamehorizon.entite.Jeu;
import com.example.gamehorizon.Adapter.JeuxAdapter;
import com.example.gamehorizon.entite.Plateform;
import com.example.gamehorizon.R;
import com.example.gamehorizon.RequeteAPI;
import com.google.android.material.slider.Slider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Recherche extends AppCompatActivity implements View.OnClickListener, JeuxAdapter.OnItemClickListener {

    RecyclerView items;
    View header, footer;
    RequeteAPI requeteAPI;
    TextView headerText, textNom;
    EditText textRechercheJeu,textRechercheDate;
    Slider sliderEtoile;
    Spinner spinnerPlateforme, spinnerCategorie;
    ImageView page_accueil, page_connexion, page_principal, page_ajoutJeu, page_jeu, imageJeu;
    private int idUtilisateur;
    private String nomUtilisateur;
    private static final String TAG = "Recherche";

    private String rechercheJeu = "";
    private String dateSortie = "";
    private Float noteSpiner = 0f;
    private Plateform plateformeSelectionnee = null;
    private Categorie categorieSelectionnee = null;

    private JeuxAdapter jeuxAdapter;
    private List<Jeu> listeJeux = new ArrayList<>();
    private List<Plateform> listPlatform = new ArrayList<Plateform>();
    private List<Categorie> listCategorie = new ArrayList<Categorie>();

    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private final long SEARCH_DELAY = 500;
    private boolean initialPlatformSelectionDone = false;
    private boolean initialCategorySelectionDone = false;
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

        //Récup les infos du intent
        Intent intent = getIntent();
        idUtilisateur = intent.getIntExtra("ID_UTILISATEUR", 1);
        nomUtilisateur = intent.getStringExtra("NAME_UTILISATEUR");
        Log.d(TAG,"L'id du user choisi est: " + idUtilisateur);

        items = findViewById(R.id.recyclerViewjeux);
        items.setLayoutManager(new LinearLayoutManager(this));
        jeuxAdapter = new JeuxAdapter(listeJeux, this);
        items.setAdapter(jeuxAdapter);

        requeteAPI = new RequeteAPI(this);

        textRechercheJeu = findViewById(R.id.rechercheJeuTextView);
        textRechercheDate = findViewById(R.id.dateSortieRecherceTextView);
        sliderEtoile = findViewById(R.id.etoile_range_slider);
        spinnerPlateforme = findViewById(R.id.champ_plateforme);
        spinnerCategorie = findViewById(R.id.champ_categorie);

        page_accueil.setOnClickListener(this);
        page_connexion.setOnClickListener(this);
        page_principal.setOnClickListener(this);
        page_ajoutJeu.setOnClickListener(this);
        page_jeu.setOnClickListener(this);

        remplirSpinner();
        setupListeners();
        rechercherJeux();
    }

    //Gère les clicks sur les boutons du footer et du header
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
            intent.putExtra("ID_UTILISATEUR", idUtilisateur);
            intent.putExtra("NAME_UTILISATEUR", nomUtilisateur);
            startActivity(intent);
        }
        else if (v == page_ajoutJeu){
            Intent intent = new Intent(Recherche.this, ajoutJeux.class);
            intent.putExtra("ID_UTILISATEUR", idUtilisateur);
            intent.putExtra("NAME_UTILISATEUR", nomUtilisateur);
            startActivity(intent);
        }
        else if (v == page_jeu){
            Intent intent = new Intent(Recherche.this, Recherche.class);
            intent.putExtra("ID_UTILISATEUR", idUtilisateur);
            intent.putExtra("NAME_UTILISATEUR", nomUtilisateur);
            startActivity(intent);
        }
    }

    //Quand on click sur un jeu de la liste
    @Override
    public void onItemClick(Jeu jeu) {
        Intent jeuInfo = new Intent(Recherche.this, jeuCommentaire.class);
        jeuInfo.putExtra("ID_JEU", jeu.getId());
        jeuInfo.putExtra("ID_UTILISATEUR", idUtilisateur);
        jeuInfo.putExtra("NAME_UTILISATEUR", nomUtilisateur);
        startActivity(jeuInfo);
    }

    //Remplir les spinner de platforme et de genre
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

    //Listeners pour tous les critères de recherche
    private void setupListeners() {
        //Listener pour le champ de recherche de jeu
        textRechercheJeu.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                rechercheJeu = editable.toString();
                searchRunnable = new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Debounced search triggered for: " + rechercheJeu);
                        rechercherJeux();
                    }
                };
                searchHandler.postDelayed(searchRunnable, SEARCH_DELAY);
            }
        });

        //Listener pour le champ de date de sortie
        textRechercheDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                dateSortie = editable.toString();
                if (dateSortie.isEmpty() || dateSortie.length() >= 4) {
                    final String currentDateQuery = dateSortie;
                    Runnable dateSearchRunnable = new Runnable() {
                        @Override
                        public void run() {
                            if (dateSortie.equals(currentDateQuery)) {
                                Log.d(TAG, "Debounced date search triggered for: " + dateSortie);
                                rechercherJeux();
                            }
                        }
                    };
                    searchHandler.postDelayed(dateSearchRunnable, SEARCH_DELAY);
                }
            }
        });

        //Listener pour le RangeSlider
        sliderEtoile.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(Slider slider) {
            }

            @Override
            public void onStopTrackingTouch(Slider slider) {
                noteSpiner = slider.getValue();
                Log.d(TAG, "Slider onStopTrackingTouch - Nouvelle note: " + noteSpiner + ", déclenchement recherche.");
                rechercherJeux();
            }
        });

        //Listener pour le Spinner Plateforme
        spinnerPlateforme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (listPlatform.size() > position) {
                    Plateform selected = listPlatform.get(position);
                    boolean selectionChanged = (plateformeSelectionnee == null || plateformeSelectionnee.getId() != selected.getId());

                    if (selectionChanged) {
                        plateformeSelectionnee = selected;
                        if (initialPlatformSelectionDone) {
                            Log.d(TAG, "SpinnerPlatform - Selection changed by user/later event, triggering search.");
                            rechercherJeux();
                        } else {
                            Log.d(TAG, "SpinnerPlatform - Initial selection, ignoring search trigger.");
                            initialPlatformSelectionDone = true;
                        }
                    } else if (!initialPlatformSelectionDone) {
                        initialPlatformSelectionDone = true;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if (initialPlatformSelectionDone && plateformeSelectionnee != null) {
                    Log.d(TAG, "SpinnerPlatform - Nothing selected, triggering search.");
                    plateformeSelectionnee = null;
                    rechercherJeux();
                } else {
                    plateformeSelectionnee = null;
                    if (!initialPlatformSelectionDone) initialPlatformSelectionDone = true;
                }
            }
        });

        //Listener pour le Spinner Catégorie
        spinnerCategorie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (listCategorie.size() > position) {
                    Categorie selected = listCategorie.get(position);
                    boolean selectionChanged = (categorieSelectionnee == null || categorieSelectionnee.getId() != selected.getId());

                    if (selectionChanged) {
                        categorieSelectionnee = selected;
                        if (initialCategorySelectionDone) {
                            Log.d(TAG, "SpinnerCategorie - Selection changed by user/later event, triggering search.");
                            rechercherJeux();
                        } else {
                            Log.d(TAG, "SpinnerCategorie - Initial selection, ignoring search trigger.");
                            initialCategorySelectionDone = true;
                        }
                    } else if (!initialCategorySelectionDone) {
                        initialCategorySelectionDone = true;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if (initialCategorySelectionDone && categorieSelectionnee != null) {
                    Log.d(TAG, "SpinnerCategorie - Nothing selected, triggering search.");
                    categorieSelectionnee = null;
                    rechercherJeux();
                } else {
                    categorieSelectionnee = null;
                    if (!initialCategorySelectionDone) initialCategorySelectionDone = true;
                }
            }
        });
    }

    //Construire la grosse requête pour la recherhce de jeux
    private String buildApiUrl() {
        String baseUrl = "https://equipe100.tch099.ovh/api/jeux/get/multiParams?";
        StringBuilder urlBuilder = new StringBuilder(baseUrl);

        if (!rechercheJeu.isEmpty()) {
            urlBuilder.append("recherche=").append(rechercheJeu).append("&");
        }
        if (!dateSortie.isEmpty()) {
            urlBuilder.append("date=").append(dateSortie).append("&");
        }
        if (noteSpiner !=0){
            urlBuilder.append("note=").append(noteSpiner).append("&");
        }

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

        //Supprimer le dernier "&" si il y en a un
        if (urlBuilder.charAt(urlBuilder.length() - 1) == '&') {
            urlBuilder.deleteCharAt(urlBuilder.length() - 1);
        }

        return urlBuilder.toString();
    }

    //Requête à l'API pour la recherche
    private void rechercherJeux() {
        String apiUrl = buildApiUrl();
        Log.d(TAG, "URL de recherche: " + apiUrl);

        //Get un liste de jeu
        requeteAPI.getJSONArray(apiUrl, new RequeteAPI.RequeteJSONArrayCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                List<Jeu> nouveauxJeux = new ArrayList<>();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jeuObject = response.getJSONObject(i);
                        String imageUrl = jeuObject.optString("image", "");

                        if (imageUrl != null && imageUrl.startsWith("//")) {
                            imageUrl = "https:" + imageUrl;
                        }

                        Jeu jeu = new Jeu(
                                jeuObject.getInt("id"),
                                jeuObject.getString("name"),
                                imageUrl
                        );
                        nouveauxJeux.add(jeu);
                    }

                    listeJeux.clear();
                    listeJeux.addAll(nouveauxJeux);
                    jeuxAdapter.notifyDataSetChanged();

                    Log.d(TAG, "Adapter mis à jour avec " + listeJeux.size() + " jeux.");

                } catch (JSONException e) {
                    Log.e(TAG, "Erreur lors du parsing JSON", e);
                    Toast.makeText(Recherche.this, "Erreur lors du traitement des données des jeux", Toast.LENGTH_SHORT).show();
                    listeJeux.clear();
                    jeuxAdapter.notifyDataSetChanged();
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