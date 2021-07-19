package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    public static final String TAG = "ComposeActivity";
    public static final int MAX_TWEET_LENGTH = 140;
    TwitterClient client;
    Tweet tweet;
    // ActivityComposeBinding binding;
    EditText etCompose;
    Button btTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        etCompose = findViewById(R.id.etCompose);
        btTweet = findViewById(R.id.btnTweet);

        // binding = ActivityComposeBinding.inflate(getLayoutInflater());

        tweet = Parcels.unwrap(getIntent().getParcelableExtra("Tweet"));
        if(tweet != null){
            etCompose.setText("@" + tweet.user.handle + " ");
        }

        etCompose.requestFocus();
        if(etCompose.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

        client = TwitterApp.getRestClient(this);
        btTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(etCompose.getText())){
                    etCompose.setError("Cannot be empty!");
                    return;
                }
                if(etCompose.getText().length() > MAX_TWEET_LENGTH){
                    etCompose.setError("Tweet is too long!");
                    return;
                }

                if(tweet == null) {
                    client.publishTweet(etCompose.getText().toString(), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TAG, "onSuccess to publish tweet");
                            try {
                                Tweet tweet = Tweet.fromJsonObject(json.jsonObject);
                                Intent i = new Intent();
                                i.putExtra("NEW_TWEET", Parcels.wrap(tweet));
                                setResult(RESULT_OK);
                                finish();
                                Log.i(TAG, "Published tweet says: " + tweet.body);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TAG, "OnFailure: " + throwable.getMessage());
                        }
                    });
                }else{
                    client.replyTo(tweet.id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TAG, "onSuccess to reply tweet");
                            try {
                                Tweet tweet = Tweet.fromJsonObject(json.jsonObject);
                                Intent i = new Intent();
                                i.putExtra("NEW_TWEET", Parcels.wrap(tweet));
                                setResult(RESULT_OK);
                                finish();
                                Log.i(TAG, "Published tweet says: " + tweet.body);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TAG, "Failed to reply: " + throwable.getMessage());
                        }
                    }, etCompose.getText().toString());
                }
            }
        });
    }
}