package com.harry9425.yaari;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants;
import com.eftimoff.viewpagertransformers.DepthPageTransformer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.WriterException;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import de.hdodenhof.circleimageview.CircleImageView;

    public class accountsettings extends AppCompatActivity {

        private DatabaseReference mDatabase;
        public static String uname,status,phoneno,image;
        TextView username,aboutsec,postcount,friendcount,userid;
        int a=0;
        String uid;
        Toolbar toolbar;
        Uri resultUri;
        static final int REQUEST_IMAGE_CAPTURE = 1;
        String currentPhotoPath;
        ProgressDialog progressDialog;
        RoundedImageView roundedImageView;
        private StorageReference mref;
        CardView qrcard;
        Bitmap qrbitmap=null;
        ImageView qrcodedisplay;
        ImageButton qrcodebtn,sharecode,closecode,addpoststorybtn,lock;
        private TabLayout mtablayout;
        String curuser;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_accountsettings);
            curuser=FirebaseAuth.getInstance().getCurrentUser().getUid();
            lock=(ImageButton) findViewById(R.id.accountsetting_profilelock);
            userid=(TextView) findViewById(R.id.userid_accounpage);
            ViewPager mpager=(ViewPager) findViewById(R.id.accounttabpager);
            mpager.setOffscreenPageLimit(3);
            lock.setImageResource(R.drawable.ic_baseline_lock_open_24);
            lock.setAlpha(1f);
            postfragment.curuser=curuser;
            accountspageadapter sectionspageadapter=new accountspageadapter(getSupportFragmentManager());
            mpager.setAdapter(sectionspageadapter);
            mpager.setPageTransformer(true, new DepthPageTransformer());
            mtablayout=findViewById(R.id.maintabsaccountpage);
            mtablayout.setupWithViewPager(mpager);
            username=(TextView) findViewById(R.id.usernametodisplay);
            aboutsec=(TextView) findViewById(R.id.aboutuser);
            roundedImageView=(RoundedImageView) findViewById(R.id.accountsdp);
            postcount=(TextView) findViewById(R.id.accountpagepostcount);
            friendcount=(TextView) findViewById(R.id.accountpagefriendcount);
            addpoststorybtn=(ImageButton) findViewById(R.id.accountaddpostbtn);
            addpoststorybtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i =new Intent(accountsettings.this,socialmediapage.class);
                    startActivity(i);
                }
            });
            qrcodebtn=(ImageButton) findViewById(R.id.qrcodebtn);
            qrcard=(CardView) findViewById(R.id.showqrcode);
            qrcard.setVisibility(View.GONE);
            qrcodedisplay=(ImageView) findViewById(R.id.qrcodedisplay);
            sharecode=(ImageButton) findViewById(R.id.sharemyqrcode);
            closecode=(ImageButton) findViewById(R.id.closeqrsection);
            closecode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    qrcard.setVisibility(View.GONE);
                }
            });
            qrcodebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    qrcard.setVisibility(View.VISIBLE);
                    QRGEncoder qrgEncoder = new QRGEncoder(curuser, null, QRGContents.Type.TEXT, 540);
                    try {
                        qrbitmap = qrgEncoder.encodeAsBitmap();
                        qrcodedisplay.setImageBitmap(qrbitmap);
                    } catch (WriterException e) {
                        Log.e("Tag", e.toString());
                    }
                }
            });

            sharecode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(qrbitmap!=null) {
                        Uri imageuri = getImageUri(qrbitmap);
                        Toast.makeText(accountsettings.this,imageuri+"",Toast.LENGTH_SHORT).show();
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_TEXT, "Add me on Caffeine!\nUsername: "+mainpage.curname+"\nhttps://www.caffeinechatapp.com/??"+mainpage.curname.replaceAll(" ","_")+"?shareid="+curuser);
                        shareIntent.putExtra(Intent.EXTRA_STREAM,imageuri );
                        shareIntent.setType("image/jpeg");
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(Intent.createChooser(shareIntent, "send"));
                    }
                }
            });
            displayval1();
        }
        private Uri getImageUri(Bitmap image) {
            File imagesFolder = new File(getCacheDir(), "images");
            Uri uri = null;
            try {
                imagesFolder.mkdirs();
                File file = new File(imagesFolder, "my Latte code.png");

                FileOutputStream stream = new FileOutputStream(file);
                image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                stream.flush();
                stream.close();
                uri = FileProvider.getUriForFile(this, "com.harry9425.android.fileprovider", file);

            } catch (IOException e) {
            }
            return uri;
        }
        @Override
        protected void onStart() {
            super.onStart();
            DatabaseReference databaseReference;
            databaseReference=FirebaseDatabase.getInstance().getReference().child("AAA usernames").child(curuser);
            databaseReference.child("presence").setValue("online").addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
        @Override
        protected void onStop() {
            super.onStop();
            DatabaseReference databaseReference;
            databaseReference=FirebaseDatabase.getInstance().getReference().child("AAA usernames").child(curuser);
            databaseReference.child("presence").setValue("online").addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }

        public void bk(View view)
        {
            onBackPressed();
        }

        public void editvalues(View view)
        {
            Intent i =new Intent(this,accounteditsettings.class);
            startActivity(i);
        }

        private void displayval1() {
           uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            mDatabase = FirebaseDatabase.getInstance().getReference().child("AAA usernames").child(uid);
            mDatabase.keepSynced(true);
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    uname=snapshot.child("name").getValue().toString();
                    status=snapshot.child("status").getValue().toString();
                    phoneno=snapshot.child("phoneno").getValue().toString();
                    image=snapshot.child("userdp").getValue().toString();
                    if(snapshot.hasChild("username"))
                        userid.setText(snapshot.child("username").getValue().toString());
                    long posts=snapshot.child("Posts").getChildrenCount();

                    if(snapshot.hasChild("profile"))
                    {
                        if(snapshot.child("profile").getValue().toString().equals("lock")){
                            lock.setImageResource(R.drawable.ic_baseline_lock_24);
                            lock.setAlpha(0.98f);
                        }
                        else if(snapshot.child("profile").getValue().toString().equals("open"))
                        {
                            lock.setImageResource(R.drawable.ic_baseline_lock_open_24);
                            lock.setAlpha(1f);
                        }
                    }
                    Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.userdefaultdp)
                            .into(roundedImageView, new Callback() {
                                @Override
                                public void onSuccess() {}
                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(image).placeholder(R.drawable.userdefaultdp).into(roundedImageView);
                                }
                            });
                    postcount.setText(posts+"");
                    username.setText(uname);
                    aboutsec.setText(status);
                    mDatabase=FirebaseDatabase.getInstance().getReference().child("Friends list data").child(uid);
                    mDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                        friendcount.setText(snapshot.getChildrenCount()+"");
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(accountsettings.this, "CAN'T CONNECT TO SERVER", Toast.LENGTH_LONG).show();
                }
            });
        }

        public void lockprofile(View view){
            String val;
            if(lock.getAlpha()==1f) {
                val="lock";
            }
            else {
                val="open";
            }
            mDatabase = FirebaseDatabase.getInstance().getReference().child("AAA usernames").child(uid);
            mDatabase.keepSynced(true);
            mDatabase.child("profile").setValue(val).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if(val.equals("lock"))
                        Toast.makeText(accountsettings.this, "Locking profile", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(accountsettings.this, "Removing Profile Lock", Toast.LENGTH_LONG).show();
                }
            });
        }

    }


