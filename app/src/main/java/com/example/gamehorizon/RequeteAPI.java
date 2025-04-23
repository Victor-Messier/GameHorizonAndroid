package com.example.gamehorizon;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class RequeteAPI {

    private RequestQueue requestQueue;
    private Context context;
    private static final String TAG = "RequêteAPI";

    public RequeteAPI(Context context){
        this.context = context.getApplicationContext(); // Utilisation de getApplicationContext
        requestQueue = Volley.newRequestQueue(this.context);
    }

    public interface RequeteJSONArrayCallback {
        void onSuccess(JSONArray response);
        void onError(VolleyError error);
    }
    public interface RequeteJSONObjectCallback {
        void onSuccess(JSONObject response);
        void onError(VolleyError error);
    }
    public interface RequeteStringCallback {
        void onSuccess(String response); // La réponse peut être une chaîne vide ou un message
        void onError(VolleyError error);
    }

    public interface SimpleCallback {
        void onSuccess(); // Peut-être onSuccess(String response) si le serveur renvoie qqc
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
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    // Get pour plusieurs objets (liste) - utilisant aussi les classes anonymes
    public void postJSONObject(String url, JSONObject data, RequeteJSONObjectCallback callback) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            try {
                                String responseBody = new String(error.networkResponse.data, "utf-8");
                                Log.e("RequeteAPI_ErrorBody", "postJSONObject Status: " + error.networkResponse.statusCode + " Body: " + responseBody);
                            } catch (Exception e) {
                                Log.e("RequeteAPI_ErrorBody", "postJSONObject Erreur lecture corps erreur Volley", e);
                            }
                        }
                        callback.onError(error);
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    public void getString(String url, RequeteStringCallback callback) {
        Log.d("RequeteAPI", "getString Request URL: " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("RequeteAPI", "getString Success. Response: '" + response + "'");
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("RequeteAPI", "getString Error for URL " + url + ": " + error.toString());
                        if (error.networkResponse != null) {
                            try {
                                String responseBody = new String(error.networkResponse.data, "utf-8");
                                Log.e("RequeteAPI_ErrorBody", "getString Status: " + error.networkResponse.statusCode + " Body: " + responseBody);
                            } catch (Exception e) {
                                Log.e("RequeteAPI_ErrorBody", "getString Erreur lecture corps erreur Volley", e);
                            }
                        }
                        callback.onError(error);
                    }
                });

        requestQueue.add(stringRequest);
    }

