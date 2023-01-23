package com.harry9425.yaari;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class editpostimage extends AppCompatActivity {

    Uri image;
    String caption;
    ImageView main,mini;
    CircleImageView dp;
    ImageButton crop,bk;
    Button filter,editor;
    String type;
    EditText messsage;
    StorageReference mref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editpostimage);
        main=(ImageView) findViewById(R.id.editpostimage);
        mini=(ImageView) findViewById(R.id.editpostminiimage);
        dp=(CircleImageView) findViewById(R.id.editpostdp);
        editor=(Button) findViewById(R.id.openeditor);
        mref = FirebaseStorage.getInstance().getReference();;
        messsage=(EditText) findViewById(R.id.captionwrite);
        type=getIntent().getStringExtra("type");
        bk=(ImageButton) findViewById(R.id.backtosocialpage);
        bk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        crop=(ImageButton) findViewById(R.id.editpostcrop);
        image=socialmediapage.posturi;
        DatabaseReference mDatabase;
        mDatabase= FirebaseDatabase.getInstance().getReference();
        mDatabase.child("AAA usernames").child(mainpage.curuser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String dpval=snapshot.child("userdp").getValue().toString();
                Picasso.get().load(dpval).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.userdefaultdp)
                        .into(dp, new Callback() {
                            @Override
                            public void onSuccess() {
                            }
                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(dpval).placeholder(R.drawable.userdefaultdp).into(dp);
                            }
                        });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }});
        Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.userdefaultdp)
                .into(main, new Callback() {
                    @Override
                    public void onSuccess() {}
                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(image).placeholder(R.drawable.userdefaultdp).into(main);
                    }
                });
        Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.userdefaultdp)
                .into(mini, new Callback() {
                    @Override
                    public void onSuccess() {}
                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(image).placeholder(R.drawable.userdefaultdp).into(mini);
                    }
                });

    }

    public void sendpost(View view)
    {
      caption=messsage.getText().toString().trim();
      if(caption.isEmpty())
      {
        messsage.setError("Empty");
        messsage.setFocusable(true);
      }
      else {
          Toast.makeText(editpostimage.this, "Posting", Toast.LENGTH_SHORT).show();
          DatabaseReference mDatabase;
          mDatabase= FirebaseDatabase.getInstance().getReference();
          String randomkey = mDatabase.push().getKey();
          String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
          StorageReference filepath = mref.child("Posts").child(uid).child("Imagepost").child(randomkey);
              filepath.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                  @Override
                  public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                      taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(
                              new OnCompleteListener<Uri>() {
                                  @Override
                                  public void onComplete(@NonNull Task<Uri> task) {
                                      String fileLink = task.getResult().toString();
                                      postmodel postmodel =new postmodel();
                                      postmodel.setUrl(fileLink);
                                      postmodel.setPostuid(randomkey);
                                      postmodel.setTime(System.currentTimeMillis());
                                      postmodel.setCaption(caption);
                                      postmodel.setPostby(uid);
                                      mDatabase.child("AAA usernames").child(uid).child("Posts").child(randomkey).setValue(postmodel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                          @Override
                                          public void onComplete(@NonNull Task<Void> task) {
                                              if (task.isSuccessful()) {
                                                  Intent intent = new Intent(getApplicationContext(), accountsettings.class);
                                                  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                  startActivity(intent);
                                                  Toast.makeText(editpostimage.this, "Posted SUCCESSFULLY", Toast.LENGTH_SHORT).show();

                                              } else {
                                                  Toast.makeText(editpostimage.this,"Something went wrong", Toast.LENGTH_SHORT).show();
                                              }
                                          }
                                      });
                                  }
                              }).addOnFailureListener(new OnFailureListener() {
                          @Override
                          public void onFailure(@NonNull Exception e) {
                              Toast.makeText(editpostimage.this, "ERROR OCCURRED", Toast.LENGTH_SHORT).show();
                          }
                      });
                  }
              });
      }
    }

    public void editor(View view) {
        Intent dsPhotoEditorIntent = new Intent(this, DsPhotoEditorActivity.class);
        dsPhotoEditorIntent.setData(image);
        dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY, "YAARI");
        dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_TOOL_BAR_BACKGROUND_COLOR, Color.parseColor("#000000"));
        dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_MAIN_BACKGROUND_COLOR, Color.parseColor("#000000"));
        startActivityForResult(dsPhotoEditorIntent, 200);
    }
    public void croponly(View view)
    {
        Intent dsPhotoEditorIntent = new Intent(this, DsPhotoEditorActivity.class);
        dsPhotoEditorIntent.setData(image);
        dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY, "YAARI");
        int[] toolsToHide = {DsPhotoEditorActivity.TOOL_FRAME,DsPhotoEditorActivity.TOOL_CONTRAST,DsPhotoEditorActivity.TOOL_PIXELATE,DsPhotoEditorActivity.TOOL_DRAW,
                DsPhotoEditorActivity.TOOL_EXPOSURE,DsPhotoEditorActivity.TOOL_FILTER,DsPhotoEditorActivity.TOOL_ORIENTATION,DsPhotoEditorActivity.TOOL_ROUND,
                DsPhotoEditorActivity.TOOL_SATURATION,DsPhotoEditorActivity.TOOL_SHARPNESS,DsPhotoEditorActivity.TOOL_STICKER,DsPhotoEditorActivity.TOOL_TEXT,
                DsPhotoEditorActivity.TOOL_VIGNETTE,DsPhotoEditorActivity.TOOL_WARMTH};
        dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_TOOL_BAR_BACKGROUND_COLOR, Color.parseColor("#000000"));
        dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_MAIN_BACKGROUND_COLOR, Color.parseColor("#000000"));
        dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_TOOLS_TO_HIDE, toolsToHide);
        startActivityForResult(dsPhotoEditorIntent, 200);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 200:
                    Uri resultUri = data.getData();
                    image=resultUri;
                    Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.wallpaperbg)
                            .into(main, new Callback() {
                                @Override
                                public void onSuccess() {
                                    // sendmess();
                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(image).placeholder(R.drawable.wallpaperbg).into(main);
                                }
                            });
                    Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.wallpaperbg)
                            .into(mini, new Callback() {
                                @Override
                                public void onSuccess() {
                                    // sendmess();
                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(image).placeholder(R.drawable.wallpaperbg).into(mini);
                                }
                            });
                    break;

            }
        }
    }
}