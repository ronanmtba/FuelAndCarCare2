package com.dornier.fuelcarcare;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deneb on 15/11/2017.
 */

public class ModelNetworkManager {

    private static final String TAG = ModelNetworkManager.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private Boolean sessionStart;
    private static ModelNetworkManager singleton;

    private ModelNetworkManager(){
        CookieManager cm = new CookieManager();
        CookieHandler.setDefault(cm);
    }

    public static ModelNetworkManager getInstance(){
        if(singleton == null){
            singleton = new ModelNetworkManager();
        }
        return singleton;
    }

    public void requestToServer(final String page, final JSONObject array, ReceiveFromServer o,
                                final String identifier) {
        final ReceiveFromServer requestOwner = o;

        Log.v("identifier", " => " +array);

        StringRequest sr = new StringRequest(Request.Method.POST, "http://192.168.95.128/fcc/" + page,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v(TAG,page+" => "+response);
                requestOwner.serverCall(response, identifier);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                requestOwner.serverCall(null, identifier);
                sessionStart = false;
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                for(int i = 0; i<array.names().length(); i++){
                    try {
                        params.put(array.names().getString(i), array.getString(array.names().getString(i)));
                    }
                    catch (Exception e){
                        ModelDataManager.printErrorToConsole(e);
                    }
                }
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("Content-Length",String.valueOf(getBody().length));
                return params;
            }

        };

        sr.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        sr.setShouldCache(true);

        getInstance().addToRequestQueue(sr);
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(ModelDataManager.getInstance().getActualContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

}
