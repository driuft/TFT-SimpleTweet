package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Parcel
public class Tweet {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public String body;
    public String createdAt;
    public User user;
    public String mediaUrl;
    public String videoUrl;
    public long id;
    public int retweets;
    public int favorites;
    public boolean favoriteStatus;
    public boolean retweetStatus;

    public Tweet(){

    }

    public static Tweet fromJsonObject(JSONObject object) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = object.getString("text");
        tweet.createdAt = object.getString("created_at");
        tweet.user = User.fromJsonObject(object.getJSONObject("user"));
        tweet.id = object.getLong("id");
        tweet.retweets = object.getInt("retweet_count");
        tweet.retweetStatus = object.getBoolean("retweeted");
        tweet.favorites = object.getInt("favorite_count");
        tweet.favoriteStatus = object.getBoolean("favorited");

        if (!object.isNull("extended_entities")) {
            JSONObject extendedEntitites = object.getJSONObject("extended_entities");
            JSONArray jsonArray = extendedEntitites.getJSONArray("media");
            JSONObject media = jsonArray.getJSONObject(0);
            if (media.has("type")) {
                String type = media.getString("type");
                if (type.equals("photo")) {
                    tweet.mediaUrl = String.format("%s:large",media.getString("media_url_https"));
                } else if (type.equals("animated_gif")) {
                    tweet.mediaUrl = String.format("%s:large",media.getString("media_url_https"));
                } else if (type.equals("video")) {
                    JSONObject videoInfo = media.getJSONObject("video_info");
                    JSONArray variants = videoInfo.getJSONArray("variants");
                    JSONObject url = variants.getJSONObject(1);
                    tweet.videoUrl = url.getString("url");
                    Log.d("video", tweet.videoUrl);
                }
            }
        } else {
            tweet.videoUrl = "";
            tweet.mediaUrl = "";
        }

        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++){
            tweets.add(fromJsonObject(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        try {
            long time = sf.parse(rawJsonDate).getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + "m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + "h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + "d";
            }
        } catch (ParseException e) {
            Log.i("Tweet", "getRelativeTimeAgo failed");
            e.printStackTrace();
        }

        return "";
    }
}