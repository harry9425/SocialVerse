package com.harry9425.yaari;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URI;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class editreels extends AppCompatActivity {

    PlayerView playerView;
    SimpleExoPlayer simpleExoPlayer;
    SeekBar seekBar;
    public  static Uri reeluri;
    public static float speed;
    public static TextView way;
    public static  int initime=0,endtim=0;
    public static String songurl=null,songname=null;
    CircleImageView circleImageView;
    ImageButton removemusic;
    TextView displaysongname;
    int duration=0,pos=0;
    LinearLayout musiccheck;
    CardView volumecard;
    public static int mv=100,ov=100;
    MediaPlayer mediaPlayer=null;
    Timer timer=null;
    StorageReference mref;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editreels);
        removemusic=(ImageButton) findViewById(R.id.removemusicfromreel);
        volumecard=(CardView) findViewById(R.id.volume_controls_for_reels);
        volumecard.setVisibility(View.GONE);
        mref= FirebaseStorage.getInstance().getReference();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Uploading video :");
        circleImageView=(CircleImageView) findViewById(R.id.editreeldp);
        displaysongname=(TextView) findViewById(R.id.displaysongnamereels);
        musiccheck=(LinearLayout) findViewById(R.id.ismusic_there_reels);
        musiccheck.setVisibility(View.GONE);
        seekBar=(SeekBar) findViewById(R.id.reelseekbar);
        seekBar.setProgress(0);
        seekBar.setVisibility(View.VISIBLE);
        way=(TextView) findViewById(R.id.alertwayforreels);
        way.setVisibility(View.GONE);
        playerView=(PlayerView) findViewById(R.id.reelview);
        DatabaseReference mDatabase;
        mDatabase= FirebaseDatabase.getInstance().getReference().child("AAA usernames").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mDatabase.keepSynced(true);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String dp=snapshot.child("userdp").getValue().toString();
                Picasso.get().load(dp).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.userdefaultdp)
                        .into(circleImageView, new Callback() {
                            @Override
                            public void onSuccess() {}
                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(dp).placeholder(R.drawable.userdefaultdp).into(circleImageView);}});
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }});
        try {
            simpleExoPlayer = new SimpleExoPlayer.Builder(this).build();
            playerView.setPlayer(simpleExoPlayer);
            reeluri=socialmediapage.reeluri;
            MediaItem mediaItem = MediaItem.fromUri(reeluri);
            simpleExoPlayer.addMediaItems(Collections.singletonList(mediaItem));
            Toast.makeText(editreels.this, mediaItem+"\n\n\n"+Collections.singletonList(mediaItem), Toast.LENGTH_SHORT).show();
            simpleExoPlayer.prepare();
            playerView.hideController();
            playerView.setUseController(false);
            speed=socialmediapage.inispeed;
            simpleExoPlayer.setPlaybackSpeed(speed);
            simpleExoPlayer.setPlayWhenReady(true);
            simpleExoPlayer.addListener(new Player.Listener() {
                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    if(playbackState==SimpleExoPlayer.STATE_READY && playWhenReady){
                        if(timer!=null) timer.cancel();
                        duration= (int) (simpleExoPlayer.getDuration()/speed);
                        seekBar.setMax((int) (duration*speed));
                        seekbarupdate();
                    }
                    else if(playbackState==SimpleExoPlayer.STATE_READY)
                    {
                        if(timer!=null){timer.cancel();}
                    }
                    else if(playbackState==SimpleExoPlayer.STATE_ENDED)
                    {
                        pos=0;
                        if(timer!=null) timer.cancel();
                        seekBar.setProgress(0);
                        simpleExoPlayer.seekTo(0);
                        simpleExoPlayer.pause();
                        if(mediaPlayer!=null)
                        {
                            mediaPlayer.seekTo(initime);
                        }
                        else {
                            simpleExoPlayer.setPlayWhenReady(true);
                        }
                    }
                }
            });
        }catch (NumberFormatException e) {
            e.printStackTrace();
        }
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
                        if(songurl!=null && songname!=null) {
                            setupplayer();
                        }
                    }
                }
            }
        });
        removemusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer!=null)
                {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer=null;
                }
                initime=0;
                endtim=0;
                musiccheck.setVisibility(View.GONE);
                duration=15000;
                songurl=null;
                songname=null;
                removemusic.setVisibility(View.GONE);
                editreels.way.setVisibility(View.GONE);
            }
        });
        CardView save=(CardView) findViewById(R.id.savereel);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(musiccheck.getVisibility()==View.VISIBLE)
                {
                    uploadvideo(reeluri, speed, true, mv, ((int)(simpleExoPlayer.getVolume()*100)), initime, duration);
                }
                else {
                    Toast.makeText(editreels.this,reeluri.toString(),Toast.LENGTH_SHORT).show();
                    uploadvideo(reeluri, speed, false, 100, 100, 0, duration);
                }
            }
        });
    }
    private void setupplayer()
    {

        if(simpleExoPlayer!=null)
        {
            simpleExoPlayer.pause();
            simpleExoPlayer.seekTo(0);
            if(timer!=null) timer.cancel();
            seekBar.setProgress(0);
            pos=0;
            simpleExoPlayer.setVolume(ov*0.01f);
        }
        musiccheck.setVisibility(View.VISIBLE);
        displaysongname.setText(songname);
        displaysongname.setSelected(true);
        removemusic.setVisibility(View.GONE);
        mediaPlayer=new MediaPlayer();
        try {
            mediaPlayer.setDataSource(songurl);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.setVolume(mv*0.01f,mv*0.01f);
                    mediaPlayer.seekTo(initime);
                    mediaPlayer.start();
                    mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                        @Override
                        public void onSeekComplete(MediaPlayer mp) {
                            if(mediaPlayer!=null) {
                                removemusic.setVisibility(View.VISIBLE);
                                if(timer!=null) timer.cancel();
                                pos=0;
                                simpleExoPlayer.seekTo(0);
                                simpleExoPlayer.setPlayWhenReady(true);
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
                            if(timer!=null) timer.cancel();
                            pos=0;
                            simpleExoPlayer.seekTo(0);
                            simpleExoPlayer.setPlayWhenReady(true);
                            mediaPlayer.start();
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
                if(simpleExoPlayer!=null) {
                    seekBar.setProgress(pos);
                    pos+=100;
                }
            }
        }, 0, 100);
    }

    @Override
    protected void onStop() {
        super.onStop();
        playerView.getPlayer().pause();
        if(mediaPlayer!=null)
        {
            mediaPlayer.pause();
        }
        if(timer!=null){timer.cancel();}
    }
    @Override
    protected void onResume() {
        super.onResume();
        playerView.getPlayer().setPlayWhenReady(true);
        if(mediaPlayer!=null)
        {
            mediaPlayer.start();
        }
        if(timer!=null){timer.cancel();
            seekbarupdate();}
    }

    public void bkbk(View view)
    {
        onBackPressed();
    }

    public void openmusic(View view)
    {
        Intent i= new Intent(this,addmusic_to_story.class);
        i.putExtra("from","editreels");
        startActivity(i);
    }
    
    public void volumeset(View view)
    {
        if(mediaPlayer!=null) {
            volumecard.setVisibility(View.VISIBLE);
            ImageButton cancel = (ImageButton) findViewById(R.id.dismissreelaudioset);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    volumecard.setVisibility(View.GONE);
                }
            });
            TextView name = (TextView) findViewById(R.id.volumesetdisplayname);
            name.setSelected(true);
            name.setText("Track: "+songname);
            SeekBar videovol=(SeekBar) findViewById(R.id.videoogvol);
            SeekBar musicvol=(SeekBar) findViewById(R.id.videoogvol2);
            videovol.setProgress(ov);
            musicvol.setProgress(mv);
            simpleExoPlayer.setVolume(ov*0.01f);
            mediaPlayer.setVolume(mv*0.01f,mv*0.01f);
            videovol.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(fromUser==true && simpleExoPlayer!=null)
                    {
                        ov=progress;
                        simpleExoPlayer.setVolume(progress*0.01f);
                    }
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
            musicvol.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(fromUser==true && mediaPlayer!=null)
                    {
                        mv=progress;
                        mediaPlayer.setVolume(progress*0.01f,progress*0.01f);
                    }
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }
    }


    private void uploadvideo(Uri uri,Float speed,Boolean addedsong,int songvol,int videovol,int songinit,int duration) {
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("uploading video to server...");
        DatabaseReference mDatabase;
        mDatabase= FirebaseDatabase.getInstance().getReference();
        String randomkey = mDatabase.push().getKey();
        Toast.makeText(editreels.this,socialmediapage.reeluri.toString(),Toast.LENGTH_SHORT).show();
        StorageReference filepath = mref.child("Posts").child(mainpage.curuser).child("reelpost").child(randomkey);
        filepath.putFile(socialmediapage.reeluri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(
                        new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                String fileLink = task.getResult().toString();
                                progressDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(editreels.this, "ERROR OCCURRED", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                Toast.makeText(editreels.this, progress+"", Toast.LENGTH_SHORT).show();
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(editreels.this, "dcsvbjhvckjsbdckjb", Toast.LENGTH_SHORT).show();
            }
        });;
    }

}