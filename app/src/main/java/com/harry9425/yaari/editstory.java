package com.harry9425.yaari;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.Constraints;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.util.NalUnitUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gowtham.library.utils.TrimType;
import com.gowtham.library.utils.TrimVideo;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.harry9425.yaari.addmusic_to_story.link;

public class editstory extends AppCompatActivity {

    PhotoView photoView;
    CircleImageView circleImageView;
    ImageButton bk,cancel,removemusic;
    CardView upload;
    String username;
    public static Uri image;
    public static String userimage=null;
    public static TextView way;
    public static  int initime=0,endtim=0;
    ImageButton ten,thirty;
    public static String songurl=null,songname=null;
    LinearLayout musiccheck,timecheck;
    int duration=15000;
    MediaPlayer mediaPlayer=null;
    Timer timer=null;
    StorageReference mref;
    SeekBar pageseekbar;
    String curuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editstory);
        musiccheck=(LinearLayout) findViewById(R.id.ismusic_there);
        musiccheck.setVisibility(View.GONE);
        ten=(ImageButton) findViewById(R.id.Tensectime_story);
        upload=(CardView) findViewById(R.id.sendstorybtn);
        mref = FirebaseStorage.getInstance().getReference();;
        thirty=(ImageButton) findViewById(R.id.Thirtysectime_story);
        thirty.setAlpha(0.5f);
        removemusic=(ImageButton) findViewById(R.id.removemusicfrstroy);
        ten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                duration=15000;
                pageseekbar.setMax(duration);
                ten.setAlpha(1f);
                thirty.setAlpha(0.5f);
            }
        });
        thirty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                duration=30000;
                pageseekbar.setMax(duration);
                ten.setAlpha(0.5f);
                thirty.setAlpha(1f);
            }
        });
        timecheck=(LinearLayout) findViewById(R.id.timeselectstory);
        timecheck.setVisibility(View.GONE);
        photoView=(PhotoView) findViewById(R.id.storyzoomview);
        circleImageView=(CircleImageView) findViewById(R.id.editstorydp);
        bk=(ImageButton) findViewById(R.id.backfromstorypage);
        image=socialmediapage.posturi;
        pageseekbar=(SeekBar) findViewById(R.id.editstoryseekbar);
        pageseekbar.setVisibility(View.GONE);
        way=(TextView) findViewById(R.id.alertway);
        way.setVisibility(View.GONE);
        curuser= FirebaseAuth.getInstance().getCurrentUser().getUid();
        cancel=(ImageButton) findViewById(R.id.cancelstory);
        removemusic.setVisibility(View.GONE);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), mainpage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        bk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        DatabaseReference mDatabase;
        mDatabase= FirebaseDatabase.getInstance().getReference().child("AAA usernames").child(curuser);
        mDatabase.keepSynced(true);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String dp=snapshot.child("userdp").getValue().toString();
                userimage=dp;
                username=snapshot.child("username").getValue().toString();
                Picasso.get().load(dp).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.userdefaultdp)
                        .into(circleImageView, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(dp).placeholder(R.drawable.userdefaultdp).into(circleImageView);
                            }
                        });
                Picasso.get().load(socialmediapage.posturi).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.userdefaultdp)
                        .into(photoView, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(socialmediapage.posturi).placeholder(R.drawable.userdefaultdp).into(photoView);
                            }
                        });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }});

        way.setTag(way.getVisibility());
        way.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onGlobalLayout() {
                int newVis = way.getVisibility();
                if((int)way.getTag() != newVis)
                {
                    way.setTag(way.getVisibility());
                    if(way.getVisibility()==View.VISIBLE) {
                        way.setVisibility(View.VISIBLE);
                        if(songurl!=null && songname!=null && (endtim>initime)) {
                            setmusiconstory(songurl, songname, initime, endtim);
                        }
                    }
                }
            }
        });

    }

    private void setmusiconstory(String url, String songname, int initime, int endtime) {
        timecheck.setVisibility(View.VISIBLE);
        musiccheck.setVisibility(View.VISIBLE);
        TextView musicchtext=(TextView) findViewById(R.id.displaysongname);
        musicchtext.setText(songname);
        musicchtext.setSelected(true);
        pageseekbar.setVisibility(View.VISIBLE);
        pageseekbar.setProgress(0);
        pageseekbar.setMax(duration);
        if(timer!=null)
        {
            timer.cancel();
        }
        if(mediaPlayer!=null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
        mediaPlayer=new MediaPlayer();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    removemusic.setVisibility(View.VISIBLE);
                    pageseekbar.setProgress(0);
                    mediaPlayer.seekTo(initime);
                    mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                        @Override
                        public void onSeekComplete(MediaPlayer mp) {
                            if(mediaPlayer!=null) {

                                seekbarupdate();
                                mediaPlayer.start();
                            }
                        }
                    });
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            if(timer!=null){ timer.cancel();}
                            mediaPlayer.pause();
                            mediaPlayer.seekTo(initime);
                        }
                    });
                    pageseekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            if(!fromUser) {
                                if (pageseekbar.getProgress() >= pageseekbar.getMax()) {
                                    if (timer != null) {
                                        timer.cancel();
                                    }
                                    mediaPlayer.pause();
                                    mediaPlayer.seekTo(initime);
                                }
                            }
                        }
                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {}
                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                        }
                    });
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void seekbarupdate() {
        timer=new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(mediaPlayer!=null) {
                    pageseekbar.setProgress(mediaPlayer.getCurrentPosition() - initime);
                }
            }
        }, 0, 100);
    }

    public void upload_story(View view){
        if(userimage!= null && username!=null && image!=null) {
            Toast.makeText(editstory.this, "Posting", Toast.LENGTH_SHORT).show();
            DatabaseReference mDatabase;
            mDatabase= FirebaseDatabase.getInstance().getReference();
            String randomkey = mDatabase.push().getKey();
            String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference filepath = mref.child("Story").child(uid).child("Imagestory").child(randomkey);
            filepath.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(
                            new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    String fileLink = task.getResult().toString();
                                    storymodelcalss storymodelcalss = new storymodelcalss(image.toString(), userimage, username);
                                    storymodelcalss.setPostuid(randomkey);
                                    storymodelcalss.setStatus(0);
                                    storymodelcalss.setUid(uid);
                                    storymodelcalss.setIsmusic("n");
                                    storymodelcalss.setTime(System.currentTimeMillis());
                                    if(mediaPlayer!=null){
                                        storymodelcalss.setIsmusic("y");
                                        storymodelcalss.setMusicurl(songurl);
                                        storymodelcalss.setInitime(initime);
                                        storymodelcalss.setEndtime(endtim);
                                    }
                                    mDatabase.child("AAA usernames").child(uid).child("Story").child(randomkey).setValue(storymodelcalss).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Intent intent = new Intent(getApplicationContext(), socialworldpage.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                Toast.makeText(editstory.this, "Posted SUCCESSFULLY", Toast.LENGTH_SHORT).show();

                                            } else {
                                                Toast.makeText(editstory.this,"Something went wrong", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(editstory.this, "ERROR OCCURRED", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mediaPlayer!=null)
        {
            mediaPlayer.pause();
        }
        if(timer!=null){timer.cancel();}
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(mediaPlayer!=null)
        {
            mediaPlayer.start();
        }
        if(timer!=null){timer.cancel();
        seekbarupdate();}
    }

    public void removemusic(View view)
    {
        pageseekbar.setVisibility(View.GONE);
        if(timer!=null){timer.cancel();}
        pageseekbar.setMax(15000);
        pageseekbar.setProgress(0);
        if(mediaPlayer!=null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
        initime=0;
        endtim=0;
        timecheck.setVisibility(View.GONE);
        musiccheck.setVisibility(View.GONE);
        duration=15000;
        songurl=null;
        songname=null;
        removemusic.setVisibility(View.GONE);
        editstory.way.setVisibility(View.GONE);
    }


    public void filteronly(View view)
    {
        Intent dsPhotoEditorIntent = new Intent(this, DsPhotoEditorActivity.class);
        dsPhotoEditorIntent.setData(image);
        dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY, "YAARI");
        int[] toolsToHide = {DsPhotoEditorActivity.TOOL_FRAME,DsPhotoEditorActivity.TOOL_CONTRAST,DsPhotoEditorActivity.TOOL_PIXELATE,DsPhotoEditorActivity.TOOL_DRAW,
                DsPhotoEditorActivity.TOOL_EXPOSURE,DsPhotoEditorActivity.TOOL_CROP,DsPhotoEditorActivity.TOOL_ORIENTATION,DsPhotoEditorActivity.TOOL_ROUND,
                DsPhotoEditorActivity.TOOL_SATURATION,DsPhotoEditorActivity.TOOL_SHARPNESS,DsPhotoEditorActivity.TOOL_STICKER,DsPhotoEditorActivity.TOOL_TEXT,
                DsPhotoEditorActivity.TOOL_VIGNETTE,DsPhotoEditorActivity.TOOL_WARMTH};
        dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_TOOL_BAR_BACKGROUND_COLOR, Color.parseColor("#000000"));
        dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_MAIN_BACKGROUND_COLOR, Color.parseColor("#000000"));
        dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_TOOLS_TO_HIDE, toolsToHide);
        startActivityForResult(dsPhotoEditorIntent, 200);
    }
    public void Textonly(View view)
    {
        Intent dsPhotoEditorIntent = new Intent(this, DsPhotoEditorActivity.class);
        dsPhotoEditorIntent.setData(image);
        dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY, "YAARI");
        int[] toolsToHide = {DsPhotoEditorActivity.TOOL_FRAME,DsPhotoEditorActivity.TOOL_CONTRAST,DsPhotoEditorActivity.TOOL_PIXELATE,DsPhotoEditorActivity.TOOL_DRAW,
                DsPhotoEditorActivity.TOOL_EXPOSURE,DsPhotoEditorActivity.TOOL_CROP,DsPhotoEditorActivity.TOOL_ORIENTATION,DsPhotoEditorActivity.TOOL_ROUND,
                DsPhotoEditorActivity.TOOL_SATURATION,DsPhotoEditorActivity.TOOL_SHARPNESS,DsPhotoEditorActivity.TOOL_STICKER,DsPhotoEditorActivity.TOOL_FILTER,
                DsPhotoEditorActivity.TOOL_VIGNETTE,DsPhotoEditorActivity.TOOL_WARMTH};
        dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_TOOL_BAR_BACKGROUND_COLOR, Color.parseColor("#000000"));
        dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_MAIN_BACKGROUND_COLOR, Color.parseColor("#000000"));
        dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_TOOLS_TO_HIDE, toolsToHide);
        startActivityForResult(dsPhotoEditorIntent, 200);
    }
    public void frameonly(View view)
    {
        Intent dsPhotoEditorIntent = new Intent(this, DsPhotoEditorActivity.class);
        dsPhotoEditorIntent.setData(image);
        dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY, "YAARI");
        int[] toolsToHide = {DsPhotoEditorActivity.TOOL_FILTER,DsPhotoEditorActivity.TOOL_CONTRAST,DsPhotoEditorActivity.TOOL_PIXELATE,DsPhotoEditorActivity.TOOL_DRAW,
                DsPhotoEditorActivity.TOOL_EXPOSURE,DsPhotoEditorActivity.TOOL_CROP,DsPhotoEditorActivity.TOOL_ORIENTATION,DsPhotoEditorActivity.TOOL_ROUND,
                DsPhotoEditorActivity.TOOL_SATURATION,DsPhotoEditorActivity.TOOL_SHARPNESS,DsPhotoEditorActivity.TOOL_STICKER,DsPhotoEditorActivity.TOOL_TEXT,
                DsPhotoEditorActivity.TOOL_VIGNETTE,DsPhotoEditorActivity.TOOL_WARMTH};
        dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_TOOL_BAR_BACKGROUND_COLOR, Color.parseColor("#000000"));
        dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_MAIN_BACKGROUND_COLOR, Color.parseColor("#000000"));
        dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_TOOLS_TO_HIDE, toolsToHide);
        startActivityForResult(dsPhotoEditorIntent, 200);
    }
    public void drawonly(View view)
    {
        Intent dsPhotoEditorIntent = new Intent(this, DsPhotoEditorActivity.class);
        dsPhotoEditorIntent.setData(image);
        dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY, "YAARI");
        int[] toolsToHide = {DsPhotoEditorActivity.TOOL_FRAME,DsPhotoEditorActivity.TOOL_CONTRAST,DsPhotoEditorActivity.TOOL_PIXELATE,DsPhotoEditorActivity.TOOL_FILTER,
                DsPhotoEditorActivity.TOOL_EXPOSURE,DsPhotoEditorActivity.TOOL_CROP,DsPhotoEditorActivity.TOOL_ORIENTATION,DsPhotoEditorActivity.TOOL_ROUND,
                DsPhotoEditorActivity.TOOL_SATURATION,DsPhotoEditorActivity.TOOL_SHARPNESS,DsPhotoEditorActivity.TOOL_STICKER,DsPhotoEditorActivity.TOOL_TEXT,
                DsPhotoEditorActivity.TOOL_VIGNETTE,DsPhotoEditorActivity.TOOL_WARMTH};
        dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_TOOL_BAR_BACKGROUND_COLOR, Color.parseColor("#000000"));
        dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_MAIN_BACKGROUND_COLOR, Color.parseColor("#000000"));
        dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_TOOLS_TO_HIDE, toolsToHide);
        startActivityForResult(dsPhotoEditorIntent, 200);
    }

    public void addmusic(View view)
    {
        pageseekbar.setVisibility(View.GONE);
        Intent i=new Intent(this,addmusic_to_story.class);
        i.putExtra("from","editstory");
        startActivity(i);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 200:
                    Uri resultUri = data.getData();
                    image=resultUri;
                    Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.wallpaperbg)
                            .into(photoView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    // sendmess();
                                }
                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(image).placeholder(R.drawable.wallpaperbg).into(photoView);
                                }
                            });
                    break;

            }
        }
    }
}