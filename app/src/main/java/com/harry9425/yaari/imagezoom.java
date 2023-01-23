package com.harry9425.yaari;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class imagezoom extends AppCompatActivity {

    String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagezoom);
        PhotoView photoView = (PhotoView) findViewById(R.id.photo_view);
        photoView.setMinimumScale(1f);
        photoView.setMaximumScale(8f);
        photoView.setHapticFeedbackEnabled(true);
        image=getIntent().getStringExtra("zoomuri");
        Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.userdefaultdp).into(photoView, new Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(image).placeholder(R.drawable.userdefaultdp).into(photoView);
            }
        });
        ImageButton imageButton=(ImageButton) findViewById(R.id.photozoomback);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}