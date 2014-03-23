package com.codepath.imagesearch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.loopj.android.image.SmartImageView;

public class ImageDisplayActivity extends Activity {
	private ImageResult result;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_display);
		
		Intent i = getIntent();
		result = (ImageResult) i.getSerializableExtra("result");
		SmartImageView ivResult = (SmartImageView) findViewById(R.id.ivResult);
		ivResult.setImageUrl(result.getFullUrl());
	}
}
