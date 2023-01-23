package com.harry9425.yaari;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class registername extends AppCompatActivity {

    EditText username;
    TextView sstext;
    int a=0;
    private DatabaseReference mDatabase;
    public static String name;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registername);
        username=(EditText) findViewById(R.id.username);
        sstext=(TextView) findViewById(R.id.sstext);
        progressDialog =new ProgressDialog((this));
        progressDialog.setTitle("CREATING USER PROFILE");
        progressDialog.setMessage("Wait while we create your profile..");
        sstext.setMovementMethod(new ScrollingMovementMethod());
        sstext.setHorizontallyScrolling(true);
        username.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void onTextChanged(CharSequence s, int start,int before, int count) {
                if(!username.getText().toString().trim().isEmpty()) {
                    sstext.setText(username.getText().toString().toUpperCase().trim());
                }
                else {
                    sstext.setText("XXXX");
                }
            }
        });
    }

    public void register(View view)
    {
        name=username.getText().toString().trim();
        if(name.isEmpty())
        {
            username.setError("Empty");
            username.requestFocus();
        }
        else {
                String uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
                progressDialog.show();
                mDatabase= FirebaseDatabase.getInstance().getReference();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("name", name);
                hashMap.put("phoneno", otpverification.phoneno);
                hashMap.put("status","Hey there, I'm using YAARI app");
                hashMap.put("userdp","https://firebasestorage.googleapis.com/v0/b/yaari-8ae4e.appspot.com/o/profile_images%2Fdefault%2Fuserdefaultdp.jpg?alt=media&token=e60fa3cc-43c7-44a9-9000-99fea647b1a7");
                hashMap.put("uid",uid);
                hashMap.put("gender","notsay");
                hashMap.put("profile","open");
                hashMap.put("tokenid",otpverification.token);
                mDatabase.child("AAA usernames").child(uid).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(getApplicationContext(), mainpage.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            Toast.makeText(registername.this, "CREATED SUCCESSFULLY", Toast.LENGTH_SHORT).show();

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(registername.this, "YOUR INTERNET IS LOW", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }

    }
}
