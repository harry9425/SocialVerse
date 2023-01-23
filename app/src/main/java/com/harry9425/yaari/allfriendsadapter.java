package com.harry9425.yaari;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class allfriendsadapter extends RecyclerView.Adapter<allfriendsadapter.viewholder> {

    ArrayList<friendsclass> list;
    Context context;
    private DatabaseReference mDatabase,getmDatabase;
    public allfriendsadapter(ArrayList<friendsclass> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public allfriendsadapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.allfriendssample,parent,false);
        return new allfriendsadapter.viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final allfriendsadapter.viewholder holder, int position) {
        final friendsclass detailsm = list.get(position);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("AAA usernames");
        mDatabase.keepSynced(true);
        mDatabase.child(detailsm.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                detailsm.setUserdp(dataSnapshot.child("userdp").getValue().toString());
                detailsm.setName(dataSnapshot.child("name").getValue().toString());
                Picasso.get().load(dataSnapshot.child("userdp").getValue().toString()).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.userdefaultdp).into(holder.circleImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(dataSnapshot.child("userdp").getValue().toString()).placeholder(R.drawable.userdefaultdp).into(holder.circleImageView);
                    }
                });
                holder.textView.setText(detailsm.getName());
                holder.circleImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context,imagezoom.class);
                        i.putExtra("zoomuri",dataSnapshot.child("userdp").getValue().toString());
                        context.startActivity(i);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        getmDatabase=FirebaseDatabase.getInstance().getReference().child("AAA usernames").child(detailsm.getUid()).child("presence");
        getmDatabase.keepSynced(true);
        getmDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                detailsm.setTime(snapshot.getValue().toString());
                    if (detailsm.getTime().equals("online")) {
                        holder.text2.setText("Active now");
                        holder.text2.setTextColor(Color.parseColor("#E91E63"));
                    } else {
                        long seconds = Long.valueOf(detailsm.getTime());
                        Date myDate = new Date(seconds);
                        Date cur = new Date(System.currentTimeMillis());
                        SimpleDateFormat formatter = new SimpleDateFormat("K:mm a");
                        SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/YY");
                        if (formatter2.format(cur).compareTo(formatter2.format(myDate)) == 0) {
                            String myTime = formatter.format(myDate);
                            holder.text2.setText("Last seen: " + myTime);
                        } else {
                            String myTime = formatter2.format(myDate);
                            holder.text2.setText("Last seen: " + myTime);
                        }
                        holder.text2.setTextColor(Color.parseColor("#C6C6C6"));

                         }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
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
            holder.unfr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Friends list data");
                    mDatabase.child(detailsm.getUid()).child(mainpage.curuser).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mDatabase = FirebaseDatabase.getInstance().getReference().child("Friends list data");
                                mDatabase.child(mainpage.curuser).child(detailsm.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                    }
                                });
                            } else {
                            }
                        }
                    }).addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {

                        }
                    });
                    ;
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("Friends Request data").child(mainpage.curuser).child(detailsm.getUid()).child("request type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mDatabase = FirebaseDatabase.getInstance().getReference().child("Friends Request data");
                                mDatabase.child(detailsm.getUid()).child(mainpage.curuser).child("request type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                    }
                                });
                            } else {
                            }
                        }
                    }).addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {

                        }
                    });
                }
            });
          //  holder.textView.setSelected(true);
        }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void filteredlist(ArrayList<friendsclass> temp) {
        list=temp;
        notifyDataSetChanged();
    }

    public class viewholder extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        TextView textView, text2;
        Button unfr;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.allfrienddp);
            textView = itemView.findViewById(R.id.friendname);
            text2 = itemView.findViewById(R.id.date);
            unfr=itemView.findViewById(R.id.unfriend);
        }
    }
}
