package com.scowluga.android.pagerduty;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    public TextInputLayout user;
    public TextInputLayout pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        user = (TextInputLayout)findViewById(R.id.username);
        pass = (TextInputLayout)findViewById(R.id.password);


        Button login = (Button)findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginTask task = new LoginTask(user.getEditText().getText().toString(), pass.getEditText().getText().toString(), LoginActivity.this);
                task.execute();
            }
        });
    }

}
