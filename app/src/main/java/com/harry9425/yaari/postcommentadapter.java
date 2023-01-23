package com.harry9425.yaari;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class postcommentadapter extends RecyclerView.Adapter {

    ArrayList<commentclass> list;
    Context context;
    private DatabaseReference mDatabase;
    int normal=1,Reply=2,pos=-1;
    String replyid;
    public postcommentadapter(ArrayList<commentclass> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if(list.get(position).getType().equals("N")){
            return  normal;
        }
        else {
            return Reply;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==normal)
        {
            View view= LayoutInflater.from(context).inflate(R.layout.sample_commentsview,parent,false);

            return new normalholder(view);
        }
        else{
            View view= LayoutInflater.from(context).inflate(R.layout.sample_commentreplyview,parent,false);
            return new replyholder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final commentclass detailsm=list.get(position);
        
        if(holder.getClass()==normalholder.class){
            
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(detailsm.getDate());
            int mYear = calendar.get(Calendar.YEAR);
            int mMonth=calendar.get(Calendar.MONTH);
            int mDay = calendar.get(Calendar.DAY_OF_MONTH);
            int mhour=calendar.get(Calendar.HOUR_OF_DAY);
            int mmin=calendar.get(Calendar.MINUTE);
            Calendar c2=Calendar.getInstance();
            if(c2.get(Calendar.YEAR)>mYear){
                int v=(c2.get(Calendar.YEAR)-mYear);
                if(v<0) v=v*(-1);
                ((normalholder)holder).time.setText(v+" yr");
            }
            else {
                if(c2.get(Calendar.MONTH)>mMonth){
                    int v=(c2.get(Calendar.MONTH)-mMonth);
                    if(v<0) v=v*(-1);
                    ((normalholder)holder).time.setText(v+" mon");
                }
                else {
                    if(c2.get(Calendar.DAY_OF_MONTH)>mDay){
                        int v=(c2.get(Calendar.DAY_OF_MONTH)-mDay);
                        if(v<0) v=v*(-1);
                        ((normalholder)holder).time.setText(v+" day");
                    }
                    else {
                        if(c2.get(Calendar.HOUR_OF_DAY)>mhour){
                            int v=(c2.get(Calendar.HOUR_OF_DAY)-mhour);
                            if(v<0) v=v*(-1);
                            ((normalholder)holder).time.setText(v+" hr");
                        }
                        else {
                            if(c2.get(Calendar.MINUTE)>mmin){
                                ((normalholder)holder).time.setText((c2.get(Calendar.MINUTE)-mmin)+" min");
                            }
                            else {
                                ((normalholder)holder).time.setText("now");
                            }
                        }
                    }
                }
            }
            mDatabase=FirebaseDatabase.getInstance().getReference().child("AAA usernames");
            mDatabase.keepSynced(true);
            mDatabase.child(detailsm.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    detailsm.setDp(snapshot.child("userdp").getValue().toString());
                    detailsm.setUname(snapshot.child("username").getValue().toString());
                    SpannableString ss = new SpannableString(detailsm.getUname()+"\t\t"+detailsm.getMessage());
                    StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
                    ss.setSpan(boldSpan, 0, detailsm.getUname().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ((normalholder)holder).message.setText(ss);
                    Picasso.get().load(detailsm.getDp()).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.userdefaultdp).into(((normalholder)holder).circleImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(detailsm.dp).placeholder(R.drawable.userdefaultdp).into(((normalholder)holder).circleImageView);
                        }
                    });
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
            DatabaseReference databaseReference;
            databaseReference=FirebaseDatabase.getInstance().getReference();
            databaseReference.keepSynced(true);
            ((normalholder)holder).likebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(((normalholder)holder).likebtn.getAlpha()==1f) {
                        databaseReference.child("Posts").child(postcommentspage.postby).child(postcommentspage.postuid)
                                .child("Comments").child(detailsm.commentid).child("likesshow").child(postcommentspage.curuser).setValue("liked").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                ((normalholder)holder).likebtn.setAlpha(0.99f);
                                ((normalholder)holder).likebtn.setImageResource(R.drawable.ic_baseline_thumb_up_24);
                            }
                        });
                    }
                    else {
                        databaseReference.child("Posts").child(postcommentspage.postby).child(postcommentspage.postuid)
                                .child("Comments").child(detailsm.commentid).child("likesshow").child(postcommentspage.curuser).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                ((normalholder)holder).likebtn.setAlpha(1f);
                                ((normalholder)holder).likebtn.setImageResource(R.drawable.ic_baseline_thumb_up_off_alt_24);
                            }
                        });
                    }
                }
            });
            databaseReference.child("Posts").child(postcommentspage.postby).child(postcommentspage.postuid)
                    .child("Comments").child(detailsm.commentid).child("likesshow").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getChildrenCount()!=0) {
                        ((normalholder) holder).like.setText(snapshot.getChildrenCount() + " Likes");
                    }else ((normalholder) holder).like.setText("No likes");

                    if(snapshot.hasChild(postcommentspage.curuser)){
                        ((normalholder)holder).likebtn.setAlpha(0.99f);
                        ((normalholder)holder).likebtn.setImageResource(R.drawable.ic_baseline_thumb_up_24);
                    }
                    else {
                        ((normalholder)holder).likebtn.setAlpha(1f);
                        ((normalholder)holder).likebtn.setImageResource(R.drawable.ic_baseline_thumb_up_off_alt_24);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
            if(pos!=position){
                ((normalholder)holder).reply.setText("Reply");
                ((normalholder)holder).reply.setTextColor(Color.parseColor("#CCCCCC"));
            }else {
                postcommentspage.replybar.setText("Replying to "+detailsm.getUname());
                ((normalholder)holder).reply.setText("Replying");
                ((normalholder)holder).reply.setTextColor(Color.parseColor("#FFE91E63"));
                replyid=detailsm.getCommentid();
            }
            ((normalholder)holder).reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    postcommentspage.replybar.setText("Replying to "+detailsm.getUname());
                    ((normalholder)holder).reply.setText("Replying");
                    pos=position;
                    replyid=detailsm.getCommentid();
                    ((normalholder)holder).reply.setTextColor(Color.parseColor("#FFE91E63"));
                    notifyDataSetChanged();
                }
            });
        }
        else {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(detailsm.getDate());
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth=calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);
                int mhour=calendar.get(Calendar.HOUR_OF_DAY);
                int mmin=calendar.get(Calendar.MINUTE);
                Calendar c2=Calendar.getInstance();
                if(c2.get(Calendar.YEAR)>mYear){
                    int v=(c2.get(Calendar.YEAR)-mYear);
                    if(v<0) v=v*(-1);
                    ((replyholder)holder).time.setText(v+" yr");
                }
                else {
                    if(c2.get(Calendar.MONTH)>mMonth){
                        int v=(c2.get(Calendar.MONTH)-mMonth);
                        if(v<0) v=v*(-1);
                        ((replyholder)holder).time.setText(v+" mon");
                    }
                    else {
                        if(c2.get(Calendar.DAY_OF_MONTH)>mDay){
                            int v=(c2.get(Calendar.DAY_OF_MONTH)-mDay);
                            if(v<0) v=v*(-1);
                            ((replyholder)holder).time.setText(v+" day");
                        }
                        else {
                            if(c2.get(Calendar.HOUR_OF_DAY)>mhour){
                                int v=(c2.get(Calendar.HOUR_OF_DAY)-mhour);
                                if(v<0) v=v*(-1);
                                ((replyholder)holder).time.setText(v+" hr");
                            }
                            else {
                                if(c2.get(Calendar.MINUTE)>mmin){
                                    ((replyholder)holder).time.setText((c2.get(Calendar.MINUTE)-mmin)+" min");
                                }
                                else {
                                    ((replyholder)holder).time.setText("now");
                                }
                            }
                        }
                    }
                }
                mDatabase=FirebaseDatabase.getInstance().getReference().child("AAA usernames");
                mDatabase.keepSynced(true);
                mDatabase.child(detailsm.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        detailsm.setDp(snapshot.child("userdp").getValue().toString());
                        detailsm.setUname(snapshot.child("username").getValue().toString());
                        SpannableString ss = new SpannableString(detailsm.getUname()+"\t\t"+detailsm.getMessage());
                        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
                        ss.setSpan(boldSpan, 0, detailsm.getUname().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ((replyholder)holder).message.setText(ss);
                        Picasso.get().load(detailsm.getDp()).networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.userdefaultdp).into(((replyholder)holder).circleImageView, new Callback() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(detailsm.dp).placeholder(R.drawable.userdefaultdp).into(((replyholder)holder).circleImageView);
                            }
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                DatabaseReference databaseReference;
                databaseReference=FirebaseDatabase.getInstance().getReference();
                databaseReference.keepSynced(true);
                ((replyholder)holder).likebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(((replyholder)holder).likebtn.getAlpha()==1f) {
                            databaseReference.child("Posts").child(postcommentspage.postby).child(postcommentspage.postuid)
                                    .child("Comments").child(detailsm.outid).child("replies").child(detailsm.commentid).child("likesshow").child(postcommentspage.curuser).setValue("liked").addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    ((replyholder)holder).likebtn.setAlpha(0.99f);
                                    ((replyholder)holder).likebtn.setImageResource(R.drawable.ic_baseline_thumb_up_24);
                                }
                            });
                        }
                        else {
                            databaseReference.child("Posts").child(postcommentspage.postby).child(postcommentspage.postuid)
                                    .child("Comments").child(detailsm.outid).child("replies").child(detailsm.commentid).child("likesshow").child(postcommentspage.curuser).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    ((replyholder)holder).likebtn.setAlpha(1f);
                                    ((replyholder)holder).likebtn.setImageResource(R.drawable.ic_baseline_thumb_up_off_alt_24);
                                }
                            });
                        }
                    }
                });
                databaseReference.child("Posts").child(postcommentspage.postby).child(postcommentspage.postuid)
                        .child("Comments").child(detailsm.outid).child("replies").child(detailsm.commentid).child("likesshow").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getChildrenCount()!=0) {
                            ((replyholder) holder).like.setText(snapshot.getChildrenCount() + " Likes");
                        }else ((replyholder) holder).like.setText("No likes");
                        if(snapshot.hasChild(postcommentspage.curuser)){
                            ((replyholder)holder).likebtn.setAlpha(0.99f);
                            ((replyholder)holder).likebtn.setImageResource(R.drawable.ic_baseline_thumb_up_24);
                        }
                        else {
                            ((replyholder)holder).likebtn.setAlpha(1f);
                            ((replyholder)holder).likebtn.setImageResource(R.drawable.ic_baseline_thumb_up_off_alt_24);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                if(pos!=position){
                    ((replyholder)holder).reply.setText("Reply");
                    ((replyholder)holder).reply.setTextColor(Color.parseColor("#CCCCCC"));
                }else {
                    postcommentspage.replybar.setText("Replying to "+detailsm.getUname());
                    ((replyholder)holder).reply.setText("Replying");
                    ((replyholder)holder).reply.setTextColor(Color.parseColor("#FFE91E63"));
                    if(detailsm.getOutid()==null) {
                        replyid = detailsm.getCommentid();
                    }
                    else {
                        replyid = detailsm.getOutid();
                    }
                }
                ((replyholder)holder).reply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        postcommentspage.replybar.setText("Replying to "+detailsm.getUname());
                        ((replyholder)holder).reply.setText("Replying");
                        pos=position;
                        if(detailsm.getOutid()==null) {
                            replyid = detailsm.getCommentid();
                        }
                        else {
                            replyid = detailsm.getOutid();
                        }
                        ((replyholder)holder).reply.setTextColor(Color.parseColor("#FFE91E63"));
                        notifyDataSetChanged();
                    }
                });
            }
        }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class normalholder extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        TextView message,time,like,reply,more;
        ImageButton likebtn;

        public normalholder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.commentviewdp);
            message=itemView.findViewById(R.id.commentviewmessage);
            more=itemView.findViewById(R.id.samplecomment_more);
            time=itemView.findViewById(R.id.samplecommentdate);
            likebtn=itemView.findViewById(R.id.samplecommentlike);
            like=itemView.findViewById(R.id.samplecommentlikeshow);
            reply=itemView.findViewById(R.id.samplecomment_replybtn);
        }
    }

    public class replyholder extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        TextView message,time,like,reply;
        ImageButton likebtn;

        public replyholder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.commentreplyviewdp);
            message=itemView.findViewById(R.id.commentreplyviewmessage);
            time=itemView.findViewById(R.id.samplecommentreplydate);
            likebtn=itemView.findViewById(R.id.samplecommentreplylike);
            like=itemView.findViewById(R.id.samplecommentreplylikeshow);
            reply=itemView.findViewById(R.id.samplecommentreply_replybtn);
        }
    }

}
