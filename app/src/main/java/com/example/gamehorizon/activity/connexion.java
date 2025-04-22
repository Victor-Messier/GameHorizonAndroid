package com.example.gamehorizon.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // Importer Log
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast; // Importer Toast

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError; // Importer VolleyError
import com.example.gamehorizon.R;
import com.example.gamehorizon.RequeteAPI; // Importer RequeteAPI

import org.json.JSONException; // Importer JSONException
import org.json.JSONObject; // Importer JSONObject

public class connexion extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ConnexionActivity";

    public static final String EXTRA_USER_ID = "com.example.gamehorizon.USER_ID";

    View header, footer;
    TextView headerText;
    ImageView page_accueil, page_connexion, page_principal, page_ajoutJeu, page_jeu;

    EditText champ_email, champ_mdp;
    Button btnconnexion;

    RequeteAPI requeteAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connexion);

        requeteAPI = new RequeteAPI(this);

        // Header/Footer
        header = findViewById(R.id.header_connexion);
        footer = findViewById(R.id.footerConnexion);
        headerText = header.findViewById(R.id.titre_page);
        headerText.setText(getString(R.string.connexion));

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

        champ_email = findViewById(R.id.champ_email);
        champ_mdp = findViewById(R.id.champ_mdp);
        btnconnexion = findViewById(R.id.bouton_connexion);

        //Set Click Listener pour le bouton de connexion
        btnconnexion.setOnClickListener(this);
    }

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
            Intent intent = new Intent(connexion.this, Recommandation.class);
            startActivity(intent);
        } else if (viewId == R.id.icone_page_ajoutJeux) {
            Intent intent = new Intent(connexion.this, ajoutJeux.class);
            startActivity(intent);
        } else if (viewId == R.id.icone_page_recherche) {
            Intent intent = new Intent(connexion.this, Recherche.class);
            startActivity(intent);
        }
        // Logique de Connexion
        else if (btnconnexion != null && viewId == btnconnexion.getId()) {

            String email = champ_email.getText().toString().trim();
            String mdp = champ_mdp.getText().toString().trim();

            if (email.isEmpty() || mdp.isEmpty()) {
                Toast.makeText(connexion.this, "Veuillez entrer votre email et votre mot de passe.", Toast.LENGTH_LONG).show();
                return;
            }

            //
            JSONObject loginData = new JSONObject();
            try {
                loginData.put("email", email);
                loginData.put("mdp", mdp);

                String urlLogin = "https://equipe100.tch099.ovh/login.php";

                requeteAPI.postJSONObject(urlLogin, loginData, new RequeteAPI.RequeteJSONObjectCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        Log.d(TAG, "Réponse API connexion: " + response.toString());
                        try {
                            if (response.has("success") && response.getBoolean("success")) {

                                int userId = response.optInt("user_id", -1);

                                // Vérifier si on a bien reçu un ID valide
                                if (userId != -1) {
                                    String message = response.optString("message", "Connexion réussie !");
                                    Toast.makeText(connexion.this, message, Toast.LENGTH_LONG).show();

                                    champ_email.setText("");
                                    champ_mdp.setText("");

                                    // Lancement sur la page recommandation
                                    Intent intent = new Intent(connexion.this, Recommandation.class);
                                    // Ajouter l'ID comme extra
                                    intent.putExtra("ID_utilisateur", userId);
                                    Log.d(TAG, "Envoi de l'ID utilisateur: " + userId + " à RecommandationActivity");

                                    startActivity(intent);
                                    finish();

                                } else {
                                    // L'API a renvoyé success=true mais pas d'user_id valide
                                    Log.e(TAG, "Connexion réussie mais user_id manquant ou invalide dans la réponse API.");
                                    Toast.makeText(connexion.this, "Erreur: Données utilisateur manquantes après connexion.", Toast.LENGTH_LONG).show();
                                }

                            } else {
                                // L'API a renvoyé success=false ou une autre erreur
                                String errorMsg = response.optString("error", "Email ou mot de passe incorrect.");
                                Toast.makeText(connexion.this, "Échec Connexion: " + errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Erreur parsing réponse JSON succès connexion", e);
                            Toast.makeText(connexion.this, "Erreur de lecture de la réponse serveur", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {

                        Log.e(TAG, "Erreur Volley lors de la connexion: " + error.toString());
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
                                    errorMsg += "\nRéponse serveur (non-JSON ou vide): " + responseBody.substring(0, Math.min(responseBody.length(), 100));
                                    Log.e(TAG, "Le corps de l'erreur Volley n'était pas du JSON valide ou était vide: '" + responseBody + "'");
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Impossible de lire ou traiter le corps de l'erreur Volley", e);
                            }
                        }
                        Toast.makeText(connexion.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                });

            } catch (JSONException e) {
                Log.e(TAG, "Erreur création JSON pour connexion", e);
                Toast.makeText(connexion.this, "Erreur interne lors de la préparation des données", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requeteAPI != null) {
            Log.d(TAG, "Tentative d'annulation des requêtes pour ConnexionActivity");

        }
    }
}