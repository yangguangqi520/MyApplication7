package cn.test.myapplication.date;

import cn.bmob.v3.BmobObject;

public class UserComment extends BmobObject {
    private String username;
    private String thingId;
    private String comment;

    public String getName() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getThingId() {
        return thingId;
    }
    public void setThingId(String thingId) {
        this.thingId = thingId;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
}
