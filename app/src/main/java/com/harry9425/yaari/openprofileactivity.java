package com.harry9425.yaari;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eftimoff.viewpagertransformers.DepthPageTransformer;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class openprofileactivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    String uname,status,name,image;
    TextView username,namedis,aboutsec,postcount,friendcount;
    RoundedImageView roundedImageView;
    private TabLayout mtablayout;
    String curuser=null,mainuser=null;
    Button sendreqbtn,accreqbtn,reqpendingbtn,removefrbtn;
    String[] keyforreq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_openprofileactivity);
        curuser=getIntent().getStringExtra("uid");
        mainuser=FirebaseAuth.getInstance().getCurrentUser().getUid();
        username=(TextView) findViewById(R.id.opa_titlename);
        namedis=(TextView) findViewById(R.id.opa_username);
        aboutsec=(TextView) findViewById(R.id.opa_about);
        postcount=(TextView) findViewById(R.id.opa_postcount);
        friendcount=(TextView) findViewById(R.id.opa_frcount);
        sendreqbtn=(Button) findViewById(R.id.opa_sendbtn);
        accreqbtn=(Button) findViewById(R.id.opa_acceptbtn);
        reqpendingbtn=(Button) findViewById(R.id.opa_pendingbtn);
        removefrbtn=(Button) findViewById(R.id.opa_unfriendbtn);
        roundedImageView=(RoundedImageView) findViewById(R.id.opa_dp);
        reset();
        ViewPager mpager=(ViewPager) findViewById(R.id.opa_pager);
        mpager.setOffscreenPageLimit(3);
        postfragment.curuser=curuser;
        accountspageadapter sectionspageadapter=new accountspageadapter(getSupportFragmentManager());
        mpager.setAdapter(sectionspageadapter);
        mpager.setPageTransformer(true, new DepthPageTransformer());
        mtablayout=findViewById(R.id.opa_pagertab);
        mtablayout.setupWithViewPager(mpager);
        displayval1();
        sendreqbtn.setVisibility(View.VISIBLE);
        reqlistener();
        friendslistner();
        sendreqbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendreq();
            }
        });
        accreqbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptreq();
            }
        });
        removefrbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removefriend();
            }
        });
        reqpendingbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removereq();
            }
        });
    }

    private void reqlistener()
    {
        mDatabase=FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);
        mDatabase.child("Friends Request data").child(mainuser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(curuser))
                {
                    reset();
                    String reqtype=snapshot.child(curuser).child("request type").getValue().toString().trim();
                    if(reqtype.equals("sent"))
                        reqpendingbtn.setVisibility(View.VISIBLE);
                    else if(reqtype.equals("received"))
                        accreqbtn.setVisibility(View.VISIBLE);
                }
                else {
                    reset();
                    sendreqbtn.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }});
    }
    private void friendslistner()
    {
        mDatabase=FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);
        mDatabase.child("Friends list data").child(mainuser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            if(snapshot.hasChild(curuser)){
                reset();
                removefrbtn.setVisibility(View.VISIBLE);
            }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void sendreq(){
    mDatabase=FirebaseDatabase.getInstance().getReference();
    mDatabase.keepSynced(true);
    mDatabase.child("Friends Request data").child(mainuser).child(curuser).child("request type").setValue("sent").addOnSuccessListener(new OnSuccessListener<Void>() {
        @Override
        public void onSuccess(Void aVoid) {
            mDatabase.child("Friends Request data").child(curuser).child(mainuser).child("request type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    reqlistener();
                }
            });
        }
    });
    }
    private void removereq(){
        mDatabase=FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);
        mDatabase.child("Friends Request data").child(mainuser).child(curuser).child("request type").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    mDatabase.child("Friends Request data").child(curuser).child(mainuser).child("request type").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            reqlistener();
                            friendslistner();
                        }
                    });
                }
                else {
                    Toast.makeText(openprofileactivity.this, "YOUR INTERNET IS LOW", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void acceptreq(){
        removereq();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("videocallid",curuser+"&"+mainuser);
        hashMap.put("status",DateFormat.getDateInstance().format(new Date()));
        mDatabase=FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);
        mDatabase.child("Friends list data").child(mainuser).child(curuser).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDatabase.child("Friends list data").child(curuser).child(mainuser).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        reqlistener();
                        friendslistner();
                    }
                });
            }
        });
    }

    private void removefriend(){
        mDatabase=FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);
        mDatabase.child("Friends list data").child(mainuser).child(curuser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String[] val=snapshot.child("videocallid").getValue().toString().split("&");
                mDatabase=FirebaseDatabase.getInstance().getReference();
                mDatabase.child("Friends list data").child(mainuser).child(curuser).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mDatabase.child("Friends list data").child(curuser).child(mainuser).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                resetreq(val);
                            }
                        });
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    private void resetreq(String[] val)
    {
        mDatabase=FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);
        mDatabase.child("Friends Request data").child(val[0]).child(val[1]).child("request type").setValue("sent").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDatabase.child("Friends Request data").child(val[1]).child(val[0]).child("request type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        reqlistener();
                        friendslistner();
                    }
                });
            }
        });
    }

    private void reset(){
        sendreqbtn.setVisibility(View.GONE);
        accreqbtn.setVisibility(View.GONE);
        reqpendingbtn.setVisibility(View.GONE);
        removefrbtn.setVisibility(View.GONE);
    }

    private void displayval1() {
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("AAA usernames").child(curuser);
        mDatabase.keepSynced(true);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name=snapshot.child("name").getValue().toString();
                status=snapshot.child("status").getValue().toString();
                image=snapshot.child("userdp").getValue().toString();
                if(snapshot.hasChild("username")) {
                    uname=snapshot.child("username").getValue().toString();
                    username.setText(uname);
                }
                long posts=snapshot.child("Posts").getChildrenCount();
                Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.userdefaultdp)
                        .into(roundedImageView, new Callback() {
                            @Override
                            public void onSuccess() {}
                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(image).placeholder(R.drawable.userdefaultdp).into(roundedImageView);
                            }
                        });
                postcount.setText(posts+"");
                namedis.setText(name);
                aboutsec.setText(status);
                DatabaseReference mDatabase;
                mDatabase=FirebaseDatabase.getInstance().getReference().child("Friends list data").child(curuser);
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        friendcount.setText(snapshot.getChildrenCount()+"");
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(openprofileactivity.this, "CAN'T CONNECT TO SERVER", Toast.LENGTH_LONG).show();
            }
        });
    }
    public void messagechat(View view)
    {
        Intent i=new Intent(this,chatactivity.class);
        i.putExtra("name",name);
        i.putExtra("dp",image);
        i.putExtra("uidm",curuser);
        startActivity(i);
    }
}