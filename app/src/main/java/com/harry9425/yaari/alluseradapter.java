package com.harry9425.yaari;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class alluseradapter extends RecyclerView.Adapter<alluseradapter.viewholder>{

   ArrayList<allusersmodel> list;
   Context context;
   DatabaseReference databaseReference;

    public alluseradapter(ArrayList<allusersmodel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.sampleallusersshowbig,parent,false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewholder holder, int position) {

        final allusersmodel detailsm=list.get(position);
        holder.textView.setText(detailsm.getUsername());
        Picasso.get().load(detailsm.getUserdp()).networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.userdefaultdp).into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(detailsm.getUserdp()).placeholder(R.drawable.userdefaultdp).into(holder.imageView);
                    }
                });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {

        RoundedImageView imageView;
        TextView textView;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.userdp_samplealluserbig);
            textView=itemView.findViewById(R.id.username_sampleallusershowbig);
           // text2=itemView.findViewById(R.id.friendssum);
        }
    }


}
