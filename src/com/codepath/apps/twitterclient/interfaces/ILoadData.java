package com.codepath.apps.twitterclient.interfaces;

public interface ILoadData {

	public void getMoreTweets(String screenName, String sinceId, String maxId);

}
