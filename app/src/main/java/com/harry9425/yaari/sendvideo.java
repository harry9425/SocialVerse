package com.harry9425.yaari;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Range;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gowtham.library.utils.CompressOption;
import com.gowtham.library.utils.TrimType;
import com.gowtham.library.utils.TrimVideo;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class sendvideo extends AppCompatActivity {

    VideoView videoView;
    MediaController mediaController;
    Uri videouri;
    TextView name,indi;
    String chattext, namett, uid, dp;
    CircleImageView circleImageView;
    ProgressDialog progressDialog;
    EditText message;
    Button once,replay,keep;
    String typeofvideo="keep";
    int duration;
    DatabaseReference gettypo;
    ImageButton imageButton;
    StorageReference mref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendvideo);
        mediaController = new MediaController(this);
        videoView = (VideoView) findViewById(R.id.videoView);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videouri = chatactivity.videouri;
        videoView.setVideoURI(videouri);
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Uploading video :");
        circleImageView = (CircleImageView) findViewById(R.id.sendvideodp);
        name = (TextView) findViewById(R.id.sendvideoname);
        indi=(TextView) findViewById(R.id.onoffindivideo);
        message=(EditText) findViewById(R.id.sendvideonmess);
        gettypo=FirebaseDatabase.getInstance().getReference();
        gettypo = FirebaseDatabase.getInstance().getReference().child("AAA usernames").child(chatactivity.uid);
        gettypo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String val = snapshot.child("presence").getValue().toString();
                if(val.equals("online"))
                    indi.setText("Active");
                else
                {
                    indi.setText("InActive");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        message = (EditText) findViewById(R.id.sendvideonmess);
        name.setText(chatactivity.name);
        namett = chatactivity.name;
        dp = chatactivity.dp;
        uid = chatactivity.uid;
        once=(Button) findViewById(R.id.oncevideo);
        keep=(Button) findViewById(R.id.keepvideo);
        replay=(Button) findViewById(R.id.replayvideo);
        keep.setTextColor(Color.RED);
        imageButton=(ImageButton) findViewById(R.id.videosendbtbn);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadvideo();
            }
        });
        once.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeofvideo="once";
                once.setTextColor(Color.RED);
                keep.setTextColor(Color.GRAY);
                replay.setTextColor(Color.GRAY);
            }
        });
        keep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeofvideo="keep";
                once.setTextColor(Color.GRAY);
                replay.setTextColor(Color.GRAY);
                keep.setTextColor(Color.RED);
            }
        });
        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeofvideo="replay";
                keep.setTextColor(Color.GRAY);
                once.setTextColor(Color.GRAY);
                replay.setTextColor(Color.RED);
            }
        });
        mref=FirebaseStorage.getInstance().getReference();
        duration=videoView.getDuration();
        videoView.start();
        float len=videoView.getDuration();
        len+=len*0.2;
        videoView.seekTo((int) len);
        videoView.pause();
        Picasso.get().load(chatactivity.dp).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.userdefaultdp)
                .into(circleImageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(chatactivity.dp).placeholder(R.drawable.userdefaultdp).into(circleImageView);
                    }
                });
    }
    public void trim(View view)
    {
        /*TrimVideo.activity(String.valueOf(videouri))
                .setTrimType(TrimType.MIN_MAX_DURATION)
                .setMinToMax(2, 600)  //seconds
                .setTitle(System.currentTimeMillis()+"")
                .setAccurateCut(true)
                .start(this,startForResult);
         */
        Intent i =new Intent(this,ffmpegtut.class);
        i.putExtra("mpeguri",videouri.toString());
        startActivity(i);
    }


    private void uploadvideo() {
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        chattext = message.getText().toString().trim();
        progressDialog.setMessage("uploading video to server...");
        Calendar calendar = Calendar.getInstance();
        StorageReference filepath = mref.child("chats").child(mainpage.curuser).child(mainpage.curuser+chatactivity.uid).child("Videos").child(calendar.getTimeInMillis() + "");
        filepath.putFile(videouri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(
                        new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                String fileLink = task.getResult().toString();
                                if (chattext.isEmpty()) {
                                    final DatabaseReference mDatabase;
                                    final messagemodel model = new messagemodel(mainpage.curuser);
                                    model.setImageurl(fileLink);
                                    model.setFeelings(15);
                                    model.setChecker("video w/o text");
                                    model.setImagetype(typeofvideo);
                                    if(!typeofvideo.equals("keep"))
                                    {
                                        model.setMessagekey("none");
                                    }
                                    model.setTimestamp(System.currentTimeMillis());
                                    message.setText("");
                                    mDatabase = FirebaseDatabase.getInstance().getReference();
                                    String randomkey = mDatabase.push().getKey();

                                    mDatabase.child("Chats").child(mainpage.curuser).child(mainpage.curuser + chatactivity.uid).child(randomkey).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mDatabase.child("Chats").child(chatactivity.uid).child(chatactivity.uid + mainpage.curuser).child(randomkey).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    progressDialog.dismiss();
                                                    HashMap<String, Object> hashMap = new HashMap<>();
                                                    hashMap.put("lastmess", "You recieved a video");
                                                    hashMap.put("lastmess time", model.getTimestamp());
                                                    mDatabase.child("Chats").child(chatactivity.uid).child(chatactivity.uid + mainpage.curuser).child("AA details").setValue(hashMap);
                                                    hashMap.put("lastmess", "You sended a video");
                                                    mDatabase.child("Chats").child(mainpage.curuser).child(mainpage.curuser + chatactivity.uid).child("AA details").setValue(hashMap);
                                                    Intent i = new Intent(sendvideo.this, chatactivity.class);
                                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    i.putExtra("name", namett);
                                                    i.putExtra("dp", dp);
                                                    i.putExtra("uidm", uid);
                                                    startActivity(i);
                                                }
                                            });
                                        }
                                    });
                                } else {
                                    final DatabaseReference mDatabase;
                                    final messagemodel model = new messagemodel(mainpage.curuser, chattext);
                                    model.setImageurl(fileLink);
                                    model.setFeelings(15);
                                    model.setChecker("video with text");
                                    model.setImagetype(typeofvideo);
                                    if(!typeofvideo.equals("keep"))
                                    {
                                        model.setMessagekey("none");
                                    }
                                    model.setTimestamp(System.currentTimeMillis());
                                    message.setText("");
                                    mDatabase = FirebaseDatabase.getInstance().getReference();
                                    String randomkey = mDatabase.push().getKey();
                                    mDatabase.child("Chats").child(mainpage.curuser).child(mainpage.curuser + chatactivity.uid).child(randomkey).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mDatabase.child("Chats").child(chatactivity.uid).child(chatactivity.uid + mainpage.curuser).child(randomkey).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    progressDialog.dismiss();
                                                    HashMap<String, Object> hashMap = new HashMap<>();
                                                    hashMap.put("lastmess", "You recieved a video");
                                                    hashMap.put("lastmess time", model.getTimestamp());
                                                    mDatabase.child("Chats").child(chatactivity.uid).child(chatactivity.uid + mainpage.curuser).child("AA details").setValue(hashMap);
                                                    hashMap.put("lastmess", "You sended a video");
                                                    mDatabase.child("Chats").child(mainpage.curuser).child(mainpage.curuser + chatactivity.uid).child("AA details").setValue(hashMap);
                                                    Intent i = new Intent(sendvideo.this, chatactivity.class);
                                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    i.putExtra("name", namett);
                                                    i.putExtra("dp", dp);
                                                    i.putExtra("uidm", uid);
                                                    startActivity(i);
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(sendvideo.this, "ERROR OCCURRED", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            // Progress Listener for loading
            // percentage on the dialog box
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                // show the progress bar
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressDialog.setMessage("Uploaded " + (int) progress + "%");
                if(progress==100)
                {
                    progressDialog.dismiss();
                }
            }
        });;
    }


    ActivityResultLauncher<Intent> startForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK &&
                        result.getData() != null) {
                    Uri uri = Uri.parse(TrimVideo.getTrimmedVideoPath(result.getData()));
                    videoView.setVideoURI(uri);
                    videoView.start();
                    float len=videoView.getDuration();
                    len+=len*0.2;
                    videoView.seekTo((int) len);
                    videoView.pause();
                }
            });
}