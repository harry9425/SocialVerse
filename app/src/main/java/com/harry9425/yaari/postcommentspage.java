package com.harry9425.yaari;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class postcommentspage extends AppCompatActivity {

    RecyclerView recyclerView;
    postcommentadapter postcommentadapter;
    commentclass commentclass1;
    public static String curuser,curusername=null;
     ArrayList<commentclass> list=new ArrayList<commentclass>();
    public static String postby,postuid;
    DatabaseReference qr;
    public static EditText commentmessage;
    public static TextView replybar;
    ImageButton sendcomment;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postcommentspage);
        postby=getIntent().getStringExtra("postby");
        postuid=getIntent().getStringExtra("postuid");
        replybar=(TextView) findViewById(R.id.replystatus_commentspage);
        replybar.setText("Commenting on Post");
        recyclerView=(RecyclerView) findViewById(R.id.commentspagerecycler);
        commentmessage=(EditText) findViewById(R.id.editcommentmessage);
        sendcomment=(ImageButton) findViewById(R.id.sendcommentserver);
        curuser= FirebaseAuth.getInstance().getCurrentUser().getUid().trim();
        postcommentadapter =new postcommentadapter(list,this);
        recyclerView.setAdapter(postcommentadapter);
        DatabaseReference databaseReference;
        databaseReference=FirebaseDatabase.getInstance().getReference().child("AAA usernames").child(curuser);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                curusername=snapshot.child("username").getValue().toString();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                curusername=null;
            }
        });
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        if(!postby.isEmpty() && !postuid.isEmpty()) {
            enterrr(this);
        }
        else {
            Toast.makeText(this,"Can't load feed now",Toast.LENGTH_SHORT).show();
        }

        replybar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             postcommentadapter.pos=-1;
             postcommentadapter.notifyDataSetChanged();
             replybar.setText("Commenting on Post");
            }
        });
    }

    public void setSendcomment(View view)
    {
        String comment=commentmessage.getText().toString().trim();
        if(!comment.isEmpty())
        {
            if(!postby.isEmpty() && !postuid.isEmpty() && curusername!=null) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.keepSynced(true);
                String commentkey=databaseReference.push().getKey();
                commentclass commentclass=new commentclass();
                commentclass.setUid(curuser);
                commentclass.setDate(System.currentTimeMillis());
                commentclass.setUname(curusername);
                commentclass.setMessage(comment);
                commentclass.setLikes(0);
                commentclass.setCommentid(commentkey);
                commentmessage.setText("");
                hideSoftKeyboard(this);
                if(postcommentadapter.pos!=-1){
                    commentclass.setType("R");
                    databaseReference.child("Posts").child(postby).child(postuid).child("Comments").child(postcommentadapter.replyid).child("replies").child(commentkey).setValue(commentclass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        }});
                }
                else {
                    databaseReference.child("Posts").child(postby).child(postuid).child("Comments").child(commentkey).setValue(commentclass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        }});
                }}
            else {
                Toast.makeText(this,"Can't load feed now",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void enterrr(Context context)
    {
        FirebaseDatabase mDatabase;
        mDatabase= FirebaseDatabase.getInstance();
        mDatabase.getReference().keepSynced(true);
        mDatabase.getReference().child("Posts").child(postby).child(postuid).child("Comments").addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    commentclass1 = snapshot.getValue(commentclass.class);
                    if(snapshot.hasChild("replies")){
                        commentclass1.setType("N");
                        list.add(commentclass1);
                            for (DataSnapshot snapshot1 : snapshot.child("replies").getChildren()) {
                                commentclass commentclass = snapshot1.getValue(commentclass.class);
                                commentclass.setType("R");
                                commentclass.setOutid(snapshot.getKey());
                                list.add(commentclass);
                            }
                    }
                    else {
                        commentclass1.setType("N");
                        list.add(commentclass1);
                    }
                }
                postcommentadapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "CAN'T CONNECT TO SERVER", Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText()){
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(),
                    0
            );
        }
    }

}