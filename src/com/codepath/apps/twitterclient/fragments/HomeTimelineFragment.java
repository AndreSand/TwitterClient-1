package com.codepath.apps.twitterclient.fragments;

import org.json.JSONArray;

import android.util.Log;
import android.widget.Toast;

import com.codepath.apps.twitterclient.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

public class HomeTimelineFragment extends TweetsListFragment {

	@Override
	protected void loadMoreDataFromApi(String sinceId, String maxId) {
		twitterClient.getHomeTimeline(sinceId, maxId,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONArray jsonTweets) {
						Log.d("DEBUG", "About to load tweets to adapter in home timeline");
						getAdapter().addAll(Tweet.fromJson(jsonTweets));
						getAdapter().sort(new TweetComparator());
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

}