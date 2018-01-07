package com.scowluga.android.pagerduty.discontinued;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;

import com.cloudinary.Url;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by DavidLu on 2017-09-16.
 */

public class ImageAsync extends AsyncTask<String, String, String> {

    public static final String GOOGLE_URL = "https://vision.googleapis.com/v1/images:annotate?key=AIzaSyDR_actsGj_gqBNwQZjV8uDfpP-LpCgobs";

    Bitmap bitmap;
    Context context;

    public ImageAsync (Bitmap bitmap, Context context) {
        this.bitmap = bitmap;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {

        String image = encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 100);

        String data = "{\"requests\":[{\"image\":{\"content\":\"" + image + "\"},\"features\":[{\"type\":\"FACE_DETECTION\",\"maxResults\":1}]}]}";

        OutputStream out = null;
        try {

            // Connect
            URL url = new URL(GOOGLE_URL);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);

            urlConnection.connect();


            // Writing
            out = new BufferedOutputStream(urlConnection.getOutputStream());
            BufferedWriter writer = new BufferedWriter (new OutputStreamWriter(out, "UTF-8"));
            writer.write(data);
            writer.flush();
            writer.close();
            out.close();

            // Read
            String response = "";
            String line;

            BufferedReader br=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            while ((line=br.readLine()) != null) {
                response+=line;
            }
            return response;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {

        int x = 1;

        // If this is good,

        /*

        Create MyAsync object and execute 


         */
    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
