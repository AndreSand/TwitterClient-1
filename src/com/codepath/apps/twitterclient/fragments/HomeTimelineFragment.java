package com.codepath.apps.twitterclient.fragments;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.codepath.apps.twitterclient.adapters.TweetsAdapter;
import com.codepath.apps.twitterclient.core.TwitterClientApp;
import com.codepath.apps.twitterclient.helpers.TwitterClient;
import com.codepath.apps.twitterclient.interfaces.ILoadData;
import com.codepath.apps.twitterclient.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

public class HomeTimelineFragment extends TweetsListFragment {
	private ILoadData dataLoader;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
	}

	@Override
	protected void loadMoreDataFromApi(String screenName, String sinceId, String maxId) {
		twitterClient.getHomeTimeline(sinceId, maxId,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONArray jsonTweets) {
						Log.d("DEBUG", "About to load tweets to adapter in home timeline");
						getAdapter().addAll(Tweet.fromJson(jsonTweets));
						markRefreshComplete();

					}

					@Override
					public void onFailure(Throwable arg0) {
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

	@Override
	protected void loadMoreDataFromSql(String screenName, String sinceId, String maxId) {

		List<Tweet> storedTweets = Tweet.getTweets(sinceId, maxId); 
		Log.d("DEBUG", "SQL: " + storedTweets);

		getAdapter().addAll(storedTweets);
	}


	@Override
	protected String getScreenName() {
		return null;
	}
}