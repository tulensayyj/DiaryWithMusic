package com.example.yyj.diaryapp;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleCursorAdapter;



public class MainActivity extends AppCompatActivity {
    SimpleCursorAdapter lvAdapter=null;
    ListView lvDiary=null;
    FloatingActionButton btnAdd=null;
    MySqlite mySqlite=null;
    SQLiteDatabase mdbWriter=null;
    SQLiteDatabase mdbReader=null;
    private MusicService musicService;
    FloatingActionButton btnMusic=null;
    FloatingActionButton btnMusicStop=null;
    Intent intent;


    Boolean mBound = false;

    MusicService mService;

    SeekBar seekBar;

    //多线程，后台更新UI
    Thread myThread;

    //控制后台线程退出
    boolean playStatus = true;

    MusicService.MyBinder musicBinder;

    boolean CheckStart;


    //处理进度条更新

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0:
                    //从bundle中获取进度，是double类型，播放的百分比
                    double progress = msg.getData().getDouble("progress");

                    //根据播放百分比，计算seekbar的实际位置
                    int max = seekBar.getMax();
                    int position = (int) (max*progress);

                    //设置seekbar的实际位置
                    seekBar.setProgress(position);
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        /**************************************************************************/



        /*****************************************************************************/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       /* mService=((MusicService.MyBinder)*/

        lvDiary=(ListView)findViewById(R.id.lvDiary);
        mySqlite=new MySqlite(this);
        mdbWriter=mySqlite.getWritableDatabase();
        mdbReader=mySqlite.getReadableDatabase();
        btnAdd=(FloatingActionButton) findViewById(R.id.btnAdd);
        btnMusic=(FloatingActionButton) findViewById(R.id.btnMusic);
        btnMusicStop=(FloatingActionButton) findViewById(R.id.btnMusicStop);

        lvAdapter=new SimpleCursorAdapter(MainActivity.this,R.layout.lv_item,null
                ,new String[]{"Title"},new int[]{R.id.lv_item_title}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        lvDiary.setAdapter(lvAdapter);
        refreshListview();

        intent = new Intent();
        intent.setClass(MainActivity.this, MusicService.class);


        lvDiary.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(MainActivity.this,DetailActivity.class);
                Cursor mCursor = lvAdapter.getCursor();
                mCursor.moveToPosition(position);
                String title=mCursor.getString(mCursor.getColumnIndex("Title"));
                String details=mCursor.getString(mCursor.getColumnIndex("Details"));
                intent.putExtra("Title",title);
                intent.putExtra("Details",details);
                startActivity(intent);
            }
        });
        lvDiary.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("提示")
                        .setMessage("是否确定删除该项?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteData(position);
                            }
                        })
                        .setNegativeButton("取消",null)
                        .show();
                return  true;

            }
        });


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent intent=new Intent(MainActivity.this,AddActivity.class);
                startActivity(intent);
            }
        });

         /* *************************************************************************** */


        myThread = new Thread(new MyThread());



        //绑定service;
        Intent serviceIntent = new Intent(this , MusicService.class);

        //如果未绑定，则进行绑定
        if(!mBound){
            bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
        }


        //初始化播放按钮
        btnMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBound&&musicBinder.check()){
                    musicBinder.play();
                }
            }
        });

        //初始化暂停按钮
        btnMusicStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBound&&musicBinder.check()){
                    musicBinder.pause();
                }
            }
        });

        seekBar = (SeekBar)findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //手动调节进度
                // TODO Auto-generated method stub
                //seekbar的拖动位置
                int dest = seekBar.getProgress();
                //seekbar的最大值
                int max = seekBar.getMax();
                //调用service调节播放进度
                musicBinder.setProgress(max, dest);
            }

            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub

            }

        });

         /* *************************************************************************** */
        /*btnMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(intent);
                bindService(intent, conn, Context.BIND_AUTO_CREATE);
                finish();
            }
        });

        btnMusicStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unbindService(conn);
                stopService(intent);
                finish();
            }
        });*/



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent intent=new Intent(MainActivity.this,AddActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void refreshListview(){
        Cursor mCursor=mdbWriter.query("Diary",null,null,null,null,null,null);
        lvAdapter.changeCursor(mCursor);
    }

    public void deleteData(int position){
        Cursor mCursor=lvAdapter.getCursor();
        mCursor.moveToPosition(position);
        int id=mCursor.getInt(mCursor.getColumnIndex("_id"));
        mdbWriter.delete("Diary","_id=?",new String[]{id+""});
        refreshListview();
    }


   /* ********************************************************  */
   public class MyThread implements Runnable{



       //通知UI更新的消息


       //用来向UI线程传递进度的值
       Bundle data = new Bundle();

       //更新UI间隔时间
       int milliseconds = 100;
       double progress;
       @Override
       public void run() {
           // TODO Auto-generated method stub
               //用来标识是否还在播放状态，用来控制线程退出
               while (playStatus) {
                       try {
                           //绑定成功才能开始更新UI
                           if (mBound) {

                               //发送消息，要求更新UI

                               Message msg = new Message();
                               data.clear();

                               progress = musicBinder.getProgress();
                               msg.what = 0;

                               data.putDouble("progress", progress);
                               msg.setData(data);
                               mHandler.sendMessage(msg);
                           }
                           Thread.sleep(milliseconds);
                           //Thread.currentThread().sleep(milliseconds);
                           //每隔100ms更新一次UI

                       } catch (InterruptedException e) {
                           // TODO Auto-generated catch block
                           e.printStackTrace();
                       }


               }
       }

   }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder binder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            musicBinder = (MusicService.MyBinder) binder;

            //获取service
            /*mService = (MusicService) musicBinder.getService();*/


            //绑定成功

            mBound = true;

            myThread.start();   //Thread里面的 run() 方法会在一定条件下自动不断循环执行
                                // 这也就是监听器实现按钮响应事件的原理
            //开启线程，更新UI
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    public void onDestroy(){
        //销毁activity时，要记得销毁线程
        playStatus = false;
        super.onDestroy();
    }
    /* ********************************************************************* */

    /*@Override
    protected void onResume() {
        super.onResume();
        mHandler.sendEmptyMessage(MSG_RESUME_PENDING);
        mResumed = true;
        mFragments.execPendingActions();
    }*/
}
