package cn.homework.myweixin;

import java.util.Date;

public class FriendEntity {



//    private String userId1;

    private String senderId;

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    private String friendId;


    private Date lastTime;

    private String lastMessage;

    private String avatarUrl;

    private String friendName;         // 好友的用户名

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }



    // Getters and Setters

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }


//    public String getUserId1() {
//        return userId1;
//    }
//
//    public void setUserId1(String userId1) {
//        this.userId1 = userId1;
//    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }
}
