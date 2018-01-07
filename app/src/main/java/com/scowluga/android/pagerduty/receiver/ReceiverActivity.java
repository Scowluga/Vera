package com.scowluga.android.pagerduty.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.scowluga.android.pagerduty.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class ReceiverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver);

        try {
            Socket mySocket = IO.socket("https://htn.apiserver.link");
            mySocket.connect();

            mySocket.on("notification", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject data = (JSONObject) args[0];
                            String title;
                            String image;
                            try {
                                title = data.getString("title");
                                image = data.getString("image");
                            } catch (JSONException e) {
                                return;
                            }

                            createNotification(title, image, ReceiverActivity.this);
                        }
                    });                }
            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    public void createNotification(final String title, String image, final Context context) {

        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                sendNotification(title, bitmap, context);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };

        Picasso.with(this).load(image).into(target);
    }

    private void sendNotification(String description, Bitmap bitmap, Context context) {

        Notification.Builder builder = new Notification.Builder(context);

        builder.setSmallIcon(R.mipmap.vera_launcher);

        builder.setLargeIcon(bitmap);
        builder.setContentTitle(description);
        builder.setContentText("Click to learn more");

        Notification.BigPictureStyle style = new Notification.BigPictureStyle();
        style.bigPicture(bitmap);

        builder.setStyle(style);

        // Launching
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify("TAG", 0, builder.build());
    }
}
