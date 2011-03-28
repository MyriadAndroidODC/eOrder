package com.androidodc.eorder.engine;

import android.os.Bundle;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import com.androidodc.eorder.utils.LogUtils;
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

public class RequestHelper {
    private static final int SUCCESS_STATUS = 200;
    private static final int FILE_END = -1;
    private static final int CONNECTION_TIME_OUT = 5000;
    private static final int SOCKET_TIME_OUT = 3000;
    private static final int FILE_BUFFER_SIZE = 1024;
    
    public static String doGet(String url, Bundle params) {
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
        HttpParams httpParameters = new BasicHttpParams(); 
        HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIME_OUT); 
        HttpConnectionParams.setSoTimeout(httpParameters, SOCKET_TIME_OUT); 
      
        try {
            HttpResponse httpResponse = new DefaultHttpClient(httpParameters).execute(httpRequest);
            
            if (httpResponse.getStatusLine().getStatusCode() == SUCCESS_STATUS) {  
                String strResult = EntityUtils.toString(httpResponse.getEntity());  
                return strResult;                 
            } else {
                LogUtils.logD("Http response error when do get operation!");
                return null;
            }
        } catch (Exception e) {
            LogUtils.logD(e.getMessage());
            return null;
        }
    }
        
    public static String doPost(String url, Bundle params) {
        HttpPost httpRequest = new HttpPost(url);  
        ArrayList<BasicNameValuePair> paramsList = new ArrayList<BasicNameValuePair>();
        BasicNameValuePair paramPair;
        
        if (params != null) {
            for (String key : params.keySet()) {
                paramPair = new BasicNameValuePair(key, params.getString(key));
                paramsList.add(paramPair);
            }
        }

        HttpParams httpParameters = new BasicHttpParams(); 
        HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIME_OUT); 
        HttpConnectionParams.setSoTimeout(httpParameters, SOCKET_TIME_OUT); 
        
        try { 
            httpRequest.setEntity(new UrlEncodedFormEntity(paramsList, HTTP.UTF_8));
            HttpResponse httpResponse = new DefaultHttpClient(httpParameters).execute(httpRequest); 
            
            if (httpResponse.getStatusLine().getStatusCode() == SUCCESS_STATUS) { 
                String strResult = EntityUtils.toString(httpResponse.getEntity());
                return strResult;
            } else {
                LogUtils.logD("Http response error when do post operation!");
                return null;
            }
        } catch (Exception e) {
            LogUtils.logD(e.getMessage());
            return null;
        } 
    }

    public static boolean getFileFromServer(String path, Bundle params, OutputStream fos) {
         byte[] bf = new byte[FILE_BUFFER_SIZE];
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
             connect.setConnectTimeout(CONNECTION_TIME_OUT);
             connect.setReadTimeout(SOCKET_TIME_OUT);
             int code = connect.getResponseCode();
             
             if (code == HttpURLConnection.HTTP_OK) {
                 InputStream is = connect.getInputStream();
                 BufferedInputStream bis = new BufferedInputStream(is);
                 while((current = bis.read(bf)) != FILE_END){
                     fos.write(bf, 0, current);
                 }
                 
                 bis.close();
                 fos.close();
                 connect.disconnect();
             }
             return true;
         } catch (MalformedURLException e) {
             LogUtils.logD(e.getMessage());
             return false;
         } catch (IOException e) {
             LogUtils.logD(e.getMessage());
             return false;
         }
    }
}
