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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class trackadapter extends RecyclerView.Adapter<trackadapter.viewholder> {

    ArrayList<trackmodelclass> list;
    Context context;
    int a=-100;
    public trackadapter(ArrayList<trackmodelclass> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public trackadapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.sountracks_sample,parent,false);
        return new trackadapter.viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        final trackmodelclass val=list.get(position);
        holder.name.setText(val.getName());
        holder.artist.setText(val.getArtist());
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addmusic_to_story.player.getVisibility()==View.VISIBLE) {
                    if (position != a) {
                        addmusic_to_story.playersetup(val.getArtist(), val.getName(), val.getUrl());
                    }
                    a = position;
                }
                else {
                    addmusic_to_story.playersetup(val.getArtist(), val.getName(), val.getUrl());
                    a = position;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {

        TextView name, artist;
        LinearLayout linearLayout;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.trackname);
            linearLayout=itemView.findViewById(R.id.trackcard);
            artist = itemView.findViewById(R.id.trackartist);
        }
    }
}
