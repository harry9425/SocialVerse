package com.harry9425.yaari;

import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.media2.exoplayer.external.source.ExtractorMediaSource;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.ADMMessageHandler;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class chatadapter extends RecyclerView.Adapter{

    ArrayList<messagemodel> messagemodels;
    Context context;
    FirebaseAuth auth;
    String curuser;
    DatabaseReference databaseReference;
    int SenderViewType=1,Receiverviewtype=2,senderfiletype=3,receiverfiletype=4,senderimagetype=5,
            receiverimagetype=6,sendervideo=7,receivervideo=8,sendermap=9,receivermap=10,senderaudio=11,receiveraudio=12;
    String senderRoom,receiverRoom,uid;

    public chatadapter(ArrayList<messagemodel> messagemodels, Context context,String senderRoom,String receiverRoom,String uid) {
        this.messagemodels = messagemodels;
        this.context = context;
        this.senderRoom=senderRoom;
        this.uid=uid;
        this.receiverRoom=receiverRoom;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==SenderViewType)
        {
            View view= LayoutInflater.from(context).inflate(R.layout.sample_sender,parent,false);
            return new SenderViewHolder(view);
        }
       else if(viewType==Receiverviewtype){
            View view= LayoutInflater.from(context).inflate(R.layout.sample_receiver,parent,false);
            return new ReceiverViewHolder(view);
        }
       else if(viewType==senderfiletype)
        {
            View view= LayoutInflater.from(context).inflate(R.layout.samplesenderfile,parent,false);
            return new SenderViewfile(view);
        }
        else if(viewType==senderimagetype)
        {
            View view= LayoutInflater.from(context).inflate(R.layout.samplesenderimagediff,parent,false);
            return new Senderimagediff(view);
        }
        else if(viewType==receiverimagetype)
        {
            View view= LayoutInflater.from(context).inflate(R.layout.receiverimagediff,parent,false);
            return new Receiverimagediff(view);
        }
        else if(viewType==sendervideo)
        {
            View view= LayoutInflater.from(context).inflate(R.layout.sendervideosample,parent,false);
            return new Sendervideo(view);
        }
        else if(viewType==receivervideo)
        {
            View view= LayoutInflater.from(context).inflate(R.layout.samplereceivervideo,parent,false);
            return new Receivervideo(view);
        }
        else if(viewType==sendermap)
        {
            View view= LayoutInflater.from(context).inflate(R.layout.sendermapsample,parent,false);
            return new Sendermap(view);
        }
        else if(viewType==receivermap)
        {
            View view= LayoutInflater.from(context).inflate(R.layout.receivermapsample,parent,false);
            return new Receivermap(view);
        }
        else if(viewType==receiveraudio)
        {
            View view= LayoutInflater.from(context).inflate(R.layout.receiveraudiosample,parent,false);
            return new receiveraudio(view);
        }
        else if(viewType==senderaudio)
        {
            View view= LayoutInflater.from(context).inflate(R.layout.senderaudiosample,parent,false);
            return new senderaudio(view);
        }
       else {
            View view= LayoutInflater.from(context).inflate(R.layout.samplereceiverfiles,parent,false);
            return new ReceiverViewfile(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if((messagemodels.get(position).getUid()).equals(curuser)) {
            if(messagemodels.get(position).getChecker().equals("audio"))
                return senderaudio;
            else {
                if (messagemodels.get(position).getChecker().equals("location"))
                    return sendermap;
                else {
                    if (messagemodels.get(position).getChecker().equals("pdf") || messagemodels.get(position).getChecker().equals("docx"))
                        return senderfiletype;
                    else if (messagemodels.get(position).getChecker().equals("video w/o text") || messagemodels.get(position).getChecker().equals("video with text")) {
                        if (messagemodels.get(position).getImagetype().equals("keep"))
                            return sendervideo;
                        else
                            return senderimagetype;
                    } else {
                        if (messagemodels.get(position).getImagetype().equals("keep") || messagemodels.get(position).getImagetype().equals("none")) {
                            return SenderViewType;
                        } else {
                            return senderimagetype;
                        }
                    }
                }
            }
        }
        else {
            if (messagemodels.get(position).getChecker().equals("audio"))
                return receiveraudio;
            else {
                if (messagemodels.get(position).getChecker().equals("location"))
                    return receivermap;
                else {
                    if (messagemodels.get(position).getChecker().equals("pdf") || messagemodels.get(position).getChecker().equals("docx"))
                        return receiverfiletype;
                    else if (messagemodels.get(position).getChecker().equals("video w/o text") || messagemodels.get(position).getChecker().equals("video with text")) {
                        if (messagemodels.get(position).getImagetype().equals("keep"))
                            return receivervideo;
                        else
                            return receiverimagetype;
                    } else {
                        if (messagemodels.get(position).getImagetype().equals("keep") || messagemodels.get(position).getImagetype().equals("none")) {
                            return Receiverviewtype;
                        } else {
                            return receiverimagetype;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final messagemodel messagemodel= messagemodels.get(position);
        int[] reactions=new int[]{
                R.drawable.ic_fb_like,
                R.drawable.ic_fb_love,
                R.drawable.ic_fb_laugh,
                R.drawable.ic_fb_wow,
                R.drawable.ic_fb_sad,
                R.drawable.ic_fb_angry,
                R.drawable.cancelemojiic
        };
        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();
        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {
            if (pos == 0 || pos == 1 || pos == 2 || pos == 3 || pos == 4 || pos == 5 || pos==6) {
                if(pos!=6)
                if (holder.getClass() == SenderViewHolder.class ) {
                    ((SenderViewHolder) holder).sfeel.setVisibility(View.VISIBLE);
                    ((SenderViewHolder) holder).sfeel.setImageResource(reactions[pos]);
                } else if(holder.getClass()==ReceiverViewHolder.class){
                    ((ReceiverViewHolder) holder).rfeel.setVisibility(View.VISIBLE);
                    //if(pos!=6)
                    ((ReceiverViewHolder) holder).rfeel.setImageResource(reactions[pos]);
                }
                else if(holder.getClass()==SenderViewfile.class)
                {
                    ((SenderViewfile) holder).sfeel.setVisibility(View.VISIBLE);
                    ((SenderViewfile) holder).sfeel.setImageResource(reactions[pos]);
                }
                else if(holder.getClass()==ReceiverViewfile.class)
                {
                    ((ReceiverViewfile) holder).rfeel.setVisibility(View.VISIBLE);
                    ((ReceiverViewfile) holder).rfeel.setImageResource(reactions[pos]);
                }
                else if(holder.getClass()==Receiverimagediff.class)
                {
                    ((Receiverimagediff) holder).rfeel.setVisibility(View.VISIBLE);
                    ((Receiverimagediff) holder).rfeel.setImageResource(reactions[pos]);
                }
                else if(holder.getClass()==Senderimagediff.class)
                {
                    ((Senderimagediff) holder).sfeel.setVisibility(View.VISIBLE);
                    ((Senderimagediff) holder).sfeel.setImageResource(reactions[pos]);
                }
                else if(holder.getClass()==Sendervideo.class)
                {
                    ((Sendervideo) holder).sfeel.setVisibility(View.VISIBLE);
                    ((Sendervideo) holder).sfeel.setImageResource(reactions[pos]);
                }
                else if(holder.getClass()==Sendermap.class)
                {
                    ((Sendermap) holder).sfeel.setVisibility(View.VISIBLE);
                    ((Sendermap) holder).sfeel.setImageResource(reactions[pos]);
                }
                else if(holder.getClass()==Receivermap.class)
                {
                    ((Receivermap) holder).rfeel.setVisibility(View.VISIBLE);
                    ((Receivermap) holder).rfeel.setImageResource(reactions[pos]);
                }
                DatabaseReference databaseReference;
                databaseReference = FirebaseDatabase.getInstance().getReference().child("Chats");
                if(pos==6)
                    pos=15;
                databaseReference.child(mainpage.curuser).child(senderRoom).child(messagemodel.getMessid()).child("feelings").setValue(pos).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
                databaseReference.child(uid).child(receiverRoom).child(messagemodel.getMessid()).child("feelings").setValue(pos).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
            }
                return true;
                // true is closing popup, false is requesting a new selection
        });
        if(holder.getClass()==SenderViewHolder.class)
        {
            if(messagemodel.getFeelings()!=15)
            {
                ((SenderViewHolder)holder).sfeel.setImageResource(reactions[messagemodel.getFeelings()]);
                ((SenderViewHolder)holder).sfeel.setVisibility(View.VISIBLE);
            }
            else {
                ((SenderViewHolder)holder).sfeel.setVisibility(View.GONE);
            }

            ((SenderViewHolder)holder).slayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(!messagemodel.getUid().equals(mainpage.curuser)) {
                        popup.onTouch(v, event);
                    }
                    return false;
                }
            });

            ((SenderViewHolder)holder).simageView.setMinimumScale(1);
            ((SenderViewHolder)holder).simageView.setMaximumScale(12);

            if(messagemodel.getChecker().equals("text")) {
                ((SenderViewHolder) holder).smessage.setVisibility(View.VISIBLE);
                ((SenderViewHolder) holder).smessage.setText(messagemodel.getMessage());
                ((SenderViewHolder)holder).simageView.setVisibility(View.GONE);
                ((SenderViewHolder)holder).sfull.setVisibility(View.GONE);
            }
            else {
                    if (messagemodel.getChecker().equals("image w/o text")) {
                        ((SenderViewHolder) holder).simageView.setVisibility(View.VISIBLE);
                        ((SenderViewHolder) holder).smessage.setVisibility(View.GONE);
                        ((SenderViewHolder)holder).sfull.setVisibility(View.VISIBLE);
                    } else {
                        ((SenderViewHolder) holder).simageView.setVisibility(View.VISIBLE);
                        ((SenderViewHolder)holder).sfull.setVisibility(View.VISIBLE);
                        ((SenderViewHolder) holder).smessage.setVisibility(View.VISIBLE);
                        ((SenderViewHolder) holder).smessage.setText(messagemodel.getMessage());
                    }
                    Picasso.get().load(messagemodel.getImageurl()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.userdefaultdp)
                            .into(((SenderViewHolder) holder).simageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(messagemodel.getImageurl()).placeholder(R.drawable.userdefaultdp).into(((SenderViewHolder) holder).simageView);
                                }
                            });
            }
            ((SenderViewHolder)holder).sfull.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context,imagezoom.class);
                    i.putExtra("zoomuri",messagemodel.getImageurl());
                    context.startActivity(i);
                }
            });
        }
        else if(holder.getClass()==Sendermap.class)
        {
            if(messagemodel.getFeelings()!=15)
            {
                ((Sendermap)holder).sfeel.setImageResource(reactions[messagemodel.getFeelings()]);
                ((Sendermap)holder).sfeel.setVisibility(View.VISIBLE);
            }
            else {
                ((Sendermap)holder).sfeel.setVisibility(View.GONE);
            }

            Picasso.get().load(messagemodel.getImageurl()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.userdefaultdp)
                    .into(((Sendermap)holder).smap, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(messagemodel.getImageurl()).placeholder(R.drawable.userdefaultdp).into(((Sendermap)holder).smap);
                        }
                    });
           ((Sendermap)holder).smap.setOnLongClickListener(new View.OnLongClickListener() {
               @Override
               public boolean onLongClick(View v) {
                   String[] val =messagemodel.getMessage().split("&&");
                   Uri mapUri = Uri.parse("geo:0,0?q="+val[0]+","+val[1]+"("+chatactivity.name+")");
                   Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
                   mapIntent.setPackage("com.google.android.apps.maps");
                   context.startActivity(mapIntent);
                   return false;
               }});

            ((Sendermap)holder).slay.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(!messagemodel.getUid().equals(mainpage.curuser)) {
                        popup.onTouch(v, event);
                    }
                    return false;
                }
            });
        }
        else if(holder.getClass()==senderaudio.class)
        {
            MediaPlayer mediaPlayer = new MediaPlayer();
            ((senderaudio)holder).seekBar.setVisibility(View.GONE);
            ((senderaudio)holder).sname.setVisibility(View.VISIBLE);

            ((senderaudio)holder).splay.setImageResource(R.drawable.ic_baseline_play_arrow_24bb);
            ((senderaudio)holder).seekBar.setEnabled(false);
            messagemodel.setMessage("pause");
            messagemodel.setMessagekey(0+"");
            final Timer[] timer = new Timer[1];
            try {
                mediaPlayer.setDataSource(messagemodel.getImageurl());
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        ((senderaudio)holder).splay.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                if(messagemodel.getMessage().equals("pause")) {
                                    ((senderaudio) holder).splay.setImageResource(R.drawable.ic_baseline_pause_24s);
                                    mediaPlayer.seekTo(Integer.valueOf(messagemodel.getMessagekey()));
                                    messagemodel.setMessage("playing");
                                    mediaPlayer.start();
                                    ((senderaudio)holder).seekBar.setVisibility(View.VISIBLE);
                                    ((senderaudio)holder).sname.setVisibility(View.GONE);
                                    timer[0] =new Timer();
                                    timer[0].scheduleAtFixedRate(new TimerTask() {
                                        @Override
                                        public void run() {
                                            ((senderaudio)holder).seekBar.setProgress(mediaPlayer.getCurrentPosition());
                                        }
                                    },0, 100);
                                }else {
                                    ((senderaudio) holder).splay.setImageResource(R.drawable.ic_baseline_play_arrow_24bb);
                                    messagemodel.setMessage("pause");
                                    messagemodel.setMessagekey(mediaPlayer.getCurrentPosition()+"");
                                    mediaPlayer.pause();
                                    timer[0].cancel();
                                }
                                return false;
                            }
                        });
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                timer[0].cancel();
                                messagemodel.setMessagekey(0+"");
                                messagemodel.setMessage("pause");
                                ((senderaudio)holder).seekBar.setProgress(0);
                                mediaPlayer.pause();
                                ((senderaudio)holder).seekBar.setVisibility(View.GONE);
                                ((senderaudio)holder).sname.setVisibility(View.VISIBLE);
                                ((senderaudio)holder).splay.setImageResource(R.drawable.ic_baseline_play_arrow_24bb);
                            }
                        });
                        ((senderaudio)holder).seekBar.setEnabled(true);
                        ((senderaudio)holder).seekBar.setMax(mediaPlayer.getDuration());
                        ((senderaudio)holder).seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                            }
                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {
                            }
                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                mediaPlayer.pause();
                                mediaPlayer.seekTo(seekBar.getProgress());
                                mediaPlayer.start();
                            }
                        });
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(holder.getClass()==receiveraudio.class)
        {
            DatabaseReference database;
            String r;
            r=receiverRoom.replace(mainpage.curuser,"");
            database=FirebaseDatabase.getInstance().getReference().child("AAA usernames").child(r);
            database.addValueEventListener(new ValueEventListener() {
                @Override public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(snapshot.hasChild("userdp"))
                    {
                        String dp = snapshot.child("userdp").getValue().toString();
                        Picasso.get().load(dp).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.userdefaultdp)
                                .into(((receiveraudio) holder).rdp, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Picasso.get().load(dp).placeholder(R.drawable.userdefaultdp).into(((receiveraudio) holder).rdp);
                                    }
                                });
                    }
                }
                @Override public void onCancelled(@NonNull DatabaseError error) {

                } });
            MediaPlayer mediaPlayer = new MediaPlayer();
            ((receiveraudio)holder).rseekbar.setVisibility(View.GONE);
            ((receiveraudio)holder).rname.setVisibility(View.VISIBLE);
            ((receiveraudio)holder).rplay.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            ((receiveraudio)holder).rseekbar.setEnabled(false);
            messagemodel.setMessage("pause");
            messagemodel.setMessagekey(0+"");
            final Timer[] timer = new Timer[1];
            try {
                mediaPlayer.setDataSource(messagemodel.getImageurl());
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        ((receiveraudio)holder).rplay.setEnabled(true);
                        ((receiveraudio)holder).rseekbar.setEnabled(true);
                        ((receiveraudio)holder).rplay.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                if(messagemodel.getMessage().equals("pause")) {
                                    ((receiveraudio) holder).rplay.setImageResource(R.drawable.ic_baseline_pause_24sw);
                                    mediaPlayer.seekTo(Integer.valueOf(messagemodel.getMessagekey()));
                                    messagemodel.setMessage("playing");
                                    mediaPlayer.start();
                                    ((receiveraudio)holder).rseekbar.setVisibility(View.VISIBLE);
                                    ((receiveraudio)holder).rname.setVisibility(View.GONE);
                                    timer[0] =new Timer();
                                    timer[0].scheduleAtFixedRate(new TimerTask() {
                                        @Override
                                        public void run() {
                                            ((receiveraudio)holder).rseekbar.setProgress(mediaPlayer.getCurrentPosition());
                                        }
                                    },0, 100);
                                }else {
                                    ((receiveraudio)holder).rplay.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                                    messagemodel.setMessage("pause");
                                    messagemodel.setMessagekey(mediaPlayer.getCurrentPosition()+"");
                                    mediaPlayer.pause();
                                    timer[0].cancel();
                                }
                                return false;
                            }
                        });
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                timer[0].cancel();
                                ((receiveraudio)holder).rseekbar.setVisibility(View.GONE);
                                ((receiveraudio)holder).rname.setVisibility(View.VISIBLE);
                                messagemodel.setMessagekey(0+"");
                                messagemodel.setMessage("pause");
                                ((receiveraudio)holder).rseekbar.setProgress(0);
                                mediaPlayer.pause();
                                ((receiveraudio)holder).rplay.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                            }
                        });
                        ((receiveraudio)holder).rseekbar.setMax(mediaPlayer.getDuration());
                        ((receiveraudio)holder).rseekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                            }
                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {
                            }
                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                mediaPlayer.pause();
                                mediaPlayer.seekTo(seekBar.getProgress());
                                mediaPlayer.start();
                            }
                        });
                    }
                });

                ((receiveraudio)holder).rlay.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                    @Override
                    public void onViewAttachedToWindow(View v) {

                    }

                    @Override
                    public void onViewDetachedFromWindow(View v) {
                        if(mediaPlayer!=null)
                        {
                            mediaPlayer.stop();
                            mediaPlayer.reset();
                        }

                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(holder.getClass()== Receivermap.class)
        {
            if(messagemodel.getFeelings()!=15)
            {
                ((Receivermap)holder).rfeel.setImageResource(reactions[messagemodel.getFeelings()]);
                ((Receivermap)holder).rfeel.setVisibility(View.VISIBLE);
            }
            else {
                ((Receivermap)holder).rfeel.setVisibility(View.GONE);
            }
            DatabaseReference database;
            String r;
            r=receiverRoom.replace(mainpage.curuser,"");
            database=FirebaseDatabase.getInstance().getReference().child("AAA usernames").child(r);
            database.addValueEventListener(new ValueEventListener() {
                @Override public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(snapshot.hasChild("userdp"))
                    {
                        String dp = snapshot.child("userdp").getValue().toString();
                        Picasso.get().load(dp).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.userdefaultdp)
                                .into(((Receivermap) holder).rdp, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Picasso.get().load(dp).placeholder(R.drawable.userdefaultdp).into(((Receivermap) holder).rdp);
                                    }
                                });
                    }
                }
                @Override public void onCancelled(@NonNull DatabaseError error) {

                } });
            Picasso.get().load(messagemodel.getImageurl()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.userdefaultdp)
                    .into(((Receivermap)holder).rmap, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(messagemodel.getImageurl()).placeholder(R.drawable.userdefaultdp).into(((Receivermap)holder).rmap);
                        }
                    });
            ((Receivermap)holder).rmap.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    String[] val =messagemodel.getMessage().split("&&");
                    Uri mapUri = Uri.parse("geo:0,0?q="+val[0]+","+val[1]+"("+chatactivity.name+")");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    context.startActivity(mapIntent);
                    return false;
                }});
            ((Receivermap)holder).rlay.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(!messagemodel.getUid().equals(mainpage.curuser)) {
                        popup.onTouch(v, event);
                    }
                    return false;
                }
            });
        }
        else if(holder.getClass()==ReceiverViewHolder.class){

            DatabaseReference database;
            String r;
            r=receiverRoom.replace(mainpage.curuser,"");
            database=FirebaseDatabase.getInstance().getReference().child("AAA usernames").child(r);
            database.addValueEventListener(new ValueEventListener() {
                @Override public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(snapshot.hasChild("userdp"))
                    {
                        String dp = snapshot.child("userdp").getValue().toString();
                        Picasso.get().load(dp).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.userdefaultdp)
                                .into(((ReceiverViewHolder) holder).circleImageView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Picasso.get().load(dp).placeholder(R.drawable.userdefaultdp).into(((ReceiverViewHolder) holder).circleImageView);
                                    }
                                });
                    }
                }
                @Override public void onCancelled(@NonNull DatabaseError error) {

                } });

            ((ReceiverViewHolder)holder).rimageView.setMinimumScale(1);
            ((ReceiverViewHolder)holder).rimageView.setMaximumScale(12);

            if(messagemodel.getFeelings()!=15)
            {
                ((ReceiverViewHolder)holder).rfeel.setImageResource(reactions[messagemodel.getFeelings()]);
                ((ReceiverViewHolder)holder).rfeel.setVisibility(View.VISIBLE);
            }
            else {
                ((ReceiverViewHolder)holder).rfeel.setVisibility(View.GONE);
            }

            ((ReceiverViewHolder)holder).rlayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(!messagemodel.getUid().equals(mainpage.curuser)) {
                        popup.onTouch(v, event);
                    }
                    return false;
                }
            });

            if(messagemodel.getChecker().equals("text")) {
                ((ReceiverViewHolder)holder).rmessage.setVisibility(View.VISIBLE);
                ((ReceiverViewHolder)holder).rmessage.setText(messagemodel.getMessage());
                ((ReceiverViewHolder)holder).rimageView.setVisibility(View.GONE);
                ((ReceiverViewHolder) holder).rfull.setVisibility(View.GONE);
            }
            else {
                    if (messagemodel.getChecker().equals("image w/o text")) {
                        ((ReceiverViewHolder) holder).rimageView.setVisibility(View.VISIBLE);
                        ((ReceiverViewHolder) holder).rmessage.setVisibility(View.GONE);
                        ((ReceiverViewHolder) holder).rfull.setVisibility(View.VISIBLE);
                    } else {
                        ((ReceiverViewHolder) holder).rimageView.setVisibility(View.VISIBLE);
                        ((ReceiverViewHolder) holder).rmessage.setVisibility(View.VISIBLE);
                        ((ReceiverViewHolder) holder).rmessage.setText(messagemodel.getMessage());
                        ((ReceiverViewHolder) holder).rfull.setVisibility(View.VISIBLE);
                    }
                    Picasso.get().load(messagemodel.getImageurl()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.userdefaultdp)
                            .into(((ReceiverViewHolder) holder).rimageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(messagemodel.getImageurl()).placeholder(R.drawable.userdefaultdp).into(((ReceiverViewHolder) holder).rimageView);
                                }
                            });
                ((ReceiverViewHolder) holder).rfull.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context,imagezoom.class);
                        i.putExtra("zoomuri",messagemodel.getImageurl());
                        context.startActivity(i);
                    }
                });
            }

        }
        else if(holder.getClass()==Sendervideo.class)
        {
            if(messagemodel.getFeelings()!=15)
            {
                ((Sendervideo)holder).sfeel.setImageResource(reactions[messagemodel.getFeelings()]);
                ((Sendervideo)holder).sfeel.setVisibility(View.VISIBLE);
            }
            else {
                ((Sendervideo)holder).sfeel.setVisibility(View.GONE);
            }

            if(messagemodel.getChecker().equals("video with text"))
            {
                ((Sendervideo)holder).sname.setVisibility(View.VISIBLE);
                ((Sendervideo)holder).sname.setText(messagemodel.getMessage());
            }
            else   ((Sendervideo)holder).sname.setVisibility(View.GONE);
            SimpleExoPlayer simpleExoPlayer = new SimpleExoPlayer.Builder(context).build();
           try {
               ((Sendervideo)holder).playerView.setPlayer(simpleExoPlayer);
               MediaItem mediaItem=MediaItem.fromUri(Uri.parse(messagemodel.getImageurl()));
               simpleExoPlayer.addMediaItems(Collections.singletonList(mediaItem));
               simpleExoPlayer.prepare();
               simpleExoPlayer.setPlayWhenReady(false);
           } catch (Exception e) {
               e.printStackTrace();
           }
            ((Sendervideo)holder).full.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(context,fullscreenvideo.class);
                    simpleExoPlayer.pause();
                    i.putExtra("uri", messagemodel.getImageurl());
                    i.putExtra("pos", simpleExoPlayer.getCurrentPosition());
                    ((Sendervideo)holder).playerView.getPlayer().stop();
                    context.startActivity(i);
                }
            });

            ((Sendervideo)holder).playerView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {

                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    ((Sendervideo)holder).playerView.getPlayer().stop();

                }
            });

            ((Sendervideo)holder).slayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(!messagemodel.getUid().equals(mainpage.curuser)) {
                        popup.onTouch(v, event);
                    }
                    return false;
                }
            });
        }
        else if(holder.getClass()==Receivervideo.class)
        {
            DatabaseReference database;
            String r;
            r=receiverRoom.replace(mainpage.curuser,"");
            database=FirebaseDatabase.getInstance().getReference().child("AAA usernames").child(r);
            database.addValueEventListener(new ValueEventListener() {
                @Override public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(snapshot.hasChild("userdp"))
                    {
                        String dp = snapshot.child("userdp").getValue().toString();
                        Picasso.get().load(dp).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.userdefaultdp)
                                .into(((Receivervideo) holder).dpvideo, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Picasso.get().load(dp).placeholder(R.drawable.userdefaultdp).into(((Receivervideo) holder).dpvideo);
                                    }
                                });
                    }
                }
                @Override public void onCancelled(@NonNull DatabaseError error) {

                } });

            if(messagemodel.getFeelings()!=15)
            {
                ((Receivervideo)holder).rfeel.setImageResource(reactions[messagemodel.getFeelings()]);
                ((Receivervideo)holder).rfeel.setVisibility(View.VISIBLE);
            }
            else {
                ((Receivervideo)holder).rfeel.setVisibility(View.GONE);
            }

            if(messagemodel.getChecker().equals("video with text"))
            {
                ((Receivervideo)holder).rname.setVisibility(View.VISIBLE);
                ((Receivervideo)holder).rname.setText(messagemodel.getMessage());
            }
            else    ((Receivervideo)holder).rname.setVisibility(View.GONE);
            SimpleExoPlayer simpleExoPlayer = new SimpleExoPlayer.Builder(context).build();
            try {
                ((Receivervideo)holder).rplayerView.setPlayer(simpleExoPlayer);
                MediaItem mediaItem=MediaItem.fromUri(Uri.parse(messagemodel.getImageurl()));
                simpleExoPlayer.addMediaItems(Collections.singletonList(mediaItem));
                simpleExoPlayer.prepare();
                simpleExoPlayer.setPlayWhenReady(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ((Receivervideo)holder).rfull.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(context,fullscreenvideo.class);
                    simpleExoPlayer.pause();
                    i.putExtra("uri", messagemodel.getImageurl());
                    i.putExtra("pos", simpleExoPlayer.getCurrentPosition());
                    ((Receivervideo)holder).rplayerView.getPlayer().stop();
                    context.startActivity(i);
                }
            });

            ((Receivervideo)holder).rplayerView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {

                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    ((Receivervideo)holder).rplayerView.getPlayer().stop();

                }
            });

            ((Receivervideo)holder).rlayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(!messagemodel.getUid().equals(mainpage.curuser)) {
                        popup.onTouch(v, event);
                    }
                    return false;
                }
            });
        }
        else if(holder.getClass()==SenderViewfile.class)
        {
            if(messagemodel.getFeelings()!=15)
            {
                ((SenderViewfile)holder).sfeel.setImageResource(reactions[messagemodel.getFeelings()]);
                ((SenderViewfile)holder).sfeel.setVisibility(View.VISIBLE);
            }
            else {
                ((SenderViewfile)holder).sfeel.setVisibility(View.GONE);
            }

            if(messagemodel.getChecker().equals("pdf"))
                ((SenderViewfile)holder).simageView.setImageResource(R.drawable.pdflogo);
            else  ((SenderViewfile)holder).simageView.setImageResource(R.drawable.wordlogo);

            ((SenderViewfile)holder).slayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(!messagemodel.getUid().equals(mainpage.curuser)) {
                        popup.onTouch(v, event);
                    }
                    return false;
                }
            });
            ((SenderViewfile)holder).sname.setText(messagemodel.getMessage().toUpperCase());
            ((SenderViewfile)holder).sname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(Intent.ACTION_VIEW, Uri.parse(messagemodel.getImageurl()));
                    context.startActivity(i);
                }
            });
        }
        else if(holder.getClass()==Senderimagediff.class)
        {
            if(messagemodel.getFeelings()!=15)
            {
                ((Senderimagediff)holder).sfeel.setImageResource(reactions[messagemodel.getFeelings()]);
                ((Senderimagediff)holder).sfeel.setVisibility(View.VISIBLE);
            }
            else {
                ((Senderimagediff)holder).sfeel.setVisibility(View.GONE);
            }

            ((Senderimagediff)holder).slayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(messagemodel.getChecker().contains("image")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        final View view = LayoutInflater.from(context).inflate(R.layout.customimagechatalert, null);
                        builder.setView(view);
                        AlertDialog alertDialog = builder.create();
                        TextView textView = view.findViewById(R.id.imagemessage);
                        ImageButton feel = view.findViewById(R.id.alertimagezoom);
                        ImageButton down = view.findViewById(R.id.alertimagedownload);
                        down.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(messagemodel.getImageurl()));
                                context.startActivity(i);
                            }
                        });
                        ImageButton cancel = view.findViewById(R.id.alertimagecancel);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                                alertDialog.cancel();
                            }
                        });
                        feel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(context, imagezoom.class);
                                i.putExtra("zoomuri", messagemodel.getImageurl());
                                context.startActivity(i);
                            }
                        });
                        if (messagemodel.getChecker().equals("image w/o text"))
                            textView.setVisibility(View.GONE);
                        else
                            textView.setVisibility(View.VISIBLE);
                        ProgressBar simpleProgressBar = (ProgressBar) view.findViewById(R.id.progressBar); // initiate the progress bar
                        simpleProgressBar.setMax(1500); // 100 maximum value for the progress value
                        textView.setText(messagemodel.getMessage());
                        PhotoView photoView = view.findViewById(R.id.alertphotoview);
                        photoView.setMinimumScale(1);
                        photoView.setMaximumScale(12);
                        // simpleProgressBar.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                        Picasso.get().load(messagemodel.getImageurl()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.userdefaultdp)
                                .into(photoView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        new CountDownTimer(15000, 10) {
                                            public void onTick(long millisUntilFinished) {
                                                // timer.setText((millisUntilFinished / 1000)+"");
                                                simpleProgressBar.setProgress((int) (millisUntilFinished / 10));
                                            }

                                            public void onFinish() {
                                                alertDialog.dismiss();
                                            }
                                        }.start();
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Picasso.get().load(messagemodel.getImageurl()).placeholder(R.drawable.userdefaultdp).into(photoView);
                                        new CountDownTimer(15000, 10) {
                                            public void onTick(long millisUntilFinished) {
                                                simpleProgressBar.setProgress((int) (millisUntilFinished / 10));
                                            }

                                            public void onFinish() {
                                                alertDialog.dismiss();
                                            }
                                        }.start();
                                    }
                                });

                        alertDialog.show();
                    }
                    else {
                       // Toast.makeText(context,"dcjwnvhjcd",Toast.LENGTH_LONG).show();
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        final View view = LayoutInflater.from(context).inflate(R.layout.customvideodiffalert, null);
                        builder.setView(view);
                        AlertDialog alertDialog = builder.create();
                        alertDialog.setCanceledOnTouchOutside(false);
                        TextView textView = view.findViewById(R.id.videomessage);
                        PlayerView alertvideo=view.findViewById(R.id.alertvideoview);
                        ImageView fullzoom=alertvideo.findViewById(R.id.exo_fullscreen);
                        ImageButton feel = view.findViewById(R.id.alertvideozoom);
                        ImageButton down = view.findViewById(R.id.alertvideodownload);
                        ImageButton vol=view.findViewById(R.id.alertvideovolume);
                        ImageButton voldown=view.findViewById(R.id.alertvideovolumedown);
                        voldown.setVisibility(View.GONE);
                        vol.setVisibility(View.VISIBLE);
                        SimpleExoPlayer simpleExoPlayer = new SimpleExoPlayer.Builder(context).build();
                        vol.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                simpleExoPlayer.setVolume(0);
                                vol.setVisibility(View.GONE);
                                voldown.setVisibility(View.VISIBLE);
                            }
                        });
                        voldown.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                simpleExoPlayer.setVolume(simpleExoPlayer.getDeviceVolume());
                                voldown.setVisibility(View.GONE);
                                vol.setVisibility(View.VISIBLE);
                            }
                        });
                        down.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(messagemodel.getImageurl()));
                                context.startActivity(i);
                            }
                        });
                        ImageButton cancel = view.findViewById(R.id.alertvideocancel);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                                alertDialog.cancel();
                            }
                        });
                        feel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                simpleExoPlayer.pause();
                                Intent i = new Intent(context, fullscreenvideo.class);
                                i.putExtra("uri", messagemodel.getImageurl());
                                i.putExtra("pos", simpleExoPlayer.getCurrentPosition());
                                context.startActivity(i);

                            }
                        });
                        if (messagemodel.getChecker().equals("video w/o text"))
                            textView.setVisibility(View.GONE);
                        else
                            textView.setVisibility(View.VISIBLE);
                        textView.setText(messagemodel.getMessage());
                        try {
                            alertvideo.setPlayer(simpleExoPlayer);
                            MediaItem mediaItem=MediaItem.fromUri(Uri.parse(messagemodel.getImageurl()));
                            simpleExoPlayer.addMediaItems(Collections.singletonList(mediaItem));
                            simpleExoPlayer.prepare();
                            simpleExoPlayer.setPlayWhenReady(true);
                            simpleExoPlayer.addListener(new Player.Listener() {
                                @Override
                                public void onPlaybackStateChanged(int state) {
                                    if(state==Player.STATE_ENDED)
                                    {
                                        alertvideo.getPlayer().stop();
                                        alertDialog.dismiss();
                                    }
                                }
                            });
                            if(simpleExoPlayer.getPlaybackState()== Player.STATE_ENDED)
                            {

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        fullzoom.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i=new Intent(context,fullscreenvideo.class);
                                i.putExtra("uri",messagemodel.getImageurl());
                                alertvideo.getPlayer().stop();
                                context.startActivity(i);
                            }
                        });
                        alertDialog.show();
                    }
                    return false;
                }
            });

            ((Senderimagediff)holder).slayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(!messagemodel.getUid().equals(mainpage.curuser)) {
                        popup.onTouch(v, event);
                    }
                    return false;
                }
            });
            if (messagemodel.getChecker().contains("image")) {
                if (messagemodel.getMessagekey().equals("none"))
                    ((Senderimagediff) holder).sname.setText("Photo");
                else
                    ((Senderimagediff) holder).sname.setText("Opened");
            }else {
                if (messagemodel.getMessagekey().equals("none"))
                    ((Senderimagediff) holder).sname.setText("Play Video");
                else
                    ((Senderimagediff) holder).sname.setText("Video Opened");
            }
        }
        else if(holder.getClass()==Receiverimagediff.class)
        {
            final int[] allow = {1};
            if(messagemodel.getChecker().contains("image")) {
                if (messagemodel.getMessagekey().equals("none")) {
                    ((Receiverimagediff) holder).rname.setText("Photo");
                    ((Receiverimagediff) holder).rimageView.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                } else if ((messagemodel.getMessagekey().equals("once") && messagemodel.getImagetype().equals("once")) ||
                        (messagemodel.getMessagekey().equals("replay") && messagemodel.getImagetype().equals("replay"))) {
                    ((Receiverimagediff) holder).rname.setText("Opened");
                    allow[0] = 0;
                    ((Receiverimagediff) holder).rimageView.setImageResource(R.drawable.ic_baseline_done_all_24);
                } else if (messagemodel.getImagetype().equals("replay") && messagemodel.getMessagekey().equals("once")) {
                    ((Receiverimagediff) holder).rimageView.setImageResource(R.drawable.ic_baseline_replay_24);
                    ((Receiverimagediff) holder).rname.setText("Replay");
                } else {
                    ((Receiverimagediff) holder).rimageView.setImageResource(R.drawable.ic_baseline_done_all_24);
                    ((Receiverimagediff) holder).rname.setText("Opened");
                    allow[0] = 0;
                }
            }
            else {
                if (messagemodel.getMessagekey().equals("none")) {
                    ((Receiverimagediff) holder).rname.setText("Play Video");
                    ((Receiverimagediff) holder).rimageView.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                } else if ((messagemodel.getMessagekey().equals("once") && messagemodel.getImagetype().equals("once")) ||
                        (messagemodel.getMessagekey().equals("replay") && messagemodel.getImagetype().equals("replay"))) {
                    ((Receiverimagediff) holder).rname.setText("Opened");
                    allow[0] = 0;
                    ((Receiverimagediff) holder).rimageView.setImageResource(R.drawable.ic_baseline_done_all_24);
                } else if (messagemodel.getImagetype().equals("replay") && messagemodel.getMessagekey().equals("once")) {
                    ((Receiverimagediff) holder).rimageView.setImageResource(R.drawable.ic_baseline_replay_24);
                    ((Receiverimagediff) holder).rname.setText("Replay Video");
                } else {
                    ((Receiverimagediff) holder).rimageView.setImageResource(R.drawable.ic_baseline_done_all_24);
                    ((Receiverimagediff) holder).rname.setText("Opened");
                    allow[0] = 0;
                }
            }
            DatabaseReference database;
            String r;
            r=receiverRoom.replace(mainpage.curuser,"");
            database=FirebaseDatabase.getInstance().getReference().child("AAA usernames").child(r);
            database.addValueEventListener(new ValueEventListener() {
                @Override public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(snapshot.hasChild("userdp"))
                    {
                        String dp = snapshot.child("userdp").getValue().toString();
                        Picasso.get().load(dp).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.userdefaultdp)
                                .into(((Receiverimagediff) holder).circleImageView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Picasso.get().load(dp).placeholder(R.drawable.userdefaultdp).into(((Receiverimagediff) holder).circleImageView);
                                    }
                                });
                    }
                }
                @Override public void onCancelled(@NonNull DatabaseError error) {

                } });

            if(messagemodel.getFeelings()!=15)
            {
                ((Receiverimagediff)holder).rfeel.setImageResource(reactions[messagemodel.getFeelings()]);
                ((Receiverimagediff)holder).rfeel.setVisibility(View.VISIBLE);
            }
            else {
                ((Receiverimagediff)holder).rfeel.setVisibility(View.GONE);
            }

            ((Receiverimagediff)holder).rimageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if(allow[0]!=0) {
                        if (messagemodel.getMessagekey().equals("none"))
                            messagemodel.setMessagekey("once");
                        else messagemodel.setMessagekey("replay");

                        DatabaseReference databaseReference;
                        databaseReference = FirebaseDatabase.getInstance().getReference();
                        databaseReference.child("Chats").child(mainpage.curuser).child(senderRoom).child(messagemodel.getMessid())
                                .child("messagekey").setValue(messagemodel.getMessagekey()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                databaseReference.child("Chats").child(chatactivity.uid).child(receiverRoom).child(messagemodel.getMessid())
                                        .child("messagekey").setValue("opened").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                    }
                                });
                            }
                        });
                    }
                    if (allow[0]==1) {
                        if (messagemodel.getChecker().contains("image")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            final View view = LayoutInflater.from(context).inflate(R.layout.customimagechatalert, null);
                            builder.setView(view);

                            AlertDialog alertDialog = builder.create();
                            TextView textView = view.findViewById(R.id.imagemessage);
                            ImageButton feel = view.findViewById(R.id.alertimagezoom);
                            ImageButton down = view.findViewById(R.id.alertimagedownload);
                            down.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(messagemodel.getImageurl()));
                                    context.startActivity(i);
                                }
                            });
                            ImageButton cancel = view.findViewById(R.id.alertimagecancel);
                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialog.dismiss();
                                    alertDialog.cancel();
                                }
                            });
                            feel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent(context, imagezoom.class);
                                    i.putExtra("zoomuri", messagemodel.getImageurl());
                                    context.startActivity(i);
                                }
                            });
                            if (messagemodel.getChecker().equals("image w/o text"))
                                textView.setVisibility(View.GONE);
                            else
                                textView.setVisibility(View.VISIBLE);
                            ProgressBar simpleProgressBar = (ProgressBar) view.findViewById(R.id.progressBar); // initiate the progress bar
                            simpleProgressBar.setMax(1500); // 100 maximum value for the progress value
                            textView.setText(messagemodel.getMessage());
                            PhotoView photoView = view.findViewById(R.id.alertphotoview);
                            photoView.setMinimumScale(1);
                            photoView.setMaximumScale(12);
                            // simpleProgressBar.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                            Picasso.get().load(messagemodel.getImageurl()).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.userdefaultdp)
                                    .into(photoView, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            new CountDownTimer(15000, 10) {
                                                public void onTick(long millisUntilFinished) {
                                                    // timer.setText((millisUntilFinished / 1000)+"");
                                                    simpleProgressBar.setProgress((int) (millisUntilFinished / 10));
                                                }

                                                public void onFinish() {
                                                    alertDialog.dismiss();
                                                }
                                            }.start();
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Picasso.get().load(messagemodel.getImageurl()).placeholder(R.drawable.userdefaultdp).into(photoView);
                                            new CountDownTimer(15000, 10) {
                                                public void onTick(long millisUntilFinished) {
                                                    simpleProgressBar.setProgress((int) (millisUntilFinished / 10));
                                                }

                                                public void onFinish() {
                                                    alertDialog.dismiss();
                                                }
                                            }.start();
                                        }
                                    });

                            alertDialog.show();
                        }
                        else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            final View view = LayoutInflater.from(context).inflate(R.layout.customvideodiffalert, null);
                            builder.setView(view);
                            AlertDialog alertDialog = builder.create();
                            alertDialog.setCanceledOnTouchOutside(false);
                            TextView textView = view.findViewById(R.id.videomessage);
                            PlayerView alertvideo=view.findViewById(R.id.alertvideoview);
                            ImageView fullzoom=alertvideo.findViewById(R.id.exo_fullscreen);
                            SimpleExoPlayer simpleExoPlayer = new SimpleExoPlayer.Builder(context).build();
                            ImageButton feel = view.findViewById(R.id.alertvideozoom);
                            ImageButton down = view.findViewById(R.id.alertvideodownload);
                            ImageButton vol=view.findViewById(R.id.alertvideovolume);
                            ImageButton voldown=view.findViewById(R.id.alertvideovolumedown);
                            voldown.setVisibility(View.GONE);
                            vol.setVisibility(View.VISIBLE);
                            vol.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    simpleExoPlayer.setVolume(0);
                                    vol.setVisibility(View.GONE);
                                    voldown.setVisibility(View.VISIBLE);
                                }
                            });
                            voldown.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    simpleExoPlayer.setVolume(simpleExoPlayer.getDeviceVolume());
                                    voldown.setVisibility(View.GONE);
                                    vol.setVisibility(View.VISIBLE);
                                }
                            });
                            down.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(messagemodel.getImageurl()));
                                    context.startActivity(i);
                                }
                            });
                            ImageButton cancel = view.findViewById(R.id.alertvideocancel);
                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialog.dismiss();
                                    alertDialog.cancel();
                                }
                            });
                            feel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    simpleExoPlayer.pause();
                                    Intent i = new Intent(context, fullscreenvideo.class);
                                    i.putExtra("uri", messagemodel.getImageurl());
                                    i.putExtra("pos", simpleExoPlayer.getCurrentPosition());
                                    context.startActivity(i);
                                }
                            });
                            if (messagemodel.getChecker().equals("video w/o text"))
                                textView.setVisibility(View.GONE);
                            else
                                textView.setVisibility(View.VISIBLE);
                            textView.setText(messagemodel.getMessage());
                            try {
                                alertvideo.setPlayer(simpleExoPlayer);
                                MediaItem mediaItem=MediaItem.fromUri(Uri.parse(messagemodel.getImageurl()));
                                simpleExoPlayer.addMediaItems(Collections.singletonList(mediaItem));
                                simpleExoPlayer.prepare();
                                simpleExoPlayer.setPlayWhenReady(true);
                                simpleExoPlayer.addListener(new Player.Listener() {
                                    @Override
                                    public void onPlaybackStateChanged(int state) {
                                        if(state==Player.STATE_ENDED)
                                        {
                                            alertvideo.getPlayer().stop();
                                            alertDialog.dismiss();
                                        }
                                    }
                                });
                                if(simpleExoPlayer.getPlaybackState()== Player.STATE_ENDED)
                                {

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            fullzoom.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i=new Intent(context,fullscreenvideo.class);
                                    i.putExtra("uri",messagemodel.getImageurl());
                                    alertvideo.getPlayer().stop();
                                    context.startActivity(i);
                                }
                            });
                            alertDialog.show();
                        }
                    }
                    return false;
                }
            });

            ((Receiverimagediff)holder).rlayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction()==MotionEvent.ACTION_DOWN) {
                        if (!messagemodel.getUid().equals(mainpage.curuser)) {
                            popup.onTouch(v, event);
                        }
                    }
                    return false;
                }
            });
        }
        else {
            DatabaseReference database;
            String r;
            r=receiverRoom.replace(mainpage.curuser,"");
            database=FirebaseDatabase.getInstance().getReference().child("AAA usernames").child(r);
            database.addValueEventListener(new ValueEventListener() {
                @Override public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if(snapshot.hasChild("userdp"))
                    {
                        String dp = snapshot.child("userdp").getValue().toString();
                        Picasso.get().load(dp).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.userdefaultdp)
                                .into(((ReceiverViewfile) holder).circleImageView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Picasso.get().load(dp).placeholder(R.drawable.userdefaultdp).into(((ReceiverViewfile) holder).circleImageView);
                                    }
                                });
                    }
                }
                @Override public void onCancelled(@NonNull DatabaseError error) {

                } });
            if(messagemodel.getFeelings()!=15)
            {
                ((ReceiverViewfile)holder).rfeel.setImageResource(reactions[messagemodel.getFeelings()]);
                ((ReceiverViewfile)holder).rfeel.setVisibility(View.VISIBLE);
            }
            else {
                ((ReceiverViewfile)holder).rfeel.setVisibility(View.GONE);
            }
            if(messagemodel.getChecker().equals("pdf"))
                ((ReceiverViewfile)holder).rimageView.setImageResource(R.drawable.pdflogo);
            else  ((ReceiverViewfile)holder).rimageView.setImageResource(R.drawable.wordlogo);

            ((ReceiverViewfile)holder).rlayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(!messagemodel.getUid().equals(mainpage.curuser)) {
                        popup.onTouch(v, event);
                    }
                    return false;
                }
            });
            ((ReceiverViewfile)holder).rname.setText(messagemodel.getMessage().toUpperCase());
            ((ReceiverViewfile)holder).rname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(Intent.ACTION_VIEW, Uri.parse(messagemodel.getImageurl()));
                    context.startActivity(i);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return messagemodels.size();
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder{

        TextView rmessage,rtime;
        ImageView rfeel;
        ImageButton rfull;
        PhotoView rimageView;
        ConstraintLayout rlayout;
        CircleImageView circleImageView;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            auth=FirebaseAuth.getInstance();
            curuser=auth.getCurrentUser().getUid();
            rlayout=itemView.findViewById(R.id.rlay);
            rfull=itemView.findViewById(R.id.rfullscreen);
            rfull.setVisibility(View.GONE);
            rfeel=itemView.findViewById(R.id.rfeelings);
            rimageView=itemView.findViewById(R.id.rimageview);
            rimageView.setImageResource(R.drawable.wallpaperbg);
            rmessage=itemView.findViewById(R.id.chattext);
            rtime=itemView.findViewById(R.id.chat_text_time);
            rfeel.setVisibility(View.GONE);
            circleImageView=itemView.findViewById(R.id.layoutreceiverdp3);
        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder{

        TextView smessage,stime;
        ImageView sfeel;
        PhotoView simageView;
        ImageButton sfull;
        ConstraintLayout slayout;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            auth=FirebaseAuth.getInstance();
            curuser=auth.getCurrentUser().getUid();
            sfeel=itemView.findViewById(R.id.sfeelings);
            sfeel.setVisibility(View.GONE);
            slayout=itemView.findViewById(R.id.slay);
            sfull=itemView.findViewById(R.id.sfullscreen);
            sfull.setVisibility(View.GONE);
            simageView=itemView.findViewById(R.id.simageview);
            simageView.setImageResource(R.drawable.wallpaperbg);
            smessage=itemView.findViewById(R.id.chat_textsend);
            stime=itemView.findViewById(R.id.schat_text_timesend);
        }
    }

    public class ReceiverViewfile extends RecyclerView.ViewHolder{

        TextView rname,rtime;
        ImageView rimageView,rfeel;
        ConstraintLayout rlayout;
        CircleImageView circleImageView;

        public ReceiverViewfile(@NonNull View itemView) {
            super(itemView);
            auth=FirebaseAuth.getInstance();
            curuser=auth.getCurrentUser().getUid();
            rfeel=itemView.findViewById(R.id.rfilefeelings);
            rfeel.setVisibility(View.GONE);
            rlayout=itemView.findViewById(R.id.rlayfiles);
            rimageView=itemView.findViewById(R.id.rfilephoto);
            rimageView.setImageResource(R.drawable.pdflogo);
            rname=itemView.findViewById(R.id.rfilemess);
            circleImageView=itemView.findViewById(R.id.layoutfilereceiverdp3);
            rtime=itemView.findViewById(R.id.rfile_text_time);
        }
    }

    public class SenderViewfile extends RecyclerView.ViewHolder{

        TextView sname,stime;
        ImageView simageView,sfeel;
        ConstraintLayout slayout;

        public SenderViewfile(@NonNull View itemView) {
            super(itemView);
            auth=FirebaseAuth.getInstance();
            curuser=auth.getCurrentUser().getUid();
            sfeel=itemView.findViewById(R.id.sfilefeelings);
            sfeel.setVisibility(View.GONE);
            slayout=itemView.findViewById(R.id.slayfilescons);
            simageView=itemView.findViewById(R.id.sfileimage);
            sname=itemView.findViewById(R.id.sfilemess);
            simageView.setImageResource(R.drawable.pdflogo);
            stime=itemView.findViewById(R.id.sfiletime);
        }
    }

    public class Senderimagediff extends RecyclerView.ViewHolder{

        TextView sname,stime;
        ImageView simageView,sfeel;
        ConstraintLayout slayout;
        int count=0;

        public Senderimagediff(@NonNull View itemView) {
            super(itemView);
            auth=FirebaseAuth.getInstance();
            curuser=auth.getCurrentUser().getUid();
            sfeel=itemView.findViewById(R.id.simagetypefeelings);
            sfeel.setVisibility(View.GONE);
            slayout=itemView.findViewById(R.id.simgtypecons);
            simageView=itemView.findViewById(R.id.simagetypedp);
            sname=itemView.findViewById(R.id.senderimagetypetext);
            simageView.setImageResource(R.drawable.ic_baseline_play_arrow_24_black);
            stime=itemView.findViewById(R.id.simgtypetime);
        }
    }

    public class Receiverimagediff extends RecyclerView.ViewHolder{

        TextView rname,rtime;
        ImageView rimageView,rfeel;
        ConstraintLayout rlayout;
        int count=0;
        CircleImageView circleImageView;

        public Receiverimagediff(@NonNull View itemView) {
            super(itemView);
            auth=FirebaseAuth.getInstance();
            curuser=auth.getCurrentUser().getUid();
            rfeel=itemView.findViewById(R.id.rimagetypefeelings);
            rfeel.setVisibility(View.GONE);
            circleImageView=itemView.findViewById(R.id.layoutreceiverdiffdp);
            rlayout=itemView.findViewById(R.id.rimgtypecons);
            rimageView=itemView.findViewById(R.id.rimagetypedp);
            rname=itemView.findViewById(R.id.receiverimagetypetext);
            rimageView.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            rtime=itemView.findViewById(R.id.rimgtypetime);
        }
    }

    public class Sendervideo extends RecyclerView.ViewHolder{

        TextView sname,stime;
        ImageView sfeel;
        ConstraintLayout slayout;
        SimpleExoPlayer exoPlayer;
        PlayerView playerView;
        ImageView full;

        public Sendervideo(@NonNull View itemView) {
            super(itemView);
            auth=FirebaseAuth.getInstance();
            curuser=auth.getCurrentUser().getUid();
            sfeel=itemView.findViewById(R.id.sfeelingsvideo);
            sfeel.setVisibility(View.GONE);
            slayout=itemView.findViewById(R.id.slayvideo);
            sname=itemView.findViewById(R.id.videomessage);
            stime=itemView.findViewById(R.id.schat_video_timesend);
            playerView=itemView.findViewById(R.id.svideoview);
            full=playerView.findViewById(R.id.exo_fullscreen);
        }
    }

    public class Receivervideo extends RecyclerView.ViewHolder{

        TextView rname,rtime;
        ImageView rfeel;
        ConstraintLayout rlayout;
        SimpleExoPlayer rexoPlayer;
        PlayerView rplayerView;
        ImageView rfull,dpvideo;

        public Receivervideo(@NonNull View itemView) {
            super(itemView);
            auth=FirebaseAuth.getInstance();
            curuser=auth.getCurrentUser().getUid();
            rfeel=itemView.findViewById(R.id.rvideofeelings);
            rfeel.setVisibility(View.GONE);
            rlayout=itemView.findViewById(R.id.rlayvideo);
            rname=itemView.findViewById(R.id.rvideochattext);
            rtime=itemView.findViewById(R.id.r_videochat_text_time);
            rplayerView=itemView.findViewById(R.id.rvideoview);
            rfull=rplayerView.findViewById(R.id.exo_fullscreen);
            dpvideo=itemView.findViewById(R.id.layoutreceiverdpvideo3);
        }
    }
    public class Receivermap extends RecyclerView.ViewHolder{

        TextView rtime;
        ImageView rfeel,rdp,rmap;
        ConstraintLayout rlay;

        public Receivermap(@NonNull View itemView) {
            super(itemView);
            auth=FirebaseAuth.getInstance();
            curuser=auth.getCurrentUser().getUid();
            rfeel=itemView.findViewById(R.id.rfeelingsmap);
            rfeel.setVisibility(View.GONE);
            rlay=itemView.findViewById(R.id.rlaymap);
            rmap=itemView.findViewById(R.id.rmap);
            rtime=itemView.findViewById(R.id.rmaptime);
            rdp=itemView.findViewById(R.id.rmapdp);
        }
    }
    public class Sendermap extends RecyclerView.ViewHolder{

        TextView stime;
        ConstraintLayout slay;
        ImageView sfeel,smap;

        public Sendermap(@NonNull View itemView) {
            super(itemView);
            auth=FirebaseAuth.getInstance();
            curuser=auth.getCurrentUser().getUid();
            sfeel=itemView.findViewById(R.id.sfeelingmap);
            sfeel.setVisibility(View.GONE);
            slay=itemView.findViewById(R.id.slaymap);
            smap=itemView.findViewById(R.id.smap);
            stime=itemView.findViewById(R.id.smaptime);

        }
    }
    public class receiveraudio extends RecyclerView.ViewHolder{

        TextView rtime,rname;
        ImageView rdp,rplay;
        ConstraintLayout rlay;
        SeekBar rseekbar;

        public receiveraudio(@NonNull View itemView) {
            super(itemView);
            auth=FirebaseAuth.getInstance();
            curuser=auth.getCurrentUser().getUid();
            rseekbar=itemView.findViewById(R.id.rseekbaraudio);
            rlay=itemView.findViewById(R.id.raudiocons);
            rplay=itemView.findViewById(R.id.rplayaudio);
            rname=itemView.findViewById(R.id.raudioname);
            rtime=itemView.findViewById(R.id.raudiotime);
            rdp=itemView.findViewById(R.id.raudiodp);
        }
    }
    public class senderaudio extends RecyclerView.ViewHolder{

        TextView stime,sname;
        ConstraintLayout slay;
        ImageView splay;
        SeekBar seekBar;

        public senderaudio(@NonNull View itemView) {
            super(itemView);
            auth=FirebaseAuth.getInstance();
            curuser=auth.getCurrentUser().getUid();
            slay=itemView.findViewById(R.id.saudiocons);
            splay=itemView.findViewById(R.id.saudioplay);
            stime=itemView.findViewById(R.id.saudiotime);
            sname=itemView.findViewById(R.id.saudioname);
            seekBar=itemView.findViewById(R.id.seekbarsender);
        }
    }



}
