package com.liteon.icampusguardian;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class RegisteredActivity extends AppCompatActivity implements View.OnClickListener{

    Button mBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered);
        findViews();
        setListener();

    }

    private void findViews() {
        mBack = findViewById(R.id.back_to_login);
    }

    private void setListener() {
        mBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        finish();
    }
}
