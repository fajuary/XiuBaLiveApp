package com.seek.db.column;

/**
 * Created by chunyang on 2018/6/9.
 */

public class FriendColumnBean extends FriendColumn {

    public FriendColumnBean(FriendColumn friendColumn){
        this.setAge(friendColumn.getAge());
        this.setAvatar(friendColumn.getAvatar());
        this.setNick(friendColumn.getNick());
        this.setSex(friendColumn.getSex());
        this.setRemarks(friendColumn.getRemarks());
        this.setUserId(friendColumn.getUserId());
    }

    private int type;
    private boolean isFriends;
    private int role;

    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }

    public boolean isFriends() {
        return isFriends;
    }

    public void setFriends(boolean friends) {
        isFriends = friends;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    //1普通用户．　2系统用户．
    public static int isSystemUser(long id){
        if( id== 1L || id ==2L || id ==3 ){
            return 2;
        }else {
            return 1;
        }
    }
}
