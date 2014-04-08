package com.codepath.apps.twitterclient.fragments;

import org.json.JSONObject;

import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.twitterclient.R;
import com.codepath.apps.twitterclient.core.TwitterClientApp;
import com.codepath.apps.twitterclient.helpers.TwitterClient;
import com.codepath.apps.twitterclient.models.Tweet;
import com.codepath.apps.twitterclient.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ComposeTweetDialog extends DialogFragment {
	private EditText etTweetText;
	private TextView tvCharactersRemaining;
	private Button btnTweet;
	private Button btnCancel;
	private TwitterClient restClient;
	private TextView tvUserName;
	private ImageView ivProfileImage;
	private User user;
	private String replyToId;

	public ComposeTweetDialog() {
	}

	public interface ComposeTweetDialogListener {
		void onPostTweet(Tweet tweet);
	}

	public static ComposeTweetDialog newInstance(User user, String replyToId) {
		ComposeTweetDialog fragment = new ComposeTweetDialog();
		fragment.user = user;
		fragment.replyToId = replyToId;
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_compose_tweet, container);
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		tvCharactersRemaining = (TextView) view
				.findViewById(R.id.tvCharactersRemaining);
		etTweetText = (EditText) view.findViewById(R.id.etTweetText);
		btnTweet = (Button) view.findViewById(R.id.btnTweet);
		restClient = TwitterClientApp.getRestClient();
		ivProfileImage = (ImageView) view.findViewById(R.id.ivProfileImage);
		tvUserName = (TextView) view.findViewById(R.id.tvUserName);

		restClient.getUserInfo("nickaiwazian", new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject jsonUser) {
				User u = User.fromJson(jsonUser);

				ImageLoader.getInstance().displayImage(u.getProfileImageUrl(),
						ivProfileImage);

				tvUserName.setText(u.getScreenName());
				String formattedName = "<b>" + u.getName() + "</b>"
						+ " <br><small><font color='#777777'>@"
						+ u.getScreenName() + "</font></small>";
				tvUserName.setText(Html.fromHtml(formattedName));
			}

			@Override
			public void onFailure(Throwable arg0) {
				Log.d("DEBUG", "Got UNsuccessful user info");
			}
		});

		btnTweet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String tweetText = etTweetText.getText().toString();
				restClient.postTweet(tweetText, replyToId,
						new JsonHttpResponseHandler() {
							@Override
							public void onSuccess(JSONObject jsonTweet) {

								Tweet t = Tweet.fromJson(jsonTweet);
								// Log.d("DEBUG", "Tweets: " + t.toString());
								ComposeTweetDialogListener listener = (ComposeTweetDialogListener) getActivity();
								listener.onPostTweet(t);
								dismiss();
							}

							@Override
							public void onFailure(Throwable arg0) {
								Log.d("DEBUG", "Got UNsuccessful post response");
							}
						});
			}
		});
		btnCancel = (Button) view.findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		btnTweet.setEnabled(false);
		etTweetText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				int inputLength = s.length();

				if (inputLength > 0) {
					btnTweet.setEnabled(true);
				} else {
					btnTweet.setEnabled(false);
				}
				int remainingCount = 140 - inputLength;
				tvCharactersRemaining.setText(Integer.toString(remainingCount));
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		String text = "";
		if (user != null) {
			text = "@" + user.screenName + " ";
			etTweetText.setText(text);
		}

		etTweetText.requestFocus();
		etTweetText.setSelection(text.length());
		getDialog().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		return view;
	}
}
