package com.codepath.apps.twitterclient.activities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.activities.ComposeTweetDialog.ComposeTweetDialogListener;
import com.codepath.apps.twitterclient.adapters.TweetsAdapter;
import com.codepath.apps.twitterclient.core.TwitterClientApp;
import com.codepath.apps.twitterclient.helpers.TwitterClient;
import com.codepath.apps.twitterclient.interfaces.EndlessScrollListener;
import com.codepath.apps.twitterclient.interfaces.ILoadData;
import com.codepath.apps.twitterclient.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class TimelineActivity extends Activity implements
		ComposeTweetDialogListener {

	PullToRefreshListView lvTweets;
	TweetsAdapter adapter;
	TwitterClient restClient;
	ArrayList<Tweet> tweets;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		restClient = TwitterClientApp.getRestClient();
		lvTweets = (PullToRefreshListView) findViewById(R.id.lvTweets);
		final ILoadData dataLoader;

		// Simple connectivity check for now. Should expand this to receive
		// broadcast intents when network status changes.
		if (restClient.isOnline()) {
			Log.d("DEBUG", "Detected we are online");
			dataLoader = new ILoadData() {
				@Override
				public void getMoreTweets(String sinceId, String maxId) {
					restClient.getHomeTimeline(sinceId, maxId,
							new JsonHttpResponseHandler() {
								@Override
								public void onSuccess(JSONArray jsonTweets) {
									if (tweets == null) {
										tweets = new ArrayList<Tweet>(Tweet.fromJson(jsonTweets));
										adapter = new TweetsAdapter(getBaseContext(), tweets);
										lvTweets.setAdapter(adapter);
										//Log.d("DEBUG", "Adding more tweets from network for the first time");
									} else {
										adapter.addAll(Tweet.fromJson(jsonTweets));
										//Log.d("DEBUG", "Adding more tweets from network not first time");
									}
									lvTweets.onRefreshComplete();
								}

								@Override
								public void onFailure(Throwable arg0) {
									//Log.d("DEBUG", "Got UNsuccessful tweets");
									Toast.makeText(getBaseContext(), "FAIL",
											Toast.LENGTH_LONG).show();
								}
							});
					}
				};
		} else { // No network connection, load data from SQLite
			Log.d("DEBUG", "Detected we are offline");
			dataLoader = new ILoadData() {
				@Override
				public void getMoreTweets(String sinceId, String maxId) {
					List<Tweet> storedTweets = Tweet.getTweets(sinceId, maxId); 
					//Log.d("DEBUG", "SQL: " + storedTweets);
										
					if (tweets == null) {
						tweets = new ArrayList<Tweet>(storedTweets);
						adapter = new TweetsAdapter(getBaseContext(), tweets);
						lvTweets.setAdapter(adapter);
						//Log.d("DEBUG", "Adding more tweets from SQLite for the first time");

					} else {
						adapter.addAll(storedTweets);
						//Log.d("DEBUG", "Adding more tweets from SQLite not first time");
					}
					lvTweets.onRefreshComplete();
				}
			};
		}

		lvTweets.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				//loadMoreDataFromApi(getSinceId(), null);
				dataLoader.getMoreTweets(getSinceId(), null);
			}
		});

		tweets = null;
		dataLoader.getMoreTweets(null, null);
		//Log.d("DEBUG", "Making first call to get tweets");
		lvTweets.setOnScrollListener(new EndlessScrollListener() {
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				String maxId = getMaxId();
				//Log.d("DEBUG", "load more with max id: " + maxId + " page: "
				//		+ page + " totalItemsCount: " + totalItemsCount);
				dataLoader.getMoreTweets(null, maxId);
			}
		});
	}

	public void handleComposeTweet() {
		showComposeTweetDialog();
	}

	private void showComposeTweetDialog() {
		FragmentManager fm = this.getFragmentManager();
		
		ComposeTweetDialog composeTweetDialog = ComposeTweetDialog.newInstance(null);
		composeTweetDialog.show(fm, "fragment_compose_tweet");
	}

	private String getSinceId() {
		if (tweets.size() > 0) {
			Tweet t = tweets.get(0);
			String id = t.getTweetId();
			//Log.d("DEBUG", "Got since id" + id);
			return id;
		} else {
			return "0";
		}
	}

	private String getMaxId() {
		int length = tweets.size();
		String id;
		if (length > 0) {
			Tweet t = tweets.get(length - 1);
			id = t.getTweetId();
		}
		else {
			id = "9999999999"; //HACK - Fix this later
		}
		//Log.d("DEBUG", "Got max id" + id);
		return id;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.timeline, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		handleComposeTweet();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onPostTweet(Tweet tweet) {
		adapter.insert(tweet, 0);
	}
}
