package com.harry9425.yaari;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.gowtham.library.utils.TrimType;
import com.gowtham.library.utils.TrimVideo;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;

public class socialmediapage extends AppCompatActivity {


    PhotoView photoview;
    Uri file;
    CircleImageView circleImageView;
    TextView titlepost,t1,t2,t3;
    ImageView imageView;
    PlayerView videoView;
    ImageButton cancel,next,change,speedbtn;
    CardView c1,c2,c3,out1,out2,out3;
    int choose=1;
    public static Float inispeed=1f;
    public static Uri reeluri;
    public  static int duration=30;
    Long originalduration,speedduration=null;
    String type;
    Float a;
    ConstraintLayout timebar;
    public  static Uri posturi;
    SimpleExoPlayer simpleExoPlayer;
    public static String currentPhotoPath=null;
    AlertDialog alertDialog,alertDialog2;
    String curuser;
    TextView speedshow;
    MediaController mediaController;
    ConstraintLayout constraintLayout;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socialmediapage);
        FirebaseAuth firebaseAuth;
        speedbtn=(ImageButton) findViewById(R.id.videospeed);
        speedshow=(TextView) findViewById(R.id.speedshow);
        ImageButton ten=(ImageButton) findViewById(R.id.Tensectime);
        ImageButton thirty=(ImageButton) findViewById(R.id.Thirtysectime);
        ten.setAlpha(0.5f);
        ten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                duration=15;
                ten.setAlpha(1f);
                thirty.setAlpha(0.5f);
            }
        });
        thirty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                duration=30;
                ten.setAlpha(0.5f);
                thirty.setAlpha(1f);
            }
        });
        timebar=(ConstraintLayout) findViewById(R.id.timeselect);
        timebar.setVisibility(View.GONE);
        curuser=FirebaseAuth.getInstance().getCurrentUser().getUid();
        circleImageView=(CircleImageView) findViewById(R.id.storypostimagedp) ;
        cancel=(ImageButton) findViewById(R.id.backtomainpage);
        next=(ImageButton) findViewById(R.id.next_to_socialpage);
        constraintLayout=(ConstraintLayout) findViewById(R.id.addpopup);
        titlepost=(TextView) findViewById(R.id.titleinfopost);
        titlepost.setText("Add To Post");
        change=(ImageButton) findViewById(R.id.changepostreel);
        change.setVisibility(View.GONE);
        imageView=(ImageView) findViewById(R.id.imageviewpost);
        videoView=(PlayerView) findViewById(R.id.videoviewpost);
        imageView.setVisibility(View.GONE);
        videoView.setVisibility(View.GONE);
        constraintLayout.setVisibility(View.VISIBLE);
        c1= (CardView) findViewById(R.id.post);
        choose=1;
        c1.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        out1=(CardView) findViewById(R.id.postbtn);
        out2=(CardView) findViewById(R.id.storybtn);
        out3=(CardView) findViewById(R.id.reelbtn);
        out1.setVisibility(View.VISIBLE); out2.setVisibility(View.VISIBLE); out3.setVisibility(View.VISIBLE);
        c2= (CardView) findViewById(R.id.story);
        c3= (CardView) findViewById(R.id.reel);
        t1=(TextView) findViewById(R.id.posttext);
        t1.setTextColor(Color.parseColor("#FF000000"));
        t2=(TextView) findViewById(R.id.storttext);
        t3=(TextView) findViewById(R.id.reeltext);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageView.getVisibility()==View.VISIBLE) {
                    Intent i = new Intent(socialmediapage.this, editpostimage.class);
                    if(choose==1)
                    i.putExtra("type","post");
                    else if(choose==2) {
                        i=new Intent(socialmediapage.this,editstory.class);
                        i.putExtra("type", "story");
                    }

                    startActivity(i);
                }else
                {
                    if(choose==3) {
                        trim(String.valueOf(reeluri));
                    }
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
                if(alertDialog!=null) {
                    alertDialog.show();
                }
                if(alertDialog2!=null) {
                    alertDialog2.dismiss();
                }
                timebar.setVisibility(View.GONE);
                change.setVisibility(View.GONE);
                if(imageView!=null)
                {
                    imageView.setVisibility(View.GONE);
                }
                if(videoView!=null)
                {
                    if(videoView.getPlayer()!=null)
                    {
                        videoView.setVisibility(View.GONE);
                        videoView.getPlayer().stop();
                        videoView.getPlayer().release();
                        videoView.setVisibility(View.GONE);
                        simpleExoPlayer=null;
                        originalduration=null;
                        speedduration=null;
                        inispeed=1f;
                    }
                }
            }
        });
        next.setEnabled(false);
        next.setAlpha(0.5f);
        mDatabase= FirebaseDatabase.getInstance().getReference();
        mDatabase.child("AAA usernames").child(curuser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String dp=snapshot.child("userdp").getValue().toString();
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
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }});

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        final View view2=getLayoutInflater().inflate(R.layout.chooseintentalertbox,null);
        builder.setView(view2);
        alertDialog =builder.create();
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.setCanceledOnTouchOutside(false);
      //  alertDialog.onBackPressed();
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                onBackPressed();
            }
        });
        ImageButton cam=view2.findViewById(R.id.intentpic);
        ImageButton vid=view2.findViewById(R.id.intentvideo);
        ImageButton gallery=view2.findViewById(R.id.intentgallery);
        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/* video/*");
                startActivityForResult(pickIntent, 3);
            }
        });
        alertDialog.show();
        speedbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(socialmediapage.this);
                final View view=getLayoutInflater().inflate(R.layout.customvideospeedalert,null);
                builder.setView(view);
                alertDialog2 =builder.create();
                alertDialog2.getWindow().setGravity(Gravity.CENTER);
                alertDialog2.setCanceledOnTouchOutside(true);
                ImageButton up=view.findViewById(R.id.speedupalert);
                ImageButton down=view.findViewById(R.id.speedlessalert);
                TextView show=view.findViewById(R.id.speedalertshow);
                ImageButton cancel=view.findViewById(R.id.cancelspeedalert);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog2.dismiss();
                        alertDialog2.cancel();
                    }
                });
                show.setText(inispeed+"x");
                alertDialog2.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        speedshow.setText(inispeed+"x");
                        if(simpleExoPlayer!=null)
                        {
                            originalduration= simpleExoPlayer.getDuration();
                            a = (originalduration / inispeed);
                            speedduration=a.longValue();
                            simpleExoPlayer.setPlaybackSpeed(inispeed);
                        }
                    }
                });
                up.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(inispeed==2f){
                            inispeed+=2f;
                        }
                        else if(inispeed==1.5f)
                        {
                            inispeed+=0.5f;
                        }
                        else if(inispeed>=4f) {
                            inispeed = 4f;
                        }
                        else {
                            inispeed+=0.25f;
                        }
                        show.setText(inispeed+"x");
                    }
                });
                down.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(inispeed==4f){
                            inispeed-=2f;
                        }
                        else if(inispeed==2f)
                        {
                            inispeed-=0.5f;
                        }
                        else if(inispeed<=0.25f) {
                            inispeed = 0.25f;
                        }
                        else {
                            inispeed-=0.25f;
                        }
                        show.setText(inispeed+"x");
                    }
                });
                alertDialog2.show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            timebar.setVisibility(View.GONE);
            alertDialog.dismiss();
            constraintLayout.setVisibility(View.GONE);
            imageView=(ImageView) findViewById(R.id.imageviewpost);
            videoView=(PlayerView) findViewById(R.id.videoviewpost);
            change.setVisibility(View.VISIBLE);
            next.setEnabled(true);
            next.setAlpha(1f);
            switch (requestCode) {
                case 1:
                    imageView.setVisibility(View.VISIBLE);
                    videoView.setVisibility(View.GONE);
                    File f = new File(currentPhotoPath);
                    out3.setVisibility(View.GONE);
                    out1.setVisibility(View.VISIBLE);
                    posturi = Uri.fromFile(f);
                    t1.setTextColor(Color.parseColor("#FF000000"));
                    c1.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                    titlepost.setText("Add to Post");
                    Picasso.get().load(posturi).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.userdefaultdp)
                            .into(imageView, new Callback() {
                                @Override
                                public void onSuccess() {}
                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(posturi).placeholder(R.drawable.userdefaultdp).into(imageView);
                                }
                            });
                    break;
                case 3:
                    Uri commonuri = data.getData();
                    if (commonuri.toString().contains("image")) {
                        posturi=commonuri;
                        t1.setTextColor(Color.parseColor("#FF000000"));
                        c1.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                        titlepost.setText("Add to Post");
                        out1.setVisibility(View.VISIBLE);
                        imageView.setVisibility(View.VISIBLE);
                        videoView.setVisibility(View.GONE);
                        out3.setVisibility(View.GONE);
                        Picasso.get().load(commonuri).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.userdefaultdp)
                                .into(imageView, new Callback() {
                                    @Override
                                    public void onSuccess() {}
                                    @Override
                                    public void onError(Exception e) {
                                        Picasso.get().load(commonuri).placeholder(R.drawable.userdefaultdp).into(imageView);
                                    }
                                });
                    } else  if (commonuri.toString().contains("video")) {
                        reeluri=commonuri;
                        timebar.setVisibility(View.VISIBLE);
                        speedduration=null;
                        t2.setTextColor(Color.parseColor("#FF000000"));
                        c2.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                        titlepost.setText("Add to Story");
                        videoView.setVisibility(View.VISIBLE);
                        imageView.setVisibility(View.GONE);
                        out1.setVisibility(View.GONE);
                        out3.setVisibility(View.VISIBLE);
                        try {
                            simpleExoPlayer = new SimpleExoPlayer.Builder(this).build();
                            videoView.setPlayer(simpleExoPlayer);
                            MediaItem mediaItem = MediaItem.fromUri(reeluri);
                            simpleExoPlayer.addMediaItems(Collections.singletonList(mediaItem));
                            simpleExoPlayer.setRepeatMode(simpleExoPlayer.REPEAT_MODE_ONE);
                            simpleExoPlayer.prepare();
                            simpleExoPlayer.setPlayWhenReady(true);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }

                    break;
            }}else {
        }
    }

    public void changepostitem(View view)
    {
        if(!alertDialog.isShowing()) {
            movecursor();
            c1.setCardBackgroundColor(Color.parseColor("#FF000000"));
            t1.setTextColor(Color.parseColor("#FFFFFF"));
            switch (view.getId()) {
                case R.id.post: {
                    choose = 1;
                    TextView text = (TextView) findViewById(R.id.posttext);
                    text.setTextColor(Color.parseColor("#FF000000"));
                    titlepost.setText("Add To Post");
                    c1.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                }
                break;
                case R.id.story: {
                    titlepost.setText("Add To Story");
                    choose = 2;
                    TextView text = (TextView) findViewById(R.id.storttext);
                    text.setTextColor(Color.parseColor("#FF000000"));
                    c2.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                }
                break;
                case R.id.reel: {
                    choose = 3;
                    titlepost.setText("Make Reels");
                    TextView text = (TextView) findViewById(R.id.reeltext);
                    text.setTextColor(Color.parseColor("#FF000000"));
                    c3.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                }
                break;
            }
        }
    }

    private void reset()
    {
        videoView.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);
        constraintLayout.setVisibility(View.VISIBLE);
        choose=0;
        alertDialog.show();
        c1.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        c2.setCardBackgroundColor(Color.parseColor("#FF000000"));
        c3.setCardBackgroundColor(Color.parseColor("#FF000000"));
        t1.setTextColor(Color.parseColor("#FF000000"));
        t2.setTextColor(Color.parseColor("#FFFFFF"));
        t3.setTextColor(Color.parseColor("#FFFFFF"));
    }
    private void movecursor()
    {
        c1.setCardBackgroundColor(Color.parseColor("#FF000000"));
        c2.setCardBackgroundColor(Color.parseColor("#FF000000"));
        c3.setCardBackgroundColor(Color.parseColor("#FF000000"));
        t1.setTextColor(Color.parseColor("#FFFFFF"));
        t2.setTextColor(Color.parseColor("#FFFFFF"));
        t3.setTextColor(Color.parseColor("#FFFFFF"));
    }

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
                startActivityForResult(takePictureIntent, 1);
            }
        }
    }

    private void trim(String uri)
    {
        if(speedduration==null)
        {
        if(simpleExoPlayer.getDuration()>duration*1000) {
            TrimVideo.activity(uri)
                    .setTrimType(TrimType.FIXED_DURATION)
                    .setFixedDuration(duration)
                    .setTitle("Edit your Reels")
                    .setAccurateCut(true)
                    .start(this, startForResult);
        }
        else {
            Toast.makeText(this,"length should be more than "+duration+" seconds to continue",Toast.LENGTH_SHORT).show();
        }
        }
        else {
            if(simpleExoPlayer.getDuration()/inispeed>duration*1000) {
                TrimVideo.activity(uri)
                        .setTrimType(TrimType.FIXED_DURATION)
                        .setFixedDuration((long) (duration*inispeed))
                        .setTitle("Edit your Reels")
                        .setAccurateCut(true)
                        .start(this, startForResult);
            }
            else {
                Toast.makeText(this,"length should be more than "+duration+" seconds to continue",Toast.LENGTH_SHORT).show();
            }
        }
    }
    ActivityResultLauncher<Intent> startForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK &&
                        result.getData() != null) {
                    Uri uri = Uri.parse(TrimVideo.getTrimmedVideoPath(result.getData()));
                    Toast.makeText(this,reeluri+"\n\n\n"+uri,Toast.LENGTH_LONG).show();

                    if(choose==3) {
                       // Toast.makeText(this,inispeed+"",Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(socialmediapage.this, editreels.class);
                        reeluri=uri;
                        startActivity(i);
                    }
                }
            });

    @Override
    public void onPause() {
        super.onPause();
        if(videoView!=null) {
            if(videoView.getPlayer()!=null) {
                timebar.setVisibility(View.VISIBLE);
                videoView.getPlayer().pause();
                if(alertDialog!=null)
                    alertDialog.dismiss();
                if(alertDialog2!=null) {
                    speedshow.setText(inispeed+"x");
                    alertDialog2.cancel();
                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(videoView!=null) {
            if(videoView.getPlayer()!=null) {
                timebar.setVisibility(View.VISIBLE);
                videoView.getPlayer().pause();
                if(alertDialog!=null)
                    alertDialog.dismiss();
                if(alertDialog2!=null) {
                    alertDialog2.cancel();
                    speedshow.setText(inispeed+"x");
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(videoView.getVisibility()== View.GONE && imageView.getVisibility()==View.GONE)
        {
            super.onBackPressed();
        }
       else {
           videoView.setVisibility(View.GONE);
           if(videoView.getPlayer()!=null)
           {
               videoView.getPlayer().stop();
               videoView.getPlayer().release();
               timebar.setVisibility(View.GONE);
               if(alertDialog2!=null)
               {
                   alertDialog2.cancel();
                   inispeed=1f;
                   speedshow.setText(inispeed+"x");
               }
               if(alertDialog!=null)
               {
                   alertDialog.show();
               }
           }
           imageView.setVisibility(View.GONE);
        }
    }
}