package com.harry9425.yaari;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class splashscreen extends AppCompatActivity {

    String uri="none";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        Uri data = this.getIntent().getData();
        if (data != null && data.isHierarchical()) {
             uri= this.getIntent().getDataString();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i=new Intent(splashscreen.this,mainpage.class);
                i.putExtra("fromurl",uri);
                startActivity(i);
                finish();
            }
        }, 3000);
    }
}