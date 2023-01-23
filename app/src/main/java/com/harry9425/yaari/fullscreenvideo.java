package com.harry9425.yaari;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.Collections;

public class fullscreenvideo extends AppCompatActivity {

    Boolean fullscreen=false;
    ImageView full;
    PlayerView playerView;
    SimpleExoPlayer simpleExoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreenvideo);
        playerView=(PlayerView) findViewById(R.id.fullvideoview);
        full=playerView.findViewById(R.id.exo_fullscreen);
        full.setImageResource(R.drawable.exo_controls_fullscreen_exit);
        full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
         try {

             Uri url = Uri.parse(getIntent().getStringExtra("uri"));
             String time = getIntent().getStringExtra("pos");
             simpleExoPlayer = new SimpleExoPlayer.Builder(this).build();
             playerView.setPlayer(simpleExoPlayer);
             MediaItem mediaItem = MediaItem.fromUri(url);
             simpleExoPlayer.addMediaItems(Collections.singletonList(mediaItem));
             simpleExoPlayer.prepare();
             simpleExoPlayer.seekTo(Long.parseLong(time));
             Toast.makeText(this,Long.parseLong(time)+"",Toast.LENGTH_LONG).show();
             simpleExoPlayer.setPlayWhenReady(true);
         } catch (NumberFormatException e) {
             e.printStackTrace();
         }
    }
    @Override
    public void onPause() {
        super.onPause();
        playerView.getPlayer().stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        playerView.getPlayer().stop();
    }

    @Override
    protected void onStop() {
        super.onStop();
        playerView.getPlayer().stop();
    }
}