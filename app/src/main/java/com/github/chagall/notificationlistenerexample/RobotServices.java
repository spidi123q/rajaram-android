package com.github.chagall.notificationlistenerexample;

import android.content.Context;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Network;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by suraj on 10/1/17.
 */

public class RobotServices {

    private static RequestQueue queue;
    private static String robotUrl = "http://92269bc0.ngrok.io";
    public RobotServices(Context appContext) {
        queue = Volley.newRequestQueue(appContext);
    }

    public void setRobotUrl(String url){
        robotUrl = url;
    }
    public void postNotification(StatusBarNotification sbn){
        final String TAG = "postNotification";
        Map<String, String> params = new HashMap<String, String>();
        params.put("tickerText", sbn.getNotification().tickerText.toString());
        // Request a string response from the provided URL.
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, robotUrl + "/appNotification",new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Display the first 500 characters of the response string.
                        Log.d(TAG, "onResponse: " + response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: " + error);
            }


        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

}
