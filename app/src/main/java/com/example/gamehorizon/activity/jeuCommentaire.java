package com.example.gamehorizon.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.example.gamehorizon.Adapter.CommentaireAdapteur;
import com.example.gamehorizon.entite.Commentaire;
import com.example.gamehorizon.Adapter.JeuxAdapter;
import com.example.gamehorizon.R;
import com.example.gamehorizon.RequeteAPI;
import com.example.gamehorizon.entite.Jeu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class jeuCommentaire extends AppCompatActivity implements View.OnClickListener, CommentaireAdapteur.OnCommentaireActionListener {

    View header, footer;
    RecyclerView items;
    TextView headerText,textNomJeu,textDescription,textDateSortie,textPlateforme,textGenre,textNote;
    ImageView imageJeu;
    private static final String TAG = "jeuCommentaire";
    private int idJeu;
    private CommentaireAdapteur commentaireAdapteur;
    RequeteAPI requeteAPI;
    private List<Commentaire> listeCom = new ArrayList<>();
    private int idUtilisateur;
    private String nomUtilisateur;

    private EditText nouveauCom;
    private Button btnEnvoyerCom;

    ImageView page_accueil, page_connexion, page_principal, page_ajoutJeu, page_jeu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jeu);

        textNomJeu = findViewById(R.id.titreJeu);
        textDescription = findViewById(R.id.description2);
        textDescription.setMovementMethod(new ScrollingMovementMethod());
        textDateSortie = findViewById(R.id.date_sortie2);
        textPlateforme = findViewById(R.id.nomPlateforme);
        textGenre = findViewById(R.id.studio2);
        textNote = findViewById(R.id.noteJeuValeur);
        imageJeu = findViewById(R.id.imageJeu);


        header = findViewById(R.id.header_jeu);
        footer = findViewById(R.id.footerJeu);
        footer.bringToFront();

        headerText = header.findViewById(R.id.titre_page);
        headerText.setText(getString(R.string.Jeu));

        //Récupéation des intents
        Intent intent = getIntent();
        idJeu = intent.getIntExtra("ID_JEU",1);
        idUtilisateur = intent.getIntExtra("ID_UTILISATEUR", 1);
        nomUtilisateur = intent.getStringExtra("NAME_UTILISATEUR");
        Log.d(TAG,"L'id du jeu choisi est: " + idJeu);
        Log.d(TAG,"L'id du user choisi est: " + idUtilisateur);
        Log.d(TAG,"Le nom du user choisi est: " + nomUtilisateur);

        items = findViewById(R.id.recyclerViewCom);
        items.setLayoutManager(new LinearLayoutManager(this));
        commentaireAdapteur = new CommentaireAdapteur(listeCom,idUtilisateur, this);
        items.setAdapter(commentaireAdapteur);

        nouveauCom = findViewById(R.id.ecrire_commentaire);
        btnEnvoyerCom = findViewById(R.id.bouton_ajouter_com);

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
        btnEnvoyerCom.setOnClickListener(this);

        requeteAPI = new RequeteAPI(this);

        //Remplir les infos
        remplirInfo();
        remplirCommentaire();
    }

    //Gère les clicks
    @Override
    public void onClick(View v) {
        if (v == page_accueil){
            Intent intent = new Intent(jeuCommentaire.this, MainActivity.class);
            startActivity(intent);
        }
        else if (v == page_connexion){
            Intent intent = new Intent(jeuCommentaire.this, connexion.class);
            startActivity(intent);
        }
        else if (v == page_principal){
            Intent intent = new Intent(jeuCommentaire.this, Recommandation.class);
            intent.putExtra("ID_UTILISATEUR", idUtilisateur);
            intent.putExtra("NAME_UTILISATEUR", nomUtilisateur);
            startActivity(intent);
        }
        else if (v == page_ajoutJeu){
            Intent intent = new Intent(jeuCommentaire.this, ajoutJeux.class);
            intent.putExtra("ID_UTILISATEUR", idUtilisateur);
            intent.putExtra("NAME_UTILISATEUR", nomUtilisateur);
            startActivity(intent);
        }
        else if (v == page_jeu){
            Intent intent = new Intent(jeuCommentaire.this, Recherche.class);
            intent.putExtra("ID_UTILISATEUR", idUtilisateur);
            intent.putExtra("NAME_UTILISATEUR", nomUtilisateur);
            startActivity(intent);
        } else if(v == btnEnvoyerCom){
            boolean aDejaCommente = false;
            for (Commentaire commentaireExistant : listeCom) {
                if (commentaireExistant.getIdUser() == idUtilisateur) {
                    aDejaCommente = true;
                    break;
                }
            }

            if (aDejaCommente) {
                Toast.makeText(this, "Vous avez déjà laissé un commentaire pour ce jeu.", Toast.LENGTH_LONG).show();
            } else {
                String contenuNouveauCom = nouveauCom.getText().toString().trim();

                if (contenuNouveauCom.isEmpty()) {
                    Toast.makeText(this, "Le commentaire ne peut pas être vide.", Toast.LENGTH_SHORT).show();
                } else {
                    envoyerNouveauCommentaire(contenuNouveauCom);
                }
            }
        }
    }

    //Remplir les infos d'un jeu mais pas les commentaires
    public void remplirInfo(){
        String urlRecupJeu = "https://equipe100.tch099.ovh/api/jeux/";
        StringBuilder urlBuilder = new StringBuilder(urlRecupJeu);
        urlBuilder.append(idJeu);
        Log.d(TAG,"urlPourJeu: " + urlBuilder.toString());

        requeteAPI.getJSONObject(urlBuilder.toString(),new RequeteAPI.RequeteJSONObjectCallback(){
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    String nomJeu = response.getString("jeu_name");
                    String descriptionJeu = response.getString("jeu_description");
                    String dateSortieJeu = response.getString("jeu_release_date");
                    String genreJeu = response.getString("genres");
                    String platformJeu = response.getString("plateformes");
                    Double noteJeu = response.getDouble("jeu_moyenne_note");
                    String imageJeuURL = response.getString("jeu_cover_url");

                    textNomJeu.setText(nomJeu);
                    textDescription.setText(descriptionJeu);
                    textDateSortie.setText(dateSortieJeu);
                    textGenre.setText(genreJeu);
                    textPlateforme.setText(platformJeu);
                    textNote.setText(noteJeu.toString());
                    Glide.with(jeuCommentaire.this)
                            .load(imageJeuURL)
                            .into(imageJeu); // <- Utiliser la variable membre ImageView
                } catch (JSONException e) {
                    Log.e(TAG, "Erreur lors du parsing JSON", e);
                    Toast.makeText(jeuCommentaire.this, "Erreur lors du traitement des données du jeu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Erreur lors de la requête API", error);
                Toast.makeText(jeuCommentaire.this, "Erreur de connexion au serveur lors de la récupération du jeu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Remplir les commentaires d'un jeu
    public void remplirCommentaire(){
        String urlRecupCommentaire = "https://equipe100.tch099.ovh/api/jeux/commentaire/";
        StringBuilder urlBuilder = new StringBuilder(urlRecupCommentaire);
        urlBuilder.append(idJeu);
        urlBuilder.append("?id_utilisateur=");
        urlBuilder.append(idUtilisateur);
        Log.d(TAG, "URL Commentaires: " + urlBuilder.toString());

        requeteAPI.getJSONArray(urlBuilder.toString(), new RequeteAPI.RequeteJSONArrayCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                Log.d(TAG, "Commentaires reçus (onSuccess): " + response.toString());
                List<Commentaire> nouveauxCommentaires = new ArrayList<>();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject commentaireObject = response.getJSONObject(i);
                        int idUser = commentaireObject.getInt("id_utilisateur");
                        String username = commentaireObject.getString("username");
                        String lastMaj = commentaireObject.getString("last_maj");
                        String contenu = commentaireObject.getString("contenu");

                        Commentaire Com = new Commentaire(idUser,username,lastMaj,contenu);
                        nouveauxCommentaires.add(Com);
                    }
                    listeCom.clear();
                    listeCom.addAll(nouveauxCommentaires);
                    commentaireAdapteur.notifyDataSetChanged();

                } catch (JSONException e) {
                    Log.e(TAG, "Erreur lors du parsing JSON des commentaires", e);
                    Toast.makeText(jeuCommentaire.this, "Erreur lors du traitement des données des commentaires reçus.", Toast.LENGTH_SHORT).show();
                    listeCom.clear();
                    commentaireAdapteur.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(VolleyError error) {
                if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                    Log.d(TAG, "Erreur 404 reçue pour les commentaires - Traité comme liste vide.");
                    listeCom.clear();
                    commentaireAdapteur.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "Erreur lors de la requête API des commentaires: " + error.toString());
                    if (error.networkResponse != null) {
                        Log.e(TAG, "Status Code Erreur Commentaires: " + error.networkResponse.statusCode);
                        try {
                            String body = new String(error.networkResponse.data,"UTF-8");
                            Log.e(TAG, "Corps Erreur Commentaires: " + body);
                        } catch (Exception e) { /* Ignorer */ }
                    }
                    Toast.makeText(jeuCommentaire.this, "Erreur serveur lors de la récupération des commentaires.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Quand on click sur le bouton pour supprimer un commentaire
    @Override
    public void onDeleteClick(final int position) {
        Log.d(TAG, "Demande de suppression pour la position : " + position);

        if (idUtilisateur == -1) {
            Toast.makeText(this, "Vous devez être connecté pour supprimer.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (position < 0 || position >= listeCom.size()) {
            Log.e(TAG, "Position invalide pour suppression : " + position);
            return;
        }

        final Commentaire commentaireASupprimer = listeCom.get(position);

        //Regarder que le user qui est connecter veut supprimer son commentaire et pas celui des autres
        if (commentaireASupprimer.getIdUser() != this.idUtilisateur) {
            Log.w(TAG, "Tentative de suppression non autorisée (client) par user " + this.idUtilisateur + " sur commentaire de user " + commentaireASupprimer.getIdUser());
            Toast.makeText(this, "Vous ne pouvez supprimer que vos propres commentaires.", Toast.LENGTH_SHORT).show();
            return;
        }

        //Confirmation pour supprimer un commentaire
        new AlertDialog.Builder(this)
                .setTitle("Confirmer Suppression")
                .setMessage("Êtes-vous sûr de vouloir supprimer ce commentaire ? Cette action est irréversible.")
                .setPositiveButton("Oui, Supprimer", (dialog, which) -> {
                    JSONObject requestBody = new JSONObject();
                    try {
                        requestBody.put("id_jeu", idJeu);
                        requestBody.put("id_utilisateur", idUtilisateur);
                        Log.d(TAG, "Corps JSON pour la suppression: " + requestBody.toString());
                    } catch (JSONException e) {
                        Log.e(TAG, "Erreur lors de la création du corps JSON pour la suppression", e);
                        Toast.makeText(jeuCommentaire.this, "Erreur interne (création JSON)", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String urlDelete = "https://equipe100.tch099.ovh/api/commentaires/delete";

                    Log.d(TAG, "Appel API DELETE vers: " + urlDelete);

                    requeteAPI.postJSONObject(urlDelete, requestBody, new RequeteAPI.RequeteJSONObjectCallback() {

                        @Override
                        public void onSuccess(JSONObject response) {
                            Log.d(TAG, "PUTAIN DE DELETE RÉUSSI");
                            Log.d(TAG, "Suppression API réussie. Réponse JSON: " + response.toString());
                            try {
                                boolean success = response.optBoolean("success", false); // Utiliser optBoolean pour éviter crash si absent
                                String message = response.optString("message", "Suppression traitée.");

                                if (success) {
                                    Toast.makeText(jeuCommentaire.this, "Commentaire supprimé !", Toast.LENGTH_SHORT).show();

                                    if (position >= 0 && position < listeCom.size()) {
                                        listeCom.remove(position);
                                        commentaireAdapteur.notifyItemRemoved(position);
                                        commentaireAdapteur.notifyItemRangeChanged(position, listeCom.size());
                                        Log.d(TAG,"RecyclerView mis à jour après suppression à la position: " + position);
                                    } else {
                                        Log.w(TAG, "Position invalide (" + position + ") après retour succès API. Taille liste: " + listeCom.size() + ". Rafraîchissement complet.");
                                        remplirCommentaire();
                                    }

                                } else {
                                    Log.w(TAG, "L'API a indiqué un échec logique pour la suppression: " + message);
                                    Toast.makeText(jeuCommentaire.this, "Échec de la suppression: " + message, Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Erreur lors du traitement de la réponse succès API", e);
                                Toast.makeText(jeuCommentaire.this, "Réponse inattendue du serveur après suppression.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(VolleyError error) {
                            Log.e(TAG,"Ça pas marcher erreur: " + error);
                        }
                    });
                })
                .setNegativeButton("Annuler", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    //Pour ajouter un nouveau commentaire à un jeu
    private void envoyerNouveauCommentaire(String contenu) {
        String urlAjoutCommentaire = "https://equipe100.tch099.ovh/api/ajoutCommentaire";

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("id_jeu", idJeu);
            requestBody.put("id_utilisateur", idUtilisateur);
            requestBody.put("contenu", contenu);

            Log.d(TAG, "Préparation de l'envoi du commentaire: " + requestBody.toString());

        } catch (JSONException e) {
            Log.e(TAG, "Erreur lors de la création du JSON pour l'ajout de commentaire", e);
            Toast.makeText(this, "Erreur interne lors de la préparation du commentaire.", Toast.LENGTH_SHORT).show();
            return;
        }

        requeteAPI.postJSONObject(urlAjoutCommentaire, requestBody, new RequeteAPI.RequeteJSONObjectCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.d(TAG, "Commentaire ajouté avec succès. Réponse API: " + response.toString());
                Toast.makeText(jeuCommentaire.this, "Commentaire ajouté !", Toast.LENGTH_SHORT).show();

                nouveauCom.setText("");

                remplirCommentaire();
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Erreur lors de l'envoi du commentaire à l'API", error);
                if (error.networkResponse != null) {
                    Log.e(TAG, "Status Code Erreur Ajout: " + error.networkResponse.statusCode);
                    try {
                        String responseBody = new String(error.networkResponse.data, "utf-8");
                        Log.e(TAG, "Corps Erreur Ajout: " + responseBody);
                        Toast.makeText(jeuCommentaire.this, "Erreur serveur lors de l'ajout du commentaire.", Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        Toast.makeText(jeuCommentaire.this, "Erreur serveur lors de l'ajout du commentaire.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(jeuCommentaire.this, "Erreur réseau lors de l'ajout du commentaire.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}