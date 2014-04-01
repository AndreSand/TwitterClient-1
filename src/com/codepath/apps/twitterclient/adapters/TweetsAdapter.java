package com.codepath.apps.twitterclient.adapters;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.models.Tweet;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TweetsAdapter extends ArrayAdapter<Tweet>{

	Typeface lightType;
	Typeface mediumType;
	Typeface romanType;
	
	public TweetsAdapter(Context context, List<Tweet> tweets) {
		super(context, 0, tweets);
		
		lightType = Typeface.createFromAsset(context.getAssets(), "fonts/HelveticaNeueLTStd-Lt.otf");
		mediumType = Typeface.createFromAsset(context.getAssets(), "fonts/HelveticaNeueLTStd-Md.otf");
		romanType = Typeface.createFromAsset(context.getAssets(), "fonts/HelveticaNeueLTStd-Roman.otf");
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.tweet_item, null);
		}
		
		Tweet tweet = getItem(position);
		
		ImageView imageView = (ImageView) view.findViewById(R.id.ivProfile);
		ImageLoader.getInstance().displayImage(tweet.getUser().getProfileImageUrl(), imageView);
		
		TextView nameView = (TextView)view.findViewById(R.id.tvName);
		String formattedName = "<b>" + tweet.getUser().getName() + "</b>" + " <small><font color='#777777'>@" + tweet.getUser().getScreenName() + "</font></small>";
		nameView.setTypeface(mediumType);
		nameView.setText(Html.fromHtml(formattedName));
		
		TextView tweetAgeView = (TextView)view.findViewById(R.id.tvTweetAge);
		Date createdDate = tweet.getCreatedDate();
		String d = (String) DateUtils.getRelativeDateTimeString(getContext(), createdDate.getTime(), DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0);

		tweetAgeView.setText(truncateFormattedDate(d));
		
		TextView bodyView = (TextView) view.findViewById(R.id.tvBody);
		
		bodyView.setText(Html.fromHtml(tweet.getBody()));
		
		return view;
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
