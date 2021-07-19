package com.codepath.apps.restclienttemplate.fragments;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeFragment extends DialogFragment {

    public static final String TAG = "ComposeFragment";
    public static final int MAX_TWEET_LENGTH = 140;
    TwitterClient client;
    Tweet tweet;
    // ActivityComposeBinding binding;
    EditText etCompose;
    Button btTweet;

    public ComposeFragment() {
        // Required empty public constructor
    }

    public static ComposeFragment newInstance(String title) {
        ComposeFragment frag = new ComposeFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etCompose = view.findViewById(R.id.etCompose);
        btTweet = view.findViewById(R.id.btnTweet);

        etCompose.requestFocus();
        if(etCompose.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

        client = TwitterApp.getRestClient(getContext());
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
                                getActivity().finish();
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
                }
            }
        });
    }

    public void onResume() {
        // Store access variables for window and blank point
        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((int) (size.x * 0.95), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        // Call super onResume after sizing
        super.onResume();
    }
}