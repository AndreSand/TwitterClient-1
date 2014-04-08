package com.codepath.apps.twitterclient.fragments;

import org.json.JSONArray;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.codepath.apps.twitterclient.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

public class UserTimelineFragment extends TweetsListFragment {

	private String userName;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userName = getArguments().getString("userName", "");	
	}
	
	@Override
	protected void loadMoreDataFromApi(String sinceId, String maxId) {
		twitterClient.getUserTimeline(userName, sinceId, maxId,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONArray jsonTweets) {
						Log.d("DEBUG", "Got user timeline success");
						Log.d("DEBUG", Tweet.fromJson(jsonTweets).toString());
						getAdapter().addAll(Tweet.fromJson(jsonTweets));
						getAdapter().sort(new TweetComparator());
						markRefreshComplete();
					}

					@Override
					public void onFailure(Throwable arg0) {
						Log.d("DEBUG", "Got user timeline failure");
						//Log.d("DEBUG", "Got UNsuccessful tweets");
						Toast.makeText(getActivity(), "FAIL",
								Toast.LENGTH_LONG).show();
					}
					@Override
					public void handleFailureMessage(Throwable e, String responseBody) {
						Log.d("DEBUG", "FAILURE!!!" + responseBody);
					}
				}
		);					
	}

    public static UserTimelineFragment newInstance(String userName) {
    	UserTimelineFragment fragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString("userName", userName);
        fragment.setArguments(args);
        return fragment;
    }
	
}
