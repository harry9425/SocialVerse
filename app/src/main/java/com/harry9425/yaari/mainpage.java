package com.harry9425.yaari;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.MutableLiveData;
import androidx.viewpager.widget.ViewPager;

import com.eftimoff.viewpagertransformers.AccordionTransformer;
import com.eftimoff.viewpagertransformers.CubeInTransformer;
import com.eftimoff.viewpagertransformers.CubeOutTransformer;
import com.eftimoff.viewpagertransformers.DepthPageTransformer;
import com.eftimoff.viewpagertransformers.DrawFromBackTransformer;
import com.eftimoff.viewpagertransformers.FlipHorizontalTransformer;
import com.eftimoff.viewpagertransformers.ParallaxPageTransformer;
import com.eftimoff.viewpagertransformers.StackTransformer;
import com.eftimoff.viewpagertransformers.TabletTransformer;
import com.google.android.exoplayer2.C;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.onesignal.OSSubscriptionObserver;
import com.onesignal.OSSubscriptionStateChanges;
import com.onesignal.OneSignal;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class mainpage extends AppCompatActivity{

    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    private ViewPager mpager;
    private SectionsPageAdapter msectionspageadapter;
    private TabLayout mtablayout;
    public static String curuser,dp=null,curname=null,about=null;
    public static Uri resultUri;
    CircleImageView maindp;
    Button make=null;
    public static String onesignalid="NA";
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);
        mAuth = FirebaseAuth.getInstance();
        toolbar=(Toolbar) findViewById(R.id.appbar);
        toolbar.setTitle("");
        maindp=(CircleImageView) findViewById(R.id.mainpagedp);
        setSupportActionBar(toolbar);
        mpager=(ViewPager) findViewById(R.id.maintabpager);
        mpager.setOffscreenPageLimit(3);
        AnimationDrawable animationDrawable=(AnimationDrawable) mpager.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
        msectionspageadapter=new SectionsPageAdapter(getSupportFragmentManager());
        mpager.setAdapter(msectionspageadapter);
        mpager.setPageTransformer(true, new DepthPageTransformer());
     //   Intent fcmintent= new Intent(this,pushnotificationservice.class);
       // startService(fcmintent);
        //Toast.makeText(mainpage.this,msectionspageadapter.getPageTitle(mpager.getCurrentItem()),Toast.LENGTH_LONG).show();
        mtablayout=findViewById(R.id.maintabs);
        mtablayout.setupWithViewPager(mpager);
        onesignalid=OneSignal.getDeviceState().getUserId();
        Toast.makeText(this,onesignalid,Toast.LENGTH_LONG).show();
        String val=getIntent().getStringExtra("fromurl");
        if(!val.equals("none")) {
            showaddalert(val);
        }
        ImageButton scanner=(ImageButton) findViewById(R.id.qrcodescanner);
        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator scanIntegrator = new IntentIntegrator(mainpage.this);
                scanIntegrator.setPrompt("Scan Latte code");
                scanIntegrator.setBeepEnabled(true);
                scanIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                scanIntegrator.setOrientationLocked(false);
                scanIntegrator.setBarcodeImageEnabled(true);
                scanIntegrator.initiateScan();
            }
        });

        maindp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(mainpage.this,accountsettings.class);
                startActivity(i);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanningResult != null) {
            if (scanningResult.getContents() != null) {
                String scanContent = scanningResult.getContents().toString();
                if(!scanContent.equals(mainpage.curuser)) {
                    DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("AAA usernames");
                    databaseReference.keepSynced(true);
                    databaseReference.child(scanContent).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String v=(snapshot.child("profile").getValue().toString());
                            if (v.equals("lock")) {
                                Intent i = new Intent(mainpage.this, profileactivity.class);
                                i.putExtra("uidm", scanContent);
                                startActivity(i);
                            }
                            else{
                                Intent i = new Intent(mainpage.this, openprofileactivity.class);
                                i.putExtra("uid", scanContent);
                                startActivity(i);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
                else {
                    Intent i2 = new Intent(mainpage.this, accountsettings.class);
                    startActivity(i2);
                }
            }
        }
    }

    private void showaddalert(String value)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(mainpage.this);
        View view=getLayoutInflater().inflate(R.layout.customaddfromqrcode,null);
        builder.setView(view);
        AlertDialog alertDialog =builder.create();
        ImageView circleImageView=view.findViewById(R.id.alertadduserdp);
        TextView textView=view.findViewById(R.id.alertaddusername);
        ImageButton open=view.findViewById(R.id.addalertopenprofile);
        make=view.findViewById(R.id.alertaddusermakefriend);
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                make=null;
            }
        });
        String[] splited=value.split("shareid=");
        if(splited[1].equals(mainpage.curuser))
        {
            make.setEnabled(false);
            make.setAlpha(0.5f);
            make.setText("U Naughty");
        }
        else {
           checkiffriend(splited[1]);
        }
        make.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase=FirebaseDatabase.getInstance().getReference();
                mDatabase.child("Friends Request data").child(mainpage.curuser).child(splited[1]).child("request type").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Friends Request data");
                            mDatabase.child(splited[1]).child(mainpage.curuser).child("request type").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                }
                            });
                        } else {
                            Toast.makeText(mainpage.this, "YOUR INTERNET IS LOW", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {

                    }
                });;
                setallfrlist(splited[1]);
            }
        });
        mDatabase = FirebaseDatabase.getInstance().getReference().child("AAA usernames").child(splited[1]);
        mDatabase.keepSynced(true);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uname=snapshot.child("name").getValue().toString();
                String status=snapshot.child("status").getValue().toString();
                String image=snapshot.child("userdp").getValue().toString();
                String profile=snapshot.child("profile").getValue().toString();
                Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.userdefaultdp)
                        .into(circleImageView, new Callback() {
                            @Override
                            public void onSuccess() {
                            }
                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(image).placeholder(R.drawable.userdefaultdp).into(circleImageView);
                            }
                        });
                textView.setText(uname);
                open.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!make.getText().toString().contains("Naughty")) {
                            if(profile.equals("lock"))
                            {
                                Intent i = new Intent(mainpage.this, profileactivity.class);
                                i.putExtra("uidm", splited[1]);
                                startActivity(i);
                            }
                            else{
                                Intent i = new Intent(mainpage.this, openprofileactivity.class);
                                i.putExtra("uid", splited[1]);
                                startActivity(i);
                            }
                        }
                        else {
                            Intent i2 = new Intent(mainpage.this, accountsettings.class);
                            startActivity(i2);
                        }
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mainpage.this, "CAN'T CONNECT TO SERVER", Toast.LENGTH_LONG).show();
            }
        });
        alertDialog.show();
    }


    private void getcuruserdata(String uid)
    {
        HashMap<String, Object> hashMap2 = new HashMap<>();
        hashMap2.put("status", DateFormat.getDateInstance().format(new Date()));
        hashMap2.put("videocallid",mainpage.curuser+uid);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Friends list data");
        mDatabase.child(uid).child(mainpage.curuser).setValue(hashMap2).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(make!=null) {
                    make.setText("Added");
                    make.setEnabled(false);
                    make.setAlpha(0.5f);
                }
            }
        });
    }
    private void checkiffriend(String uid)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Friends list data");
        mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(uid))
                {
                    if(make!=null){
                        make.setEnabled(false);
                        make.setAlpha(0.5f);
                        make.setText("ALready added");
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setallfrlist(String uid) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("videocallid",mainpage.curuser+uid);
        hashMap.put("status",DateFormat.getDateInstance().format(new Date()));
        mDatabase=FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Friends list data").child(mainpage.curuser).child(uid).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    getcuruserdata(uid);
                } else {
                    Toast.makeText(mainpage.this, "YOUR INTERNET IS LOW", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {

            }
        });;
    }



    public  void opensocialpage(View view)
    {
        Intent i =new Intent(this,socialmediapage.class);
        startActivity(i);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser== null)
        {
            Intent i=new Intent(this,signingactivity.class);
            startActivity(i);
            finish();
        }
        else {
            curuser = currentUser.getUid().trim();
            mDatabase = FirebaseDatabase.getInstance().getReference().child("AAA usernames").child(mainpage.curuser);
            mDatabase.child("ONEID").setValue(mainpage.onesignalid).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
            mDatabase.child("presence").setValue("online").addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
            mDatabase=FirebaseDatabase.getInstance().getReference();
            mDatabase.child("AAA usernames").child(curuser).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    dp=snapshot.child("userdp").getValue().toString();
                    Picasso.get().load(dp).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.userdefaultdp)
                            .into(maindp, new Callback() {
                                @Override
                                public void onSuccess() {
                                }
                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(dp).placeholder(R.drawable.userdefaultdp).into(maindp);
                                }
                            });
                    curname=snapshot.child("name").getValue().toString();
                    about=snapshot.child("status").getValue().toString();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) { }});
        }
    }


    public void addpoststory(View view)
    {
        Intent i2 = new Intent(mainpage.this,socialmediapage.class);
        startActivity(i2);
    }

  public  void optionmenu(View view)
  {
      mDatabase=FirebaseDatabase.getInstance().getReference();
      mDatabase.child("AAA usernames").child(curuser).addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot snapshot) {
              dp=snapshot.child("userdp").getValue().toString();
              curname=snapshot.child("name").getValue().toString();
              about=snapshot.child("status").getValue().toString();
          }
          @Override
          public void onCancelled(@NonNull DatabaseError error) { }});
      android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(this);
      final View view2=getLayoutInflater().inflate(R.layout.mainpagealertoption,null);
      builder.setView(view2);
      AlertDialog alertDialog =builder.create();
      LinearLayout logout=view2.findViewById(R.id.logoutmenu);
      logout.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              mAuth.signOut();
              OneSignal.disablePush(true);
              Intent i1 = new Intent(mainpage.this, signingactivity.class);
              startActivity(i1);
              finish();
          }
      });
      LinearLayout profile=view2.findViewById(R.id.manageprofilemenu);
      LinearLayout roomchat=view2.findViewById(R.id.roomchatid);
      roomchat.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
             alertDialog.dismiss();
              Intent i2 = new Intent(mainpage.this, createroom_activity.class);
              startActivity(i2);
          }
      });
      profile.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent i2 = new Intent(mainpage.this, accountsettings.class);
              startActivity(i2);
          }
      });
      ImageButton cancel=view2.findViewById(R.id.cancelmenubtn);
      CircleImageView circleImageView=view2.findViewById(R.id.menuimage);
      TextView name=view2.findViewById(R.id.menuname);
      TextView abtuser=view2.findViewById(R.id.menustatus);
      abtuser.setSelected(true);
      name.setSelected(true);
      cancel.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              alertDialog.dismiss();
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
              Intent i = new Intent(mainpage.this,imagezoom.class);
              i.putExtra(("zoomuri"),dp);
              startActivity(i);
          }
      });
      name.setText(curname);
      abtuser.setText(about);
      alertDialog.show();
  }

  public void alluser(View view)
  {
      Intent i =new Intent (this,allusersactivity.class);
      startActivity(i);
  }

  public void opensocialworld(View view){
     Intent i=new Intent(this,socialworldpage.class);
     startActivity(i);
  }

}
