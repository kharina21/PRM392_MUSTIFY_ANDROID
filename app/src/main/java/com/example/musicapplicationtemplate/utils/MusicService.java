package com.example.musicapplicationtemplate.utils;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class MusicService extends Service {
    public static final String ACTION_UPDATE_SEEKBAR = "com.example.UPDATE_SEEKBAR";
    public static final String EXTRA_CURRENT_POSITION = "currentPosition";
    private static final String TAG = "MusicService";

    private MediaPlayer mediaPlayer;
    private MusicPlayerManager musicPlayerManager;
    private Handler handler = new Handler();
    private Runnable updateSeekBar;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "MusicService created");

        // Khởi tạo MusicPlayerManager
        musicPlayerManager = MusicPlayerManager.getInstance();
        if (musicPlayerManager == null) {
            Log.e(TAG, "MusicPlayerManager is NULL!");
            return;  // Tránh lỗi null
        }

        // Khởi tạo MediaPlayer
        mediaPlayer = new MediaPlayer();
        // TODO: Thêm setDataSource() & prepare() nếu cần thiết

        // Khởi động cập nhật SeekBar chỉ khi mediaPlayer đang chạy
        updateSeekBar = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    Intent intent = new Intent(ACTION_UPDATE_SEEKBAR);
                    intent.putExtra(EXTRA_CURRENT_POSITION, currentPosition);
                    sendBroadcast(intent);
                    handler.postDelayed(this, 500);
                }
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "MusicService onStartCommand");

        if (intent != null) {
            String action = intent.getAction();
            if ("ACTION_SEEKBAR_CHANGE".equals(action)) {
                int progress = intent.getIntExtra("progress", 0);
                if (mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
            } else if ("ACTION_PLAY".equals(action)) {
                // Xử lý phát nhạc
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    handler.post(updateSeekBar);  // Bắt đầu cập nhật SeekBar
                }
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "MusicService destroyed");

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacks(updateSeekBar);
    }
}
