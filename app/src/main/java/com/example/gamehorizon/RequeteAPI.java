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
        this.context = context.getApplicationContext();
        requestQueue = Volley.newRequestQueue(this.context);
    }

    //Toutes les interfaces nécessaires
    public interface RequeteJSONArrayCallback {
        void onSuccess(JSONArray response);
        void onError(VolleyError error);
    }
    public interface RequeteJSONObjectCallback {
        void onSuccess(JSONObject response);
        void onError(VolleyError error);
    }
    public interface RequeteStringCallback {
        void onSuccess(String response);
        void onError(VolleyError error);
    }

    public interface SimpleCallback {
        void onSuccess();
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

    //Get pour plusieurs objets (liste) - utilisant aussi les classes anonymes
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
}