package com.codepath.apps.twitterclient.models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "Tweets")
public class Tweet extends Model {
	@Column(name = "remote_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)	
	public String tweetId;
	@Column(name="userId")
	public long userId;
	@Column(name="body")
	public String body;
	@Column(name="favorited")
	public boolean favorited;
	@Column(name="retweeted")
	public boolean retweeted;
	@Column(name="createdDate")
	public String createdDate;
	
	public int retweetCount;
	public int favoriteCount;
	
	private User user;
	
	public User getUser() {
		//Log.d("DEBUG", "Current user is: " + user);
		// Workaround for an issue with not persisting foreign keys properly in SQLite
		if (user == null) {
			user = User.getUser(userId);
		}
		return user;
	}	
	
	public String getBody() {
		return body;
	}
	public String getTweetId() {
		return tweetId;
	}
	public boolean isFavorited() {
		return favorited;
	}
	public boolean isRetweeted() {
		return retweeted;
	}
	
	public int getFavoriteCount() {
		return favoriteCount;
	}
	
	public int getRetweetCount() {
		return retweetCount;
	}
	
	public Date getCreatedDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy", Locale.getDefault());
		Date d = null;
		try {
			d = sdf.parse(createdDate);
		} catch(java.text.ParseException e) {
			e.printStackTrace();
			//Log.d("DEBUG", "Unable to parse date: " + createdDate);
		}
		return d;
	}

	public static Tweet fromJson(JSONObject jsonObject) {
		Tweet tweet = new Tweet();
		try {
			tweet.tweetId = jsonObject.getString("id");
			tweet.body = jsonObject.getString("text");
			tweet.favorited = jsonObject.getBoolean("favorited");
			tweet.retweeted = jsonObject.getBoolean("retweeted");
			tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
			tweet.userId = tweet.user.getUserId();
			tweet.createdDate = jsonObject.getString("created_at");
			tweet.favoriteCount = jsonObject.getInt("favorite_count");
			tweet.retweetCount = jsonObject.getInt("retweet_count");
			//Log.d("DEBUG", "Saving tweet: " + tweet);
			//tweet.save();
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return tweet;
	}
	
	public static ArrayList<Tweet> fromJson(JSONArray jsonArray) {
		ArrayList<Tweet> tweets = new ArrayList<Tweet>(jsonArray.length());
	
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject tweetJson = null;
			try {
				tweetJson = jsonArray.getJSONObject(i);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			
			Tweet tweet = Tweet.fromJson(tweetJson);
			if (tweet != null) {
				tweets.add(tweet);
			}
		}
		return tweets;
	}
	
	public Tweet() {
		super();
	}
	
	@Override
	public String toString() {
		return String.format("Tweet id: %s, Tweet user: %s, Tweet body: %s", tweetId, user, body);	
	}
	public static List<Tweet> getTweets(String sinceId, String maxId) {
		//Log.d("DEBUG", "About to query all tweets from SQLite with sinceId: " + sinceId + " and maxId: " + maxId);
		if (sinceId != null) {
			//Log.d("DEBUG", "SinceId query");
			return new Select()
				.from(Tweet.class)
				.where("remote_id > ?", sinceId)
				.orderBy("createdDate DESC")
				.execute();
		}
		else if (maxId != null) {
			//Log.d("DEBUG", "MaxId query");
			return new Select()
				.from(Tweet.class)
				.where("remote_id < ?", maxId)
				.orderBy("createdDate DESC")
				.execute();
		}
		else {
			//Log.d("DEBUG", "ALL query");
			return new Select()
			.from(Tweet.class)
			.orderBy("createdDate DESC")
			.execute();			
		}
	}
}
