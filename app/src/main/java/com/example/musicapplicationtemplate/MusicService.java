package com.example.musicapplicationtemplate;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;

public class MusicService extends Service {
    public static final String ACTION_UPDATE_SEEKBAR = "com.example.UPDATE_SEEKBAR";
    public static final String EXTRA_CURRENT_POSITION = "currentPosition";

    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Runnable updateSeekBar;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        // Khởi tạo MediaPlayer, set datasource, prepare...

        updateSeekBar = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    Intent intent = new Intent(ACTION_UPDATE_SEEKBAR);
                    intent.putExtra(EXTRA_CURRENT_POSITION, currentPosition);
                    sendBroadcast(intent);
                }
                // Cập nhật sau mỗi 1 giây
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(updateSeekBar);
    }

    // Xử lý intent khi thay đổi vị trí từ seekbar
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null && "ACTION_SEEKBAR_CHANGE".equals(intent.getAction())){
            int progress = intent.getIntExtra("progress", 0);
            if(mediaPlayer != null) {
                mediaPlayer.seekTo(progress);
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
