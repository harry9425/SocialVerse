package com.harry9425.yaari;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.loader.content.CursorLoader;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.Observable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import java.io.File;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import it.sephiroth.android.library.rangeseekbar.RangeSeekBar;


public class ffmpegtut extends AppCompatActivity {

    Uri mpeguri=null;
    VideoView videoView;
    MediaController mediaController;
    RangeSeekBar seekBar;
    String uripath,filePath;
    File endpath;
    String[] command;
    Timer timer=null;
    TextView donetrim;
    int duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ffmpegtut);
        videoView=(VideoView) findViewById(R.id.ffmpegview);
        mediaController=new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        seekBar=(RangeSeekBar) findViewById(R.id.ffmpeg_progressbar);
        donetrim=(TextView) findViewById(R.id.donetrim_ffmpeg);
        String mpeg= getIntent().getStringExtra("mpeguri");
        mpeguri=Uri.parse(mpeg);
        videoView.setVideoURI(mpeguri);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
                seekBar.setMax(mp.getDuration());
                previewseekbarupdate();
                seekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(RangeSeekBar rangeSeekBar, int i, int i1, boolean b) {
                        videoView.seekTo(rangeSeekBar.getProgressStart());
                    }
                    @Override
                    public void onStartTrackingTouch(RangeSeekBar rangeSeekBar) {}
                    @Override
                    public void onStopTrackingTouch(RangeSeekBar rangeSeekBar) {}});
                donetrim.setOnClickListener(new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View v) {
                        trimdone(seekBar.getProgressStart(),seekBar.getProgressEnd());
                    }
                });
            }
        });
    }

    private void trimdone(int startMs,int endMs){
        File folder = new File(Environment.getExternalStorageDirectory() + "/CaffeineVideos");
        if (!folder.exists()) {
            folder.mkdir();
        }
        String filePrefix = System.currentTimeMillis()+"";
        String fileExt = ".mp4";
        System.out.println("audio"+fileExt);
        endpath = new File(folder, filePrefix + fileExt);
        uripath = getRealPathFromUri(this,mpeguri);
        duration = (endMs - startMs) / 1000;
        filePath = endpath.getAbsolutePath();
        command = new String[]{"-ss", "" + startMs / 1000, "-y", "-i", uripath, "-t", "" + (endMs - startMs) / 1000, "-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000", "-ac", "2", "-ar", "22050", filePath};
       // execffmpegBinary(command);
    }

    private String getRealPathFromUri(Context context, Uri contentUri) {

        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    private void previewseekbarupdate() {
        timer=new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(videoView.getCurrentPosition()>=seekBar.getProgressEnd())
                {
                    videoView.seekTo(seekBar.getProgressStart());
                }
            }
        }, 0, 500);
    }

}