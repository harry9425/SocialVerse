package com.harry9425.yaari;

import android.content.Context;
import android.content.Intent;
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

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class postpageadpater extends RecyclerView.Adapter<postpageadpater.viewholder> {

    ArrayList<postmodel> list;
    ArrayList<String> likelist=new ArrayList<String>();
    Context context;
    String uid;
    String curuser,dp;
    String likekey;

    private DatabaseReference mDatabase;

    public postpageadpater(ArrayList<postmodel> list, Context context,String uid) {
        this.list = list;
        this.context = context;
        this.curuser=uid;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @NonNull
    @Override
    public postpageadpater.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.samplepostpage_layout,parent,false);
        return new postpageadpater.viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final postpageadpater.viewholder holder, int position) {
        final postmodel detailsm = list.get(position);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(detailsm.getTime());
        int mYear = calendar.get(Calendar.YEAR);
        String mMonth = getMonthFormat(calendar.get(Calendar.MONTH));
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        holder.date.setText(mDay+" "+mMonth+" "+mYear);
        SpannableString ss = new SpannableString("Caption - "+detailsm.caption);
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        ss.setSpan(boldSpan, 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.caption.setText(ss);
        holder.comment.setSelected(true);
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
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(context,postcommentspage.class);
                i.putExtra("postby",detailsm.getPostby());
                i.putExtra("postuid",detailsm.getPostuid());
                context.startActivity(i);
            }
        });
        holder.showcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(context,postcommentspage.class);
                i.putExtra("postby",detailsm.getPostby());
                i.putExtra("postuid",detailsm.getPostuid());
                context.startActivity(i);
            }
        });
        Picasso.get().load(dp).networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.userdefaultdp).into(holder.maindp, new Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(dp).placeholder(R.drawable.userdefaultdp).into(holder.maindp);
            }
        });
        DatabaseReference databaseReference;
        databaseReference=FirebaseDatabase.getInstance().getReference().child("Posts").child(detailsm.getPostby()).child(detailsm.getPostuid());
        databaseReference.keepSynced(true);
        databaseReference.child("Comments").addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long count=dataSnapshot.getChildrenCount();
                if(count==1) {
                    holder.showcomment.setText("Expand and show comment");
                    holder.shortcommentview.setVisibility(View.VISIBLE);
                    holder.shortcommentview.setText("");
                    Query q=databaseReference.child("Comments");
                    q.limitToLast(3).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot1:snapshot.getChildren()){
                                String val=dataSnapshot1.child("uname").getValue().toString();
                                SpannableString ss = new SpannableString(val+"\t\t"+dataSnapshot1.child("message").getValue().toString()+"\n");
                                StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
                                ss.setSpan(boldSpan, 0, val.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                holder.shortcommentview.append(ss);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else if(count==0){
                    holder.showcomment.setText("No comments till now\n");
                    holder.shortcommentview.setVisibility(View.GONE);
                }
                else {
                    holder.shortcommentview.setVisibility(View.VISIBLE);
                    holder.showcomment.setText("Expand and show all "+count+" comments");
                    holder.shortcommentview.setText("");
                    Query q=databaseReference.child("Comments");
                    q.limitToLast(3).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot1:snapshot.getChildren()){
                                String val=dataSnapshot1.child("uname").getValue().toString();
                                SpannableString ss = new SpannableString(val+"\t\t"+dataSnapshot1.child("message").getValue().toString()+"\n");
                                StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
                                ss.setSpan(boldSpan, 0, val.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                holder.shortcommentview.append(ss);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                holder.showcomment.setText("Expand and show all comments");
                holder.shortcommentview.setVisibility(View.GONE);
            }
        });
        holder.name.setText(uid);
        likecheck(holder,detailsm);
        setliketext(holder,detailsm);
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference;
                databaseReference=FirebaseDatabase.getInstance().getReference();
                databaseReference.keepSynced(true);
                if(holder.like.getAlpha()==1f) {
                    likekey=databaseReference.push().getKey();
                    databaseReference.child("Posts").child(detailsm.getPostby()).child(detailsm.postuid).child("likes").child(likekey).setValue(curuser).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            holder.like.setAlpha(0.99f);
                            holder.like.setImageResource(R.drawable.ic_baseline_thumb_up_24);
                        }
                    });
                }
                else {
                    databaseReference.child("Posts").child(detailsm.getPostby()).child(detailsm.postuid).child("likes").child(likekey).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            holder.like.setAlpha(1f);
                            holder.like.setImageResource(R.drawable.ic_baseline_thumb_up_off_alt_24);
                        }
                    });

                }
            }
        });
    }

    private void likecheck(postpageadpater.viewholder holder,postmodel detailsm)
    {
        DatabaseReference databaseReference;
        databaseReference=FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);
        databaseReference.child("Posts").child(detailsm.getPostby()).child(detailsm.postuid).child("likes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getChildrenCount();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    if(dataSnapshot.getValue().toString().equals(curuser)){
                        holder.like.setAlpha(0.99f);
                        holder.like.setImageResource(R.drawable.ic_baseline_thumb_up_24);
                        likekey=dataSnapshot.getKey();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void setliketext(postpageadpater.viewholder holder,postmodel detailsm)
    {
    DatabaseReference mdatabase=FirebaseDatabase.getInstance().getReference().child("Posts").child(detailsm.getPostby()).child(detailsm.getPostuid()).child("likes");
    Query q=mdatabase.limitToLast(1);
    q.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if(snapshot.exists()) {
                holder.likedp.setVisibility(View.VISIBLE);
                String[] val=snapshot.getValue().toString().split("=");
                String uid =val[1].replaceAll("\\}","");
                DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("AAA usernames").child(uid);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String dp=snapshot.child("userdp").getValue().toString();
                        String name=snapshot.child("username").getValue().toString();
                        mdatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int likescount=(int) snapshot.getChildrenCount();
                                if(likescount!=1) {
                                    holder.likescount.setText("Recently liked by "+name+" and " + (likescount-1) + " others");
                                }
                                else {
                                    holder.likescount.setText("Recently liked by "+name+" only");
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}});

                        Picasso.get().load(dp).networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.userdefaultdp).into(holder.likedp, new Callback() {
                            @Override
                            public void onSuccess() {
                            }
                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(dp).placeholder(R.drawable.userdefaultdp).into(holder.likedp);
                            }
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
            else {
                holder.likescount.setText("No Likes till now");
                holder.likedp.setVisibility(View.GONE);
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            holder.likescount.setText(View.VISIBLE);
            holder.likescount.setText("No Likes till now");
            holder.likedp.setVisibility(View.GONE);
        }
    });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class viewholder extends RecyclerView.ViewHolder {

        PhotoView imageView;
        CircleImageView maindp,likedp;
        TextView name,showcomment,caption;
        TextView likescount,date,shortcommentview;
        ImageButton like,comment;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.allpostviewimage);
            maindp=itemView.findViewById(R.id.allpostviewdp);
            likescount=itemView.findViewById(R.id.allpostviewlikecount);
            name=itemView.findViewById(R.id.allpostviewname);
            like=itemView.findViewById(R.id.allpostviewlike);
            likedp=itemView.findViewById(R.id.allpostviewcommentdp);
            date=itemView.findViewById(R.id.allpostviewdate);
            showcomment=itemView.findViewById(R.id.allpostviewcommentcountandopen);
            caption=itemView.findViewById(R.id.allpostviewcaption);
            comment=itemView.findViewById(R.id.allpostviewcomment);
            shortcommentview=itemView.findViewById(R.id.allpostview3commentshow);
        }
    }
    private String getMonthFormat(int month)
    {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }
}
