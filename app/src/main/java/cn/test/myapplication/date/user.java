package cn.test.myapplication.date;

import cn.bmob.v3.BmobObject;

/**
 * Created by Gin on 2018/2/13.
 */

public class user extends BmobObject {

    private String username;
    private String password;

    public String getName() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}