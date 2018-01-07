package com.scowluga.android.pagerduty.sender;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.scowluga.android.pagerduty.R;

import java.io.IOException;

public class SenderActivity extends AppCompatActivity {

    public ImageView imageView;
    public Camera camera;

    public Bitmap lastSmall;

    public boolean takingPicture;

    public Handler handler;
    public Runnable runnable;

    public final int interval = 250;
    public final int movementDifference = 400;

    Camera.PictureCallback mPicture;

    Button resumeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender);

        imageView = (ImageView) findViewById(R.id.imageView);
        resumeButton = (Button) findViewById(R.id.resumeCamera);
        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCamera();
            }
        });

        mPicture = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                camera.stopPreview();

                Bitmap temp = BitmapFactory.decodeByteArray(data, 0, data.length);
                Matrix matrix = new Matrix();

                matrix.postRotate(90);

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(temp,temp.getWidth(),temp.getHeight(),true);

                Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap .getWidth(), scaledBitmap .getHeight(), matrix, true);


                final Bitmap thisLarge = Bitmap.createScaledBitmap(rotatedBitmap, (int) rotatedBitmap.getWidth() / 8, (int) (rotatedBitmap.getHeight() / 8), false);

                Bitmap thisSmall = Bitmap.createScaledBitmap(thisLarge, 8, 10, false);
                imageView.setImageBitmap(thisLarge);

                if (lastSmall != null) {
                    int dif = findDifference(lastSmall, thisSmall);
                    if (dif >= movementDifference) {
                        Toast.makeText(SenderActivity.this, "Different with: " + dif, Toast.LENGTH_SHORT).show();

                        stopCamera();
                        MicrosoftRestClient.MicrosoftAsyncTask task = new MicrosoftRestClient.MicrosoftAsyncTask(thisLarge, SenderActivity.this);
                        task.execute();
                        return;

                    }
                }
                lastSmall = thisSmall;
                takingPicture = true;
                startCamera();
            }
        };

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (takingPicture) {
                    startCamera();
                    camera.startPreview();
                    camera.takePicture(null, null, mPicture);
                    try {
                    } catch (Exception e) {
                        Toast.makeText(SenderActivity.this, "Uh oh", Toast.LENGTH_SHORT).show();
                    }
                    takingPicture = false;
                }
                handler.postDelayed(this, interval);
            }
        };
        startCamera();
        handler.postDelayed(runnable, interval);
    }

    public void startCamera() {
        camera = Camera.open();
        camera.setDisplayOrientation(0);
        SurfaceTexture st = new SurfaceTexture(MODE_PRIVATE);
        try {
            camera.setPreviewTexture(st);
        } catch (IOException e) {
            e.printStackTrace();
        }
        takingPicture = true;

    }

    public void stopCamera() {
//        handler.removeCallbacks(runnable);
        camera.stopPreview();
        camera.release();
    }

    private int findDifference (Bitmap b1, Bitmap b2) {
        if (b1.getHeight() != b2.getHeight() || b1.getWidth() != b2.getWidth()) {
            return -1;
        } else {
            int totalDifference = 0;
            for (int x = 0; x < b1.getWidth(); x ++) {
                for (int y = 0; y < b1.getHeight(); y ++) {
                    int p1 = b1.getPixel(x, y);
                    int p2 = b2.getPixel(x, y);
                    int totalDiff = (int)(
                            Math.pow(Math.abs(Color.red(p1) - Color.red(p2)), 2)
                                    + Math.pow(Math.abs(Color.green(p1) - Color.green(p2)), 2)
                                    + Math.pow(Math.abs(Color.blue(p1) - Color.blue(p2)), 2));
                    totalDifference += totalDiff;
                }
            }
            totalDifference = (int)Math.sqrt(totalDifference);

            return totalDifference;
        }
    }
}
