package com.harry9425.yaari;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
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

public class selectedroomadapter extends RecyclerView.Adapter<selectedroomadapter.viewholder>{

    ArrayList<allusersmodel> list;
    Context context;
    DatabaseReference databaseReference;

    public selectedroomadapter(ArrayList<allusersmodel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.selectedroomlayout,parent,false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewholder holder, int position) {
        final allusersmodel detailsm=list.get(position);
        holder.textView.setSelected(true);
        holder.textView.setText("X "+detailsm.getUsername());
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
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.remove(list.indexOf(detailsm));
                createroom_activity.list.get(createroom_activity.list.indexOf(detailsm)).setRoomselect(null);
                createroom_activity.createroomadapter.notifyDataSetChanged();
                notifyDataSetChanged();
                if(list.size()==0){
                    createroom_activity.save.setVisibility(View.GONE);
                    createroom_activity.selectedrecycler.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {

        CircleImageView imageView;
        TextView textView;
        ConstraintLayout constraintLayout;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.selectedroomuserdp);
            textView=itemView.findViewById(R.id.selectedusername);
        }
    }


}
