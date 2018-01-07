package com.scowluga.android.pagerduty;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Base64;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by DavidLu on 2017-09-17.
 */

public class LoginTask extends AsyncTask<String, String, String> {

    String username;
    String password;
    Context context;

    public LoginTask(String username, String password, Context context) {
        this.username = username;
        this.password = password;
        this.context = context;
    }

    @Override
    protected String doInBackground(String... strings) {
        OutputStream out = null;
        try {
            // Connect
            URL url = new URL("https://htn.apiserver.link/api/login");

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);

            urlConnection.connect();

            String data = "{\"username\":\"" + username + "\", \"password\":\"" + password + "\"}";

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
        Intent intent = new Intent(context, ChoiceActivity.class);
        context.startActivity(intent);
    }
}
