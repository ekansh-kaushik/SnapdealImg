package com.ekansh.snapdealimg;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;


import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends Activity {

	private MyAdapter mAdapter;
 private Button searchB;
  private EditText 	     searchText;
private ListView mListView;
private Integer start = 0,end=8;
private ProgressBar progress;
private boolean click = false;
private Context con = this;
private final String INTENT_NAME = "PicShow";
private ArrayList<String>  urls = new ArrayList<String>(); 

@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		searchB = (Button) findViewById(R.id.search);
	     searchText = (EditText)findViewById(R.id.search_text);	
	     progress =(ProgressBar) findViewById(R.id.progressBar1);
	        mListView = (ListView)findViewById(R.id.results);
	        mAdapter = new MyAdapter(this);
	        mListView.setAdapter(mAdapter);

			progress.setVisibility(progress.GONE);
mListView.setOnItemClickListener(new OnItemClickListener() {

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		String url = mListView.getAdapter().getItem(arg2).toString();
		Intent i = new Intent(MainActivity.this,ShowPic.class);
		i.putExtra(INTENT_NAME, url);
		startActivity(i);
		
		
	}

});
	        mListView.setOnScrollListener(new InfiniteScrollListener() {
				
				@Override
				public void onLoadMore(int page, int totalItemsCount) {
					// TODO Auto-generated method stub
					new LoadPics().execute((Void[])null);
				urls.clear();
				Log.e("scrolled", "scrolled");
				}
			});
	        
  }



	
	


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	
	
	
	}

	private void clearStuff()
	{
		
		//mAdapter.clear();
		Log.e("on click 1", "onclick 1");
	//click=false;
	urls.clear();
	mAdapter.clear();
	start=0;
	end=8;
	}
	private class LoadPics extends AsyncTask<Void, Void, Void>
	{
		

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		progress.setVisibility(progress.VISIBLE);
			//Toast.makeText(con,start.toString() , Toast.LENGTH_SHORT).show();
		
		}
		
		
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Log.e("on click 2", "onclick 2");
	
			try{
			 
             while(start<end)
             {
            	
            	 
            	  DefaultHttpClient client = new DefaultHttpClient();
                  HttpGet get = new HttpGet(String.format("https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=%s&start=%d", Uri.encode(searchText.getText().toString()), start));
                  HttpResponse resp = client.execute(get);
                  HttpEntity entity = resp.getEntity();
                  InputStream is = entity.getContent();
                  final JSONObject json = new JSONObject(readToEnd(is));
                  is.close();
                  final JSONArray results = json.getJSONObject("responseData").getJSONArray("results");
                  for (int i = 0; i < results.length(); i++) {
                      JSONObject result = results.getJSONObject(i);
                      urls.add(result.getString("url"));
                
             }
                  start += results.length();
		
		}
			}
			catch(Exception e)
			{
				e.printStackTrace();

			}
			end=end+(start-1);
			
	return null;	
		
	}

		
	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		progress.setVisibility(progress.GONE);
		
	if(!urls.equals(null))
	{ 
		Log.e("on click 3", "onclick 3");
		for (String url: urls) {
        mAdapter.add(url);
       }
	}
	 
	}
	}
	 
	
	private String readToEnd(InputStream input) throws IOException
	    {
	        DataInputStream dis = new DataInputStream(input);
	        byte[] stuff = new byte[1024];
	        ByteArrayOutputStream buff = new ByteArrayOutputStream();
	        int read = 0;
	        while ((read = dis.read(stuff)) != -1)
        {
	            buff.write(stuff, 0, read);
	        }
	        
	        return new String(buff.toByteArray());
	    }
	
    private class MyAdapter extends ArrayAdapter<String> {

        public MyAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = getLayoutInflater().inflate(R.layout.liststuff, null);

            final ImageView iv = (ImageView)convertView.findViewById(R.id.showImg);

            iv.setAnimation(null);
            // yep, that's it. it handles the downloading and showing an interstitial image automagically.
            UrlImageViewHelper.setUrlDrawable(iv, getItem(position), R.drawable.loading, new UrlImageViewCallback() {
                @Override
                public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url, boolean loadedFromCache) {
                    if (!loadedFromCache) {
                        ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
                        scale.setDuration(300);
                        scale.setInterpolator(new OvershootInterpolator());
                        imageView.startAnimation(scale);
                        Log.e("on click", "onclick");
                    }
                }
            });

            return convertView;
        }
    }

public void onSearch(View v)
{
	click = true;
String search = searchText.getText().toString();	
//urls.clear();
//mAdapter.clear();
start=0;
end=8;
clearStuff();
new LoadPics().execute((Void[])null);
Log.e("on click", "onclick");

}

}
