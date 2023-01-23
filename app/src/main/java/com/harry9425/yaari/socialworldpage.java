package com.harry9425.yaari;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class socialworldpage extends AppCompatActivity {

    RecyclerView recyclerstory;
    private FirebaseDatabase mDatabase;
    storymodelcalss storymodelcalss;
    storyadapter storyadapter;
    String curuser;
    CircleImageView circleImageView;
    ArrayList<storymodelcalss> list=new ArrayList<storymodelcalss>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socialworldpage);
        recyclerstory=(RecyclerView) findViewById(R.id.storyrecycler);
        curuser= FirebaseAuth.getInstance().getCurrentUser().getUid().trim();
        storyadapter =new storyadapter(list,this);
        recyclerstory.setAdapter(storyadapter);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerstory.setLayoutManager(linearLayoutManager);
        circleImageView=(CircleImageView) findViewById(R.id.socialworld_userdp_story);
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("AAA usernames").child(curuser);
        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Picasso.get().load(snapshot.child("userdp").getValue().toString()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.userdefaultdp)
                .into(circleImageView, new Callback() {
                    @Override
                    public void onSuccess() {}
                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(snapshot.child("userdp").getValue().toString()).placeholder(R.drawable.userdefaultdp).into(circleImageView);
                    }
                });
                if(snapshot.hasChild("Story")){
                    circleImageView.setBorderColor(Color.parseColor("#4CAF50"));
                    circleImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            storymodelcalss storymodelcalss=new storymodelcalss();
                            storymodelcalss.setUid(curuser);
                            storymodelcalss.setUserdp(snapshot.child("userdp").getValue().toString());
                            storymodelcalss.setUsername(snapshot.child("username").getValue().toString());
                            Intent i =new Intent(socialworldpage.this,storyviewsocialworld.class);
                            storyviewsocialworld.storymodelcalss=storymodelcalss;
                            startActivity(i);
                        }
                    });
                }
                else {
                    circleImageView.setBorderColor(Color.parseColor("#373737"));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        storylistener();
    }
    private void storylistener(){
        FirebaseDatabase mDatabase;
        mDatabase= FirebaseDatabase.getInstance();
        mDatabase.getReference().keepSynced(true);
        mDatabase.getReference().child("Friends list data").child(curuser).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                   getvalues(dataSnapshot);
                }
                storyadapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(socialworldpage.this, "CAN'T CONNECT TO SERVER", Toast.LENGTH_LONG).show();
            }
        });
    }
    private void getvalues(DataSnapshot snapshot){
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("AAA usernames");
        databaseReference.child(snapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshotp) {
                if(snapshotp.hasChild("Story")){
                    storymodelcalss storymodelcalss=new storymodelcalss();
                       storymodelcalss.setUid(snapshot.getKey());
                       storymodelcalss.setUserdp(snapshot.child("userdp").getValue().toString());
                       storymodelcalss.setUsername(snapshot.child("username").getValue().toString());
                        list.add(storymodelcalss);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}