package cn.homework.myweixin;
//µ¥ÀýÄ£Ê½
public class UserInfo {
    private static UserInfo instance;
    private String userId;
    private String avatarUrl;

    public static void setInstance(UserInfo instance) {
        UserInfo.instance = instance;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private String userName;

    private UserInfo() {}

    public static UserInfo getInstance() {
        if (instance == null) {
            instance = new UserInfo();
        }
        return instance;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}