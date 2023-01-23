package com.harry9425.yaari;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class createroom_activity extends AppCompatActivity {

    RecyclerView recyclerView;
    public static RecyclerView selectedrecycler;
    private FirebaseDatabase mDatabase;
    public static createroomadapter createroomadapter;
    public  static selectedroomadapter selectedroomadapter;
    EditText search;
    public static TextView selectedcount;
    allusersmodel allusersmodel,allselectedmodel;
    public static ArrayList<allusersmodel> list=new ArrayList<>();
    public  static ArrayList<allusersmodel> sellist=new ArrayList<>();
    EditText editText;
    String tosr,curuser;
    int val=1,memcount=0,size=0;
    ImageButton changesearch;
    public static TextView save;
    DatabaseReference databaseReference;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createroom_activity);
        curuser=FirebaseAuth.getInstance().getCurrentUser().getUid();
        selectedcount=(TextView) findViewById(R.id.selectedcount);
        changesearch=(ImageButton) findViewById(R.id.changesearchvalue_room);
        selectedcount.setText("Select Participants");
        save=(TextView) findViewById(R.id.next_selected);
        save.setVisibility(View.GONE);
        recyclerView=(RecyclerView) findViewById(R.id.roommakerecycler);
        createroomadapter =new createroomadapter(list,this);
        recyclerView.setAdapter(createroomadapter);
        editText=(EditText) findViewById(R.id.searchalluser_room);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        selectedrecycler=(RecyclerView) findViewById(R.id.selectedroomrecyclerview);
        selectedroomadapter =new selectedroomadapter(sellist,this);
        selectedrecycler.setAdapter(selectedroomadapter);
        LinearLayoutManager linearLayoutManager2=new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        selectedrecycler.setLayoutManager(linearLayoutManager2);
        allpersons();
        editText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tosr = editText.getText().toString().trim();
                filter(tosr);
            }
        });

        changesearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(val==0){
                    editText.setHint("Search userId");
                    val=1;
                }
                else {
                    val=0;
                    editText.setHint("Search name");
                }
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               save();
            }
        });
    }
    public void bkbk(View view){
        onBackPressed();
    }

    private void filter(String tosr) {
        ArrayList<allusersmodel> temp=new ArrayList<allusersmodel>();
        for(allusersmodel friends:list)
        {
            if(val==1) {
                if (friends.getUsername().contains(tosr)) {
                    temp.add(friends);
                }
            }
            else {
                if (friends.getName().contains(tosr)) {
                    temp.add(friends);
                }
            }
        }
        createroomadapter.filteredlist(temp);
    }

    public void allpersons()
    {
        mDatabase= FirebaseDatabase.getInstance();
        mDatabase.getReference().keepSynced(true);
        mDatabase.getReference().child("AAA usernames").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    allusersmodel=snapshot.getValue(allusersmodel.class);
                    if(!allusersmodel.getUid().equals(curuser)) {
                        allusersmodel.setRoomselect(null);
                        list.add(allusersmodel);
                    }
                }
                createroomadapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(createroom_activity.this, "CAN'T CONNECT TO SERVER", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void save(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view=getLayoutInflater().inflate(R.layout.createroomalertdialog,null);
        builder.setView(view);
        AlertDialog alertDialog =builder.create();
        alertDialog.show();
        EditText name=view.findViewById(R.id.roomname_room);
        ImageButton cancel=view.findViewById(R.id.cancel_roomnamealert);
        TextView next=view.findViewById(R.id.next_roomanmealert);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roomname=name.getText().toString().trim();
                if(roomname.isEmpty()) {
                    alertDialog.dismiss();
                    alertDialog.dismiss();
                }
                else {
                   alertDialog.dismiss();
                   startmakinggroup(roomname);
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                alertDialog.cancel();
            }
        });
    }

    private void startmakinggroup(String name) {
        size=sellist.size();
        databaseReference=FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);
        String pushid=databaseReference.push().getKey();
        databaseReference=FirebaseDatabase.getInstance().getReference().child("Group chat").child(pushid).child("Group details");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", name);
        hashMap.put("date", System.currentTimeMillis());
        hashMap.put("groupdp","https://firebasestorage.googleapis.com/v0/b/yaari-8ae4e.appspot.com/o/profile_images%2Fdefault%2Fuserdefaultdp.jpg?alt=media&token=e60fa3cc-43c7-44a9-9000-99fea647b1a7");
        hashMap.put("owner", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap.put("groupkey",pushid);
        databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    DatabaseReference mDatabase=FirebaseDatabase.getInstance().getReference().child("Chats");
                    databaseReference.child("members").child(curuser).setValue(System.currentTimeMillis()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mDatabase.child(curuser).child("groups").child(pushid).setValue("member").addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    for(allusersmodel selected:sellist) {
                                        memcount+=1;
                                        databaseReference.child("members").child(selected.getUid()).setValue(System.currentTimeMillis()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mDatabase.child(selected.getUid()).child("groups").child(pushid).setValue("member").addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        if(memcount>=size){
                                                            Intent i =new Intent(createroom_activity.this,roomchatactivity.class);
                                                            i.putExtra("group uid",pushid);
                                                            startActivity(i);
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }
}