package org.fossasia.fossasiaorgaandroidapp.Api;

import android.app.DownloadManager;
import android.app.VoiceInteractor;
import android.content.Context;
import android.provider.SyncStateContract;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.fossasia.fossasiaorgaandroidapp.Interfaces.VolleyCallBack;
import org.fossasia.fossasiaorgaandroidapp.Utils.Constants;
import org.fossasia.fossasiaorgaandroidapp.Utils.Network;
import org.fossasia.fossasiaorgaandroidapp.model.LoginDetails;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.ContentHandler;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rishabhkhanna on 25/04/17.
 */

public class LoginCall {

    public static final String TAG = "LoginCall";

    public static void login(final Context context, LoginDetails details, final VolleyCallBack callBack) {
        RequestQueue queue = Volley.newRequestQueue(context);

        Gson gson = new Gson();
        String loginDetailString = gson.toJson(details);
        JSONObject loginDetailJson = null;
        try {
            loginDetailJson = new JSONObject(loginDetailString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.LoginUrl, loginDetailJson, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                
                                Log.d(TAG, "onResponse: " + response);
                                String token = "token";
                                try {
                                    token = response.getString("access_token");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                callBack.onSuccess(token);

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "onErrorResponse: ");

                                callBack.onError(error);
                            }
                        });

        if(loginDetailJson != null){
            queue.add(jsonObjectRequest);
        }

    }
}