//    public void deleteRequestWithBody(String url, final JSONObject jsonBody, final SimpleCallback callback) {
//        Log.d(TAG, "DELETE request (using JsonDeleteWithBodyRequest) to: " + url + " with body: " + (jsonBody != null ? jsonBody.toString() : "null"));
//
//        JsonDeleteWithBodyRequest deleteRequest = new JsonDeleteWithBodyRequest(
//                url,
//                jsonBody,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.d(TAG, "DELETE Success. Response: " + response.toString());
//                        callback.onSuccess();
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e(TAG, "DELETE Error: " + error.toString(), error);
//                        if (error.networkResponse != null) {
//                            Log.e(TAG, "DELETE Error Status Code: " + error.networkResponse.statusCode);
//                            try {
//                                String body = new String(error.networkResponse.data, "UTF-8");
//                                Log.e(TAG, "DELETE Error Body: " + body);
//                            } catch (Exception e) {
//                                Log.e(TAG, "Error reading error response body", e);
//                            }
//                        }
//                        callback.onError(error);
//                    }
//                }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> headers = new HashMap<>();
//                headers.put("Accept", "*/*");
//                return headers;
//            }
//        };
//
//        requestQueue.add(deleteRequest);
//    }

    public void simulateDeleteViaPost(String url, final int idJeu, final int idUtilisateur, final SimpleCallback callback) {
        try {
            // --- Création du corps JSON ---
            final JSONObject jsonBody = new JSONObject();
            jsonBody.put("_method", "DELETE"); // Indique l'intention de suppression
            jsonBody.put("id_jeu", idJeu);
            jsonBody.put("id_utilisateur", idUtilisateur);

            final String requestBodyString = jsonBody.toString(); // Convertir en chaîne pour StringRequest
            Log.d(TAG, "Simulated DELETE (POST using StringRequest) to: " + url + " with body: " + requestBodyString);

            // --- Utilisation de StringRequest pour POST ---
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST, // <<< Méthode POST
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // La requête a réussi (code 2xx)
                            Log.d(TAG, "Simulated DELETE Success (StringRequest). Response String: '" + response + "'");
                            // La réponse peut être vide ou contenir un message texte/JSON.
                            // Peu importe ici, on appelle onSuccess car on a eu une réponse 2xx.
                            callback.onSuccess();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // La requête a échoué (erreur réseau ou code 4xx/5xx)
                            Log.e(TAG, "Simulated DELETE Error (StringRequest): " + error.toString(), error);
                            if (error.networkResponse != null) {
                                Log.e(TAG, "Error Status Code: " + error.networkResponse.statusCode);
                                try {
                                    String body = new String(error.networkResponse.data,"UTF-8");
                                    Log.e(TAG, "Error Body: " + body);
                                } catch (Exception e) {
                                    Log.e(TAG, "Error reading error response body", e);
                                }
                            }
                            callback.onError(error);
                        }
                    }) { // --- Début du bloc anonyme StringRequest ---

                // --- Fournir le corps JSON ---
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        // Utiliser la chaîne convertie plus haut
                        return requestBodyString == null ? null : requestBodyString.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        Log.e(TAG, "Unsupported Encoding", uee);
                        // Vous pourriez lancer une RuntimeException ou retourner null et gérer l'erreur
                        return null;
                    }
                }

                // --- Fournir les Headers (si nécessaire, correspondre à Postman) ---
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Accept", "*/*"); // Garder pour correspondre à Postman
                    // Ajoutez l'en-tête Authorization si jamais nécessaire
                    // headers.put("Authorization", "Bearer VOTRE_JETON");
                    Log.d(TAG, "Volley Headers (StringRequest POST) to be sent: " + headers.toString());
                    return headers;
                }

            }; // --- Fin du bloc anonyme StringRequest ---

            requestQueue.add(postRequest);

        } catch (JSONException e) { // Il faut attraper JSONException ici
            Log.e(TAG, "Error constructing JSON body for simulated DELETE", e);
            // Informer l'appelant de l'erreur de construction JSON
            callback.onError(new VolleyError("JSON Construction Error", e));
        }
    }

}

//class JsonDeleteWithBodyRequest extends Request<JSONObject> {
//
//    private final JSONObject jsonRequest;
//    private final Response.Listener<JSONObject> listener;
//    private static final String TAG = "RequêteAPI";
//
//    public JsonDeleteWithBodyRequest(
//            String url,
//            JSONObject jsonRequest,
//            Response.Listener<JSONObject> listener,
//            Response.ErrorListener errorListener
//    ) {
//        super(Method.DELETE, url, errorListener);
//        this.jsonRequest = jsonRequest;
//        this.listener = listener;
//    }
//
//    @Override
//    public byte[] getBody() throws AuthFailureError {
//        try {
//            if (jsonRequest != null) {
//                String body = jsonRequest.toString();
//                Log.d("JsonDeleteWithBodyRequest", "Body sent: " + body);
//                return body.getBytes("utf-8");
//            }
//            return null;
//        } catch (UnsupportedEncodingException uee) {
//            throw new RuntimeException("Encoding not supported: utf-8", uee);
//        }
//    }
//
//    @Override
//    public String getBodyContentType() {
//        return "application/json; charset=utf-8";
//    }
//
//    @Override
//    public Map<String, String> getHeaders() throws AuthFailureError {
//        return new HashMap<>(); // ou super.getHeaders()
//    }
//
//    @Override
//    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
//        try {
//            // Conversion de la réponse brute en chaîne de caractères
//            String responseString = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
//            Log.d(TAG, "Raw Response: " + responseString);
//
//            // Vérifie si la réponse contient un corps JSON valide
//            JSONObject result = new JSONObject(responseString);
//
//            // Si tu veux aussi vérifier le contenu de la réponse
//            boolean success = result.optBoolean("success", false);
//            String message = result.optString("message", "No message provided");
//            Log.d(TAG, "Success: " + success + ", Message: " + message);
//
//            return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
//        } catch (UnsupportedEncodingException | JSONException e) {
//            Log.e(TAG, "Error parsing response: ", e);
//            return Response.error(new ParseError(e));
//        }
//    }
//
//    @Override
//    protected void deliverResponse(JSONObject response) {
//        listener.onResponse(response);
//    }
//}
//
//class JsonPostDeleteRequest extends JsonObjectRequest {
//
//    public JsonPostDeleteRequest(
//            String url,
//            JSONObject jsonRequest,
//            Response.Listener<JSONObject> listener,
//            Response.ErrorListener errorListener
//    ) {
//        super(Method.POST, url, jsonRequest, listener, errorListener);
//    }
//
//    @Override
//    public String getBodyContentType() {
//        return "application/json; charset=utf-8";
//    }
//}