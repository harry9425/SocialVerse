package com.harry9425.yaari;

import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class allchatadapter extends RecyclerView.Adapter<allchatadapter.viewholder> {

    ArrayList<chatmodel> list;
    Context context;
    private DatabaseReference mDatabase;
    public allchatadapter(ArrayList<chatmodel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public allchatadapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.samplechatview,parent,false);
        return new allchatadapter.viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final allchatadapter.viewholder holder, int position) {
        final chatmodel detailsm=list.get(position);
        if(detailsm.getIsgroup().equals("f")) {
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
                    holder.textView.setText(dataSnapshot.child("name").getValue().toString());
                    holder.circleImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(context, imagezoom.class);
                            i.putExtra("zoomuri", dataSnapshot.child("userdp").getValue().toString());
                            context.startActivity(i);
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
            if (!detailsm.getTime().equals("NA")) {
                try {
                    long seconds = Long.parseLong(detailsm.getTime());
                    if (seconds != 0) {
                        Date myDate = new Date(seconds);
                        Date cur = new Date(System.currentTimeMillis());
                        SimpleDateFormat formatter = new SimpleDateFormat("K:mm a");
                        SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/YY");
                        if (formatter2.format(cur).compareTo(formatter2.format(myDate)) == 0) {
                            String myTime = formatter.format(myDate);
                            holder.timetick.setText(myTime);
                        } else {
                            String myTime = formatter2.format(myDate);
                            holder.timetick.setText(myTime);
                        }
                    }
                } catch (NumberFormatException e) {
                    holder.timetick.setText("NA");
                }
            } else {
                holder.timetick.setText("NA");
            }
            holder.text2.setText(detailsm.getLastmess());
            holder.touchchat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, chatactivity.class);
                    i.putExtra("uidm", detailsm.getUid());
                    i.putExtra("name", detailsm.getName());
                    i.putExtra("dp", detailsm.getUserdp());
                    context.startActivity(i);
                }
            });
        }
        else {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Group chat").child(detailsm.getUid()).child("Group details");
            mDatabase.keepSynced(true);
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    detailsm.setUserdp(dataSnapshot.child("groupdp").getValue().toString());
                    detailsm.setName(dataSnapshot.child("name").getValue().toString());

                    Picasso.get().load(detailsm.getUserdp()).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.userdefaultdp).into(holder.circleImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(detailsm.getUserdp()).placeholder(R.drawable.userdefaultdp).into(holder.circleImageView);
                        }
                    });
                    holder.textView.setText(detailsm.getName());
                    holder.circleImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(context, imagezoom.class);
                            i.putExtra("zoomuri", detailsm.getClass());
                            context.startActivity(i);
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });/*
            if (!detailsm.getTime().equals("NA")) {
                try {
                    long seconds = Long.parseLong(detailsm.getTime());
                    if (seconds != 0) {
                        Date myDate = new Date(seconds);
                        Date cur = new Date(System.currentTimeMillis());
                        SimpleDateFormat formatter = new SimpleDateFormat("K:mm a");
                        SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/YY");
                        if (formatter2.format(cur).compareTo(formatter2.format(myDate)) == 0) {
                            String myTime = formatter.format(myDate);
                            holder.timetick.setText(myTime);
                        } else {
                            String myTime = formatter2.format(myDate);
                            holder.timetick.setText(myTime);
                        }
                    }
                } catch (NumberFormatException e) {
                    holder.timetick.setText("NA");
                }
            } else {
                holder.timetick.setText("NA");
            }
            holder.text2.setText(detailsm.getLastmess());
            holder.touchchat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, roomchatactivity.class);
                    i.putExtra("group uid", detailsm.getUid());
                    context.startActivity(i);
                }
            });
            */
            Toast.makeText(context,detailsm.getTime(),Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public void filteredlist(ArrayList<chatmodel> temp) {
        list=temp;
        notifyDataSetChanged();
    }

    public class viewholder extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        TextView textView, text2,timetick;
        LinearLayout touchchat;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.allchatdp);
            textView = itemView.findViewById(R.id.chatfriendname);
            timetick=itemView.findViewById(R.id.timeshow);
            text2 = itemView.findViewById(R.id.chatlastmess);
            touchchat=itemView.findViewById(R.id.touchchat);
            textView.setSelected(true);
            text2.setSelected(true);
        }
    }
}
