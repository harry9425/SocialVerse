package com.harry9425.yaari;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class allpostadapter extends RecyclerView.Adapter<allpostadapter.viewholder> {

    ArrayList<postmodel> list;
    Context context;
    private DatabaseReference mDatabase;
    public allpostadapter(ArrayList<postmodel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public allpostadapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.samplepostlayout,parent,false);
        return new allpostadapter.viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final allpostadapter.viewholder holder, int position) {
        final postmodel detailsm = list.get(position);
        Picasso.get().load(detailsm.getUrl()).networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.userdefaultdp).into(holder.imageView, new Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(detailsm.getUrl()).placeholder(R.drawable.userdefaultdp).into(holder.imageView);
            }
        });
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(context,postpage.class);
                i.putExtra("uid",postfragment.curuser);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.samplepostimageview);
        }
    }
}
