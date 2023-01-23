package com.harry9425.yaari;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class signingactivity extends AppCompatActivity {


    public void newid(View view)
    {
        Intent i=new Intent(this,MainActivity.class);
        startActivity(i);
        otpverification.what=0;
    }

    public void oldid(View view)
    {
        Intent i=new Intent(this,MainActivity.class);
        startActivity(i);
        otpverification.what=1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signingactivity);
    }
}
