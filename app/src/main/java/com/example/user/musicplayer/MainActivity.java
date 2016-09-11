package com.example.user.musicplayer;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.user.musicplayer.bean.MediaItem;
import com.example.user.musicplayer.bean.MusicPlayMode;
import com.example.user.musicplayer.common.MusicConstant;
import com.example.user.musicplayer.service.MusicService;
import com.example.user.musicplayer.ui.LyricView;
import com.example.user.musicplayer.ui.ProgressBar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnFocusChangeListener{
    private List<MediaItem> datas;
    private MusicPlayMode mMusicMode;
    private Service mService;
    private ImageButton ivBefore;
    private ImageButton ivNext;
    private ImageButton ivStop;
    private View focus;
    private boolean isFocusViewInit = false;
    private int currentPosition = 0;
    private int currentDuration = 0;
    private boolean isPause = false;
    private BroadcastReceiver musicUIRefreshReciver;
    private ImageButton ivMode;
    private ImageButton ivMusicWord;
    private ImageButton ivBgImage;
    private boolean isShowMusicWord = true;
    private int[] modeResIds = new int[]{R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher};
    private MusicPlayMode[] modes = new MusicPlayMode[]{MusicPlayMode.sequential, MusicPlayMode.single, MusicPlayMode.random};
    private LyricView lyricView;


    private ImageView musicBgImage;
    ProgressBar progressBar;
    private static final int SCREEN_SAVER_SHOW_DELAY = 5000;
    public static final String SCREEN_ACTION_LOADING = "com.star.starbox.display.epgui.mediacenter.musiccenter.SCREEN_ACTION_LOADING";
    public static final String SCREEN_ACTION_INTERRUPT = "com.star.starbox.display.epgui.mediacenter.musiccenter.SCREEN_ACTION_INTERRUPT";



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_music_fragment);
        initFragment(MusicPlayMode.random);
        TextView channelName = (TextView) this.findViewById(R.id.channel_name);
        channelName.setText("音乐");
        ivBefore = (ImageButton) this.findViewById(R.id.btn_before);
        ivNext = (ImageButton) this.findViewById(R.id.btn_next);
        ivStop = (ImageButton) this.findViewById(R.id.btn_stop);
        ivMode = (ImageButton) this.findViewById(R.id.btn_play_mode);
        ivMusicWord = (ImageButton) this.findViewById(R.id.btn_music_word);
        ivBgImage = (ImageButton) this.findViewById(R.id.btn_bg_image);




        progressBar = (ProgressBar) this.findViewById(R.id.music_progressBar);

        lyricView = (LyricView) this.findViewById(R.id.lyricView);

        lyricView.setLineSpace(getResources().getDimension(R.dimen.music_lyric_line_space));
        lyricView.setTextSize(getResources().getDimension(R.dimen.music_lyric_text_size));
        lyricView.setHighLightTextColor(getResources().getColor(R.color.music_lyric_color));
        musicBgImage = (ImageView) this.findViewById(R.id.musicBgImage);

       // lyricView.setOnPlayerClickListener(this);
        initListener();
        loadLyric();



        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicService.MUSIC_CURRENT);
        filter.addAction(MusicService.MUSIC_DURATION);
        filter.addAction(MusicService.UPDATE_ACTION);
        filter.addAction(MusicService.MUSIC_PREPARE);
        musicUIRefreshReciver = new MusicUIRefreshReciver();
        this.registerReceiver(musicUIRefreshReciver, filter);
    }

    public void initListener(){
        ivBefore.setOnFocusChangeListener(this);
        ivNext.setOnFocusChangeListener(this);
        ivStop.setOnFocusChangeListener(this);
        ivMode.setOnFocusChangeListener(this);
        ivMusicWord.setOnFocusChangeListener(this);
        ivBgImage.setOnFocusChangeListener(this);
        ivBefore.setOnClickListener(new MyClickListener());
        ivNext.setOnClickListener(new MyClickListener());
        ivStop.setOnClickListener(new MyClickListener());
        ivMode.setOnClickListener(new MyClickListener());
        ivMusicWord.setOnClickListener(new MyClickListener());
        ivBgImage.setOnClickListener(new MyClickListener());
        lyricView.setOnClickListener(new MyClickListener());


    }


   public class MyClickListener implements View.OnClickListener{
        @Override
       public void onClick(View v) {
            switch (focus.getId()) {

                case R.id.btn_before:
                    if (currentPosition > 0) {
                        --currentPosition;
                    } else {
                        currentPosition = datas.size() - 1;
                    }
                    startService(datas.get(currentPosition).getPath(), currentPosition, MusicConstant.PRIVIOUS_MSG);
                    break;
                case R.id.btn_stop:
                    if (datas != null && currentPosition < datas.size()) {
                        if (isPause) {
                            startService(datas.get(currentPosition).getPath(), currentPosition, MusicConstant.CONTINUE_MSG);
                            isPause = false;
                            ivStop.setImageResource(R.mipmap.icon_music_stop);
                            lyricView.setPlayable(true);
                        } else {
                            startService(datas.get(currentPosition).getPath(), currentPosition, MusicConstant.PAUSE_MSG);
                            isPause = true;
                            ivStop.setImageResource(R.mipmap.icon_music_play);
                            lyricView.setPlayable(false);
                        }
                    }
                    break;
                case R.id.btn_next:
                    if (currentPosition < datas.size()) {
                        ++currentPosition;
                    } else {
                        currentPosition = 0;
                    }
                    startService(datas.get(currentPosition).getPath(), currentPosition, MusicConstant.NEXT_MSG);
                    break;
                case R.id.btn_play_mode:
                    switchMode();
                    break;
                case R.id.btn_bg_image:
                    showImageDialog();
                    break;
                case R.id.btn_music_word:
                    if (isShowMusicWord) {
                        ivMusicWord.setImageResource(R.mipmap.icon_music_word_close);
                        isShowMusicWord = false;
                        lyricView.setVisibility(View.INVISIBLE);
                        musicBgImage.setVisibility(View.VISIBLE);
                    } else {
                        ivMusicWord.setImageResource(R.mipmap.icon_music_word_open);
                        isShowMusicWord = true;
                        lyricView.setVisibility(View.VISIBLE);
                        musicBgImage.setVisibility(View.INVISIBLE);

                    }
                default:
                    break;
            }
       }
   }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    this.finish();
                    break;
                default:
                    break;
            }
        }

        return false;
    }


    public void showImageDialog() {
//        MusicBgImageSettingDialog dialog = new MusicBgImageSettingDialog(getActivity(), mCategory, mChannel, deviceInfo, property);
//        dialog.show();
    }




    private void switchMode() {
        for (int i = 0; i < 3; i++) {
            if (modes[i] == mMusicMode) {
                if (i == 2) {
                    i = -1;
                }
                i++;
                mMusicMode = modes[i];
                ivMode.setImageResource(modeResIds[i]);
                sendMusicModeBroadcast(mMusicMode);
            }
        }

    }


    @Override
    public void onDestroy() {
        unBindService();
        stopService(new Intent(this, MusicService.class));
        unregisterReceiver(musicUIRefreshReciver);
        super.onDestroy();
    }


    private void unBindService() {
        this.unbindService(sconnection);
    }

    private void bindService() {
        Intent intent = new Intent(this, MusicService.class);
        this.bindService(intent, sconnection, Context.BIND_AUTO_CREATE);
    }

    private void startService(String path, int position, int msg) {
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("url", path);
        intent.putExtra("listPosition", position);
        intent.putExtra("MSG", msg);
        this.startService(intent);
    }


    ServiceConnection sconnection = new ServiceConnection() {
        /*当绑定时执行*/
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MyBinder binder = (MusicService.MyBinder) service;
            binder.setData(datas);
            sendMusicModeBroadcast(mMusicMode);

        }

        /*当断开绑定时执行，但调用unbindService()时不会触发改方法*/
        public void onServiceDisconnected(ComponentName name) {
            Log.d("activity--->", "已断开绑定service");
        }
    };


    public void initFragment(MusicPlayMode mode) {
        mMusicMode = mode;
        initData();
        bindService();
        if (datas != null && datas.size() > 0) {
            startService(datas.get(currentPosition).getPath(), currentPosition, MusicConstant.PLAY_MSG); //先bind,后start
        }
    }

    /**
     * 初始化数据
     */
    public void initData(){
        datas = new ArrayList<MediaItem>();
        MediaItem mediaItem1 = new MediaItem();
        mediaItem1.setPath("/storage/emulated/0/bbb.mp3");
        mediaItem1.setIsPause(false);
        datas.add(mediaItem1);

        MediaItem mediaItem2 = new MediaItem();
        mediaItem2.setPath("/storage/emulated/0/aaa.mp3");
        mediaItem2.setIsPause(false);
        datas.add(mediaItem2);


        MediaItem mediaItem3 = new MediaItem();
        mediaItem3.setPath("/storage/emulated/0/ccc.mp3");
        mediaItem3.setIsPause(false);
        datas.add(mediaItem3);


    }

    private void setModeIconRes(boolean mode) {
        if (ivMode != null && mMusicMode != null) {
            switch (mMusicMode) {
                case random:
                    ivMode.setImageResource(R.mipmap.icon_randam);
                    break;
                case single:
                    ivMode.setImageResource(R.mipmap.icon_single_circulation);
                    break;
                case sequential:
                    ivMode.setImageResource(R.mipmap.icon_sequential);
                    break;
            }
        }
    }

    private void setWordMode(boolean mode){

    }

    private void sendMusicModeBroadcast(MusicPlayMode mode) {
        Intent modeIntent = new Intent(MusicService.CTL_ACTION);
        modeIntent.putExtra("control", mode.getDbNum());
        this.sendBroadcast(modeIntent);
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.btn_before:
                if(hasFocus){
                    ivBefore.setImageResource(R.mipmap.icon_music_before_focus);
                }else{
                    ivBefore.setImageResource(R.mipmap.icon_music_before);
                }
                break;
            case R.id.btn_stop:
                if(hasFocus){
                    ivStop.setImageResource(R.mipmap.icon_music_stop_focus);
                }else{
                    ivStop.setImageResource(R.mipmap.icon_music_stop);
                }
                break;
            case R.id.btn_next:
                if(hasFocus){
                    ivNext.setImageResource(R.mipmap.icon_music_next_focus);
                }else{
                    ivNext.setImageResource(R.mipmap.icon_music_next);
                }
                break;
            case R.id.btn_play_mode:
//                if(hasFocus){
//                    setModeIconRes();
//                }else{
//                    setModeIconRes();
//                }
                break;
            case R.id.btn_bg_image:
                if(hasFocus){
                    ivBgImage.setImageResource(R.mipmap.icon_music_bg_imag_focus);
                }else{
                    ivBgImage.setImageResource(R.mipmap.icon_music_bg_imag);
                }
                break;
            case R.id.btn_music_word:
//                if(hasFocus){
//                    ivMusicWord.setImageResource(R.mipmap.icon_music_);
//                }else{
//                    ivMusicWord.setImageResource(R.mipmap.icon_music_bg_imag);
//                }
            default:
                break;
        }
    }


    private class MusicUIRefreshReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MusicService.MUSIC_CURRENT)) {
                int position = intent.getIntExtra("currentTime", -1);
                lyricView.setCurrentTimeMillis(position);

                float pos = 0;
                if (currentDuration > 0 && currentDuration > -1) {
                    pos = 100 * position / currentDuration;
                }
                progressBar.setProcessRange(0, pos);

            } else if (intent.getAction().equals(MusicService.MUSIC_DURATION)) {
                currentDuration = intent.getIntExtra("duration", 0);
            } else if (intent.getAction().equals(MusicService.UPDATE_ACTION)) {
                currentPosition = intent.getIntExtra("current", 0);
                loadLyric();
                Log.i(TAG, "currentPosition  1 " + currentPosition);
            } else if (intent.getAction().equals(MusicService.MUSIC_PREPARE)) {
                lyricView.setPlayable(true);
            }
        }
    }

    private void loadLyric() {
        MediaItem currentItem = datas.get(currentPosition);
        File musicFile = new File(currentItem.getPath());

        if (musicFile.exists()) {
            String musicNameWithSuffix = musicFile.getName();
            String musicName = musicNameWithSuffix.substring(0, musicNameWithSuffix.lastIndexOf("."));
            File lyricFile = new File(musicFile.getParent() + File.separator + musicName + ".lrc");
            if (lyricFile.exists()) {
                lyricView.setLyricFile(lyricFile, "UTF-8");
            } else {
                lyricView.setLyricFile(null, "UTF-8");
            }
        }
    }

    private static final String TAG = "MusicServiceTest";

}
