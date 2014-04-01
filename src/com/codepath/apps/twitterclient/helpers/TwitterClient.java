package com.codepath.apps.twitterclient.helpers;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;
import android.net.ConnectivityManager;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; 
    public static final String REST_URL = "https://api.twitter.com/1.1"; 
    public static final String REST_CONSUMER_KEY = "7eygAo5oN0Mk8KgaYrUx9A";
    public static final String REST_CONSUMER_SECRET = "ncnwMqndHYU3Xuy78aImtMSBkcNMOhKBLzAl3rTQ9vs";
    public static final String REST_CALLBACK_URL = "oauth://mytwitterapp"; // Change this (here and in manifest)
    
    public TwitterClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }
    // Handle requests to get more recent tweets (with sinceId) and older tweets (with maxId)
    public void getHomeTimeline(String sinceId, String maxId, AsyncHttpResponseHandler handler) {
    	String url = getApiUrl("statuses/home_timeline.json");
    	RequestParams params = null;
    	if (maxId != null) {
    		params = new RequestParams();
    		params.put("max_id", maxId);
    	}
    	if (sinceId != null) {
    		params = new RequestParams();
    		params.put("since_id", sinceId);
    	}    	
        //Log.d("DEBUG", "About to send request to: " + url + " with params: " + params);
        client.get(url, params, handler);
    }    

    public void postTweet(String status, AsyncHttpResponseHandler handler) {
    	String url = getApiUrl("statuses/update.json");

		RequestParams params = new RequestParams();
		params.put("status", status);
        //Log.d("DEBUG", "About to post tweet: " + url + " with params: " + params);
        client.post(url, params, handler);        
    }

    public void getUserInfo(AsyncHttpResponseHandler handler) {
    	String url = getApiUrl("users/show.json");

		RequestParams params = new RequestParams();
		params.put("screen_name", "nickaiwazian");
        //Log.d("DEBUG", "About to send request to: " + url + " with params: " + params);
		client.get(url, params, handler);
    }

    public boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        
        return connectivityManager.getActiveNetworkInfo() != null && 
           connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }
    
    /* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
     * 	  i.e getApiUrl("statuses/home_timeline.json");
     * 2. Define the parameters to pass to the request (query or body)
     *    i.e RequestParams params = new RequestParams("foo", "bar");
     * 3. Define the request method and make a call to the client
     *    i.e client.get(apiUrl, params, handler);
     *    i.e client.post(apiUrl, params, handler);
     */
}