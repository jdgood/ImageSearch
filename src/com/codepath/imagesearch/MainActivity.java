package com.codepath.imagesearch;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class MainActivity extends Activity {
	public String size = "";
	public String color = "";
	public String type = "";
	public String site = "";
	
	private EditText etSearch;
	private GridView gvResults;
	private ArrayList<ImageResult> imageResults = new ArrayList<ImageResult>();
	private ImageResultArrayAdapter imageAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		setupViews();
		imageAdapter = new ImageResultArrayAdapter(this, imageResults);
		gvResults.setAdapter(imageAdapter);
		gvResults.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View parent, int position,
					long rowId) {
				Intent i = new Intent(getApplicationContext(), ImageDisplayActivity.class);
				ImageResult imageResult = imageResults.get(position);
				i.putExtra("result", imageResult);
				startActivity(i);
			}
			
		});
	}
	
	private void setupViews() {
		etSearch = (EditText) findViewById(R.id.etSearch);
		etSearch.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
						(keyCode == KeyEvent.KEYCODE_ENTER)) {
					onSearch(v);
					return true;
				}
				return false;
			}
		});
		
		gvResults = (GridView) findViewById(R.id.gvResults);
	}
	
	public void onSearch(View v) {
		String query = etSearch.getText().toString();
		if(query.equals("")) {
			return;
		}
		/*if(!isOnline()) {
			Toast.makeText(this,  "Please connect to the internet", Toast.LENGTH_SHORT).show();
			return;
		}*/
		Toast.makeText(this,  "Searching for " + query,  Toast.LENGTH_SHORT).show();
		AsyncHttpClient client = new AsyncHttpClient();
		
		String sizeParam = "";
		if(!size.equals("")) {
			if(size.equals("small")) {
				sizeParam = "&imgsz=icon";
			}
			else if(size.equals("medium")) {
				sizeParam = "&imgsz=" + Uri.encode("small|medium|large|xlarge");
			}
			else if(size.equals("large")) {
				sizeParam = "&imgsz=xxlarge";
			}
			else if(size.equals("extra-large")) {
				sizeParam = "&imgsz=huge";
			}
		}
		
		String colorParam = "";
		if(!color.equals("")) {
			colorParam = "&imgcolor=" + color;
		}
		
		String typeParam = "";
		if(!type.equals("")) {
			typeParam = "&imgtype=" + type;
		}
		
		String siteParam = "";
		if(!site.equals("")) {
			siteParam = "&as_sitesearch=" + Uri.encode(site);
		}
		
		String test = sizeParam + colorParam + typeParam + siteParam + "&q=" + Uri.encode(query);
		
		client.get("https://ajax.googleapis.com/ajax/services/search/images?v=1.0&rsz=8&start=0" + sizeParam + colorParam + typeParam + siteParam + "&q=" + Uri.encode(query),
			new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(JSONObject response) {
					JSONArray imageJsonResults = null;
					try {
						imageJsonResults = response.getJSONObject("responseData").getJSONArray("results");
						imageResults.clear();
						imageAdapter.addAll(ImageResult.fromJSONArray(imageJsonResults));
					}
					catch(JSONException e) {
						e.printStackTrace();
					}
				}
			});
	}
	
	/*public boolean isOnline() {
		ConnectivityManager cm =
				(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}*/
	
	public void onFilter(View v) {
		DialogFragment newFragment = new FilterDialog();
	    newFragment.show(getFragmentManager(), "filters");
	}
	
	public void onReset(View v) {
		etSearch.setText("");
		
		size = "";
		color = "";
		type = "";
		site = "";
		
		imageResults.clear();
		imageAdapter.notifyDataSetChanged();		
	}
}
