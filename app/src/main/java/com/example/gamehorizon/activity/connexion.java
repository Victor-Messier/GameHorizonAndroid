package com.example.gamehorizon.activity;

import android.content.Intent;
import android.net.Uri; // Pour encoder les paramètres d'URL
import android.os.Bundle;
import android.util.Log; // Pour les messages de débogage
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast; // Pour afficher des messages à l'utilisateur

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError; // Pour gérer les erreurs réseau
import com.example.gamehorizon.R;
import com.example.gamehorizon.RequeteAPI; // Notre classe helper pour les appels API

import org.json.JSONException; // Pour gérer les erreurs lors de la manipulation de JSON
import org.json.JSONObject; // Pour manipuler les objets JSON

/**
 * Gère l'écran et le processus de connexion de l'utilisateur.
 * L'utilisateur entre son email et son mot de passe, qui sont envoyés à une API pour vérification.
 * Si la vérification réussit, une deuxième requête est faite pour obtenir les détails de l'utilisateur
 * avant de passer à l'écran suivant.
 */
public class connexion extends AppCompatActivity implements View.OnClickListener {

    // Tag pour identifier les logs provenant de cette activité
    private static final String TAG = "ConnexionActivity";


    // Éléments de l'interface utilisateur
    View header, footer;
    TextView headerText;
    ImageView page_accueil, page_connexion, page_principal, page_ajoutJeu, page_jeu;

