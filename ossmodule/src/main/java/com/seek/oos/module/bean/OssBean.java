package com.seek.oos.module.bean;

/**
 * Created by chunyang on 2018/5/24.
 */

public class OssBean {

    /**
     * endpoint : 1
     * bucketName : 2
     * dirs : {}
     */

    private String endpoint;
    private String bucketName;
    private OssDir dirs;
    private Domains domains;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public OssDir getDirs() {
        return dirs;
    }

    public void setDirs(OssDir dirs) {
        this.dirs = dirs;
    }

    public void setDomains(Domains domains) {
        this.domains = domains;
    }

    public Domains getDomains() {
        return domains;
    }
}