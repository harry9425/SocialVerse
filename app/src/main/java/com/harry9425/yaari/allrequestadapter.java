package com.harry9425.yaari;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jgabrielfreitas.core.BlurImageView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.math.RoundingMode;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.blurry.Blurry;

public class allrequestadapter extends RecyclerView.Adapter<allrequestadapter.viewholder> {

    ArrayList<requestmodelclass> list;
    Context context;
    private DatabaseReference mDatabase,getmDatabase;
    public allrequestadapter(ArrayList<requestmodelclass> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public allrequestadapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.samplerequestsample,parent,false);
        return new allrequestadapter.viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final allrequestadapter.viewholder holder, int position) {
        final requestmodelclass detailsm = list.get(position);
        holder.dec.setVisibility(View.VISIBLE);
        holder.apt.setVisibility(View.VISIBLE);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("AAA usernames");
        mDatabase.keepSynced(true);
        mDatabase.child(detailsm.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                detailsm.setUserdp(dataSnapshot.child("userdp").getValue().toString());
                detailsm.setName(dataSnapshot.child("name").getValue().toString());
                Picasso.get().load(dataSnapshot.child("userdp").getValue().toString()).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.userdefaultdp).into(holder.blurImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(dataSnapshot.child("userdp").getValue().toString()).placeholder(R.drawable.userdefaultdp).into(holder.blurImageView);
                    }
                });
                if (detailsm.getType().equals("r"))
                holder.type.setText("Request Received");
                else {
                    holder.type.setText("Request Pending");
                    holder.apt.setVisibility(View.GONE);
                }
                holder.name.setText(detailsm.getName());

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        holder.apt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getmDatabase=FirebaseDatabase.getInstance().getReference();
                getmDatabase.keepSynced(true);
                getmDatabase.child("Friends Request data").child(mainpage.curuser).child(detailsm.getUid()).child("request type").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Friends Request data");
                            mDatabase.child(detailsm.getUid()).child(mainpage.curuser).child("request type").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    setallfrlist(detailsm.getUid());
                                }
                            });
                        }
                    }
                }).addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                    }
                });;
            }
        });
        holder.blurImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("AAA usernames");
                databaseReference.keepSynced(true);
                databaseReference.child(detailsm.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                     detailsm.setProfile(snapshot.child("profile").getValue().toString());
                        if (detailsm.getProfile().equals("lock")) {
                            Intent i = new Intent(context, profileactivity.class);
                            i.putExtra("uidm", detailsm.getUid());
                            context.startActivity(i);
                        }
                        else{
                            Intent i = new Intent(context, openprofileactivity.class);
                            i.putExtra("uid", detailsm.getUid());
                            context.startActivity(i);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    private void setallfrlist(String uid) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("videocallid",mainpage.curuser+uid);
        hashMap.put("status", DateFormat.getDateInstance().format(new Date()));
        getmDatabase=FirebaseDatabase.getInstance().getReference();
        getmDatabase.child("Friends list data").child(mainpage.curuser).child(uid).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    getcuruserdata(uid);
                }
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {

            }
        });;
    }
    private void getcuruserdata(String uid)
    {
        getmDatabase=FirebaseDatabase.getInstance().getReference();
        getmDatabase.child("AAA usernames").child(mainpage.curuser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, Object> hashMap2 = new HashMap<>();
                hashMap2.put("status",DateFormat.getDateInstance().format(new Date()));
                hashMap2.put("videocallid",mainpage.curuser+uid);
                mDatabase = FirebaseDatabase.getInstance().getReference().child("Friends list data");
                mDatabase.child(uid).child(mainpage.curuser).setValue(hashMap2).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }});
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView name,type;
        RoundedImageView blurImageView;
        ImageButton apt,dec;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.frrequestname);
            type=itemView.findViewById(R.id.frrrequeststatus);
            blurImageView=itemView.findViewById(R.id.blurimage);
            apt=itemView.findViewById(R.id.frrequestaccept);
            dec=itemView.findViewById(R.id.frrequestcancel);
        }
    }
}
