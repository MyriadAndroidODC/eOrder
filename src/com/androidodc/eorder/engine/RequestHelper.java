package com.androidodc.eorder.engine;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.os.Bundle;
import android.os.Environment;

public class RequestHelper {
	public String doPost(String url, Bundle params) throws Exception {
        HttpPost httpRequest = new HttpPost(url);  
        List paramsList = new ArrayList();
        BasicNameValuePair paramPair;
        
        for (String key : params.keySet()) {
            paramPair = new BasicNameValuePair(key, params.getString(key));
            paramsList.add(paramPair);
        }
        
        try { 
            httpRequest.setEntity(new UrlEncodedFormEntity(paramsList, HTTP.UTF_8));
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest); 
            
            if (httpResponse.getStatusLine().getStatusCode() == 200) { 
                String strResult = EntityUtils.toString(httpResponse.getEntity());
                return strResult;
            } else {      
                return "Http response access status error!";
            }   
        } catch (Exception e) { 
            throw e;
        } 
    }
    public String doGet(String url, Bundle params) throws Exception {
        if (params != null) {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            
            for (String key : params.keySet()) {
                
                if (first) {
                    first = false; 
                } else {
                    sb.append("&");
                }
                
                sb.append(URLEncoder.encode(key) + "=" +
                          URLEncoder.encode(params.getString(key)));
            }
            url = url + "?" + sb.toString();
        }

        HttpGet httpRequest = new HttpGet(url);
        try {
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
            
            if (httpResponse.getStatusLine().getStatusCode() == 200) {  
                String strResult = EntityUtils.toString(httpResponse.getEntity());  
                return strResult;                 
            } else {   
                return "Http response access status error!";   
            }
        } catch (Exception e) {
            throw e;
        }
    }
    public boolean getFileFromServer(String path, Bundle params, OutputStream fos) {
         byte[] bf = new byte[1024];
         int current = 0;
         
         try {
             if (params != null) {
                 StringBuilder sb = new StringBuilder();
                 boolean first = true;
                 
                 for (String key : params.keySet()) {
                     if (first){
                         first = false; 
                     } else {
                         sb.append("&");
                     }
                     
                     sb.append(URLEncoder.encode(key) + "=" +
                               URLEncoder.encode(params.getString(key)));
                 }
                 path = path + "?" + sb.toString();
             }
             
             URL url = new URL(path);
             HttpURLConnection connect = (HttpURLConnection)url.openConnection();
             connect.setDoInput(true);
             connect.setConnectTimeout(5 * 1000);
             connect.setReadTimeout(30 * 1000);
             int code = connect.getResponseCode();
             
             if (code == HttpURLConnection.HTTP_OK) {
                 InputStream is = connect.getInputStream();
                 BufferedInputStream bis = new BufferedInputStream(is);
                 while((current = bis.read(bf)) != -1){
                     fos.write(bf, 0, current);
                 }
                 
                 bis.close();
                 fos.close();
                 connect.disconnect();
             }
             return true;
         } catch (MalformedURLException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
             return false;
         } catch (IOException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
             return false;
         }
    }
}
