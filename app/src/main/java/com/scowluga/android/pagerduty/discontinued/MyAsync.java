package com.scowluga.android.pagerduty.discontinued;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.MessageFormat;
import android.os.AsyncTask;
import android.util.Base64;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by DavidLu on 2017-09-16.
 */

public class MyAsync extends AsyncTask <String, String, String> {

    final String BASE_URL = "https://events.pagerduty.com/v2/enqueue";
    final String BASE_SOURCE = "com.scowluga.PagerDuty";

    Bitmap bitmap;
    Context context;

    public MyAsync (Bitmap bitmap, Context context) {
        this.bitmap = bitmap;
        this.context = context;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }


    @Override
    protected String doInBackground(String... strings) {

        OutputStream out = null;
        try {

            // Connect
            URL url = new URL(BASE_URL);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);

            urlConnection.connect();


            // Logic
            SimpleDateFormat format = new SimpleDateFormat("hh:mm aa");
            String summary = format.format(new Date()) + ". Someone's at your door.";

            SharedPreferences preferences = context.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
            int temp = preferences.getInt("detectorimage", 1);
            preferences.edit().putInt("detectorimage", temp + 1).apply();

            Cloudinary cloudinary = new Cloudinary();

            // Storing bitmap to file
            File file = new File(context.getFilesDir(), "detector" + temp + ".jpg");
            OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.close();

            // Getting URL from Cloudinary
            Map uploadResult = cloudinary.uploader().unsignedUpload(file, "usynuxhv", ObjectUtils.asMap("cloud_name", "dg8u5kc5f"));

            String imageUrl = (String)uploadResult.get("url");

            // Creating data string
            String data = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                data = "{\"routing_key\":\"f91c040eff4e4a34a09ac9749af9cf71\",\"event_action\":\"trigger\",\"payload\": {\"summary\": \"" + summary + "\", \"source\": \"" + BASE_SOURCE + "\",\"severity\": \"info\",\"custom_details\": \"" + imageUrl + "\"}}";
            }

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
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String s) {

        int x = 1;

    }
}
