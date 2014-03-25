package com.codepath.imagesearch;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
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
	
	private String currentQuery = "";
	private int currentIndex = 0;
	
	private boolean loading = false;
	
	private EditText etSearch;
	private Button btSearch;
	private Button btFilter;
	
	private GridView gvResults;
	private ArrayList<ImageResult> imageResults = new ArrayList<ImageResult>();
	private ImageResultArrayAdapter imageAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		setupActionbar();
		setupViews();
	}
	
	private void setupViews() {		
		gvResults = (GridView) findViewById(R.id.gvResults);
		imageAdapter = new ImageResultArrayAdapter(this, imageResults);
		gvResults.setAdapter(imageAdapter);
		gvResults.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View parent, int position,
					long rowId) {
				Intent i = new Intent(getApplicationContext(), ImageDisplayActivity.class);
				//ImageResult imageResult = imageResults.get(position);
				i.putExtra("results", imageResults);
				i.putExtra("position", position);
				startActivity(i);
			}
			
		});
		
		gvResults.setOnScrollListener(new OnScrollListener() {
		    @Override
		    public void onScrollStateChanged(AbsListView view, int scrollState) {

		    }

		    @Override
		    public void onScroll(AbsListView view, int firstVisibleItem,
		            int visibleItemCount, int totalItemCount) {
		        int lastInScreen = firstVisibleItem + visibleItemCount;
		        if ((lastInScreen == totalItemCount) && !(loading)) {
		                searchMore(view);
		        }
		    }
		});
	}
	
	private void setupActionbar() {
		ActionBar actionBar = getActionBar();
	    // add the custom view to the action bar
	    actionBar.setCustomView(R.layout.search_bar);
	    
	    btSearch = (Button) actionBar.getCustomView().findViewById(R.id.btSearch);
	    btSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onSearch(v);
			}
		});
	    
	    btFilter = (Button) actionBar.getCustomView().findViewById(R.id.btFilter);
	    btFilter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onFilter(v);
			}
		});
	    btFilter.setVisibility(View.GONE);
	    
	    etSearch = (EditText) actionBar.getCustomView().findViewById(R.id.etSearch);
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
	    etSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				btSearch.setVisibility(View.VISIBLE);
    			btFilter.setVisibility(View.GONE);
			}
		});
	    
	    Button btReset = (Button) actionBar.getCustomView().findViewById(R.id.btReset);
	    btReset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onReset(v);
			}
		});
	    
	    actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);// | ActionBar.DISPLAY_SHOW_HOME);
	}
	
	public void onSearch(View v) {
		btSearch.setVisibility(View.GONE);
		btFilter.setVisibility(View.VISIBLE);
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
		
		Log.d("debug", sizeParam + colorParam + typeParam + siteParam + "&q=" + Uri.encode(query));
		currentQuery = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&rsz=8" + sizeParam + colorParam + typeParam + siteParam + "&q=" + Uri.encode(query);
		
		loading = true;
		
		client.get(currentQuery + "&start=0",
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
					loading = false;
				}
			});
		
		currentIndex = 7; 
	}
	
	public void searchMore(View v) {
		if(currentIndex > 56) {
			return;
		}
		AsyncHttpClient client = new AsyncHttpClient();
		loading = true;
		client.get(currentQuery + "&start=" + currentIndex,
			new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(JSONObject response) {
					JSONArray imageJsonResults = null;
					try {
						imageJsonResults = response.getJSONObject("responseData").getJSONArray("results");
						imageAdapter.addAll(ImageResult.fromJSONArray(imageJsonResults));
					}
					catch(JSONException e) {
						e.printStackTrace();
					}
					loading = false;
				}
			});
		
		currentIndex += 8; 
	}
	
	/*public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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
		currentQuery = "";
		
		size = "";
		color = "";
		type = "";
		site = "";
		
		loading = false;
		
		imageResults.clear();
		imageAdapter.notifyDataSetChanged();
	}
}
