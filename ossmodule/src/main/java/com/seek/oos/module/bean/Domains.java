package com.seek.oos.module.bean;

/**
 * Created by chunyang on 2018/5/25.
 */

public class Domains {
    /**
     * avatar : string,头像域名(https://cover.2cq.com/)
     * img : string,非头像图片域名(https://img.2cq.com/)
     * video : string,视频域名(https://media.2cq.com/)
     * voice : string,音频域名(https://media.2cq.com/)
     */

    private String avatar;
    private String img;
    private String video;
    private String voice;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

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
