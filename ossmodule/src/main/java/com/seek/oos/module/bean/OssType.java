package com.seek.oos.module.bean;

/**
 * Created by chunyang on 2018/5/24.
 */

public class OssType {

    /**
     * img : string,图片
     * video : string,视频
     * voice : string,语音
     */

    private String img;
    private String video;
    private String voice;

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }
}
