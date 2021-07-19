package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class User {

    public User(){

    }

    public String handle;
    public String username;
    public String ivProfileUrl;

    public static User fromJsonObject(JSONObject object) throws JSONException {
        User user = new User();
        user.handle = object.getString("screen_name");
        user.username = object.getString("name");
        user.ivProfileUrl = object.getString("profile_image_url_https");
        return user;
    }
}