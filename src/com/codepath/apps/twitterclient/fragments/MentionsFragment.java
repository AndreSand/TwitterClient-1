package com.codepath.apps.twitterclient.fragments;

import java.util.ArrayList;

import org.json.JSONArray;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.codepath.apps.twitterclient.core.TwitterClientApp;
import com.codepath.apps.twitterclient.helpers.TwitterClient;
import com.codepath.apps.twitterclient.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

public class MentionsFragment extends TweetsListFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void loadMoreDataFromApi(String screenName, String sinceId, String maxId) {
		Log.d("DEBUG", "About to get mentions");
		twitterClient.getMentions(sinceId, maxId,
			new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(JSONArray jsonTweets) {
					Log.d("DEBUG", "Got a successful response from mentions!");
					getAdapter().addAll(Tweet.fromJson(jsonTweets));
					markRefreshComplete();
				}

				@Override
				public void onFailure(Throwable arg0) {
					Log.d("DEBUG", "Got UNsuccessful mentions");
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
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getScreenName() {
		return null;
	}
}
