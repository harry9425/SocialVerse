package com.harry9425.yaari;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
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
import com.mukesh.OtpView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class accounteditsettings extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    EditText nametxt,useridtxt,urltxt,abouttxt,emailtxt,phonetxt,dobtxt;
    CircleImageView dpset;
    ConstraintLayout imagepicker;
    ImageButton closepicker,savedetails,availableshow,removedob;
    TextView opencamera,opengallery,removedp,wordlimitshow;
    DatabaseReference mDatabse;
    String curuser,dp,currentPhotoPath,userid,name,website=null,email=null,phone,dob=null,about,gender=null;
    ScrollView outer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounteditsettings);
        wordlimitshow=(TextView) findViewById(R.id.wordlimitshow_editaccount);
        wordlimitshow.setVisibility(View.GONE);
        wordlimitshow.setTextColor(Color.parseColor("#FFFFFF"));
        curuser=FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        mDatabse= FirebaseDatabase.getInstance().getReference().child("AAA usernames").child(curuser);
        mDatabse.keepSynced(true);
        imagepicker=(ConstraintLayout) findViewById(R.id.imagepicker_editaccount);
        imagepicker.setVisibility(View.GONE);
        closepicker=(ImageButton) findViewById(R.id.closeimagepicker_editaccount);
        closepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagepicker.setVisibility(View.GONE);
            }
        });
        removedob=(ImageButton) findViewById(R.id.removedob);
        removedob.setVisibility(View.GONE);
        availableshow=(ImageButton) findViewById(R.id.notyesuid_editaccount);
        availableshow.setVisibility(View.GONE);
        availableshow.setImageResource(R.drawable.ic_baseline_download_done_24);
        savedetails=(ImageButton) findViewById(R.id.saveseeting_editaccount);
        opencamera=(TextView) findViewById(R.id.opencamera_editaccount);
        opengallery=(TextView) findViewById(R.id.opengallery_editaccount);
        removedp=(TextView) findViewById(R.id.removephoto_editaccount);
        nametxt =(EditText) findViewById(R.id.editname_editaccount);
        useridtxt=(EditText) findViewById(R.id.editUsername_editaccount);
        urltxt=(EditText) findViewById(R.id.editurl_editaccount);
        abouttxt=(EditText) findViewById(R.id.editabout_editaccount);
        emailtxt=(EditText) findViewById(R.id.editemail_editaccount);
        phonetxt=(EditText) findViewById(R.id.editphonenumber_editaccount);
        dpset=(CircleImageView) findViewById(R.id.editaccountdp);
        dobtxt=(EditText) findViewById(R.id.datepicker_editaccount) ;
        dobtxt.setFocusable(false);
        nametxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            if(nametxt.getText().toString().isEmpty())
                nametxt.setError("empty");
            else
                nametxt.setError(null);
            }

        });
        opencamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        opengallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 438);
            }
        });
        removedp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabse.child("userdp").setValue("https://firebasestorage.googleapis.com/v0/b/yaari-8ae4e.appspot.com/o/profile_images%2Fdefault%2Fuserdefaultdp.jpg?alt=media&token=e60fa3cc-43c7-44a9-9000-99fea647b1a7")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        });
            }
        });
        dobtxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showDatePickerDialog();
            }
        });
        emailtxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(emailtxt.getText().toString()).matches() && !emailtxt.getText().toString().isEmpty())
                {
                    emailtxt.setError("I/V");
                }
                else {
                    emailtxt.setError(null);
                }
            }
        });
        abouttxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
             if(!abouttxt.getText().toString().isEmpty())
             {
                 abouttxt.setError(null);
                 wordlimitshow.setVisibility(View.VISIBLE);
                 wordlimitshow.setText(abouttxt.getText().toString().length()+"/250");
                 if(abouttxt.getText().toString().length()>250)
                 {
                     wordlimitshow.setTextColor(Color.parseColor("#FF0000"));
                 }
                 else {
                     wordlimitshow.setTextColor(Color.parseColor("#FFFFFF"));
                 }
             }
             else {
                 wordlimitshow.setVisibility(View.GONE);
                 abouttxt.setError("Empty");
             }
            }
        });
        useridtxt.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(useridtxt.hasFocus()) {
                    if(!useridtxt.getText().toString().equals(userid) && !useridtxt.getText().toString().isEmpty()) {
                        checkuid(useridtxt.getText().toString());
                    }
                    else {
                        availableshow.setVisibility(View.GONE);
                    }
                }
                if(useridtxt.getText().toString().isEmpty()){
                    availableshow.setVisibility(View.GONE);
                    useridtxt.setError("Empty");
                }
                else {
                    useridtxt.setError(null);
                }
            }
        });
        removedob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dobtxt.setText("");
            }
        });
        mDatabse.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("name"))
                    nametxt.setText(snapshot.child("name").getValue().toString());
                if(snapshot.hasChild("status"))
                    abouttxt.setText(snapshot.child("status").getValue().toString());
                if(snapshot.hasChild("phoneno"))
                    phonetxt.setText(snapshot.child("phoneno").getValue().toString());
                if(snapshot.hasChild("username")) {
                    userid=snapshot.child("username").getValue().toString();
                    useridtxt.setText(userid);
                }
                if(snapshot.hasChild("email")) {
                    emailtxt.setText(snapshot.child("email").getValue().toString());
                    email=emailtxt.getText().toString().trim();
                }
                if(snapshot.hasChild("websitelink")) {
                    urltxt.setText(snapshot.child("websitelink").getValue().toString());
                    website=urltxt.getText().toString().trim();
                }
                if(snapshot.hasChild("dob")) {
                    dobtxt.setText(snapshot.child("dob").getValue().toString());
                    dob=dobtxt.getText().toString();
                    removedob.setVisibility(View.VISIBLE);
                }
                if(snapshot.hasChild("gender")) {
                    gender=snapshot.child("gender").getValue().toString();
                    setgenderbuttoncolor(gender);
                }
                if(snapshot.hasChild("userdp")) {
                    dp = (snapshot.child("userdp").getValue().toString());
                    Picasso.get().load(dp).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.userdefaultdp)
                            .into(dpset, new Callback() {
                                @Override
                                public void onSuccess() {}
                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(dp).placeholder(R.drawable.userdefaultdp).into(dpset);
                                }
                            });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void save(View view)
    {
        mDatabse=FirebaseDatabase.getInstance().getReference().child("AAA usernames").child(curuser);
        if(!nametxt.getText().toString().trim().isEmpty()) {
            mDatabse.child("name").setValue(nametxt.getText().toString().trim())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    });
        }
        else return;
        if(!useridtxt.getText().toString().trim().isEmpty()) {
            mDatabse.child("username").setValue(useridtxt.getText().toString().trim())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    });
        }
        else return;
        if(!abouttxt.getText().toString().trim().isEmpty() && abouttxt.getText().toString().length()<=250) {
            mDatabse.child("status").setValue(abouttxt.getText().toString().trim())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    });
        }
        else return;
        if(emailtxt.getText().toString().isEmpty()) {
            mDatabse.child("email").removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    });
        }
        else {
            mDatabse.child("email").setValue(emailtxt.getText().toString().trim())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    });
        }
        if(urltxt.getText().toString().isEmpty()) {
            mDatabse.child("websitelink").removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    });
        }
        else {
            mDatabse.child("websitelink").setValue(urltxt.getText().toString().trim())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    });
        }
        if(dobtxt.getText().toString().isEmpty()) {
            mDatabse.child("dob").removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    });
        }
        else {
            mDatabse.child("dob").setValue(dobtxt.getText().toString().trim())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    });
        }
        if(gender!=null)
        {
            mDatabse.child("gender").setValue(gender)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent i =new Intent(accounteditsettings.this,accountsettings.class);
                            startActivity(i);
                            finish();
                        }
                    });
        }
    }

    private void checkuid(String newid) {
        DatabaseReference mDatabse;
        mDatabse=FirebaseDatabase.getInstance().getReference().child("AAA usernames");
        mDatabse.keepSynced(true);
        mDatabse.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(!snapshot.getKey().equals(curuser)) {
                        if (snapshot.hasChild("username")) {
                            if (snapshot.child("username").getValue().toString().equals(newid)) {
                                availableshow.setVisibility(View.VISIBLE);
                                availableshow.setImageResource(R.drawable.ic_baseline_error_24);
                                return;
                            }
                        }
                    }
                }
                availableshow.setVisibility(View.VISIBLE);
                availableshow.setImageResource(R.drawable.ic_baseline_download_done_24);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    public void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, -100); // subtract 2 years from now
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
        Calendar c2 = Calendar.getInstance();
        c2.add(Calendar.YEAR, -1); // add 4 years to min date to have 2 years after now
        datePickerDialog.getDatePicker().setMaxDate(c2.getTimeInMillis());
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = dayOfMonth+" "+getMonthFormat(month) + " " + year;
        dobtxt.setText(date);
    }

    public void backbtn(View view){
        onBackPressed();
    }
    public void changedp(View view)
    {
        if(imagepicker!=null){
            imagepicker.setVisibility(View.VISIBLE);
        }
    }
    private String getMonthFormat(int month)
    {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }

    public void genderchhose(View view)
    {
        Button male=(Button) findViewById(R.id.malebtn);
        Button female=(Button) findViewById(R.id.femalebtn);
        Button other=(Button) findViewById(R.id.otherbtn);
        Button notsay=(Button) findViewById(R.id.notsaybtn);
        male.setBackgroundColor(Color.parseColor("#0A0A0A"));
        female.setBackgroundColor(Color.parseColor("#0A0A0A"));
        other.setBackgroundColor(Color.parseColor("#0A0A0A"));
        notsay.setBackgroundColor(Color.parseColor("#0A0A0A"));

        switch (view.getId()){
            case R.id.malebtn:
                male.setBackgroundColor(Color.parseColor("#4CAF50"));
                gender="male";
                break;
            case R.id.femalebtn:
                female.setBackgroundColor(Color.parseColor("#4CAF50"));
                gender="female";
                break;
            case R.id.otherbtn:
                other.setBackgroundColor(Color.parseColor("#4CAF50"));
                gender="other";
                break;
            case R.id.notsaybtn:
                gender="notsay";
                notsay.setBackgroundColor(Color.parseColor("#4CAF50"));
                break;
        }
    }
    private void setgenderbuttoncolor(String gender)
    {
        Button male=(Button) findViewById(R.id.malebtn);
        Button female=(Button) findViewById(R.id.femalebtn);
        Button other=(Button) findViewById(R.id.otherbtn);
        Button notsay=(Button) findViewById(R.id.notsaybtn);
        male.setBackgroundColor(Color.parseColor("#0A0A0A"));
        female.setBackgroundColor(Color.parseColor("#0A0A0A"));
        other.setBackgroundColor(Color.parseColor("#0A0A0A"));
        notsay.setBackgroundColor(Color.parseColor("#0A0A0A"));

        if(gender.equals("male"))
            male.setBackgroundColor(Color.parseColor("#4CAF50"));
        else if(gender.equals("female"))
            female.setBackgroundColor(Color.parseColor("#4CAF50"));
        else if(gender.equals("female"))
            other.setBackgroundColor(Color.parseColor("#4CAF50"));
        else notsay.setBackgroundColor(Color.parseColor("#4CAF50"));
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
                Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case 438:
                    if(data.getData()!=null)
                    {
                        Intent dsPhotoEditorIntent = new Intent(this, DsPhotoEditorActivity.class);
                        dsPhotoEditorIntent.setData(data.getData());
                        dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY, "YAARI");
                        int[] toolsToHide = {DsPhotoEditorActivity.TOOL_FRAME, DsPhotoEditorActivity.TOOL_PIXELATE};
                        dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_TOOL_BAR_BACKGROUND_COLOR, Color.parseColor("#000000"));
                        dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_MAIN_BACKGROUND_COLOR,Color.parseColor("#000000"));
                        dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_TOOLS_TO_HIDE, toolsToHide);
                        startActivityForResult(dsPhotoEditorIntent, 200);
                    }
                    break;

                case 1:
                    File f=new File(currentPhotoPath);
                    Uri uri=Uri.fromFile(f);
                    Intent dsPhotoEditorIntent = new Intent(this, DsPhotoEditorActivity.class);
                    dsPhotoEditorIntent.setData(uri);
                    dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY, "YAARI");
                    int[] toolsToHide = {DsPhotoEditorActivity.TOOL_FRAME, DsPhotoEditorActivity.TOOL_PIXELATE};
                    dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_TOOL_BAR_BACKGROUND_COLOR,Color.parseColor("#000000"));
                    dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_MAIN_BACKGROUND_COLOR,Color.parseColor("#000000"));
                    dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_TOOLS_TO_HIDE, toolsToHide);
                    startActivityForResult(dsPhotoEditorIntent, 200);
                    break;

                case 200:
                    Uri outputUri =data.getData();
                    Picasso.get().load(outputUri).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.wallpaperbg)
                            .into(dpset, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(outputUri).placeholder(R.drawable.wallpaperbg).into(dpset);
                                }
                            });
                    storedata(outputUri);
                    break;

            }
        }
    }

    private void storedata(Uri resultUri)
    {
        StorageReference mref;
        mref= FirebaseStorage.getInstance().getReference();
        StorageReference filepath=mref.child("profile_images").child(curuser).child("dp");
        filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(
                        new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                String fileLink = task.getResult().toString();
                                DatabaseReference mDatabase;
                                mDatabase=FirebaseDatabase.getInstance().getReference().child("AAA usernames").child(curuser);
                                mDatabase.child("userdp").setValue(fileLink).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(accounteditsettings.this, "DP CHANGED SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                                        }}});}});
            }}).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(accounteditsettings.this, "ERROR OCCURRED", Toast.LENGTH_SHORT).show();
            }
        });;
    }

}