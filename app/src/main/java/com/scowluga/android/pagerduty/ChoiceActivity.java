package com.scowluga.android.pagerduty;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.scowluga.android.pagerduty.receiver.ReceiverActivity;
import com.scowluga.android.pagerduty.sender.SenderActivity;

public class ChoiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);


    }

    public void openSender(View view) {
        Intent intent = new Intent(ChoiceActivity.this, ReceiverActivity.class);
        startActivity(intent);

    }

    public void openReceiver(View view) {
        Intent intent = new Intent(ChoiceActivity.this, SenderActivity.class);
        startActivity(intent);

    }
}
