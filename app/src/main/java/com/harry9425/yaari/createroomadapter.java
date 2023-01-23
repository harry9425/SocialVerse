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
import android.widget.Toast;

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

public class createroomadapter extends RecyclerView.Adapter<createroomadapter.viewholder>{

    ArrayList<allusersmodel> list;
    Context context;
    DatabaseReference databaseReference;

    public createroomadapter(ArrayList<allusersmodel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.sampleallusersshow,parent,false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewholder holder, int position) {
        final allusersmodel detailsm=list.get(position);
        holder.text2.setSelected(true);
        holder.text2.setText(detailsm.getStatus());
        holder.textView.setText(detailsm.getUsername());
        if(detailsm.getRoomselect()==null){
            holder.imageButton.setVisibility(View.INVISIBLE);
        }
        else {
            holder.imageButton.setVisibility(View.VISIBLE);
            if(!createroom_activity.sellist.contains(detailsm)) {
                createroom_activity.sellist.add(detailsm);
            }
        }
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
        int scroll=createroom_activity.sellist.size()-1;
        if(scroll>=0) {
            createroom_activity.selectedcount.setText((scroll+1)+" selected");
            createroom_activity.selectedrecycler.smoothScrollToPosition(scroll);
        }
        else {
            createroom_activity.selectedcount.setText("Select Participants");
        }
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(detailsm.getRoomselect()==null){
                    createroom_activity.save.setVisibility(View.VISIBLE);
                    holder.imageButton.setVisibility(View.VISIBLE);
                    detailsm.setRoomselect("s");
                    if(createroom_activity.selectedrecycler.getVisibility()==View.GONE){
                        createroom_activity.selectedrecycler.setVisibility(View.VISIBLE);
                    }
                    if(!createroom_activity.sellist.contains(detailsm)) {
                        createroom_activity.sellist.add(detailsm);
                        createroom_activity.selectedroomadapter.notifyDataSetChanged();
                        int scroll=createroom_activity.sellist.size()-1;
                        if(scroll>=0) {
                            createroom_activity.selectedcount.setText((scroll+1)+" selected");
                            createroom_activity.selectedrecycler.smoothScrollToPosition(scroll);
                        }
                        else {
                            createroom_activity.save.setVisibility(View.GONE);
                            createroom_activity.selectedcount.setText("Select Participants");
                        }
                    }
                }
                else {
                    holder.imageButton.setVisibility(View.INVISIBLE);
                    detailsm.setRoomselect(null);
                    if(createroom_activity.sellist.contains(detailsm)) {
                        int pos=createroom_activity.sellist.indexOf(detailsm);
                        createroom_activity.sellist.remove(pos);
                        createroom_activity.selectedroomadapter.notifyDataSetChanged();
                        int scroll=createroom_activity.sellist.size()-1;
                        if(scroll>=0) {
                            createroom_activity.selectedcount.setText((scroll+1)+" selected");
                            createroom_activity.selectedrecycler.smoothScrollToPosition(scroll);
                        }
                        else {
                            createroom_activity.save.setVisibility(View.GONE);
                            createroom_activity.selectedcount.setText("Select Participants");
                            createroom_activity.selectedrecycler.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public void filteredlist(ArrayList<allusersmodel> temp) {
        list=temp;
        notifyDataSetChanged();
    }

    public class viewholder extends RecyclerView.ViewHolder {

        CircleImageView imageView;
        TextView textView,text2;
        LinearLayout linearLayout;
        ImageButton imageButton;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.alluserprofilechat);
            textView=itemView.findViewById(R.id.allusername);
             text2=itemView.findViewById(R.id.status_time);
             linearLayout=itemView.findViewById(R.id.linearroomuser);
             imageButton=itemView.findViewById(R.id.selected_or_not);
        }
    }


}