    // Champs de saisie et bouton pour le formulaire de connexion
    EditText champ_email, champ_mdp;
    Button btnconnexion;
    RequeteAPI requeteAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connexion);

        requeteAPI = new RequeteAPI(this);

        // Récupération et configuration du Header et du Footer (éléments communs)
        header = findViewById(R.id.header_connexion);
        footer = findViewById(R.id.footerConnexion);
        headerText = header.findViewById(R.id.titre_page);
        headerText.setText(getString(R.string.connexion));

        // Récupération des icônes de navigation dans le header et le footer
        page_accueil = header.findViewById(R.id.page_accueil_horizon);
        page_connexion = header.findViewById(R.id.page_connexion);
        page_principal = footer.findViewById(R.id.icone_accueil);
        page_ajoutJeu = footer.findViewById(R.id.icone_page_ajoutJeux);
        page_jeu = footer.findViewById(R.id.icone_page_recherche);

        // Attache un écouteur de clics à chaque icône de navigation
        page_accueil.setOnClickListener(this);
        page_connexion.setOnClickListener(this);
        page_principal.setOnClickListener(this);
        page_ajoutJeu.setOnClickListener(this);
        page_jeu.setOnClickListener(this);

        champ_email = findViewById(R.id.champ_email);
        champ_mdp = findViewById(R.id.champ_mdp);
        btnconnexion = findViewById(R.id.bouton_connexion);

        //  écouteur de clics au bouton de connexion
        if (btnconnexion != null) {
            btnconnexion.setOnClickListener(this);
        } else {
            // Si le bouton n'est pas trouvé, on le signale (erreur de layout probable)
            Log.e(TAG, "ERREUR CRITIQUE: Le bouton R.id.bouton_connexion n'a pas été trouvé ! Vérifiez le layout connexion.xml.");
            Toast.makeText(this, "Erreur interne: bouton manquant.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Méthode appelée lorsqu'un élément cliquable (auquel on a attaché 'this' comme listener) est cliqué.
     * @param v La vue qui a été cliquée.
     */
    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        // Navigation
        if (viewId == R.id.page_accueil_horizon) {
            Intent intent = new Intent(connexion.this, MainActivity.class);
            startActivity(intent);
        } else if (viewId == R.id.page_connexion) {
            Toast.makeText(this, "Déjà sur la page de connexion", Toast.LENGTH_SHORT).show();
        } else if (viewId == R.id.icone_accueil) {
            Toast.makeText(this, "Il faut être connecter pour aller sur les autre pages", Toast.LENGTH_SHORT).show();
        } else if (viewId == R.id.icone_page_ajoutJeux) {
            Toast.makeText(this, "Il faut être connecter pour aller sur les autre pages", Toast.LENGTH_SHORT).show();
        } else if (viewId == R.id.icone_page_recherche) {
            Toast.makeText(this, "Il faut être connecter pour aller sur les autre pages", Toast.LENGTH_SHORT).show();
        }

        //
        else if (btnconnexion != null && viewId == btnconnexion.getId()) {

            final String email = champ_email.getText().toString().trim();
            String mdp = champ_mdp.getText().toString().trim();

            // Validation simple
            if (email.isEmpty() || mdp.isEmpty()) {
                Toast.makeText(connexion.this, "Veuillez entrer votre email et votre mot de passe.", Toast.LENGTH_LONG).show();
                return;
            }

            // Prépare les données à envoyer à l'API sous forme d'objet JSON
            JSONObject loginData = new JSONObject();
            try {
                loginData.put("email", email);
                loginData.put("mdp", mdp);

                // L'URL de la route PHP
                String urlLogin = "https://equipe100.tch099.ovh/login.php";

                // Log pour déboguer : quelles données on envoie et où
                Log.d(TAG, "Étape 1: Tentative de connexion POST avec JSON: " + loginData.toString() + " à " + urlLogin);

                requeteAPI.postJSONObject(urlLogin, loginData, new RequeteAPI.RequeteJSONObjectCallback() {
                    /**
                     * Callback appelé si l'API répond avec succès (code HTTP 2xx) et renvoie du JSON.
                     * @param loginResponse L'objet JSON reçu de l'API /login.php.
                     */
                    @Override
                    public void onSuccess(JSONObject loginResponse) {
                        Log.d(TAG, "Étape 1: Réponse API connexion POST: " + loginResponse.toString());
                        try {

                            if (loginResponse.has("success") && loginResponse.getBoolean("success")) {

                                Log.d(TAG, "Étape 1: Connexion POST réussie. Passage à l'étape 2 (GET user details).");
                                getUserDetailsByEmail(email);
                            } else {

                                String errorMsg = loginResponse.optString("error", "Email ou mot de passe incorrect.");
                                Toast.makeText(connexion.this, "Échec Connexion: " + errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            // Une erreur s'est produite lors de la lecture de la réponse JSON (format inattendu?)
                            Log.e(TAG, "Étape 1: Erreur parsing réponse JSON succès connexion", e);
                            Toast.makeText(connexion.this, "Erreur lecture réponse (1)", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        // On utilise notre méthode centralisée pour afficher une erreur compréhensible
                        handleApiError("Étape 1 (POST login)", error);
                    }
                });

            } catch (JSONException e) {
                // Une erreur s'est produite lors de la création de l'objet JSON 'loginData' (très rare)
                Log.e(TAG, "Erreur création JSON pour connexion", e);
                Toast.makeText(connexion.this, "Erreur interne (préparation)", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getUserDetailsByEmail(String userEmail) {
        // Petite vérification pour s'assurer que notre helper API est prêt
        if (requeteAPI == null) {
            Log.e(TAG, "RequeteAPI non initialisée dans getUserDetailsByEmail");
            Toast.makeText(this, "Erreur interne (API)", Toast.LENGTH_SHORT).show();
            return;
        }

        try {

            String urlGetUser = "https://equipe100.tch099.ovh/api/utilisateur/email/" + userEmail;
            Log.d(TAG, "Étape 2: Appel GET (as String) pour récupérer détails utilisateur: " + urlGetUser);

            // On utilise getString car on doit gérer la réponse 'false' potentielle de l'API
            requeteAPI.getString(urlGetUser, new RequeteAPI.RequeteStringCallback() {

                @Override
                public void onSuccess(String responseString) {
                    Log.d(TAG, "Étape 2: Réponse API GET (raw String): '" + responseString + "'");

                    // 1. Vérifier si l'API a spécifiquement renvoyé 'false'
                    if (responseString != null && responseString.trim().equalsIgnoreCase("false")) {
                        // Cas où l'API GET ne trouve pas l'utilisateur (même si le login a réussi, ce qui est étrange mais on le gère)
                        Log.w(TAG, "Étape 2: API GET a retourné 'false'. Utilisateur non trouvé pour l'email: " + userEmail);
                        Toast.makeText(connexion.this, "Erreur: Utilisateur non trouvé après connexion.", Toast.LENGTH_LONG).show();

                    } else if (responseString != null && !responseString.trim().isEmpty()) {

                        try {
                            JSONObject userResponse = new JSONObject(responseString);
                            Log.d(TAG, "Étape 2: String parsé en JSONObject: " + userResponse.toString());

                            int userId = userResponse.optInt("id", -1);
                            String userName = userResponse.optString("username", "Utilisateur");

                            if (userId != -1) {

                                Log.d(TAG, "Étape 2: ID utilisateur trouvé: " + userId + ", Nom: " + userName);
                                Toast.makeText(connexion.this, "Bienvenue " + userName, Toast.LENGTH_SHORT).show();

                                champ_email.setText("");
                                champ_mdp.setText("");

                                Intent intent = new Intent(connexion.this, Recommandation.class);
                                intent.putExtra("ID_UTILISATEUR", userId);
                                intent.putExtra("NAME_UTILISATEUR", userName);
                                startActivity(intent);
                                finish();

                            } else {
                                Log.e(TAG, "Étape 2: JSON reçu mais clé 'id' utilisateur manquante ou invalide: " + userResponse.toString());
                                Toast.makeText(connexion.this, "Erreur: Données utilisateur incomplètes reçues (ID).", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            Log.e(TAG, "Étape 2: Erreur parsing de la réponse String en JSON. Réponse reçue: '" + responseString + "'", e);
                            Toast.makeText(connexion.this, "Erreur: Format de réponse serveur (GET) inattendu.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // La réponse du serveur était vide ou nulle
                        Log.e(TAG, "Étape 2: Réponse API GET vide ou nulle reçue.");
                        Toast.makeText(connexion.this, "Erreur: Réponse serveur (GET) vide.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    // On utilise notre méthode centralisée pour afficher l'erreur
                    handleApiError("Étape 2 (GET details as String)", error);
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Erreur préparation requête GET utilisateur", e);
            Toast.makeText(connexion.this, "Erreur interne (URL)", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Méthode simple pour afficher les erreurs Volley de manière un peu plus claire.
     * Elle essaie de récupérer le code de statut HTTP et le message d'erreur
     */
    private void handleApiError(String stepDescription, VolleyError error) {
        Log.e(TAG, "Erreur Volley [" + stepDescription + "]: " + error.toString());
        String errorMsg = "Erreur réseau ou serveur.";
        String responseBody = "";
        int statusCode = 0;

        // Vérifie si l'erreur contient des détails sur la réponse réseau
        if (error.networkResponse != null) {
            statusCode = error.networkResponse.statusCode;
            errorMsg += " Statut: " + statusCode;
            try {
                responseBody = new String(error.networkResponse.data, "utf-8");
                try {

                    JSONObject data = new JSONObject(responseBody);
                    if (data.has("error")) {

                        errorMsg = "Erreur API (" + statusCode + "): " + data.getString("error");
                    } else if (data.has("message")) {

                        errorMsg = "Erreur API (" + statusCode + "): " + data.getString("message");
                    } else {

                        errorMsg += "\nRéponse JSON inattendue: " + responseBody.substring(0, Math.min(responseBody.length(), 100));
                    }
                } catch (JSONException jsonEx) {

                    if (stepDescription.contains("Étape 2") && statusCode == 404) {
                        errorMsg = "Erreur: Utilisateur non trouvé pour détails.";
                    } else {
                        // Affiche le début de la réponse brute (si pas JSON)
                        errorMsg += "\nRéponse serveur (non-JSON ou vide): " + responseBody.substring(0, Math.min(responseBody.length(), 100));
                        Log.e(TAG, "["+ stepDescription +"] Le corps de l'erreur Volley n'était pas du JSON valide ou était vide: '" + responseBody + "'");
                    }
                }
            } catch (Exception e) {
                // Erreur lors de la lecture du corps de la réponse d'erreur elle-même
                Log.e(TAG, "["+ stepDescription +"] Impossible de lire ou traiter le corps de l'erreur Volley", e);
            }
        }
        // Affiche le message d'erreur final à l'utilisateur
        Toast.makeText(connexion.this, errorMsg, Toast.LENGTH_LONG).show();
    }


    /**
     * Méthode appelée lorsque l'activité est sur le point d'être détruite.
     * C'est un bon endroit pour annuler les requêtes réseau en cours pour éviter
     * les fuites de mémoire ou les crashs si une réponse arrive après la destruction.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requeteAPI != null) {
            Log.d(TAG, "Activité détruite. Annulation des requêtes (si implémentée)...");
        }
    }
}