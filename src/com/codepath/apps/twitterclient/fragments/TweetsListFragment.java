package com.codepath.apps.twitterclient.fragments;

import java.util.ArrayList;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.adapters.TweetsAdapter;
import com.codepath.apps.twitterclient.core.TwitterClientApp;
import com.codepath.apps.twitterclient.helpers.TwitterClient;
import com.codepath.apps.twitterclient.interfaces.EndlessScrollListener;
import com.codepath.apps.twitterclient.interfaces.ILoadData;
import com.codepath.apps.twitterclient.models.Tweet;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public abstract class TweetsListFragment extends Fragment {

	private TweetsAdapter adapter;
	private ArrayList<Tweet> tweets;
	private PullToRefreshListView listView;
	private ILoadData dataLoader;
	protected TwitterClient twitterClient;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_tweets_list, container, false); 
		listView = (PullToRefreshListView) v.findViewById(R.id.lvTweets);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		tweets = new ArrayList<Tweet>();
		adapter = new TweetsAdapter(getActivity(), tweets);
		twitterClient = TwitterClientApp.getRestClient();
		// RACE condition - should be in onCreateView, v.findViewById()
		// Should not reach up to activity
		// Arguments cannot be passed into static fragment
		// Move more things to onCreateView instead of onActivityCreated
		//listView = (PullToRefreshListView) getActivity().findViewById(R.id.lvTweets);
		listView.setAdapter(adapter);

		// Simple connectivity check for now. Should expand this to receive
		// broadcast intents when network status changes.
		if (TwitterClientApp.getRestClient().isOnline()) {
			Log.d("DEBUG", "Detected we are online");
			dataLoader = new ILoadData() {
				@Override
				public void getMoreTweets(String screenName, String sinceId, String maxId) {
					getActivity().setProgressBarIndeterminateVisibility(true);
					loadMoreDataFromApi(screenName, sinceId, maxId);
				}
			};
		} else { // No network connection, load data from SQLite
			Log.d("DEBUG", "Detected we are offline");
			dataLoader = new ILoadData() {
				@Override
				public void getMoreTweets(String screenName, String sinceId, String maxId) {
					loadMoreDataFromSql(screenName, sinceId, maxId);
				}
			};
		}

		listView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				String sinceId = getSinceId();
				Log.d("DEBUG", "OnRefresh is being called");
				Log.d("DEBUG", "SinceId is: " + sinceId);
				dataLoader.getMoreTweets(getScreenName(), sinceId, null);
			}
		});

		listView.setOnScrollListener(new EndlessScrollListener() {
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				String maxId = getMaxId();
				Log.d("DEBUG", "Page: " + page + " totalItemsCount: " + totalItemsCount + "currentMaxId: " + maxId);
				dataLoader.getMoreTweets(getScreenName(), null, maxId);
			}
		});


		// Log.d("DEBUG", "Adding more tweets from network for the first time");
		// adapter.addAll(Tweet.fromJson(jsonTweets));
		// Log.d("DEBUG", "Adding more tweets from network not first time");

	}

	protected abstract void loadMoreDataFromApi(String screenName, String sinceId, String maxId);

	protected abstract void loadMoreDataFromSql(String screenName, String sinceId, String maxId);

	protected abstract String getScreenName();
	
	private String getSinceId() {
		if (adapter.getCount() > 0) {
			Tweet t = adapter.getItem(0);
			String id = t.getTweetId();
			Log.d("DEBUG", "Got since id" + id);
			return id;
		} else {
			return "0";
		}
	}

	private String getMaxId() {
		int length = adapter.getCount();
		String id;
		if (length > 0) {
			Tweet t = adapter.getItem(length - 1);
			// Handle case where maxId is inclusive
			long tempId = Long.parseLong(t.getTweetId()) - 1;
			id = Long.toString(tempId);
		} else {
			id = null;
		}
		Log.d("DEBUG", "Got max id" + id);
		return id;
	}

	public TweetsAdapter getAdapter() {
		return adapter;
	}
	
	public void addTweetToAdapter(Tweet t, int offset) {
		adapter.insert(t, offset);
	}
	
	public void markRefreshComplete() {
		listView.onRefreshComplete();
		getActivity().setProgressBarIndeterminateVisibility(false);
	}
}
