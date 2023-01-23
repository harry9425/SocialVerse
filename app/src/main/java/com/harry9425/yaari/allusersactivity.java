package com.harry9425.yaari;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.widget.SearchView;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class allusersactivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private FirebaseDatabase mDatabase;
    alluseradapter alluseradapter;
    private Toolbar toolbar;
    EditText search;
    allusersmodel allusersmodel;
    ArrayList<allusersmodel> list=new ArrayList<>();
    ImageButton changesearch;
    int val=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allusersactivity);
        toolbar=(Toolbar) findViewById(R.id.appbar);
        search=(EditText) findViewById(R.id.searchalluser);
        LinearLayout linearLayout =(LinearLayout) findViewById(R.id.alluserlinear);
        changesearch=(ImageButton) findViewById(R.id.changesearchvalue);
        recyclerView=(RecyclerView) findViewById(R.id.recyclerallusers);
       alluseradapter =new alluseradapter(list,this);
        recyclerView.setAdapter(alluseradapter);
        recyclerView.setItemViewCacheSize(8);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(gridLayoutManager);
        tosr="";
        allpersons();
        changesearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(val==0){
                  search.setHint("Search UserId");
                  val=1;
                }
                else {
                    val=0;
                    search.setHint("Search Name");
                }
            }
        });
        search.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void onTextChanged(CharSequence s, int start,int before, int count) {
                tosr = search.getText().toString();
                allpersons();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference databaseReference;
        databaseReference=FirebaseDatabase.getInstance().getReference().child("AAA usernames").child(mainpage.curuser);
        databaseReference.child("presence").setValue("online").addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    @Override
    protected void onStop() {
        super.onStop();
        DatabaseReference databaseReference;
        databaseReference=FirebaseDatabase.getInstance().getReference().child("AAA usernames").child(mainpage.curuser);
        databaseReference.child("presence").setValue("online").addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    String tosr;
    public void allpersons()
    {
        mDatabase= FirebaseDatabase.getInstance();
        mDatabase.getReference().keepSynced(true);
        mDatabase.getReference().child("AAA usernames").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    allusersmodel=snapshot.getValue(allusersmodel.class);
                    if(!allusersmodel.getUid().equals(mainpage.curuser.trim()))
                        if(val==0) {
                            if (allusersmodel.getName().contains(tosr)) {
                                list.add(allusersmodel);
                            }
                        }
                    else {
                            if (allusersmodel.getUsername().contains(tosr)) {
                                list.add(allusersmodel);
                            }
                        }
                }
                alluseradapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(allusersactivity.this, "CAN'T CONNECT TO SERVER", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void bkbk(View view){
        onBackPressed();
    }

    public void qrscan(View view){
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.setPrompt("Scan Latte code");
        scanIntegrator.setBeepEnabled(true);
        scanIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        scanIntegrator.setOrientationLocked(false);
        scanIntegrator.setBarcodeImageEnabled(true);
        scanIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanningResult != null) {
            if (scanningResult.getContents() != null) {
                String scanContent = scanningResult.getContents().toString();
                if(!scanContent.equals(mainpage.curuser)) {
                    DatabaseReference databaseReference;
                    databaseReference=FirebaseDatabase.getInstance().getReference().child("AAA usernames");
                            databaseReference.child(scanContent).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild("profile")){
                                if(snapshot.child("profile").getValue().toString().equals("lock"))
                                    {
                                        Intent i = new Intent(allusersactivity.this, profileactivity.class);
                                        i.putExtra("uidm", scanContent);
                                        startActivity(i);
                                    }
                                    else{
                                        Intent i = new Intent(allusersactivity.this, openprofileactivity.class);
                                        i.putExtra("uid", scanContent);
                                        startActivity(i);
                                    }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
                else {
                    Intent i2 = new Intent(allusersactivity.this, accountsettings.class);
                    startActivity(i2);
                }
            }
        }
    }

}