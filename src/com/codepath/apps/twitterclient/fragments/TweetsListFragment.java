package com.codepath.apps.twitterclient.fragments;

import java.util.ArrayList;
import java.util.Comparator;

import android.app.Activity;
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
import com.codepath.apps.twitterclient.models.Tweet;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public abstract class TweetsListFragment extends Fragment {

	private TweetsAdapter adapter;
	private ArrayList<Tweet> tweets;
	private PullToRefreshListView listView;
	protected TwitterClient twitterClient;
	private OnNetworkRequestEvent listener;


	@Override
	public void onAttach(Activity activity) {
	    super.onAttach(activity);
	    if (activity instanceof OnNetworkRequestEvent) {
	        listener = (OnNetworkRequestEvent) activity;
	    } else {
	        throw new ClassCastException(activity.toString()
	            + " must implement TweetsListFragment.OnNetworkRequestEvent listener");
	    }
	 }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tweets = new ArrayList<Tweet>();
		adapter = new TweetsAdapter(getActivity(), tweets);	
		twitterClient = TwitterClientApp.getRestClient();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_tweets_list, container, false); 
		listView = (PullToRefreshListView) v.findViewById(R.id.lvTweets);
		listView.setAdapter(adapter);
		
		listView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				String sinceId = getSinceId();
				Log.d("DEBUG", "OnRefresh is being called");
				Log.d("DEBUG", "SinceId is: " + sinceId);
				getActivity().setProgressBarIndeterminateVisibility(true);
				loadMoreDataFromApi(sinceId, null);
			}
		});

		listView.setOnScrollListener(new EndlessScrollListener() {
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				String maxId = getMaxId();
				Log.d("DEBUG", "Page: " + page + " totalItemsCount: " + totalItemsCount + "currentMaxId: " + maxId);
				listener.onNetworkRequestInitiated();
				loadMoreDataFromApi(null, maxId);
			}
		});

		return v;
	}

	// Define the events that the fragment will use to communicate
	public interface OnNetworkRequestEvent {
		public void onNetworkRequestInitiated();
	    public void onNetworkRequestCompleted();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		//loadMoreDataFromApi(getSinceId(), maxId);
				
	}

	protected abstract void loadMoreDataFromApi(String sinceId, String maxId);

	
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
		listener.onNetworkRequestCompleted();
	}
	
}

class TweetComparator implements Comparator<Tweet> {
    @Override
    public int compare(Tweet a, Tweet b) {
    	Long aLong = Long.parseLong(a.getTweetId());
    	Long bLong = Long.parseLong(b.getTweetId());
        return bLong.compareTo(aLong);
    }
}
