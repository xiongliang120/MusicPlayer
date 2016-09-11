package com.example.user.musicplayer.bean;

/**
 * Copyright  : 2015-2033 Beijing Startimes Communication & Network Technology Co.Ltd
 * <p/>
 * Created by xiongl on 2016/9/10..
 * ClassName  :
 * Description  :
 */
public enum MusicPlayMode {
    single(0),random(1), sequential(2);
    private int dbNum;
    private MusicPlayMode(int dbNum) {
        this.dbNum = dbNum;
    }

    public int getDbNum() {
        return dbNum;
    }

    public static MusicPlayMode valueOf(int dbNum) {
        MusicPlayMode[] cs = MusicPlayMode.values();
        if(dbNum>=cs.length||dbNum<0){
            return null;
        }
        return cs[dbNum];
    }
}

