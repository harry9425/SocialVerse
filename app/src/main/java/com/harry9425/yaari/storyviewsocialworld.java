package com.harry9425.yaari;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.carlosmuvi.segmentedprogressbar.SegmentedProgressBar;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

public class storyviewsocialworld extends AppCompatActivity {

    public static storymodelcalss storymodelcalss;
    PhotoView photoView;
    SegmentedProgressBar segmentedProgressBar;
    ArrayList<storymodelcalss> arrayList=new ArrayList<storymodelcalss>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storyviewsocialworld);
        photoView=(PhotoView) findViewById(R.id.storyviewpagesocialworld);
        Picasso.get().load(storymodelcalss.getImageurl()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.wallpaperbg)
                .into(photoView, new Callback() {
                    @Override
                    public void onSuccess() {
                        // sendmess();
                    }
                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(storymodelcalss.getImageurl()).placeholder(R.drawable.wallpaperbg).into(photoView);
                    }
                });
        segmentedProgressBar = (SegmentedProgressBar) findViewById(R.id.segmented_progressbar);
        segmentedProgressBar.setContainerColor(Color.BLUE);
        segmentedProgressBar.setFillColor(Color.GREEN);
        segmentedProgressBar.setContainerColor(Color.BLUE);
        segmentedProgressBar.setFillColor(Color.GREEN);
        storylistener();
        //play next segment specifying its duration segmentedProgressBar.playSegment(5000);
        //pause segment segmentedProgressBar.pause();
        //reset segmentedProgressBar.reset();
        //set filled segments directly segmentedProgressBar.setCompletedSegments(3);
        //fill the next empty segment without animation segmentedProgressBar.incrementCompletedSegments();
    }
    private void storylistener(){
        FirebaseDatabase mDatabase;
        mDatabase= FirebaseDatabase.getInstance();
        mDatabase.getReference().keepSynced(true);
        mDatabase.getReference().child("AAA usernames").child(storymodelcalss.getUid()).child("Story").addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    segmentedProgressBar.setSegmentCount((int) dataSnapshot.getChildrenCount());
                    arrayList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                         storymodelcalss storymodelcalss =snapshot.getValue(storymodelcalss.class);
                         storymodelcalss.setCount((int) dataSnapshot.getChildrenCount());
                         arrayList.add(storymodelcalss);
                    }
                    Collections.reverse(arrayList);
                    Toast.makeText(storyviewsocialworld.this,arrayList.toString(),Toast.LENGTH_LONG).show();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(storyviewsocialworld.this, "CAN'T CONNECT TO SERVER", Toast.LENGTH_LONG).show();
            }
        });
    }
}