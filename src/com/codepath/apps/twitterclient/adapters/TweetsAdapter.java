package com.codepath.apps.twitterclient.adapters;

import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.activities.ProfileActivity;
import com.codepath.apps.twitterclient.core.TwitterClientApp;
import com.codepath.apps.twitterclient.fragments.ComposeTweetDialog;
import com.codepath.apps.twitterclient.helpers.TwitterClient;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TweetsAdapter extends ArrayAdapter<Tweet>{

	Typeface mediumType;
	int favoriteColor = Color.parseColor("#EE4000");
	int retweetColor = Color.parseColor("#00ff00");
	int defaultColor = Color.parseColor("#000000");
	TwitterClient restClient;
	
	public TweetsAdapter(Context context, List<Tweet> tweets) {
		super(context, 0, tweets);		
		mediumType = Typeface.createFromAsset(context.getAssets(), "fonts/HelveticaNeueLTStd-Md.otf");
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.tweet_item, null);
		}
		restClient = TwitterClientApp.getRestClient();
		
		// Get info
		final Tweet tweet = getItem(position);
		final User user = tweet.getUser();

		// Get views
		TextView nameView = (TextView)view.findViewById(R.id.tvName);
		ImageView imageView = (ImageView) view.findViewById(R.id.ivProfile);
		TextView tweetAgeView = (TextView)view.findViewById(R.id.tvTweetAge);
		TextView bodyView = (TextView) view.findViewById(R.id.tvBody);
		ImageView replyView = (ImageView) view.findViewById(R.id.ivReply);
		final TextView retweetCountView = (TextView) view.findViewById(R.id.tvRetweetCount);
		final TextView favoriteCountView = (TextView) view.findViewById(R.id.tvFavoriteCount);
		ImageView retweetImageView = (ImageView) view.findViewById(R.id.ivRetweet);
		ImageView favoriteImageView = (ImageView) view.findViewById(R.id.ivFavorite);
		
		imageView.setTag(tweet.getUser().getScreenName());
		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String userName = (String)v.getTag();				
				Log.d("DEBUG", "Got click from: " + userName);
				Intent i = new Intent(v.getContext(), ProfileActivity.class);
				i.putExtra(ProfileActivity.USER_SCREENNAME_EXTRA, userName);
				v.getContext().startActivity(i);
			}
		});
		
		ImageLoader.getInstance().displayImage(tweet.getUser().getProfileImageUrl(), imageView);
		
		String formattedName = "<b>" + tweet.getUser().getName() + "</b>" + " <small><font color='#777777'>@" + tweet.getUser().getScreenName() + "</font></small>";
		nameView.setTypeface(mediumType);
		nameView.setText(Html.fromHtml(formattedName));
		
		Date createdDate = tweet.getCreatedDate();
		String d = (String) DateUtils.getRelativeDateTimeString(getContext(), createdDate.getTime(), DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0);

		tweetAgeView.setText(truncateFormattedDate(d));
		bodyView.setText(Html.fromHtml(tweet.getBody()));		
		replyView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentActivity activity = (FragmentActivity)(getContext());
				FragmentManager fm = activity.getFragmentManager();

				ComposeTweetDialog composeTweetDialog = ComposeTweetDialog.newInstance(user, tweet.getTweetId());
				composeTweetDialog.show(fm, "fragment_compose_tweet");				
			}
		});
		
		if (tweet.isFavorited()) {
			favoriteCountView.setTextColor(favoriteColor);
		}
		else {
			favoriteCountView.setTextColor(defaultColor);
		}
		
		favoriteImageView.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				if (tweet.isFavorited()) {
					restClient.postFavoriteCreate(tweet.getTweetId(), new JsonHttpResponseHandler() {
						@Override
						public void onSuccess(JSONObject jsonTweet) {
							Log.d("DEBUG", "Got successful response from favoriting");
						}
						
						@Override
						public void onFailure(Throwable arg0) {
							Log.d("DEBUG", "Got UNsuccessful post response");
						}			
					});	
					favoriteCountView.setTextColor(defaultColor);			
					incrementTextViewCount(favoriteCountView, -1);
				}
				else {
					restClient.postFavoriteDestroy(tweet.getTweetId(), new JsonHttpResponseHandler() {
						@Override
						public void onSuccess(JSONObject jsonTweet) {
							Log.d("DEBUG", "Got successful response from unfavoriting");
						}
						@Override
						public void onFailure(Throwable arg0) {
							Log.d("DEBUG", "Got UNsuccessful post response");
						}			
					});	
					favoriteCountView.setTextColor(favoriteColor);			
					incrementTextViewCount(favoriteCountView, 1);					
				}
			}
		});

		if (tweet.isRetweeted()) {
			retweetCountView.setTextColor(retweetColor);			
		}
		else {
			retweetCountView.setTextColor(defaultColor);			

			retweetImageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					restClient.postRetweet(tweet.getTweetId(), new JsonHttpResponseHandler() {
						@Override
						public void onSuccess(JSONObject jsonTweet) {
							Log.d("DEBUG", "Got successful response from retweeting");
						}
							
						@Override
						public void onFailure(Throwable arg0) {
							Log.d("DEBUG", "Got UNsuccessful post response");
						}			
					});	
					incrementTextViewCount(retweetCountView, 1);
					retweetCountView.setTextColor(retweetColor);			
				}
			});
		}
		retweetCountView.setText(Integer.toString(tweet.getRetweetCount()));
		favoriteCountView.setText(Integer.toString(tweet.getFavoriteCount()));
		
		return view;
	}
	
		
	private void incrementTextViewCount(TextView tv, int value) {
		int currentCount = Integer.parseInt(tv.getText().toString());
		currentCount += value;
		tv.setText(Integer.toString(currentCount));
	}
	
	// Simple implementation.  Fix this later to be more generic
	private String truncateFormattedDate(String s) {
		String result = s;
		if (s.startsWith("in 0 seconds")) {
			result = "0s";
		}
		else {
			String[] parts = s.split(" ");
			if (parts.length > 1) {
				result = parts[0] + parts[1].charAt(0);
			}
		}
		return result;
	}
}
