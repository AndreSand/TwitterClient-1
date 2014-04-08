package com.codepath.apps.twitterclient.fragments;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        Log.d("DEBUG", "New instance of search fragment being created");
    	SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString("query", query);
        fragment.setArguments(args);
        return fragment;
    }
    
	@Override
	protected void loadMoreDataFromApi(String sinceId, String maxId) {
		Log.d("DEBUG", "Got a call to load more data from api in search");
		twitterClient.getSearch(query,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject jsonSearchResult) {
						Log.d("DEBUG", "Got a successful response from search!");
						try {
							JSONArray jsonTweets = jsonSearchResult.getJSONArray("statuses");
							getAdapter().addAll(Tweet.fromJson(jsonTweets));
							getAdapter().sort(new TweetComparator());
						} catch (JSONException e) {
							e.printStackTrace();
						}
						markRefreshComplete();
					}

					@Override
					public void onFailure(Throwable arg0) {
						Log.d("DEBUG", "Got UNsuccessful search");
						Toast.makeText(getActivity(), "FAIL",
								Toast.LENGTH_LONG).show();
					}

					@Override
					public void handleFailureMessage(Throwable e, String responseBody) {
						Log.d("DEBUG", "FAILURE in search!!!" + responseBody);
					}

				}
			);				

		
	}
}
