package com.ekansh.snapdealimg;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

public class ShowPic extends Activity {
	
	private final String INTENT_NAME = "PicShow";
	private ImageView img;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.picdisplay);
		img =(ImageView) findViewById(R.id.DisplayPic);
		Intent i = getIntent();
		String url = i.getStringExtra(INTENT_NAME);
		UrlImageViewHelper.setUrlDrawable(img, url);
	}
}
