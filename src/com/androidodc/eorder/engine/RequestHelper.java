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
    private static final int SUCCESS_STATUS = 200; //HTTP request successful status
    private static final int FILE_END = -1; //Read the EOF file character. It represents the end of the file.
    private static final int CONNECTION_TIME_OUT = 5000; //It can not connect server exceed 5000 millisecond
    private static final int SOCKET_TIME_OUT = 3000; //It can not make a socket connection exceed 3000 millisecond
    private static final int FILE_BUFFER_SIZE = 1024; //Define the storage size about operating file input stream.

    public static String doRequestGet(String url, Bundle params) {
        if (params != null) {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (String key : params.keySet()) {
                if (first) {
                    first = false;
                } else {
                    sb.append("&");
                }
                sb.append(URLEncoder.encode(key) + "=" + URLEncoder.encode(params.getString(key)));
            }
            url = url + "?" + sb.toString();
        }

        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIME_OUT);
        HttpConnectionParams.setSoTimeout(httpParameters, SOCKET_TIME_OUT);

        try {
            HttpGet httpRequest = new HttpGet(url);
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

    public static String doRequestPost(String url, Bundle params) {
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
            HttpPost httpRequest = new HttpPost(url);
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
         InputStream is = null;
         HttpURLConnection connect = null;
         BufferedInputStream bis = null;

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
                     sb.append(URLEncoder.encode(key) + "=" + URLEncoder.encode(params.getString(key)));
                 }
                 path = path + "?" + sb.toString();
             }

             URL url = new URL(path);
             connect = (HttpURLConnection)url.openConnection();
             connect.setDoInput(true);
             connect.setConnectTimeout(CONNECTION_TIME_OUT);
             connect.setReadTimeout(SOCKET_TIME_OUT);
             int code = connect.getResponseCode();

             if (code == HttpURLConnection.HTTP_OK) {
                 is = connect.getInputStream();
                 bis = new BufferedInputStream(is);
                 while((current = bis.read(bf)) != FILE_END){
                     fos.write(bf, 0, current);
                 }
             }
             return true;
         } catch (MalformedURLException e) {
             LogUtils.logD(e.getMessage());
             return false;
         } catch (IOException e) {
             LogUtils.logD(e.getMessage());
             return false;
         } catch (Exception e) {
             LogUtils.logD(e.getMessage());
             return false;
         }finally {
             try{
                 if (is != null) is.close();
                 if (fos != null) fos.close();
                 if (connect != null) connect.disconnect();
                 if (bis != null) bis.close();
             } catch (Exception e){
                 LogUtils.logD(e.getMessage());
             }
         }
    }
}
