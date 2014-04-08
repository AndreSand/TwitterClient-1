package com.codepath.apps.twitterclient.models;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name="Users")
public class User extends Model {
	
	@Column(name = "remote_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
	public long userId;
	@Column(name = "name")
	public String name;
	@Column(name = "screen_name")
	public String screenName;
	@Column(name = "profile_image_url")
	public String profileImageUrl;
	@Column(name = "profile_background_image_url")
	public String profileBackgroundImageUrl;
	@Column(name = "statusesCount")
	public int statusesCount;
	@Column(name = "followersCount")
	public int followersCount;
	//@Column(name = "tagLine")
	public String tagLine;
	@Column(name = "friendsCount")
	public int friendsCount;
	
	public long getUserId() {
		return userId;
	}
	public String getName() {
		return name;
	}
	public String getScreenName() {
		return screenName;
	}
	public String getProfileImageUrl() {
		return profileImageUrl;
	}
	public String getProfileBackgroundImageUrl() {
		return profileBackgroundImageUrl;
	}
	public int getNumTweets() {
		return statusesCount;
	}
	public int getFollowersCount() {
		return followersCount;
	}
	public String getTagline() {
		return tagLine;
	}
	public int getFriendsCount() {
		return friendsCount;
	}
	public static User fromJson(JSONObject jsonObject) {
		User u = new User();
		
		try {
			u.userId = jsonObject.getLong("id");
			u.name = jsonObject.getString("name");
			u.screenName = jsonObject.getString("screen_name");
			u.profileImageUrl = jsonObject.getString("profile_image_url");
			u.profileBackgroundImageUrl = jsonObject.getString("profile_background_image_url");
			u.statusesCount = jsonObject.getInt("statuses_count");
			u.followersCount = jsonObject.getInt("followers_count");
			u.tagLine = jsonObject.getString("description");
			u.friendsCount = jsonObject.getInt("friends_count");
		//	Log.d("DEBUG", "Saving user: " + u);
			//u.save();
			
			//Log.d("DEBUG", "User is now: " + u);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return u;
	}
	
	public static ArrayList<User> fromJson(JSONArray jsonArray) {
		ArrayList<User> users = new ArrayList<User>(jsonArray.length());
		
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject userJson = null;
			try {
				userJson = jsonArray.getJSONObject(i);
			} catch(Exception e) {
				e.printStackTrace();
				continue;
			}
			User user = User.fromJson(userJson);
			if (user != null) {
				users.add(user);
			}
		}
		return users;
	}
	
	public User() {
		super();
	}
	
	public List<Tweet> tweets() {
		return getMany(Tweet.class, "User");
	}
	@Override
	public String toString() {
		return String.format("User name: %s, user id: %s", name, userId);
	}
	public static User getUser(long userId) {
		//Log.d("DEBUG", "About to get user from SQLite with userId: " + userId);
		return new Select()
			.from(User.class)
			.where("remote_id = ?", userId)
			.executeSingle();
	}
}