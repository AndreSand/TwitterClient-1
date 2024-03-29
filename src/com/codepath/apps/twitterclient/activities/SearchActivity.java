package com.codepath.apps.twitterclient.activities;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.fragments.SearchFragment;
import com.codepath.apps.twitterclient.fragments.TweetsListFragment;

public class SearchActivity extends Activity implements TweetsListFragment.OnNetworkRequestEvent {

	public static final String SEARCH_QUERY_EXTRA = "query";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		Log.d("DEBUG", "Created searchactivity");

		String query = getIntent().getExtras().getString(SEARCH_QUERY_EXTRA);
		
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		SearchFragment fragment = SearchFragment.newInstance(query);
		ft.replace(R.id.frame_container_search, fragment);
		ft.commit();

	}



	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);

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




	@Override
	public void onNetworkRequestInitiated() {
		setProgressBarIndeterminateVisibility(true);

		
	}




	@Override
	public void onNetworkRequestCompleted() {
		setProgressBarIndeterminateVisibility(false);
	}


}
