package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Headers;

public class DetailActivity extends AppCompatActivity {

    public static final String TAG = "DetailActivity";
    Tweet tweet;
    TwitterClient client;
    // ActivityDetailBinding binding;
    private ImageButton ibFavorite;
    private ImageButton ibRetweet;
    private ImageButton ibReply;
    private ImageView ivProfile;
    private TextView tvLikes;
    private TextView tvUsername;
    private TextView tvHandle;
    private TextView tvBody;
    private TextView tvDate;
    private TextView tvRetweets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // binding = ActivityDetailBinding.inflate(getLayoutInflater());
        client = TwitterApp.getRestClient(this);

        ivProfile = findViewById(R.id.ivProfile);
        ibFavorite = findViewById(R.id.ibFavorite);
        ibRetweet = findViewById(R.id.ibRetweet);
        ibReply = findViewById(R.id.ibReply);
        tvLikes = findViewById(R.id.tvLikes);
        tvUsername = findViewById(R.id.tvUsername);
        tvHandle = findViewById(R.id.tvHandle);
        tvBody = findViewById(R.id.tvBody);
        tvDate = findViewById(R.id.tvDate);
        tvRetweets = findViewById(R.id.tvRetweets);

        ibFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tweet.favoriteStatus == true){
                    client.unFavoriteTweet(tweet.id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.d(TAG, "Unfavorited tweet onSuccess");
                            tweet.favoriteStatus = false;
                            Integer numFavorites;
                            String [] split = tvLikes.getText().toString().split(" ");
                            numFavorites = Integer.valueOf(split[0]);
                            tvLikes.setText(String.format("%d Likes", numFavorites - 1));
                            changeButtons();
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.d(TAG, "OnFailure to unfavorite: " + throwable.getMessage());
                        }
                    });
                }else {
                    client.favoriteTweet(tweet.id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.d(TAG, "Favorited Tweet onSuccess");
                            tweet.favoriteStatus = true;
                            Integer numFavorites;
                            String [] split = tvLikes.getText().toString().split(" ");
                            numFavorites = Integer.valueOf(split[0]);
                            tvLikes.setText(String.format("%d Likes", numFavorites + 1));
                            changeButtons();
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.d(TAG, "Favorited Tweet OnFailure: " + throwable.getMessage());
                        }
                    });
                }
            }
        });

        ibReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DetailActivity.this, ComposeActivity.class);
                i.putExtra("Tweet", Parcels.wrap(tweet));
                startActivity(i);
            }
        });

        tweet = Parcels.unwrap(getIntent().getParcelableExtra("Tweet"));
        Log.d(TAG, "Tweet ID: " + tweet.id);
        loadTweet();
    }

    private void loadTweet() {
        tvUsername.setText(tweet.user.username);
        tvHandle.setText("@" + tweet.user.handle);
        Glide.with(this)
                .load(tweet.user.ivProfileUrl)
                .circleCrop()
                .into(ivProfile);

        tvBody.setText(tweet.body);
        tvDate.setText(formattedDate(tweet.createdAt));

        String retweets = "<font color='black'><b>" + tweet.retweets + "</b></font> " + "Retweets";
        String likes = "<font color='black'><b>" + tweet.favorites + "</b></font> " + "Likes";
        tvRetweets.setText(Html.fromHtml(retweets));
        tvLikes.setText(Html.fromHtml(likes));

        changeButtons();
    }

    public void changeButtons(){
        if(tweet.retweetStatus == true){
            ibRetweet.setImageResource(R.drawable.ic_vector_retweet);
            ibRetweet.getDrawable().setTint(Color.RED);
        }else {
            ibRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
            ibRetweet.getDrawable().setTint(Color.GRAY);
        }

        if(tweet.favoriteStatus == true){
            ibFavorite.setImageResource(R.drawable.ic_vector_heart);
            ibFavorite.getDrawable().setTint(Color.RED);
        }
        else{
            ibFavorite.setImageResource(R.drawable.ic_vector_heart_stroke);
            ibFavorite.getDrawable().setTint(Color.GRAY);
        }
    }

    private String formattedDate(String rawDate){
        StringBuilder result = new StringBuilder();
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
        Date date = new Date(rawDate);
        result.append(formatter.format(date));
        formatter = new SimpleDateFormat("MM/dd/yyyy");
        result.append(" - " + formatter.format(date));
        return result.toString();
    }
}