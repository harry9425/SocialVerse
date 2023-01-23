package com.harry9425.yaari;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.messaging.FirebaseMessaging;


import java.util.concurrent.TimeUnit;

public class otpverification extends AppCompatActivity {

    EditText phonenoenternedbyuser;
    Button verify;
    String verificationCodeBySystem;
    public static String phoneno;
    public static int what=0;
    ProgressDialog progressDialog;
    private  DatabaseReference databaseReference;
    public static String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);
        phonenoenternedbyuser=(EditText) findViewById(R.id.otpuser);
        verify=(Button) findViewById(R.id.verify);
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("ACCOUNT VERIFICATION");
        progressDialog.setMessage("Waiting for otp");
        progressDialog.show();
        sendverificationcodetouser(phoneno);
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String code = phonenoenternedbyuser.getText().toString();

                if (code.isEmpty() || code.length() < 6) {
                    phonenoenternedbyuser.setError("Wrong OTP...");
                    phonenoenternedbyuser.requestFocus();
                    return;
                }
                verifyCode(code);
            }
        });
    }

    private void sendverificationcodetouser(String phone) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,   // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

       @Override
       public void onCodeSent(String s,PhoneAuthProvider.ForceResendingToken forceResendingToken)
       {
           super.onCodeSent(s,forceResendingToken);
           verificationCodeBySystem = s;
       }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
        String code=phoneAuthCredential.getSmsCode();
        if(code!= null)
           verifyCode(code);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(otpverification.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void verifyCode(String codeByUser) {
        try{
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, codeByUser);
            signInTheUserByCredentials(credential);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(otpverification.this, "WRONG OTP", Toast.LENGTH_SHORT).show();

        }
    }

    private void signInTheUserByCredentials(PhoneAuthCredential credential) {

        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(otpverification.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            String userid=firebaseAuth.getCurrentUser().getUid().toString();
                            token= FirebaseMessaging.getInstance().getToken().toString();
                            Toast.makeText(otpverification.this, token, Toast.LENGTH_SHORT).show();
                            if(what==0) {

                                Intent intent = new Intent(getApplicationContext(), registername.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                            else {
                                databaseReference.child("AAA usernames").child(userid).child("tokenid").setValue(token).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Intent intent = new Intent(getApplicationContext(), mainpage.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);

                                        } else {
                                            Toast.makeText(otpverification.this, "ERROR 456N", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    }
                                });
                            }

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(otpverification.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
