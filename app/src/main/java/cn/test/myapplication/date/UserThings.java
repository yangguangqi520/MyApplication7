package cn.test.myapplication.date;

import cn.bmob.v3.BmobObject;

public class UserThings extends BmobObject {
    private String username;
    private String thingId;
    private String thingTitle;
    private String thingContent;

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
    public String getThingTitle() {
        return thingTitle;
    }
    public void setThingTitle(String thingTitle) {
        this.thingTitle = thingTitle;
    }
    public String getThingContent() {
        return thingContent;
    }
    public void setThingContent(String thingContent) {
        this.thingContent = thingContent;
    }

}
