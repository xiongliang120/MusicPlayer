package com.example.user.musicplayer.bean;



/**
 * Created by hrdra on 16-8-26.
 */
public class MediaItem {
    private String mPath;
    //
    private boolean isPause;
    private boolean isBackMusic=false;

    public String getPath() {
        return mPath;
    }

    public void setPath(String mPath) {
        this.mPath = mPath;
    }
    public boolean isPause() {
        return isPause;
    }

    public void setIsPause(boolean isPause) {
        this.isPause = isPause;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        MediaItem item = (MediaItem) o;

        return !(mPath != null ? !mPath.equals(item.mPath) : item.mPath != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (mPath != null ? mPath.hashCode() : 0);
        return result;
    }
    public void setIsBackMusic(boolean isBackMusic) {
        this.isBackMusic = isBackMusic;
    }
    public boolean getIsBackMusic() {
        return isBackMusic;
    }
}
