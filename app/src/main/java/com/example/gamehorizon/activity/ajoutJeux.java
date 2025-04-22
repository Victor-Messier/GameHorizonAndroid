package com.example.gamehorizon.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.example.gamehorizon.entite.Categorie;
import com.example.gamehorizon.entite.Plateform;
import com.example.gamehorizon.R;
import com.example.gamehorizon.RequeteAPI; // Assurez-vous que cette classe est la version Singleton

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ajoutJeux extends AppCompatActivity implements View.OnClickListener {

    // TAG pour les logs, utile pour identifier d'où viennent les messages
    private static final String TAG = "AjoutJeuxActivity";

    View header, footer;
    TextView headerText;
    ImageView page_accueil, page_connexion, page_principal, page_ajoutJeu, page_jeu;
    EditText champDescription, champNomJeux, champDateParution, champImage;
    Spinner spinnerPlatform, spinnerCategorie;
    Button boutonAjouterJeux;

    RequeteAPI requeteAPI;
    List<Plateform> listPlatform = new ArrayList<>();
    List<Categorie> listCategorie = new ArrayList<>();

    private int idUtilisateur;
    private String nomUtilisateur;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajoutjeux);

        requeteAPI = new RequeteAPI(this);

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
        spinnerPlatform = findViewById(R.id.champ_plateforme);
        spinnerCategorie = findViewById(R.id.champ_categorie);
        champImage = findViewById(R.id.champ_image);
        boutonAjouterJeux = findViewById(R.id.bouton_ajouter_jeux);

        boutonAjouterJeux.setOnClickListener(this);

        Intent intent = getIntent();
        idUtilisateur = intent.getIntExtra("ID_UTILISATEUR", 1);
        nomUtilisateur = intent.getStringExtra("NAME_UTILISATEUR");

        // Remplissage des deux spinners
        remplirSpinnerPlateforme();
        remplirSpinnerCategorie();
    }

    /*
    Méthode qui remplit les spinner plateforme en utilisant l'api
     */
    public void remplirSpinnerPlateforme() {
        String urlRecupPlatform = "https://equipe100.tch099.ovh/api/platform";
        requeteAPI.getJSONArray(urlRecupPlatform, new RequeteAPI.RequeteJSONArrayCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                listPlatform.clear(); // Vider la liste


                listPlatform.add(new Plateform(-1, "Sélectionnez une plateforme"));

                Log.d(TAG, "Liste des plateformes reçues: " + response);
                try {
                    // Ajouter les éléments de l'API après l'élément par défaut
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject platformObject = response.getJSONObject(i);
                        int platformId = platformObject.getInt("id");
                        String platformName = platformObject.getString("name");
                        listPlatform.add(new Plateform(platformId, platformName));
                    }

                    // Le reste est identique: créer et définir l'adaptateur
                    ArrayAdapter<Plateform> spinnerAdapterPlatform = new ArrayAdapter<>(
                            ajoutJeux.this, android.R.layout.simple_spinner_dropdown_item, listPlatform);

                    // Assurez-vous que 'spinnerPlatform' est le bon nom de variable pour votre Spinner
                    spinnerPlatform.setAdapter(spinnerAdapterPlatform); // Utilisez le bon nom de variable Spinner

                } catch (JSONException e) {
                    Log.e(TAG, "Erreur lors du parsing JSON des plateformes", e);
                    Toast.makeText(ajoutJeux.this, "Erreur lors du traitement des plateformes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Erreur Volley pour récupérer les plateformes: " + error.toString());
                // Toujours ajouter l'élément par défaut même en cas d'erreur pour que le Spinner ne soit pas vide
                listPlatform.clear();
                listPlatform.add(new Plateform(-1, "Erreur chargement plateformes"));
                ArrayAdapter<Plateform> spinnerAdapterPlatform = new ArrayAdapter<>(
                        ajoutJeux.this, android.R.layout.simple_spinner_dropdown_item, listPlatform);
                spinnerPlatform.setAdapter(spinnerAdapterPlatform); // Utilisez le bon nom de variable Spinner
                Toast.makeText(ajoutJeux.this, "Impossible de charger les plateformes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Méthode modifiée pour remplir le Spinner des catégories/genres
    public void remplirSpinnerCategorie() {
        String urlRecupGenre = "https://equipe100.tch099.ovh/api/categorie";
        requeteAPI.getJSONArray(urlRecupGenre, new RequeteAPI.RequeteJSONArrayCallback() {
            @Override
            public void onSuccess(JSONArray response){
                listCategorie.clear(); // Vider la liste
                // *** AJOUT ICI: Ajouter l'élément par défaut en premier ***
                listCategorie.add(new Categorie(-1, "Sélectionnez une catégorie"));

                Log.d(TAG, "Liste des genres reçues: " + response);
                try {
                    // Ajouter les éléments de l'API après l'élément par défaut
                    for (int i = 0; i < response.length(); i++){
                        JSONObject genreObject = response.getJSONObject(i);
                        int categorieID = genreObject.getInt("id");
                        String name = genreObject.getString("name");
                        listCategorie.add(new Categorie(categorieID, name));
                    }

                    // Le reste est identique: créer et définir l'adaptateur
                    ArrayAdapter<Categorie> spinnerAdapterCategorie = new ArrayAdapter<>(
                            ajoutJeux.this,
                            android.R.layout.simple_spinner_dropdown_item,
                            listCategorie
                    );

                    // Assurez-vous que 'spinnerCategorie' est le bon nom de variable pour votre Spinner
                    spinnerCategorie.setAdapter(spinnerAdapterCategorie); // Utilisez le bon nom de variable Spinner

                } catch (JSONException e) {
                    Log.e(TAG, "Erreur lors du parsing JSON des categories", e);
                    Toast.makeText(ajoutJeux.this, "Erreur lors du traitement des categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Erreur Volley pour récupérer les categories: " + error.toString());
                // Toujours ajouter l'élément par défaut même en cas d'erreur
                listCategorie.clear();
                listCategorie.add(new Categorie(-1, "Erreur chargement catégories"));
                ArrayAdapter<Categorie> spinnerAdapterCategorie = new ArrayAdapter<>(
                        ajoutJeux.this, android.R.layout.simple_spinner_dropdown_item, listCategorie);
                spinnerCategorie.setAdapter(spinnerAdapterCategorie); // Utilisez le bon nom de variable Spinner
                Toast.makeText(ajoutJeux.this, "Impossible de charger les categories", Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public void onClick(View v) {
        // Navigation vers les autres page
        if (v.getId() == R.id.page_accueil_horizon) {
            Intent intent = new Intent(ajoutJeux.this, MainActivity.class);

            startActivity(intent);
        } else if (v.getId() == R.id.page_connexion) {
            Intent intent = new Intent(ajoutJeux.this, connexion.class);
            startActivity(intent);
        } else if (v.getId() == R.id.icone_accueil) {
            Intent intent = new Intent(ajoutJeux.this, Recommandation.class);
            intent.putExtra("ID_UTILISATEUR", idUtilisateur);
            intent.putExtra("NAME_UTILISATEUR", nomUtilisateur);
            startActivity(intent);
        } else if (v.getId() == R.id.icone_page_ajoutJeux) {
            // Déjà sur la page
        } else if (v.getId() == R.id.icone_page_recherche) {
            Intent intent = new Intent(ajoutJeux.this, Recherche.class);
            intent.putExtra("ID_UTILISATEUR", idUtilisateur);
            intent.putExtra("NAME_UTILISATEUR", nomUtilisateur);
            startActivity(intent);
        }
        else if (v.getId() == R.id.bouton_ajouter_jeux) {
            String nomJeu = champNomJeux.getText().toString().trim();
            String description = champDescription.getText().toString().trim();
            String dateParution = champDateParution.getText().toString().trim();
            String image = champImage.getText().toString().trim();

            String plateformeSelectionnee = "";
            String categorieSelectionnee = "";

            Object selectedPlateformeItem = spinnerPlatform.getSelectedItem();
            if (spinnerPlatform.getSelectedItemPosition() > 0 && selectedPlateformeItem instanceof Plateform) {
                Plateform selectedPlateform = (Plateform) selectedPlateformeItem;
                plateformeSelectionnee = selectedPlateform.getName();
            }

            Object selectedCategorieItem = spinnerCategorie.getSelectedItem();
            if (spinnerCategorie.getSelectedItemPosition() > 0 && selectedCategorieItem instanceof Categorie) {
                Categorie selectedCategorie = (Categorie) selectedCategorieItem;
                categorieSelectionnee = selectedCategorie.getName();
            }

            if (!nomJeu.isEmpty() &&
                    !description.isEmpty() &&
                    !dateParution.isEmpty() &&
                    !image.isEmpty() &&
                    spinnerPlatform.getSelectedItemPosition() > 0 &&
                    spinnerCategorie.getSelectedItemPosition() > 0) {

                JSONObject jeuData = new JSONObject();
                try {
                    jeuData.put("name", nomJeu);
                    jeuData.put("description", description);
                    jeuData.put("release_date", dateParution);
                    jeuData.put("genre", categorieSelectionnee);
                    jeuData.put("image", image);
                    jeuData.put("platform", plateformeSelectionnee);

                    String urlAjout = "https://equipe100.tch099.ovh/api/ajoutJeu";
                    Log.d(TAG, "Envoi des données JSON pour ajout: " + jeuData.toString());

                    requeteAPI.postJSONObject(urlAjout, jeuData, new RequeteAPI.RequeteJSONObjectCallback() {
                        // Si la requete est accepter
                        @Override
                        public void onSuccess(JSONObject response) {
                            Log.d(TAG, "Réponse API ajout: " + response.toString());
                            try {
                                if (response.has("success") && response.getBoolean("success")) {
                                    String message = response.optString("message", "Jeu ajouté avec succès !");
                                    Toast.makeText(ajoutJeux.this, message, Toast.LENGTH_LONG).show();

                                    champNomJeux.setText("");
                                    champDescription.setText("");
                                    champDateParution.setText("");
                                    champImage.setText("");
                                    spinnerCategorie.setSelection(0);
                                    spinnerPlatform.setSelection(0);
                                } else {
                                    String errorMsg = response.optString("error", "Erreur inconnue lors de l'ajout.");
                                    Toast.makeText(ajoutJeux.this, "Erreur API: " + errorMsg, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                Log.e(TAG, "Erreur parsing réponse JSON succès ajout", e);
                                Toast.makeText(ajoutJeux.this, "Erreur de lecture de la réponse serveur", Toast.LENGTH_SHORT).show();
                            }
                        }
                        // si la requête à une erreur
                        @Override
                        public void onError(VolleyError error) {
                            Log.e(TAG, "Erreur Volley lors de l'ajout: " + error.toString());
                            String errorMsg = "Erreur réseau ou serveur.";
                            String responseBody = "";

                            if (error.networkResponse != null) {
                                errorMsg += " Statut: " + error.networkResponse.statusCode;
                                try {
                                    responseBody = new String(error.networkResponse.data, "utf-8");
                                    try {
                                        JSONObject data = new JSONObject(responseBody);
                                        if (data.has("error")) {
                                            errorMsg = "Erreur API (" + error.networkResponse.statusCode + "): " + data.getString("error");
                                        } else if (data.has("message")) {
                                            errorMsg = "Erreur API (" + error.networkResponse.statusCode + "): " + data.getString("message");
                                        } else {
                                            errorMsg += "\nRéponse JSON inattendue: " + responseBody.substring(0, Math.min(responseBody.length(), 100));
                                        }
                                    } catch (JSONException jsonEx) {
                                        errorMsg += "\nRéponse serveur (non-JSON): " + responseBody.substring(0, Math.min(responseBody.length(), 100));
                                        Log.e(TAG, "Le corps de l'erreur Volley n'était pas du JSON valide: " + responseBody);
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Impossible de lire ou traiter le corps de l'erreur Volley", e);
                                }
                            }
                            Toast.makeText(ajoutJeux.this, errorMsg, Toast.LENGTH_LONG).show();
                        }
                    });

                } catch (JSONException e) {
                    Log.e(TAG, "Erreur création JSON pour l'ajout", e);
                    Toast.makeText(ajoutJeux.this, "Erreur interne lors de la préparation des données", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(ajoutJeux.this, "Veuillez remplir tous les champs et sélectionner une plateforme et une catégorie valides.", Toast.LENGTH_LONG).show();
            }
        }
    }
}