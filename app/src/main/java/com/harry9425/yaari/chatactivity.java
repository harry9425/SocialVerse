package com.harry9425.yaari;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.gestures.GestureDetector;
import com.google.android.gms.tasks.OnCanceledListener;
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
import com.nandroidex.upipayments.listener.PaymentStatusListener;
import com.nandroidex.upipayments.models.TransactionDetails;
import com.nandroidex.upipayments.utils.UPIPayment;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class chatactivity extends AppCompatActivity implements @NotNull PaymentStatusListener {

    FirebaseAuth auth;
    CircleImageView circleImageView;
    chatadapter chatadapter2;
    TextView username,indicator,audioduration,audiostatus,audioinfo;
    EditText textmessage;
    CountDownTimer countDowntimer;
    public static String name,indioffon,uid,dp;
    ImageButton bk,uploadbtn,camerabtn,sendbtn,videocall,startrecord,closeaudiolay,audiodump,sendaudiofile,playaudiobtn,downcard;
    CardView chatcard,sendcard,downview;
    String messagewrote;
    String checker="";
    ImageView imageView;
    String curuserid,videoid,record_status="none";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String currentPhotoPath;
    StorageReference mref;
    UPIPayment upiPayment;
    int allowindi=1,second=-1,minute=0,hour=0,duraudio=0;
    RelativeLayout relativeLayout;
    LinearLayout audiolay;
    LinearLayout chatlay;
    public static Uri resultUri,fileuri,videouri;
    private Toolbar toolbar;
    ProgressDialog progressDialog;
    private MediaPlayer mediaPlayer;
    private MediaRecorder mediaRecorder;
    private String AudioSavaPath=null;
    public  static int captured=0;
    DatabaseReference mDatabase,typo,gettypo;
    String chatuidmain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatactivity);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ConstraintLayout constraintLayout=(ConstraintLayout) findViewById(R.id.chatconslayout);
       /* AnimationDrawable animationDrawable=(AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
        */
        chatuidmain=FirebaseAuth.getInstance().getCurrentUser().getUid();
        audiolay=(LinearLayout) findViewById(R.id.audiolayout);
        chatlay=(LinearLayout) findViewById(R.id.chatlinear);
        chatlay.setVisibility(View.VISIBLE);
        downview=(CardView) findViewById(R.id.movetobottom);
        downview.setCardBackgroundColor(Color.parseColor("#191919"));
        audiolay.setVisibility(View.GONE);
        imageView=(ImageView) findViewById(R.id.wallpaperset);
        imageView.setVisibility(View.GONE);
        toolbar.getOverflowIcon().setTint(Color.WHITE);
        downcard=(ImageButton) findViewById(R.id.downchat);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("UPLOADING PLEASE WAIT....");
        progressDialog.setTitle("UPLOADING FILE");
        mref= FirebaseStorage.getInstance().getReference();
        sendcard=(CardView) findViewById(R.id.sendcard);
        chatcard=(CardView) findViewById(R.id.textcard);
        camerabtn=(ImageButton) findViewById(R.id.camerabtn);
        uploadbtn=(ImageButton) findViewById(R.id.uploadbtn);
        videocall=(ImageButton) findViewById(R.id.videocall);
        dp = getIntent().getStringExtra("dp");
        name = getIntent().getStringExtra("name");
        uid = getIntent().getStringExtra("uidm");
        bk=(ImageButton) findViewById(R.id.bkbtnchat);
        auth=FirebaseAuth.getInstance();
        curuserid=auth.getCurrentUser().getUid().toString();
        sendbtn=(ImageButton) findViewById(R.id.chatsendbtn);
        textmessage=(EditText) findViewById(R.id.chatmessage);
        circleImageView=(CircleImageView) findViewById(R.id.chatdp);
        username=(TextView) findViewById(R.id.chatnametext);
        indicator=(TextView) findViewById(R.id.onoffindi);
        username.setText(name);
        downcard.setVisibility(View.GONE);
        gettypo=FirebaseDatabase.getInstance().getReference();

        gettypo.child("Chats").child(uid).child(uid+chatuidmain).child("AA details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("typing"))
                { allowindi=0;
                  indicator.setText("Typing.....");
                }
                else {
                    gettypo = FirebaseDatabase.getInstance().getReference().child("AAA usernames").child(uid);
                    gettypo.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            indioffon = snapshot.child("presence").getValue().toString();
                            if(indioffon.equals("online"))
                                indicator.setText("Active");
                            else
                            {
                                indicator.setText("InActive");
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
            }
        });
        wallset();
        bk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
           textmessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            sendbtn.setImageResource(R.drawable.ic_baseline_send_24);
                camerabtn.setVisibility(View.GONE);
                typo=FirebaseDatabase.getInstance().getReference().child("Chats");
                typo.child(chatuidmain).child(chatuidmain+uid).child("AA details").child("typing").setValue("Typing....").addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {
                }
            });
            }
            @Override
            public void afterTextChanged(Editable s) {
                sendcard.setVisibility(View.VISIBLE);
                if(textmessage.getText().toString().isEmpty()) {
                    typo=FirebaseDatabase.getInstance().getReference().child("Chats");
                    typo.child(chatuidmain).child(chatuidmain+uid).child("AA details").child("typing").removeValue().addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {
                        }
                    });
                    sendbtn.setImageResource(R.drawable.ic_baseline_mic_24);
                    camerabtn.setVisibility(View.VISIBLE);
                }
            }
        });
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
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(chatactivity.this,imagezoom.class);
                i.putExtra("zoomuri",dp);
                startActivity(i);
            }
        });


        final ArrayList<messagemodel> list=new ArrayList<>();
       chatadapter2=new chatadapter(list, this,chatuidmain+uid,uid+chatuidmain,uid);
        final RecyclerView recyclerView=(RecyclerView) findViewById(R.id.chatrecyclerview);
        recyclerView.setAdapter(chatadapter2);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        mDatabase=FirebaseDatabase.getInstance().getReference().child("Chats").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(curuserid+uid);
        mDatabase.keepSynced(true);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            list.clear();
            for(DataSnapshot snapshot1:snapshot.getChildren())
            {
                if(!snapshot1.getKey().equals("AA details")) {
                    messagemodel model = snapshot1.getValue(messagemodel.class);
                    model.setMessid(snapshot1.getKey());
                    if(snapshot1.hasChild("imagetype"))
                        model.setImagetype(snapshot1.child("imagetype").getValue().toString());
                    else
                        model.setImagetype("none");
                    list.add(model);
                }
            }
            if (!recyclerView.canScrollVertically(1) && downcard.getVisibility()==View.GONE) {
                recyclerView.smoothScrollToPosition(chatadapter2.getItemCount());
                downcard.setVisibility(View.GONE);
            }
            else {
                 downcard.setVisibility(View.VISIBLE);
                 downview.setCardBackgroundColor(Color.parseColor("#4CAF50"));
            }
            chatadapter2.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (recyclerView.canScrollVertically(1)) {
                    downcard.setVisibility(View.VISIBLE);
                }
                else {
                    downcard.setVisibility(View.GONE);
                    downview.setCardBackgroundColor(Color.parseColor("#191919"));
                }
            }
        });

        downcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downview.setCardBackgroundColor(Color.parseColor("#191919"));
                recyclerView.smoothScrollToPosition(chatadapter2.getItemCount());
                downcard.setVisibility(View.GONE);
            }
        });

        URL serverurl;

        try {
            serverurl=new URL("https://meet.jit.si");
            JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(serverurl)
                    .setWelcomePageEnabled(false)
                    .build();
            JitsiMeet.setDefaultConferenceOptions(options);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        videocall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase = FirebaseDatabase.getInstance().getReference().child("Friends list data").child(chatuidmain).child(uid);
                mDatabase.keepSynced(true);
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        videoid=snapshot.child("videocallid").getValue().toString();
                        JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                                .setRoom(videoid)
                                .setVideoMuted(true)
                                .setAudioOnly(true)
                                .setWelcomePageEnabled(false)
                                .build();
                        JitsiMeetActivity.launch(chatactivity.this, options);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(chatactivity.this, "CAN'T CONNECT", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        closeaudiolay=(ImageButton) findViewById(R.id.closeaudiolay);
        startrecord=(ImageButton) findViewById(R.id.recordaudiobtn);
        audiodump=(ImageButton) findViewById(R.id.audiodump);
        audiodump.setVisibility(View.GONE);
        sendaudiofile=(ImageButton) findViewById(R.id.audiosend);
        sendaudiofile.setVisibility(View.GONE);
        audioduration=(TextView) findViewById(R.id.audiotime);
        audioinfo=(TextView) findViewById(R.id.audioinfo);
        audiostatus=(TextView) findViewById(R.id.audiostatus);
        playaudiobtn=(ImageButton) findViewById(R.id.playaudiobtn);
        closeaudiolay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatlay.setVisibility(View.VISIBLE);
                audiolay.setVisibility(View.GONE);
                startrecord.setImageResource(R.drawable.ic_baseline_mic_24);
                startrecord.setBackgroundResource(R.drawable.buttonbg);
                startrecord.setVisibility(View.VISIBLE);
                playaudiobtn.setVisibility(View.GONE);
                sendaudiofile.setVisibility(View.GONE);
                audiodump.setVisibility(View.GONE);
                audioinfo.setText("Hold for record");
                audiostatus.setText("Start Record");
                audioduration.setText("00:00:00");
                if(AudioSavaPath!=null) {
                    File file = new File(AudioSavaPath);
                    if (!file.exists())
                        file.delete();
                }
                record_status="none";
            }
        });
        sendaudiofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AudioSavaPath!=null)
                    try {
                        Uri uriAudio = Uri.fromFile(new File(AudioSavaPath).getAbsoluteFile());
                        audiochat(uriAudio);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        });
        sendbtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(textmessage.getText().toString().isEmpty()) {
                    chatlay.setVisibility(View.GONE);
                    startrecord.setImageResource(R.drawable.ic_baseline_mic_24);
                    startrecord.setBackgroundResource(R.drawable.buttonbg);
                    audioinfo.setText("Hold for record");
                    audiostatus.setText("Start Record");
                    audioduration.setText("00:00:00");
                    audiolay.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
        startrecord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if (ContextCompat.checkSelfPermission(chatactivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
                            ActivityCompat.requestPermissions(chatactivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
                        } else {
                                startrecord.setImageResource(R.drawable.ic_round_pause_24);
                                startrecord.setBackgroundResource(R.drawable.buttonbg);
                                AudioSavaPath = getExternalFilesDir(null) + "/" + "recordingAudio.mp3";
                                mediaRecorder = new MediaRecorder();
                                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                                mediaRecorder.setOutputFile(AudioSavaPath);
                                try {
                                    audioinfo.setText("Release for end record");
                                    mediaRecorder.prepare();
                                    mediaRecorder.start();
                                    showTimer();
                                    audiostatus.setText("Stop Record");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(chatactivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        startrecord.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                        startrecord.setBackgroundResource(R.drawable.buttonbgg);
                        mediaPlayer = new MediaPlayer();
                        mediaRecorder.stop();
                        mediaRecorder.release();
                        countDowntimer.cancel();
                        second=-1;minute=0;hour=0;
                        try {
                            mediaPlayer.setDataSource(AudioSavaPath);
                            mediaPlayer.prepare();
                           // Toast.makeText(chatactivity.this,mediaPlayer.getDuration()+"",Toast.LENGTH_LONG).show();
                            if(mediaPlayer.getDuration()>1000)
                            {
                                audioinfo.setText("Send Audio File");
                                audiostatus.setText("Start Playing");
                                record_status="recording_success_save";
                                audiodump.setVisibility(View.VISIBLE);
                                sendaudiofile.setVisibility(View.VISIBLE);
                                playaudiobtn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                                startrecord.setVisibility(View.GONE);
                                playaudiobtn.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                audioinfo.setText("Hold for record");
                                audiostatus.setText("Start Record");
                                audioduration.setText("00:00:00");
                                startrecord.setImageResource(R.drawable.ic_baseline_mic_24);
                                startrecord.setBackgroundResource(R.drawable.buttonbg);
                                record_status="none";
                                File file = new File(AudioSavaPath);
                                if (file.exists())
                                    file.delete();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
                return true;
            }
        });
        audiodump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startrecord.setImageResource(R.drawable.ic_baseline_mic_24);
                startrecord.setBackgroundResource(R.drawable.buttonbg);
                startrecord.setVisibility(View.VISIBLE);
                playaudiobtn.setVisibility(View.GONE);
                audioinfo.setText("Hold for record");
                audiostatus.setText("Start Record");
                audioduration.setText("00:00:00");
                record_status="none";
                sendaudiofile.setVisibility(View.GONE);
                File file = new File(AudioSavaPath);
                if (file.exists())
                file.delete();
            }
        });
        playaudiobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(record_status.equals("recording_success_save")) {
                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(AudioSavaPath);
                        mediaPlayer.prepare();
                        if(audiostatus.getText().equals("Resume Playing"))
                        mediaPlayer.seekTo(duraudio);
                        mediaPlayer.start();
                        playaudiobtn.setImageResource(R.drawable.ic_round_pause_24);
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                audioinfo.setText("Send Audio File");
                                record_status = "recording_success_save";
                                audiostatus.setText("Play audio");
                                if (mediaPlayer != null) {
                                    mediaPlayer.stop();
                                    mediaPlayer.release();
                                    playaudiobtn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                                }
                            }
                        });
                        audioinfo.setText("Send Audio File");
                        record_status = "Playing_record";
                        audiostatus.setText("Pause Playing");
                        Toast.makeText(chatactivity.this, "Start playing", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else if(record_status.equals("Playing_record"))
                {
                    audioinfo.setText("Send Audio File");
                    playaudiobtn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                    record_status = "recording_success_save";
                    audiostatus.setText("Resume Playing");
                    if (mediaPlayer != null) {
                        duraudio=mediaPlayer.getCurrentPosition();
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    }
                }
            }
        });
    }
    public void showTimer() {
        countDowntimer = new CountDownTimer(Long.MAX_VALUE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                second++;
                audioduration.setText(recorderTime());
            }
            public void onFinish() {

            }
        };
        countDowntimer.start();
    }

    //recorder time
    public String recorderTime() {
        if (second == 60) {
            minute++;
            second = 0;
        }
        if (minute == 60) {
            hour++;
            minute = 0;
        }
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions, @NonNull int[] grantResults)
    { super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(chatactivity.this, "Record Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(chatactivity.this, "Record Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStop() {
        typo=FirebaseDatabase.getInstance().getReference().child("Chats");
        typo.child(chatuidmain).child(chatuidmain+uid).child("AA details").child("typing").removeValue().addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
            }
        });
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.chatmenu,menu);
        return true;
    }

    public void sendmessage(View view)
    {
        typo=FirebaseDatabase.getInstance().getReference().child("Chats");
        typo.child(chatuidmain).child(chatuidmain+uid).child("AA details").child("typing").removeValue().addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
            }
        });
        messagewrote=textmessage.getText().toString().trim();
        if(!messagewrote.isEmpty()) {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            String randomkey = mDatabase.push().getKey();
            camerabtn.setVisibility(View.VISIBLE);
            final messagemodel model = new messagemodel(curuserid, messagewrote);
            model.setTimestamp(System.currentTimeMillis());
            model.setFeelings(15);
            model.setChecker("text");
            textmessage.setText("");
            mDatabase.child("Chats").child(curuserid).child(curuserid + uid).child(randomkey).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mDatabase.child("Chats").child(uid).child(uid + curuserid).child(randomkey).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("lastmess", messagewrote);
                            hashMap.put("lastmess time", model.getTimestamp());
                            mDatabase.child("Chats").child(uid).child(uid + curuserid).child("AA details").setValue(hashMap);
                            mDatabase.child("Chats").child(curuserid).child(curuserid+uid).child("AA details").setValue(hashMap);
                            getsignalid(messagewrote);
                        }
                    });
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId())
        {
            case R.id.clearchat: {

            mDatabase=FirebaseDatabase.getInstance().getReference().child("Chats").child(curuserid);
            mDatabase.child(chatuidmain+uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                 mDatabase=FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("Clear chat request").child(chatuidmain).setValue(chatuidmain+uid).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    });
                }
            });

            break;
            }
            case R.id.blockuser: {

            }
            case R.id.wallpaperbgset: {
                Intent i=new Intent(this,wallpaperset.class);
                startActivity(i);
                break;
            }
        }
        return true;
    }

    private void getsignalid(final String messagewrote2)
    {
        mDatabase=FirebaseDatabase.getInstance().getReference();
        mDatabase.child("AAA usernames").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("ONEID")) {
                    new sendnotifications(messagewrote2,"NEW CHAT FROM "+name.toUpperCase(),(snapshot.child("ONEID").getValue().toString()));
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }});
    }

    private void wallset()
    {
        String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("AAA usernames").child(uid);
        mDatabase.keepSynced(true);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("wallpaperlink")) {
                    imageView.setVisibility(View.VISIBLE);
                    final String image = snapshot.child("wallpaperlink").getValue().toString();
                   Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.wallpaperbg)
                            .into(imageView, new Callback() {
                                @Override
                                public void onSuccess() {}
                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(image).placeholder(R.drawable.wallpaperbg).into(imageView);
                            }});}
                else {
                   imageView.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void sendimage(View view)
    {
        dispatchTakePictureIntent();
    }

    public  void sendfile(View view)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        final View view2=getLayoutInflater().inflate(R.layout.customfilepicker,null);
        builder.setView(view2);
        AlertDialog alertDialog =builder.create();
        ImageButton gallery=view2.findViewById(R.id.galleryfilepick);
        ImageButton camera=view2.findViewById(R.id.camerafilepick);
        ImageButton location=view2.findViewById(R.id.googlemappick);
        ImageButton pdf=view2.findViewById(R.id.pdffilepick);
        ImageButton word=view2.findViewById(R.id.wordfilepick);
        ImageButton video=view2.findViewById(R.id.videofilepick);
        ImageButton money=view2.findViewById(R.id.moneyfilepick);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
                alertDialog.dismiss();
            }
        });
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if(statusOfGPS)
                {
                    alertDialog.dismiss();
                    Intent i=new Intent(chatactivity.this,sharelocation.class);
                    startActivity(i);

                }
                else {
                   alertDialog.dismiss();
                   showSettingAlert();
                }
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker="image";
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 438);
                alertDialog.dismiss();
            }
        });
        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker="pdf";
                alertDialog.dismiss();
                Intent i=new Intent();
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.setType("application/pdf");
                startActivityForResult(Intent.createChooser(i, "SELECT PDF FILE"),438);
            }
        });
        word.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker="docx";
                alertDialog.dismiss();
                Intent i=new Intent();
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.setType("application/msword");
                startActivityForResult(Intent.createChooser(i, "SELECT WORD FILE"),438);
            }
        });
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Intent i=new Intent();
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.setType("video/*");
                startActivityForResult(Intent.createChooser(i, "SELECT VIDEO"),999);
            }
        });
        money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               payUsingUpi("Sanjeevani Agrawal","9425108192@ybl","self","2.00");
            }
        });
        alertDialog.show();
    }

    void payUsingUpi(  String name,String upiId, String note, String amount) {
        upiPayment = new UPIPayment.Builder()
                .with(this)
                .setPayeeVpa("9425108192@ybl")
                .setPayeeName("sanjeevani agrawal")
                .setTransactionId(Long.toString(System.currentTimeMillis()))
                .setTransactionRefId(Long.toString(System.currentTimeMillis()))
                .setDescription("Caffeine trans")
                .setAmount("2.00")
                .build();

        upiPayment.setPaymentStatusListener(this);
        if (upiPayment.isDefaultAppExist()) {
            onAppNotFound();
            return;
        }
        upiPayment.startPayment();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    File f = new File(currentPhotoPath);
                    Uri uri = Uri.fromFile(f);
                    resultUri = uri;
                    Intent i = new Intent(this, sendimage.class);
                    i.putExtra("path",currentPhotoPath);
                    startActivity(i);
                    break;

                case 999:
                    videouri = data.getData();
                    Intent v=new Intent(chatactivity.this,sendvideo.class);
                    startActivity(v);
                   // Toast.makeText(chatactivity.this,resultdata.toString(),Toast.LENGTH_LONG).show();
                    break;

                case 101:
                    resultUri = data.getData();
                    Intent i2;
                    i2 = new Intent(chatactivity.this, sendimage.class);
                    i2.putExtra("path","$%$");
                    startActivity(i2);
                    break;

                case 438:
                    if (data != null && data.getData() != null) {
                        if (!checker.equals("image")) {
                            Uri fileuri = data.getData();
                            String filename;
                            Cursor cursor = getContentResolver().query(fileuri,null,null,null,null);
                            if(cursor == null) filename=fileuri.getPath();
                            else{
                                cursor.moveToFirst();
                                int idx = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME);
                                filename = cursor.getString(idx);
                                cursor.close();
                            }
                            String name = filename.substring(0,filename.lastIndexOf("."));
                            //String extension = filename.substring(filename.lastIndexOf(".")+1);
                            senddocstochat(fileuri,name);

                        } else if (checker.equals("image")) {
                            resultUri = data.getData();
                            Intent i3 = new Intent(chatactivity.this, sendimage.class);
                            i3.putExtra("path","$%$");
                            startActivity(i3);
                        } else {
                            Toast.makeText(chatactivity.this, "ERROR OCCURED", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
        }
    }
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    private void senddocstochat(Uri uri,String fn)
    {
        progressDialog.show();
        Calendar calendar=Calendar.getInstance();
        StorageReference filepath=mref.child("chats").child(chatuidmain).child(chatuidmain+chatactivity.uid).child("files").child(calendar.getTimeInMillis()+"");
        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(
                        new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                String fileLink = task.getResult().toString();
                                    final DatabaseReference mDatabase;
                                    final messagemodel model = new messagemodel(chatuidmain, fn);
                                   if(checker.equals("pdf"))
                                       model.setChecker("pdf");
                                   else
                                       model.setChecker("docx");

                                    model.setImageurl(fileLink);
                                    model.setFeelings(15);
                                    model.setTimestamp(System.currentTimeMillis());
                                    mDatabase = FirebaseDatabase.getInstance().getReference();
                                    String randomkey=mDatabase.push().getKey();

                                    mDatabase.child("Chats").child(chatuidmain).child(chatuidmain + chatactivity.uid).child(randomkey).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mDatabase.child("Chats").child(chatactivity.uid).child(chatactivity.uid + chatuidmain).child(randomkey).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    progressDialog.dismiss();
                                                    HashMap<String, Object> hashMap = new HashMap<>();
                                                    hashMap.put("lastmess", "You received a document");
                                                    hashMap.put("lastmess time", model.getTimestamp());
                                                    mDatabase.child("Chats").child(uid).child(uid + curuserid).child("AA details").setValue(hashMap);
                                                    hashMap.put("lastmess", "You sent a document");
                                                    mDatabase.child("Chats").child(curuserid).child(curuserid+uid).child("AA details").setValue(hashMap);
                                                    Toast.makeText(chatactivity.this, "DONE", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    });
                                }}).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(chatactivity.this, "ERROR OCCURRED", Toast.LENGTH_SHORT).show();
                    }});}});}

    private File createImageFile() throws IOException {
        String imageFileName = "JPEG_Temp_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this,"ERROR",Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.harry9425.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void audiochat(Uri path)
    {
        Calendar calendar = Calendar.getInstance();
        StorageReference filepath = mref.child("chats").child(chatuidmain).child(chatuidmain+chatactivity.uid).child("Audio").child(calendar.getTimeInMillis() + "");
        filepath.putFile(path).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(
                        new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                String fileLink = task.getResult().toString();
                                Toast.makeText(chatactivity.this, fileLink, Toast.LENGTH_SHORT).show();
                                audiodatabase(fileLink);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(chatactivity.this, "ERROR OCCURRED", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    private void audiodatabase(String link)
    {
        final DatabaseReference mDatabase;
        final messagemodel model = new messagemodel(chatuidmain);
        model.setImageurl(link);
        model.setFeelings(15);
        model.setChecker("audio");
        model.setTimestamp(System.currentTimeMillis());
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String randomkey = mDatabase.push().getKey();

        mDatabase.child("Chats").child(chatuidmain).child(chatuidmain + chatactivity.uid).child(randomkey).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDatabase.child("Chats").child(chatactivity.uid).child(chatactivity.uid + chatuidmain).child(randomkey).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("lastmess", "You Recieved a Audio clip");
                        hashMap.put("lastmess time", model.getTimestamp());
                        mDatabase.child("Chats").child(chatactivity.uid).child(chatactivity.uid + chatuidmain).child("AA details").setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                        hashMap.put("lastmess", "You Sended a Audio clip");
                        mDatabase.child("Chats").child(chatuidmain).child(chatuidmain + chatactivity.uid).child("AA details").setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                chatlay.setVisibility(View.VISIBLE);
                                audiolay.setVisibility(View.GONE);
                                startrecord.setImageResource(R.drawable.ic_baseline_mic_24);
                                startrecord.setBackgroundResource(R.drawable.buttonbg);
                                startrecord.setVisibility(View.VISIBLE);
                                playaudiobtn.setVisibility(View.GONE);
                                sendaudiofile.setVisibility(View.GONE);
                                audiodump.setVisibility(View.GONE);
                                audioinfo.setText("Hold for record");
                                audiostatus.setText("Start Record");
                                audioduration.setText("00:00:00");
                                if(AudioSavaPath!=null) {
                                    File file = new File(AudioSavaPath);
                                    if (!file.exists())
                                        file.delete();
                                }
                                record_status="none";
                            }
                        });
                            }
                        });
                    }
                });
            }
        });
    }

    public void showSettingAlert()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS setting!");
        alertDialog.setMessage("GPS is not enabled, go to settings to enavle gps to acces this feature !! ");
        alertDialog.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Intent i=new Intent(chatactivity.this,chatactivity.class);
                startActivity(i);
                finish();
            }
        });
        alertDialog.show();
    }
    @Override
    public void onAppNotFound() {
    }
    @Override
    public void onTransactionCancelled() {
    }

    @Override
    public void onTransactionCompleted(@Nullable TransactionDetails transactionDetails) {
        String status = null;
        String approvalRefNo = null;
        if (transactionDetails != null) {
            status = transactionDetails.getStatus();
            approvalRefNo = transactionDetails.getApprovalRefNo();
            Toast.makeText(chatactivity.this,status+"\n"+approvalRefNo+"\n"+transactionDetails,Toast.LENGTH_LONG).show();
        }
        upiPayment.detachListener();
    }
    @Override
    public void onTransactionFailed() {
    }
    @Override
    public void onTransactionSubmitted() {
    }
    @Override
    public void onTransactionSuccess() {
    }
}