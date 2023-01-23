package com.harry9425.yaari;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.loader.content.CursorLoader;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.exoplayer2.C;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static com.harry9425.yaari.chatactivity.fileuri;

public class sendimage extends AppCompatActivity {

    PhotoView imageView;
    TextView name;
    CircleImageView circleImageView;
    EditText message;
    Bitmap decoded;
    String typeofimage="keep";
    int compressrate=100;
    String chattext, namett, uid, dp;
    ImageButton imageButton;
    ProgressDialog progressDialog;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    StorageReference mref;
    public int changeway=0,trytocompress=0;
    Button once,replay,keep;
    byte[] imageInByte;
    public static String filepath;
    DatabaseReference gettypo;
    File imgFile;
    String typepath;
    File path=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/YAARI");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("PLEASE WAIT");
        progressDialog.setMessage("UPLOADING MESSAGE....");
        progressDialog.setCanceledOnTouchOutside(false);
        setContentView(R.layout.activity_sendimage);
        imageButton=(ImageButton) findViewById(R.id.imagesendbtbn);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendmess();
            }
        });
        typepath=getIntent().getStringExtra("path");
        circleImageView = (CircleImageView) findViewById(R.id.sendimagedp);
        imageView = (PhotoView) findViewById(R.id.imagezoomview);
        imageView.setMinimumScale(1);
        imageView.setMaximumScale(8);
        imageView.setHapticFeedbackEnabled(true);
        name = (TextView) findViewById(R.id.sendimagename);
        mref = FirebaseStorage.getInstance().getReference();
        message = (EditText) findViewById(R.id.sendimagenmess);
        name.setText(chatactivity.name);
        namett = chatactivity.name;
        dp = chatactivity.dp;
        uid = chatactivity.uid;
        once=(Button) findViewById(R.id.once);
        keep=(Button) findViewById(R.id.keep);
        replay=(Button) findViewById(R.id.replay);
        keep.setTextColor(Color.RED);
        filepath=path.getAbsolutePath();
        TextView indi=(TextView) findViewById(R.id.onoffindi);
        gettypo=FirebaseDatabase.getInstance().getReference();
        gettypo = FirebaseDatabase.getInstance().getReference().child("AAA usernames").child(uid);
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
        if(!path.exists())
        {
            path.mkdirs();
        }
        once.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeofimage="once";
                once.setTextColor(Color.RED);
                keep.setTextColor(Color.GRAY);
                replay.setTextColor(Color.GRAY);
            }
        });
        keep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeofimage="keep";
                once.setTextColor(Color.GRAY);
                replay.setTextColor(Color.GRAY);
                keep.setTextColor(Color.RED);
            }
        });
        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeofimage="replay";
                keep.setTextColor(Color.GRAY);
                once.setTextColor(Color.GRAY);
                replay.setTextColor(Color.RED);
            }
        });
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
        // Toast.makeText(this,chatactivity.resultUri+"",Toast.LENGTH_LONG).show();
        Picasso.get().load(chatactivity.resultUri).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.wallpaperbg)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(chatactivity.resultUri).placeholder(R.drawable.wallpaperbg).into(imageView);
                    }
                });
    }


    public void editimage(View view) {
        Intent dsPhotoEditorIntent = new Intent(this, DsPhotoEditorActivity.class);
        dsPhotoEditorIntent.setData(chatactivity.resultUri);
        dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY, "YAARI");
        int[] toolsToHide = {DsPhotoEditorActivity.TOOL_FRAME, DsPhotoEditorActivity.TOOL_PIXELATE};
        dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_TOOL_BAR_BACKGROUND_COLOR, Color.parseColor("#000000"));
        dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_MAIN_BACKGROUND_COLOR, Color.parseColor("#000000"));
        dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_TOOLS_TO_HIDE, toolsToHide);
        startActivityForResult(dsPhotoEditorIntent, 200);
    }

    public void compress(View view)
    {
        try {

            if(!typepath.equals("$%$")) {
                imgFile = new File(typepath);
            }else {
                imgFile = new File(getRealPathFromDocumentUri(this, chatactivity.resultUri));
            }
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            final View alert=getLayoutInflater().inflate(R.layout.customcompressalert,null);
            builder.setView(alert);
            AlertDialog alertDialog =builder.create();
            PhotoView photoView=alert.findViewById(R.id.compressphotoview);
            photoView.setMaximumScale(15);
            photoView.setMinimumScale(1);
            SeekBar seekBar=alert.findViewById(R.id.seekBar);
            TextView val=alert.findViewById(R.id.finalcompress);
            TextView fulldet=alert.findViewById(R.id.fulldetailcompress);
            seekBar.setProgress(100);
            Button undo=alert.findViewById(R.id.undoalert);
            Button save=alert.findViewById(R.id.savealert2);
            photoView.setImageURI(chatactivity.resultUri);
            String s="Original file size : "+ (imgFile.length()/1024)+" Kb\nCompressed file size : "+(imgFile.length()/1024)+" Kb";
            fulldet.setText(s);
            undo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    seekBar.setProgress(100);
                    val.setText(100+"");
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    byte[] imageInByte = out.toByteArray();
                    long lengthbmp = imageInByte.length/1024;
                    String s="Original file size : "+ (imgFile.length()/1024)+" Kb\nCompressed file size : "+lengthbmp+" Kb";
                    fulldet.setText(s);
                    decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
                    photoView.setImageBitmap(decoded);
                }
            });
            save.setEnabled(false);
            save.setAlpha(0.5f);
            save.setFocusable(false);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    changeway=1;
                    imageView.setImageBitmap(decoded);
                }
            });
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    trytocompress=1;
                    save.setAlpha(1f);
                    save.setEnabled(true);
                    save.setFocusable(true);
                   compressrate=progress;
                   val.setText(progress+"");
                   if(progress==0) {
                       seekBar.setProgress(1);
                       compressrate=1;
                       val.setText(1+"");
                   }
                   if(progress==100)
                   {
                       trytocompress=0;
                       save.setAlpha(0.5f);
                       save.setFocusable(false);
                       save.setEnabled(false);
                   }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    trytocompress=1;
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    if(myBitmap!=null) {
                        myBitmap.compress(Bitmap.CompressFormat.JPEG, compressrate, out);
                         imageInByte= out.toByteArray();
                        long lengthbmp = imageInByte.length / 1024;
                        String s = "Original file size : " + (imgFile.length() / 1024) + " Kb\nCompressed file size : " + lengthbmp + " Kb";
                        fulldet.setText(s);
                        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
                        photoView.setImageBitmap(decoded);
                    }
                    else {
                        Toast.makeText(sendimage.this,"Can't compress",Toast.LENGTH_LONG).show();
                    }
                }
            });
        alertDialog.show();

        } catch (Exception e) {
           Toast.makeText(this,"nm bsdanm",Toast.LENGTH_LONG).show();
        }

    }
    public static String getRealPathFromDocumentUri(Context context, Uri uri){
        String filePath = "";

        Pattern p = Pattern.compile("(\\d+)$");
        Matcher m = p.matcher(uri.toString());
        if (!m.find()) {
            return filePath;
        }
        String imgId = m.group();

        String[] column = { MediaStore.Images.Media.DATA };
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ imgId }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();

        return filePath;
    }

    public void sendmess() {
        progressDialog.show();
        chattext = message.getText().toString().trim();
        Calendar calendar = Calendar.getInstance();
        StorageReference filepath = mref.child("chats").child(mainpage.curuser).child(mainpage.curuser+chatactivity.uid).child("images").child(calendar.getTimeInMillis() + "");
        if(changeway==0) {
            filepath.putFile(chatactivity.resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(
                            new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    String fileLink = task.getResult().toString();
                                    databasefunction(chattext, fileLink);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(sendimage.this, "ERROR OCCURRED", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
        else {
            filepath.putBytes(imageInByte).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(
                            new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    String fileLink = task.getResult().toString();
                                    databasefunction(chattext, fileLink);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(sendimage.this, "ERROR OCCURRED", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    private void databasefunction(String chattext,String fileLink)
    {
        if (chattext.isEmpty()) {
            final DatabaseReference mDatabase;
            final messagemodel model = new messagemodel(mainpage.curuser);
            model.setImageurl(fileLink);
            model.setFeelings(15);
            model.setChecker("image w/o text");
            if(!typeofimage.equals("keep"))
            {
                model.setMessagekey("none");
            }
            model.setImagetype(typeofimage);
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
                            hashMap.put("lastmess", "You recieved a image");
                            hashMap.put("lastmess time", model.getTimestamp());
                            mDatabase.child("Chats").child(chatactivity.uid).child(chatactivity.uid + mainpage.curuser).child("AA details").setValue(hashMap);
                            hashMap.put("lastmess", "You sended a image");
                            mDatabase.child("Chats").child(mainpage.curuser).child(mainpage.curuser + chatactivity.uid).child("AA details").setValue(hashMap);
                            Intent i = new Intent(sendimage.this, chatactivity.class);
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
            model.setChecker("image with text");
            model.setImagetype(typeofimage);
            if(!typeofimage.equals("keep"))
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
                            hashMap.put("lastmess", "You recieved a image");
                            hashMap.put("lastmess time", model.getTimestamp());
                            mDatabase.child("Chats").child(chatactivity.uid).child(chatactivity.uid + mainpage.curuser).child("AA details").setValue(hashMap);
                            hashMap.put("lastmess", "You sended a image");
                            mDatabase.child("Chats").child(mainpage.curuser).child(mainpage.curuser + chatactivity.uid).child("AA details").setValue(hashMap);
                            Intent i = new Intent(sendimage.this, chatactivity.class);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 200:
                    Uri resultUri = data.getData();
                    chatactivity.resultUri=resultUri;
                    Picasso.get().load(chatactivity.resultUri).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.wallpaperbg)
                            .into(imageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                // sendmess();
                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(chatactivity.resultUri).placeholder(R.drawable.wallpaperbg).into(imageView);
                                }
                            });
                    break;

            }
        }
    }

}