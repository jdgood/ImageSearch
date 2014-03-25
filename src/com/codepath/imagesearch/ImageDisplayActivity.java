package com.codepath.imagesearch;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.loopj.android.image.SmartImageView;

public class ImageDisplayActivity extends Activity {
	private ImageResult current;
	private ArrayList<ImageResult> results;
	private int position;
	
	private SmartImageView ivResult;
	
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
					return false; 
				}
			}
			else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				// Left to right
				if(position > 0) {
					position--;
					current = results.get(position);
					ivResult.setImageUrl(current.getFullUrl());
					return false; 
				}
			}
			return false;
		}
	}
}
