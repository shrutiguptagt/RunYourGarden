package edu.gatech.garden.communicator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.os.Bundle;

public class Communicator {
    
    // Login
    public final static String LOGIN_URL = "http://ihubit.appspot.com/login";
    
    // Workout session
    public final static String SESSION_URL = "http://ihubit.appspot.com/session";
    
    public static String communicate(Bundle params, String urlServer) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(urlServer); 
        HttpResponse response;
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            for (String key : params.keySet()) {
                nameValuePairs.add(new BasicNameValuePair(key, params.getString(key)));
            }            
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            
            if (entity != null) {    
                String resp = EntityUtils.toString(entity);
                return resp;
            }
        } catch (Exception e) {
        }
        return null;
    }
}
