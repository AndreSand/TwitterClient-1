package com.codepath.apps.twitterclient.activities;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.SearchView;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.fragments.ComposeTweetDialog;
import com.codepath.apps.twitterclient.fragments.ComposeTweetDialog.ComposeTweetDialogListener;
import com.codepath.apps.twitterclient.fragments.HomeTimelineFragment;
import com.codepath.apps.twitterclient.fragments.MentionsFragment;
import com.codepath.apps.twitterclient.fragments.TweetsListFragment;
import com.codepath.apps.twitterclient.models.Tweet;

public class Tweets extends FragmentActivity implements
		ComposeTweetDialogListener, TabListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); 
		setContentView(R.layout.activity_timeline);

		setupNavigationTabs();
		handleIntent(getIntent());

	}

	private void setupNavigationTabs() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(true);
		Tab tabHome = actionBar.newTab()
				.setTag("HomeTimelineFragment")
				.setIcon(R.drawable.ic_action_home)
				.setTabListener(this);
		
		Tab tabMentions = actionBar.newTab()
				.setTag("MentionsTimelineFragment")
				.setIcon(R.drawable.ic_action_mention)
				.setTabListener(this);
		
		actionBar.addTab(tabHome);
		actionBar.addTab(tabMentions);
		actionBar.selectTab(tabHome);

	}
	public void handleComposeTweet() {
		showComposeTweetDialog();
	}

	private void showComposeTweetDialog() {
		FragmentManager fm = this.getFragmentManager();
		
		ComposeTweetDialog composeTweetDialog = ComposeTweetDialog.newInstance(null, null);
		composeTweetDialog.show(fm, "fragment_compose_tweet");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.timeline, menu);
		// Associate searchable configuration with the SearchView
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.miSearch).getActionView();
		SearchableInfo si = searchManager.getSearchableInfo(getComponentName());
		searchView.setSearchableInfo(si);
		
		return true;
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onProfileView(MenuItem item) {
		Intent i = new Intent(this, ProfileActivity.class);
		i.putExtra(ProfileActivity.USER_SCREENNAME_EXTRA, "nickaiwazian");
		startActivity(i);
	}
	
	public void onComposeTweet(MenuItem item) {
		Log.d("DEBUG", "click compose");
		handleComposeTweet();

	}
	
	@Override
	public void onPostTweet(Tweet tweet) {
		TweetsListFragment fragment = (TweetsListFragment) getFragmentManager().findFragmentById(R.id.frame_container);
	    fragment.addTweetToAdapter(tweet, 0);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		Log.d("DEBUG", "Got new intent");
		super.onNewIntent(intent);      
	    setIntent(intent);
	    handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		Log.d("DEBUG", "Got intent");
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			Log.d("DEBUG", "Got search");

			String query = intent.getStringExtra(SearchManager.QUERY);
			Intent i = new Intent(this, SearchActivity.class);
			i.putExtra(SearchActivity.SEARCH_QUERY_EXTRA, query);
			startActivity(i);
		}
	}

	
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		FragmentManager fm = getFragmentManager();
		FragmentTransaction fts = fm.beginTransaction();
		if (tab.getTag() == "HomeTimelineFragment") {
			fts.replace(R.id.frame_container, new HomeTimelineFragment());
		} else {
			fts.replace(R.id.frame_container, new MentionsFragment());			
		}
		fts.commit();
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

}
