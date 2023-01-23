package com.harry9425.yaari;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class postpage extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference mDatabase;
    postpageadpater postpageadpater;
    postmodel postmodel;
    String curname,curusername,curuser,mainuser;
    ArrayList<postmodel> list=new ArrayList<postmodel>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postpage);
        recyclerView=(RecyclerView) findViewById(R.id.recyclerallpostview);
        curuser= getIntent().getStringExtra("uid");
        mainuser=FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase=FirebaseDatabase.getInstance().getReference();
        postpageadpater =new postpageadpater(list,this,mainuser);
        mDatabase.keepSynced(true);
        mDatabase.child("AAA usernames").child(curuser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                curusername=snapshot.child("username").getValue().toString();
                String dp=snapshot.child("userdp").getValue().toString();
                postpageadpater.setDp(dp);
                postpageadpater.setUid(curusername);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        recyclerView.setAdapter(postpageadpater);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        enterrr();
        }

     private void enterrr()
    {
        FirebaseDatabase mDatabase;
        mDatabase= FirebaseDatabase.getInstance();
        mDatabase.getReference().keepSynced(true);
        mDatabase.getReference().child("AAA usernames").child(curuser).child("Posts").addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    postmodel = snapshot.getValue(postmodel.class);
                    list.add(postmodel);
                }
                postpageadpater.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(postpage.this, "CAN'T CONNECT TO SERVER", Toast.LENGTH_LONG).show();
            }
        });
    }
}