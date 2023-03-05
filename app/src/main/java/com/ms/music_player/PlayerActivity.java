package com.ms.music_player;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {
   Button btnPlay,btnNext,btnPrev,btnFr,btnFf;
   TextView textSName,txtSStart,txtSStop;
   SeekBar seekMusic;
   String sName;
   public static final String EXTRA_NAME ="song_name";
   static MediaPlayer mediaPlayer;
   Thread updateSeekbar;
   int position;
   ArrayList<File> mySongs;
   ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        btnPlay=(Button) findViewById(R.id.playbtn);
        btnNext=(Button) findViewById(R.id.btnnext);
        btnFf=(Button)findViewById(R.id.btnFF);
        btnPrev=(Button) findViewById(R.id.btnprevious);
        btnFr=(Button) findViewById(R.id.btnFR);
        textSName=(TextView) findViewById(R.id.txtsn);
        txtSStart=(TextView) findViewById(R.id.txtSStrat);
        txtSStop=(TextView) findViewById(R.id.txtSStop);
        seekMusic=(SeekBar) findViewById(R.id.seekBar);
        imageView= (ImageView) findViewById(R.id.imageView);
        if (mediaPlayer!= null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        Intent i= getIntent();
        Bundle bundle=i.getExtras();
        mySongs=(ArrayList) bundle.getParcelableArrayList("songs");
        String songName=i.getStringExtra("songname");
        position = bundle.getInt("pos",0);
        textSName.setSelected(true);
        Uri uri= Uri.parse(mySongs.get(position).toString());
        sName=mySongs.get(position).getName();
        textSName.setText(sName);
        mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();
        updateSeekbar= new Thread(){
            @Override
            public void run(){
                int totalDuration=mediaPlayer.getDuration();
                int currentPostition=0;

                while (currentPostition<totalDuration){
                    try{
                        sleep(500);
                        currentPostition=mediaPlayer.getCurrentPosition();
                        seekMusic.setProgress(currentPostition);
                    }
                    catch (InterruptedException | IllegalStateException e){
                        e.printStackTrace();
                    }
                }
            }
        };
        seekMusic.setMax(mediaPlayer.getDuration());
        updateSeekbar.start();
        seekMusic.getProgressDrawable().setColorFilter(getResources().getColor(com.karumi.dexter.R.color.design_default_color_primary), PorterDuff.Mode.MULTIPLY);
        seekMusic.getThumb().setColorFilter(getResources().getColor(com.karumi.dexter.R.color.design_default_color_primary),PorterDuff.Mode.SRC_IN);

        seekMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());

            }
        });
        String endTime=createTime(mediaPlayer.getDuration());
        txtSStop.setText(endTime);
        final Handler handler=new Handler();
        final int delay=1000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime= createTime(mediaPlayer.getCurrentPosition());
                txtSStop.setText(currentTime);
                handler.postDelayed(this,delay);
            }
        },delay);
        //////////////////////////////////////////////////////////////////////////////////////
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()){
                    btnPlay.setBackgroundResource(R.drawable.ic_baseline_play);
                    mediaPlayer.pause();
                }
                else
                {
                    btnPlay.setBackgroundResource(R.drawable.ic_baseline_pause);
                    mediaPlayer.start();
                }
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position=(position+1%mySongs.size());
                Uri u=Uri.parse(mySongs.get(position).toString());
                mediaPlayer=MediaPlayer.create(getApplicationContext(),u);
                sName=mySongs.get(position).getName();
                textSName.setText(sName);
                mediaPlayer.start();
                btnPlay.setBackgroundResource(R.drawable.ic_baseline_pause);
                startAnimation(imageView);
            }
        });
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position=((position-1)<0)?(mySongs.size()-1):(position-1);

                Uri u=Uri.parse(mySongs.get(position).toString());
                mediaPlayer=MediaPlayer.create(getApplicationContext(),u);
                sName=mySongs.get(position).getName();
                textSName.setText(sName);
                mediaPlayer.start();
                btnPlay.setBackgroundResource(R.drawable.ic_baseline_pause);
                startAnimation(imageView);
            }
        });
        btnFf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
                }
            }
        });
        btnFr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
                }
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                btnNext.performClick();
            }
        });
    }
    public void startAnimation(View view)
    {
        ObjectAnimator animator= ObjectAnimator.ofFloat(imageView,"rotation",0f,360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet=new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }
    public String createTime(int duration){
        String time="";
        int min=duration/1000/60;
        int sec=duration/1000%60;
        time=min+":";
        if (sec<10){
            time+="0";
        }
        time+=sec;
        return time;
    }
}