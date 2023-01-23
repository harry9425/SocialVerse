package com.harry9425.yaari;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class roomchatactivity extends AppCompatActivity {

    TextView groupname,indicator;
    CircleImageView groupdp;
    ImageButton bk,videocall,downchat,sendbtn,clipbtn,picbtn;
    CardView sendmessage;
    EditText plainmessage;
    RecyclerView grouprecycler;
    String curuser,groupid,grouptitle,groupdpval;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roomchatactivity);
        curuser= FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        groupid=getIntent().getStringExtra("group uid");
        groupname=(TextView) findViewById(R.id.chatnametext_group);
        indicator=(TextView) findViewById(R.id.onoffindi_group);
        groupdp=(CircleImageView) findViewById(R.id.chatdp_group);
        sendmessage=(CardView) findViewById(R.id.sendcard_group);
        sendbtn=(ImageButton) findViewById(R.id.chatsendbtn_group);
        grouprecycler=(RecyclerView) findViewById(R.id.chatrecyclerview_group);
        plainmessage=(EditText) findViewById(R.id.chatmessage_group);
        databaseReference=FirebaseDatabase.getInstance().getReference().child("Group chat").child(groupid).child("Group details");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    grouptitle=snapshot.child("name").getValue().toString();
                    groupname.setText(grouptitle);
                    groupdpval=snapshot.child("groupdp").getValue().toString().toString();
                    Picasso.get().load(groupdpval).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.userdefaultdp)
                            .into(groupdp, new Callback() {
                                @Override
                                public void onSuccess() {}
                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(groupdpval).placeholder(R.drawable.userdefaultdp).into(groupdp);
                                }
                            });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}});
        }
}