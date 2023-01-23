package com.harry9425.yaari;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

   EditText phone;
   String phoneno;

    public void gooo(View view)
    {
        phoneno=phone.getText().toString().trim();
        if(!phoneno.isEmpty()) {
            otpverification.phoneno = "+91" + phoneno;
            Intent i = new Intent(this, otpverification.class);
            startActivity(i);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        phone=(EditText) findViewById(R.id.phone);
    }
}
