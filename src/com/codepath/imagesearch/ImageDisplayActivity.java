package com.codepath.imagesearch;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ShareActionProvider;

import com.loopj.android.image.SmartImageView;

public class ImageDisplayActivity extends Activity {
	private ImageResult current;
	private ArrayList<ImageResult> results;
	private int position;
	
	private SmartImageView ivResult;
	
	private ShareActionProvider miShareAction;
	
	private static final int SWIPE_MIN_DISTANCE = 50;
    private static final int SWIPE_THRESHOLD_VELOCITY = 10;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_display);
		
		Intent i = getIntent();
		results = (ArrayList<ImageResult>) i.getSerializableExtra("results");
		position = i.getIntExtra("position", 0);
		current = results.get(position);
		ivResult = (SmartImageView) findViewById(R.id.ivResult);
		
		final GestureDetector gdt = new GestureDetector(this, new GestureListener());
		ivResult.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(final View view, final MotionEvent event) {
				gdt.onTouchEvent(event);
				return true;
			}
		});
		
		ivResult.setImageUrl(current.getFullUrl());
		
		ActionBar ab = getActionBar();
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME);
	}
	
	private class GestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				// Right to left
				if(position < (results.size() - 1)) {
					position++;
					current = results.get(position);
					ivResult.setImageUrl(current.getFullUrl());
					setupShareAction();
					return false; 
				}
			}
			else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				// Left to right
				if(position > 0) {
					position--;
					current = results.get(position);
					ivResult.setImageUrl(current.getFullUrl());
					setupShareAction();
					return false; 
				}
			}
			return false;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.image_display, menu);
	    MenuItem item = menu.findItem(R.id.menu_item_share);
	    miShareAction = (ShareActionProvider) item.getActionProvider();
	    setupShareAction();
	    return true;
	}
	
	public void setupShareAction() {
	    Intent shareIntent = new Intent();
	    shareIntent.setAction(Intent.ACTION_SEND);
	    shareIntent.setType("text/plain");
	    shareIntent.putExtra(Intent.EXTRA_TEXT, current.getFullUrl());
	    miShareAction.setShareIntent(shareIntent);
	}
}
