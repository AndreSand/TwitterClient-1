package com.codepath.apps.twitterclient.fragments;

import org.json.JSONArray;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.codepath.apps.twitterclient.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

public class SearchFragment extends TweetsListFragment {

	private String query;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		query = getArguments().getString("query", "");
		Log.d("DEBUG", "Query is: " + query);
	}
	
    public static SearchFragment newInstance(String query) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString("query", query);
        fragment.setArguments(args);
        return fragment;
    }
	@Override
	protected void loadMoreDataFromApi(String screenName, String sinceId,
			String maxId) {
		Log.d("DEBUG", "Got a call to load more data from api in search");
		twitterClient.getSearch(query,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONArray jsonTweets) {
						Log.d("DEBUG", "Got a successful response from search!");
						getAdapter().addAll(Tweet.fromJson(jsonTweets));
						markRefreshComplete();
					}

					@Override
					public void handleFailureMessage(Throwable e, String responseBody) {
						Log.d("DEBUG", "FAILURE!!!" + responseBody);
					}

				}
			);				

		
	}

	@Override
	protected void loadMoreDataFromSql(String screenName, String sinceId,
			String maxId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getScreenName() {
		// TODO Auto-generated method stub
		return null;
	}

}
