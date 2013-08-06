
package com.ivygroup.wfdplayer;

import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener{
    private static final String TAG = "MainActivity";

    private final BroadcastReceiver mMyReceiver = new MyBroadcastReceiver();    
    private WfdSinkController mWfdSinkController;
    private SinkPlayer mSinkPlayer;
    private String mHost;
    private int mPort;
    
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // mWfdSinkController = new WfdSinkController(this);

        
        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_end).setOnClickListener(this);
        
        
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.ivygroup.wfdplayer.tolinksource");
        registerReceiver(mMyReceiver, intentFilter, null, null);


        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView1);  
        /** 
         * 获取与当前surfaceView相关联的那个的surefaceHolder 
         */  
        mSurfaceHolder = mSurfaceView.getHolder();  
        /** 
         * 注册当surfaceView创建、改变和销毁时应该执行的方法 
         */  
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
              
            @Override  
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.i("通知", "surfaceHolder被销毁了");
            }  
              
            @Override  
            public void surfaceCreated(SurfaceHolder holder) {
                Log.i("通知", "surfaceHolder被create了");
            }  
              
            @Override  
            public void surfaceChanged(SurfaceHolder holder, int format, int width,  
                    int height) {  
                Log.i("通知", "surfaceHolder被改变了");  
            }  
        });  
          
        /** 
         *  这里必须设置为SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS哦，意思 
         *  是创建一个push的'surface'，主要的特点就是不进行缓冲 
         */  
        /*mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);*/
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mMyReceiver);
        super.onDestroy();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.btn_start:
                // mWfdSinkController.updateWfdEnableState();
                if (mSinkPlayer != null) {
                    mSinkPlayer.release();
                    mSinkPlayer = null;
                }
                // mSinkPlayer = new SinkPlayer(mHost, mPort);
                break;

            case R.id.btn_end:
                break;

            default:
                break;
        }
    }
    
    
    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String host = intent.getStringExtra("host");
            int port = intent.getIntExtra("port", 7236);
            Log.d(TAG, "Receive the link broadcast. host = " + host + ", port = " + port);

            mHost = host;
            mPort = port;

            if (mSinkPlayer != null) {
                mSinkPlayer.release();
                mSinkPlayer = null;
            }
            mSinkPlayer = new SinkPlayer();
            mSinkPlayer.setDisplay(mSurfaceHolder);
            mSinkPlayer.native_startSink(host, port);
        }
    }
}
