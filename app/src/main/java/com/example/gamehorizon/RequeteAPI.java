package com.example.gamehorizon;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class RequeteAPI {

    private RequestQueue requestQueue;
    private Context context;

    public RequeteAPI(Context context){
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
    }

    public interface RequeteJSONArrayCallback {
        void onSuccess(JSONArray response);
        void onError(VolleyError error);
    }
    public interface RequeteJSONObjectCallback {
        void onSuccess(JSONObject response);
        void onError(VolleyError error);
    }

    //Get pour plusieurs objet(liste)
    public void getJSONArray(String url, RequeteJSONArrayCallback callback) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }

    //Get pour juste un objet
    public void getJSONObject(String url, RequeteJSONObjectCallback callback) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response); // Appel du callback de succès
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error); // Appel du callback d'erreur
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    // Get pour plusieurs objets (liste) - utilisant aussi les classes anonymes
    public void postJSONObject(String url, JSONObject data, RequeteJSONObjectCallback callback) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, // Méthode POST
                url,
                data, // Les données JSON à envoyer
                // Utilisation de la classe anonyme interne pour Response.Listener
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response); // Appel du callback de succès
                    }
                },
                // Utilisation de la classe anonyme interne pour Response.ErrorListener
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (error.networkResponse != null) {
                            try {
                                String responseBody = new String(error.networkResponse.data, "utf-8");
                            } catch (Exception e) {
                            }
                        }
                        callback.onError(error); // Appel du callback d'erreur
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }
}