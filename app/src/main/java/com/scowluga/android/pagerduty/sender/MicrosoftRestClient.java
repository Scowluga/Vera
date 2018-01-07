package com.scowluga.android.pagerduty.sender;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.utils.URIBuilder;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by DavidLu on 2017-09-17.
 */

public class MicrosoftRestClient {
    public static final String BASE_URL = "https://westcentralus.api.cognitive.microsoft.com/vision/v1.0";

    public static final String key = "dc5e58e14755414f9e923727fdc6fa13";

    public static class MicrosoftAsyncTask extends AsyncTask<String, String, String> {

        public static final String uriBase = "https://westcentralus.api.cognitive.microsoft.com/vision/v1.0/analyze";

        Bitmap bitmap;
        Context context;


        public MicrosoftAsyncTask(Bitmap bitmap, Context context) {
            this.bitmap = bitmap;
            this.context = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpClient httpClient = new DefaultHttpClient();

            try
            {
                URIBuilder uriBuilder = new URIBuilder(uriBase);

                // Request parameters.
                // To use the Celebrities model, change "landmarks" to "celebrities" here and in uriBase.
                uriBuilder.setParameter("visualFeatures", "Categories, Description");

                // Prepare the URI for the REST API call.
                URI uri = uriBuilder.build();
                HttpPost request = new HttpPost(uri);

                // Request headers.
                request.setHeader("Content-Type", "application/json");
                request.setHeader("Ocp-Apim-Subscription-Key", key);

                // Request body.

                SharedPreferences preferences = context.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
                int temp = preferences.getInt("microsoft", 1);
                preferences.edit().putInt("microsoft", temp + 1).apply();

                Cloudinary cloudinary = new Cloudinary();

                // Storing bitmap to file
                File file = new File(context.getFilesDir(), "microsoft" + temp + ".jpg");
                OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.close();

                // Getting URL from Cloudinary
                Map uploadResult = cloudinary.uploader().unsignedUpload(file, "usynuxhv", ObjectUtils.asMap("cloud_name", "dg8u5kc5f"));

                String imageUrl = (String)uploadResult.get("url");

                StringEntity requestEntity = new StringEntity("{\"url\":\"" + imageUrl + "\"}");
                request.setEntity(requestEntity);

                // Execute the REST API call and get the response entity.
                HttpResponse response = httpClient.execute(request);
                HttpEntity entity = response.getEntity();

                Set<String> info = new HashSet<>();
                info.add("people");
                info.add("person");
                info.add("pedestrian");

                if (entity != null) {
                    // Format and display the JSON response.
                    String jsonString = EntityUtils.toString(entity);
                    JSONObject json = new JSONObject(jsonString);

                    JSONObject description = json.getJSONObject("description");
                    JSONArray array = description.getJSONArray("tags");

                    for (int i = 0; i < (array.length() > 5 ? 5 : array.length()); i ++) {
                        String obj = array.get(i).toString();
                        if (info.contains(obj)) {
                            sendToServer(imageUrl, context);
                            break;
                        }
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String s) {

            int x = 1;

            // after sending it, perhaps
            // MainActivity.takingPicture = true to resume

        }

        private void sendToServer(String imageUrl, Context context) {
            HttpClient httpClient = new DefaultHttpClient();
            try {
                URIBuilder uriBuilder = new URIBuilder("https://htn.apiserver.link/api/v1/pagerduty/incident");

                // Request parameters.
                // To use the Celebrities model, change "landmarks" to "celebrities" here and in uriBase.
//                uriBuilder.setParameter("visualFeatures", "Categories, Description");

                // Prepare the URI for the REST API call.
                URI uri = uriBuilder.build();
                HttpPost request = new HttpPost(uri);

                List<NameValuePair> params = new ArrayList<>();

                String param1 = "title=test";
                String param2 = "image=test2";
                String encodeData = URLEncoder.encode(param1 + param2, "UTF-8");

                // Request headers.
                request.setHeader("Content-Type", "application/json; charset=utf-8");


                String title = "Someone's at your door";

                JSONObject object = new JSONObject();
                object.put("title", title);
                object.put("image", imageUrl);
                String data = object.toString();


                StringEntity requestEntity = new StringEntity(data);
                request.setEntity(requestEntity);

                // Execute the REST API call and get the response entity.
                HttpResponse response = httpClient.execute(request);
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    // Format and display the JSON response.
                    String jsonString = EntityUtils.toString(entity);
                    JSONObject json = new JSONObject(jsonString);


                }
            } catch (Exception e) {
                e.printStackTrace();
            }


//            OutputStream out = null;
//            try {
//                // Connect
//                URL url = new URL("https://htn.apiserver.link/api/v1/pagerduty/incident");
//
//                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("POST");
//                urlConnection.setDoOutput(true);
//                urlConnection.setDoInput(true);
//
//                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
//
////                final String basicAuth = "Barriers " + Base64.encodeToString("htn:htn".getBytes(), Base64.NO_WRAP);
////
////                urlConnection.setRequestProperty("Authorization", basicAuth);
//
//                urlConnection.connect();
//
//                String title = "Someone's at your door";
//
//                // Creating data string
//                String data = null;
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//                    data = "{\"title\":\"" + title + "\", \"image\":\"" + imageUrl + "\"}";
//                }
//
//                // Writing
//                out = new BufferedOutputStream(urlConnection.getOutputStream());
//                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
//                writer.write(data);
//                writer.flush();
//                writer.close();
//                out.close();
//
//
//                // Read
//                String response = "";
//                String line;
//
//                BufferedReader br=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//                while ((line=br.readLine()) != null) {
//                    response+=line;
//                }
//                int x = 0;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

        }
    }


}
