package com.example.gamehorizon.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns; // Importez la classe Patterns
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.example.gamehorizon.R;
import com.example.gamehorizon.RequeteAPI;


import org.json.JSONException;
import org.json.JSONObject;

public class inscription extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "InscriptionActivity";

    View header, footer;
    TextView headerText;
    ImageView page_accueil, page_connexion, page_principal, page_ajoutJeu, page_jeu;

    EditText champ_username, champ_prenom, champ_nom, champ_Adresse_courriel, champ_MDP, champ_conf_MDP;

    Button boutonInscription;

    RequeteAPI requeteAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inscription);

        requeteAPI = new RequeteAPI(this);

        // Header / Footer
        header = findViewById(R.id.header_inscription);
        footer = findViewById(R.id.footerInscription);
        headerText = header.findViewById(R.id.titre_page);
        headerText.setText(getString(R.string.Inscription));

        // Navigation Icons
        page_accueil = header.findViewById(R.id.page_accueil_horizon);
        page_connexion = header.findViewById(R.id.page_connexion);
        page_principal = footer.findViewById(R.id.icone_accueil);
        page_ajoutJeu = footer.findViewById(R.id.icone_page_ajoutJeux);
        page_jeu = footer.findViewById(R.id.icone_page_recherche);

        // Set Click Listeners (Navigation)
        page_accueil.setOnClickListener(this);
        page_connexion.setOnClickListener(this);
        page_principal.setOnClickListener(this);
        page_ajoutJeu.setOnClickListener(this);
        page_jeu.setOnClickListener(this);

        // Initialisation des EditText (Assurez-vous que les IDs sont corrects)
        champ_username = findViewById(R.id.champ_usernam); // Vérifiez cet ID
        champ_prenom = findViewById(R.id.champ_prenom);
        champ_nom = findViewById(R.id.champ_nom);
        champ_Adresse_courriel = findViewById(R.id.champ_adresse_courriel); // Vérifiez cet ID
        champ_MDP = findViewById(R.id.champ_Mot_de_passe); // Vérifiez cet ID
        champ_conf_MDP = findViewById(R.id.champ_confirmation_Mot_de_passe); // Vérifiez cet ID

        // Initialisation et listener du bouton
        boutonInscription = findViewById(R.id.bouton_inscription); // Vérifiez cet ID
        boutonInscription.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        int viewId = v.getId();

        // Navigation entre pages
        if (viewId == R.id.page_accueil_horizon) {
            Intent intent = new Intent(inscription.this, MainActivity.class);
            startActivity(intent);
        } else if (viewId == R.id.page_connexion) {
            Intent intent = new Intent(inscription.this, connexion.class);
            startActivity(intent);
        } else if (viewId == R.id.icone_accueil) {
            Toast.makeText(this, "Il faut être connecter pour aller sur les autre pages", Toast.LENGTH_SHORT).show();
        } else if (viewId == R.id.icone_page_ajoutJeux) {
            Toast.makeText(this, "Il faut être connecter pour aller sur les autre pages", Toast.LENGTH_SHORT).show();
        } else if (viewId == R.id.icone_page_recherche) {
            Toast.makeText(this, "Il faut être connecter pour aller sur les autre pages", Toast.LENGTH_SHORT).show();
        }
        // Logique pour s'incrire en tant qu'utilisateur
        else if (viewId == R.id.bouton_inscription) {

            // Récupérer les valeurs
            String username = champ_username.getText().toString().trim();
            String prenom = champ_prenom.getText().toString().trim();
            String adresseCourriel = champ_Adresse_courriel.getText().toString().trim();
            String nom = champ_nom.getText().toString().trim();
            String mdp = champ_MDP.getText().toString().trim();
            String confMdp = champ_conf_MDP.getText().toString().trim();


            if (username.isEmpty() || prenom.isEmpty() || adresseCourriel.isEmpty() ||
                    nom.isEmpty() || mdp.isEmpty() || confMdp.isEmpty()) {
                Toast.makeText(inscription.this, "Veuillez remplir tous les champs.", Toast.LENGTH_LONG).show();
                return;
            }

            // Vérification des connexion
            if (!mdp.equals(confMdp)) {
                Toast.makeText(inscription.this, "Les mots de passe ne correspondent pas.", Toast.LENGTH_LONG).show();

                champ_MDP.setError("Les mots de passe doivent correspondre");
                champ_conf_MDP.setError("Les mots de passe doivent correspondre");
                champ_MDP.requestFocus();
                return;
            } else {

                champ_MDP.setError(null);
                champ_conf_MDP.setError(null);
            }

            // Vérification de l'adresse courriel
            if (!Patterns.EMAIL_ADDRESS.matcher(adresseCourriel).matches()) {
                Toast.makeText(inscription.this, "Veuillez entrer une adresse courriel valide.", Toast.LENGTH_LONG).show();

                champ_Adresse_courriel.setError("Adresse courriel invalide");
                champ_Adresse_courriel.requestFocus();
                return;
            } else {
                champ_Adresse_courriel.setError(null);
            }

            JSONObject utilisateurData = new JSONObject();
            try {
                // Utiliser les clés attendues par votre API
                utilisateurData.put("username", username);
                utilisateurData.put("nom", nom);
                utilisateurData.put("prenom", prenom);
                utilisateurData.put("email", adresseCourriel);
                utilisateurData.put("password", mdp);

                // !! Assurez-vous que cette URL est correcte pour votre API d'inscription !!
                String urlInscription = "https://equipe100.tch099.ovh/api/utilisateur";

                Log.d(TAG, "Envoi des données JSON pour inscription: " + utilisateurData.toString() + " à " + urlInscription);

                requeteAPI.postJSONObject(urlInscription, utilisateurData, new RequeteAPI.RequeteJSONObjectCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        Log.d(TAG, "Réponse API inscription: " + response.toString());
                        try {
                            // Adapter la vérification à ce que votre API renvoie réellement
                            if (response.has("success") && response.getBoolean("success")) {
                                String message = response.optString("message", "Inscription réussie !");
                                Toast.makeText(inscription.this, message, Toast.LENGTH_LONG).show();

                                // Vider les champs après succès
                                champ_username.setText("");
                                champ_prenom.setText("");
                                champ_Adresse_courriel.setText("");
                                champ_nom.setText("");
                                champ_MDP.setText("");
                                champ_conf_MDP.setText("");


                            } else {
                                String errorMsg = response.optString("error", "Erreur lors de l'inscription (API).");
                                // Gérer les erreurs spécifiques renvoyées par l'API (ex: email déjà pris)
                                Toast.makeText(inscription.this, "Erreur API: " + errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Erreur parsing réponse JSON succès inscription", e);
                            Toast.makeText(inscription.this, "Erreur de lecture de la réponse serveur", Toast.LENGTH_SHORT).show();
                        }
                    }

                    // Si il y a erreur dans la reqête
                    @Override
                    public void onError(VolleyError error) {

                        Log.e(TAG, "Erreur Volley lors de l'inscription: " + error.toString());
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
                                    } else if (data.has("message")) { // Vérifier aussi "message"
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
                        Toast.makeText(inscription.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                });

            } catch (JSONException e) {
                Log.e(TAG, "Erreur création JSON pour inscription", e);
                Toast.makeText(inscription.this, "Erreur interne lors de la préparation des données", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requeteAPI != null) {
            Log.d(TAG, "Tentative d'annulation des requêtes pour InscriptionActivity");

        }
    }
}