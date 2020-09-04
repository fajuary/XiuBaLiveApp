package com.seek.oos.module.bean;

/**
 * Created by chunyang on 2018/5/24.
 */

public class OssDir {

    /**
     * voice : string,语音主页目录
     * wall : string,照片墙目录
     * avatar : {"b":"string,大","m":"string,中","s":"string,小"}
     * story : {"img":"string,图片","video":"string,视频","voice":"string,语音"}
     * chat : {"img":"string,图片","video":"string,视频","voice":"string,语音"}
     */

    private String voice;
    private String wall;
    private OssImg avatar;
    private OssType story;
    private OssType chat;

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public String getWall() {
        return wall;
    }

    public void setWall(String wall) {
        this.wall = wall;
    }

    public OssImg getAvatar() {
        return avatar;
    }

    public void setAvatar(OssImg avatar) {
        this.avatar = avatar;
    }

    public OssType getStory() {
        return story;
    }

    public void setStory(OssType story) {
        this.story = story;
    }

    public OssType getChat() {
        return chat;
    }

    public void setChat(OssType chat) {
        this.chat = chat;
    }
}
