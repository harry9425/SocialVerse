package com.harry9425.yaari;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

public class addmusic_to_story extends AppCompatActivity {

    RecyclerView recyclerView;
    private FirebaseDatabase mDatabase;
    trackadapter trackadapter;
    public static ConstraintLayout player;
    public static ImageButton bk, cancelplayer,playpause=null;
    public static String state="P";
    String statealert = "S";
    public static TextView select;
    ImageButton playpausebtn=null;
    public static String link=null,name=null,art=null;
    public static TextView song, artist;
    trackmodelclass trackmodelclass;
    public static Timer timer=null;
    public static  PhotoView previewiamge=null;
    public static PlayerView playerView;
    public static SimpleExoPlayer simpleExoPlayer;
    public static MediaPlayer mediaPlayer = null;
    private MediaPlayer previewplayer=null;
    private SeekBar main,mini;
    private Timer pretimer=null;
    public static SeekBar playerbar;
    public static int duration=15000;
    String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addmusic_to_story);
        final ArrayList<trackmodelclass> list = new ArrayList<>();
        from=getIntent().getStringExtra("from");
        recyclerView = (RecyclerView) findViewById(R.id.trackrecylerview);
        bk = (ImageButton) findViewById(R.id.backfrommusicpage);
        player = (ConstraintLayout) findViewById(R.id.trackplayer);
        player.setVisibility(View.GONE);
        select=(TextView) findViewById(R.id.select_this_song);
        select.setAlpha(0.5f);
        select.setEnabled(false);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timer!=null) {
                    timer.cancel();
                }
                if (mediaPlayer != null) {
                    mediaPlayer.pause();
                }
                if(link!=null && name!=null) {
                        int startpos= mediaPlayer.getCurrentPosition();
                        showpreviewalert(link,name,startpos);
                }
            }
        });
        cancelplayer = (ImageButton) findViewById(R.id.cancelplayer);
        song = (TextView) findViewById(R.id.trackplayername);
        artist = (TextView) findViewById(R.id.trackplayerartist);
        playerbar = (SeekBar) findViewById(R.id.trackplayerseekbar);
        playpause = (ImageButton) findViewById(R.id.playsoundtrack);
        playpause.setImageResource(R.drawable.ic_baseline_pause_24sw);
        bk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        trackadapter = new trackadapter(list, this);
        recyclerView.setAdapter(trackadapter);
        mDatabase = FirebaseDatabase.getInstance();
        mDatabase.getReference().keepSynced(true);
        mDatabase.getReference().child("soundtracks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    trackmodelclass trackmodel = new trackmodelclass();
                    trackmodel.setUid(snapshot.getKey().toString());
                    trackmodel.setName(snapshot.child("name").getValue().toString());
                    trackmodel.setArtist(snapshot.child("artist").getValue().toString());
                    trackmodel.setUrl(snapshot.child("url").getValue().toString());
                    list.add(trackmodel);
                }
                trackadapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(addmusic_to_story.this, "CAN'T CONNECT TO SERVER", Toast.LENGTH_LONG).show();
            }
        });
        cancelplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timer!=null) {
                    timer.cancel();
                }
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                player.setVisibility(View.GONE);
            }
        });
    }

    private void showpreviewalert(String url,String songname,int startpos) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View view=inflater.inflate(R.layout.samplecutmusicalert,null);
        builder.setView(view);
        AlertDialog alertDialog =builder.create();
        TextView name=view.findViewById(R.id.alertselectsongname);
        alertDialog.setCanceledOnTouchOutside(false);
        ImageButton cancel=view.findViewById(R.id.alertcanceltrack);
        playpausebtn=view.findViewById(R.id.alertselectplaybtn);
        playpausebtn.setImageResource(R.drawable.ic_baseline_downloading_24);
        TextView done=view.findViewById(R.id.donealertsong);
        playerView=view.findViewById(R.id.reelcutalertview);
        ConstraintLayout reelvol=view.findViewById(R.id.reelalertviewvolume);
        reelvol.setVisibility(View.GONE);
        SeekBar reelvolbar=view.findViewById(R.id.reelvolumeseekbar);
        reelvolbar.setMax(100);
        LinearLayout mediavol=view.findViewById(R.id.volumeshowalert);
        SeekBar mediavolbar=view.findViewById(R.id.alertsongvolume);
        ImageButton volshowbtn=view.findViewById(R.id.mediaplayervolumeshowalert);
        volshowbtn.setImageResource(R.drawable.ic_baseline_volume_up_24);
        mediavolbar.setMax(100);
        playerView.setVisibility(View.GONE);
        playerView.hideController();
        playerView.setUseController(false);
        if(from.contains("reels")) {
            if(editreels.reeluri!=null) {
                reelvol.setVisibility(View.VISIBLE);
                playerView.setVisibility(View.VISIBLE);
                previewiamge=view.findViewById(R.id.samplecutiamgealert);
                previewiamge.setVisibility(View.GONE);
                previewiamge=null;
                simpleExoPlayer = new SimpleExoPlayer.Builder(this).build();
                MediaItem mediaItem = MediaItem.fromUri(editreels.reeluri);
                simpleExoPlayer.addMediaItems(Collections.singletonList(mediaItem));
                simpleExoPlayer.prepare();
                playerView.setPlayer(simpleExoPlayer);
                simpleExoPlayer.setPlaybackSpeed(editreels.speed);
                simpleExoPlayer.addListener(new Player.Listener() {
                    @Override
                    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                        if(playbackState==SimpleExoPlayer.STATE_READY && playWhenReady){
                            duration= (int) simpleExoPlayer.getDuration();
                            duration= (int) (duration/editreels.speed);
                            mini.setMax(duration);
                        }
                    }
                });
            }
        }
        else {
            if(editstory.image!=null) {
                duration=30000;
                reelvol.setVisibility(View.GONE);
                playerView.setVisibility(View.GONE);
               previewiamge=view.findViewById(R.id.samplecutiamgealert);
               previewiamge.setVisibility(View.VISIBLE);
                Picasso.get().load(editstory.image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.userdefaultdp)
                        .into(previewiamge, new Callback() {
                            @Override
                            public void onSuccess() {}
                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(editstory.image).placeholder(R.drawable.userdefaultdp).into(previewiamge);
                            }
                        });
            }
        }
        final int[] custompos = {0};
        final int[] musicvolchanged = {100};
        final int[] videovolchanged = {100};
        done.setAlpha(0.5f);
        done.setEnabled(false);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                if(pretimer!=null)
                {
                    pretimer.cancel();
                }
                if(previewplayer!=null) {
                    previewplayer.stop();
                    previewplayer.release();
                    previewplayer = null;
                }
                if(simpleExoPlayer!=null)
                {
                    simpleExoPlayer.stop();
                    simpleExoPlayer.release();
                    simpleExoPlayer=null;
                    playerView.setVisibility(View.GONE);
                    playerView=null;
                }
                state="P";
                statealert="S";
                if(from.contains("story"))
                {
                    editstory.songname=songname;
                    editstory.songurl=url;
                    editstory.initime=custompos[0];
                    editstory.endtim=(custompos[0]+15000);
                    if(editstory.way.getVisibility()==View.GONE)
                    {
                        editstory.way.setVisibility(View.VISIBLE);
                    }else {
                        editstory.way.setVisibility(View.GONE);
                        editstory.way.setVisibility(View.VISIBLE);
                    }
                }
                else if(from.contains("reel")) {
                    editreels.songname = songname;
                    editreels.songurl = url;
                    editreels.initime = custompos[0];
                    editreels.endtim = (custompos[0] + 15000);
                    editreels.mv=musicvolchanged[0];
                    editreels.ov= videovolchanged[0];
                    if (editreels.way.getVisibility() == View.GONE) {
                        editreels.way.setVisibility(View.VISIBLE);
                    } else {
                        editreels.way.setVisibility(View.GONE);
                        editreels.way.setVisibility(View.VISIBLE);
                    }
                }
                alertDialog.cancel();
                onBackPressed();
            }
        });
        main=view.findViewById(R.id.alertselectbar);
        mini=view.findViewById(R.id.alertshowcuttrack);
        mini.setProgress(0);
        mini.setSplitTrack(false);
        mini.setFocusable(false);
        mini.setClickable(false);
        mini.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;}});
        mini.setMax(duration);
        name.setText(songname);
        name.setSelected(true);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(simpleExoPlayer!=null)
                {
                    simpleExoPlayer.stop();
                    simpleExoPlayer.release();
                    simpleExoPlayer=null;
                    playerView.setVisibility(View.GONE);
                    playerView=null;
                }
                if(pretimer!=null)
                {
                    pretimer.cancel();
                }
                if(previewplayer!=null) {
                    previewplayer.stop();
                    previewplayer.release();
                    previewplayer = null;
                }
                alertDialog.cancel();
            }
        });
        statealert="S";
        main.setProgress(0);
        if(pretimer!=null)
        {
            pretimer.cancel();
        }
        if(mediaPlayer!=null)
        {
            if(previewplayer!=null)
            {
                previewplayer.stop();
                previewplayer.release();
                previewplayer=null;
            }
            previewplayer=new MediaPlayer();
            try {
                mediavolbar.setProgress(100);
                previewplayer.setVolume(1f,1f);
                if(simpleExoPlayer!=null)
                {
                    reelvolbar.setProgress(100);
                    simpleExoPlayer.setVolume(1f);
                }
                reelvolbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                     if(fromUser==true && simpleExoPlayer!=null)
                     {
                        simpleExoPlayer.setVolume(progress*0.01f);
                        videovolchanged[0]=progress;
                     }
                    }
                    @Override public void onStartTrackingTouch(SeekBar seekBar) {}
                    @Override public void onStopTrackingTouch(SeekBar seekBar) {}});
                previewplayer.setDataSource(url);
                previewplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                previewplayer.prepareAsync();
                mediavolbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if(fromUser==true && previewplayer!=null)
                        {
                            previewplayer.setVolume((progress*0.01f),(progress*0.01f));
                            musicvolchanged[0] =progress;
                            if(progress<=0)
                            {
                                volshowbtn.setImageResource(R.drawable.ic_baseline_volume_off_24);
                            }
                            else volshowbtn.setImageResource(R.drawable.ic_baseline_volume_up_24);
                        }
                    }
                    @Override public void onStartTrackingTouch(SeekBar seekBar) {}
                    @Override public void onStopTrackingTouch(SeekBar seekBar) {}});
                previewplayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        if(simpleExoPlayer!=null)
                        {
                            simpleExoPlayer.setPlayWhenReady(true);
                        }
                        done.setAlpha(1f);
                        done.setEnabled(true);
                        playpausebtn.setImageResource(R.drawable.ic_baseline_pause_24sw);
                        previewplayer.seekTo(startpos);
                        main.setProgress(0);
                        mini.setProgress(0);
                        custompos[0]=startpos;
                        try {
                            if(previewplayer!=null){main.setMax(previewplayer.getDuration());}
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        previewplayer.start();
                        if(previewplayer!=null) {
                            previewseekbarupdate();
                        }
                        playpausebtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (statealert.equals("P")) {
                                    if(simpleExoPlayer!=null) {
                                        simpleExoPlayer.setPlayWhenReady(true);
                                    }
                                    statealert = "S";
                                    playpausebtn.setImageResource(R.drawable.ic_baseline_pause_24sw);
                                    if(previewplayer!=null) {
                                        previewseekbarupdate();
                                    }
                                    previewplayer.start();
                                } else {
                                    if(simpleExoPlayer!=null) {
                                        simpleExoPlayer.pause();
                                    }

                                    statealert = "P";
                                    playpausebtn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                                    previewplayer.pause();
                                    if(pretimer!=null){ pretimer.cancel();}
                                }
                            }
                        });
                        previewplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                statealert = "P";
                                mini.setProgress(0);
                                if(simpleExoPlayer!=null) {
                                    simpleExoPlayer.pause();
                                    simpleExoPlayer.seekTo(0);
                                }
                                playpausebtn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                                if(pretimer!=null){ pretimer.cancel();}
                                main.setProgress(0);
                                previewplayer.pause();
                            }
                        });
                        main.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                if(!fromUser)
                                {
                                    mini.setProgress(mini.getProgress()+100);
                                    if(mini.getProgress()>=mini.getMax())
                                    {
                                        if(pretimer!=null){pretimer.cancel();}
                                        if(simpleExoPlayer!=null) {
                                            simpleExoPlayer.pause();
                                            simpleExoPlayer.seekTo(0);
                                            simpleExoPlayer.setPlayWhenReady(true);
                                        }
                                        previewplayer.pause();
                                        previewplayer.seekTo(custompos[0]);
                                        mini.setProgress(0);
                                        main.setProgress(custompos[0]);
                                        previewplayer.start();
                                        previewseekbarupdate();
                                        statealert="S";
                                    }
                                }
                            }
                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {}
                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                if((seekBar.getMax()-seekBar.getProgress())<=duration)
                                {
                                    seekBar.setProgress(seekBar.getMax()-duration);
                                }
                                if(simpleExoPlayer!=null) {
                                    simpleExoPlayer.pause();
                                    simpleExoPlayer.seekTo(0);
                                }
                                if(pretimer!=null){pretimer.cancel();}
                                previewplayer.pause();
                                mini.setProgress(0);
                                previewplayer.seekTo(seekBar.getProgress());
                                statealert="P";
                                custompos[0] =seekBar.getProgress();
                                playpausebtn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                            }
                        });
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        alertDialog.show();
    }

    private void previewseekbarupdate() {
        pretimer=new Timer();
        pretimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                main.setProgress(previewplayer.getCurrentPosition());
            }
        }, 0, 100);
    }

    public static void playersetup(String by, String music, String url) {
        select.setAlpha(0.5f);
        select.setEnabled(false);
        state="S";
        link=url;
        name=music;
        art=by;
        player.setVisibility(View.VISIBLE);
        song.setText(music);
        artist.setText(by);
        playerbar.setProgress(0);
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
                playpause.setImageResource(R.drawable.ic_baseline_downloading_24);
                mediaPlayer.setDataSource(url);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        select.setAlpha(1f);
                        select.setEnabled(true);
                        mediaPlayer.seekTo(0);
                        playpause.setImageResource(R.drawable.ic_baseline_pause_24sw);
                        playerbar.setProgress(0);
                        try {
                            if(mediaPlayer!=null){playerbar.setMax(mediaPlayer.getDuration());}
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mediaPlayer.start();
                        if(mediaPlayer!=null) {
                            seekbarupdate();
                        }
                        playpause.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (state.equals("P")) {
                                    state = "S";
                                    playpause.setImageResource(R.drawable.ic_baseline_pause_24sw);
                                    if(mediaPlayer!=null) {
                                        seekbarupdate();
                                    }
                                    mediaPlayer.start();
                                } else {
                                    state = "P";
                                    playpause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                                    mediaPlayer.pause();
                                    if(timer!=null){ timer.cancel();}
                                }
                            }
                        });
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                state = "P";
                                playpause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                                if(timer!=null){ timer.cancel();}
                                playerbar.setProgress(0);
                                mediaPlayer.pause();
                            }
                        });
                        playerbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}
                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {}
                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                if(timer!=null){timer.cancel();}
                                mediaPlayer.pause();
                                mediaPlayer.seekTo(seekBar.getProgress());
                                if(!state.equals("P")) {
                                    seekbarupdate();
                                    mediaPlayer.start();
                                }
                            }
                        });
                    }
                });
        } catch (IOException e) {
            e.printStackTrace();
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
        if(pretimer!=null){pretimer.cancel();}
        if(previewplayer!=null)
        {
            previewplayer.pause();
            if(simpleExoPlayer!=null)
            {
                simpleExoPlayer.pause();
            }
        }
        state="P";
        statealert="P";
        if(playpause!=null && playpausebtn!=null) {
            playpause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            playpausebtn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        }
    }

    public static void seekbarupdate() {
         timer=new Timer();
         timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                playerbar.setProgress(mediaPlayer.getCurrentPosition());
            }
        }, 0, 100);
    }
}

