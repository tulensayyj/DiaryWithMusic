package com.example.yyj.diaryapp;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;

import android.os.IBinder;
import android.util.Log;

import android.os.Handler;

public class MusicService extends Service  {
    IBinder musicBinder  = new MyBinder();
    /* int time;*/
    boolean checkStart=true;


    MediaPlayer mediaPlayer;

    String path = "/tencent/MicroMsg/Download/Schnappi-Schnappi.mp3";

    private String TAG = "MyService";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() executed");

        init();

    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        //当绑定后，返回一个musicBinder
        return musicBinder;
    }

    class MyBinder extends Binder{

        /*public Service getService(){
            return MusicService.this;
        }*/

        //返回当前的播放进度，是double类型，即播放的百分比
        public double getProgress(){
            int position = mediaPlayer.getCurrentPosition();

            int time=mediaPlayer.getDuration();

            double progress = (double)((double)position / (double)time);

            return progress;
        }

        //通过activity调节播放进度
        public void setProgress(int max , int dest){

            int time=mediaPlayer.getDuration();
            mediaPlayer.seekTo(time*dest/max);
       /* mediaPlayer.seekTo(dest);*/
        }

        //测试播放音乐
        public void play(){
            if(mediaPlayer != null){
                mediaPlayer.start();
            }

        }
        //暂停音乐
        public void pause() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }
        public boolean check(){
            return checkStart;
        }
    }

    //初始化音乐播放
    void init(){
        //进入Idle
        mediaPlayer = new MediaPlayer();
        /*mediaPlayer=MediaPlayer.create(this,R.raw.yom);*/
        try {
            //初始化
            Uri uri = Uri.parse("android.resource://com.example.yyj.diaryapp/"+R.raw.yom);
            mediaPlayer.setDataSource(this,uri);

            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            // prepare 通过异步的方式装载媒体资源
            mediaPlayer.prepareAsync();





        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }




    //service 销毁时，停止播放音乐，释放资源
    @Override
    public void onDestroy() {
        // 在activity结束的时候回收资源
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
   /* MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer mp) {

        }
    };*/
}
