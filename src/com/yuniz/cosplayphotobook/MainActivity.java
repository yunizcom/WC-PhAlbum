package com.yuniz.cosplayphotobook;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import com.revmob.RevMob;
import com.revmob.RevMobTestingMode;
import com.yuniz.cosplayphotobook.R;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class MainActivity extends Activity {

public static final String PREFS_NAME = "YunizCPISaves";
	
	public int screenWidth = 0;
	public int screenHeight = 0;
	
	private TextView statusTxt;
	private RelativeLayout loader,loadBoard,mainMenu,mainBg;
	
	private Button button2;
	private Button button3;
	private Button button4;
	
	private EditText editText1;
	
	private ImageView imageView1;
	
	private LinearLayout loadPosts;
	
	private String currentURL,pageCodes;
	
	private RevMob revmob;
	
	private int adtimers = 10;
	
	int sdk = 0;
	
	Timer WFT = new Timer();
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		sdk = android.os.Build.VERSION.SDK_INT;
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	    StrictMode.setThreadPolicy(policy);
		
	    Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		
		boolean smallScreen = false;
		try
		{ 
			display.getSize(size); 
			screenWidth = size.x; 
			screenHeight = size.y; 
			smallScreen = false;
		} 
		catch (NoSuchMethodError e) 
		{ 
			screenWidth = display.getWidth(); 
			screenHeight = display.getHeight(); 
			smallScreen = true;
		} 
	    
		statusTxt = (TextView) findViewById(R.id.textView2);
		
		loader = (RelativeLayout) findViewById(R.id.loader);
		loadBoard = (RelativeLayout) findViewById(R.id.loadBoard);
		mainMenu = (RelativeLayout) findViewById(R.id.mainMenu);
		
		mainBg = (RelativeLayout) findViewById(R.id.mainBg);
		
		imageView1 = (ImageView) findViewById(R.id.imageView1);

		button2 = (Button) findViewById(R.id.button2);
		button3 = (Button) findViewById(R.id.button3);
		button4 = (Button) findViewById(R.id.button4);
		
		editText1 = (EditText) findViewById(R.id.editText1);
		
		loadPosts = (LinearLayout) findViewById(R.id.loadPosts);
		
		try 
		{
		    InputStream ims = getAssets().open("bg.jpg");
		    Drawable d = Drawable.createFromStream(ims, null);

		    if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
		    	mainBg.setBackgroundDrawable(d);
		    } else {
		    	mainBg.setBackground(d);
		    }
		    
		    InputStream ims1 = getAssets().open("logo.png");
		    Drawable d1 = Drawable.createFromStream(ims1, null);
		    imageView1.setImageDrawable(d1);
		}
		catch(IOException ex) 
		{
		    return;
		}
		
		button3.setText("< Prev");
		button4.setText("Next >");
		
		double setNewHeight = screenHeight;
		double setNewWidth = screenWidth;
		
		setNewWidth = screenWidth * 0.3;
		button2.setWidth((int)setNewWidth);
		
		setNewWidth = screenWidth * 0.20;
		button3.setWidth((int)setNewWidth);
		button4.setWidth((int)setNewWidth);
		setNewWidth = screenWidth * 0.30;
		editText1.setWidth((int)setNewWidth);
		
		if(!isNetworkAvailable()){
			Toast.makeText(getApplicationContext(), "You need a smooth internet connection before you can use this app." , Toast.LENGTH_LONG).show();
		}
		
		/*----RevMob Ads----*/
		revmob = RevMob.start(this);
//revmob.setTestingMode(RevMobTestingMode.WITH_ADS);
		revmob.showFullscreen(this);
        /*----RevMob Ads----*/
	}

	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	public void loadURLBtn(View v) {
			currentURL = "http://worldcosplay.net/photo/1";

			saveURLValue();
		
			statusTxt.setText("Connecting to server...");
			mainMenu.setVisibility(View.INVISIBLE);
			loader.setVisibility(View.VISIBLE);
			setScanLoadWFT();
	}

	public void loadURLBtn2(View v) {
			currentURL = "http://www.cosplay.com/photo/1000000";
			
			saveURLValue();
		
			statusTxt.setText("Connecting to server...");
			mainMenu.setVisibility(View.INVISIBLE);
			loader.setVisibility(View.VISIBLE);
			setScanLoadWFT();
	}
	
	public void loadOldURLBtn(View v) {
		retriveURLValue();
	
		if(currentURL.length() == 0){
			Toast.makeText(getApplicationContext(), "No previous history found, please start with a new album." , Toast.LENGTH_LONG).show();
		}else{
			//editText1.setText(currentPageNo(currentURL));
			statusTxt.setText("Connecting to server...");
			mainMenu.setVisibility(View.INVISIBLE);
			loader.setVisibility(View.VISIBLE);
			setScanLoadWFT();
		}
	}
	
	public void prevURLBtn(View v) {
		String preURL = currentURL;
		String currentPageNo = editText1.getText().toString();//currentPageNo(currentURL);
		String[] stringSpliter,stringSpliter2,stringSpliter3;
		int numberPage = 0;
		
		stringSpliter = currentURL.split("photo/");
		stringSpliter2 = stringSpliter[0].split("");
		
		stringSpliter3 = currentPageNo.split("/");
		numberPage = Integer.valueOf(stringSpliter3[0]);
		
		if(numberPage > 1){
			numberPage--;
			
			if(stringSpliter2[stringSpliter2.length - 1].equals("/") ){
				preURL = stringSpliter[0] + "photo/" + numberPage + "/";
			}else{
				preURL = stringSpliter[0] + "/photo/" + numberPage + "/";
			}
		}
		
		currentURL = preURL;
		saveURLValue();
		
		statusTxt.setText("Connecting to server...");
		loadBoard.setVisibility(View.INVISIBLE);
		loader.setVisibility(View.VISIBLE);
		setScanLoadWFT();
	}
	
	public void nextURLBtn(View v) {
		String preURL = currentURL;
		String currentPageNo = editText1.getText().toString();//currentPageNo(currentURL);
		String[] stringSpliter,stringSpliter2,stringSpliter3;
		int numberPage = 0;
		
		stringSpliter = currentURL.split("photo/");
		stringSpliter2 = stringSpliter[0].split("");
		
		stringSpliter3 = currentPageNo.split("/");
		numberPage = Integer.valueOf(stringSpliter3[0]);
		
		numberPage++;
		
		if(stringSpliter2[stringSpliter2.length - 1].equals("/") ){
			preURL = stringSpliter[0] + "photo/" + numberPage + "/";
		}else{
			preURL = stringSpliter[0] + "/photo/" + numberPage + "/";
		}
		
		currentURL = preURL;
		saveURLValue();
		
		statusTxt.setText("Connecting to server...");
		loadBoard.setVisibility(View.INVISIBLE);
		loader.setVisibility(View.VISIBLE);
		setScanLoadWFT();
	}

	public String currentPageNo(String url){
		String pageNo = "1";
		String[] stringSpliter;
		
		stringSpliter = url.split("/photo/");
		
		if(stringSpliter.length>1){
			pageNo = stringSpliter[stringSpliter.length - 1];
		}
		
		return pageNo;
	}
	
	public void newBlogBtn(View v) {
		loadBoard.setVisibility(View.INVISIBLE);
		mainMenu.setVisibility(View.VISIBLE);
	}
	
	public String getUrlContents(String url){
	    String content = "";
	    HttpClient hc = new DefaultHttpClient();
	    HttpGet hGet = new HttpGet(url);
	    ResponseHandler<String> rHand = new BasicResponseHandler();
	    try {
	        content = hc.execute(hGet,rHand);
	    } catch (ClientProtocolException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return content;
	}
	
	public void getPhotos(String rawSource){
		String[] stringSpliter,processStr,processStr2,processStr3;
		ArrayList<String> titles = new ArrayList<String>(),urls = new ArrayList<String>(),dates = new ArrayList<String>(),tempImages = new ArrayList<String>(),tempImagesOut = new ArrayList<String>();
		ArrayList<ArrayList<String>> imagesGroup = new ArrayList<ArrayList<String>>();
		
		stringSpliter = rawSource.split("<div id=\"photo_container\">");
		
		if(stringSpliter.length>1){

			for(int a=1;a<stringSpliter.length;a++){
				// process with photo
				processStr = stringSpliter[a].split("</div>");
				processStr = processStr[0].split("<img src=\"");
				processStr = processStr[1].split("\"");
				titles.add(processStr[0].toString());
			}
		
		}else{
		
			stringSpliter = rawSource.split("class=\"center fit\"");
			
			if(stringSpliter.length>1){
				// process with titles
				processStr = stringSpliter[0].split("<img src=\"");
				processStr = processStr[processStr.length - 1].split("\"");
				titles.add(processStr[0].toString());Log.v("debug",processStr[0].toString());
				
			}
			
		}

		//--------------------generate the contents for user-----------------------

		Bitmap bitmap = null;
		loadPosts.removeAllViewsInLayout();

		for(int a=0;a<titles.size();a++){

			try {
				bitmap = BitmapFactory.decodeStream((InputStream)new URL(titles.get(a)).getContent());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			ImageView postImage = new ImageView(this);
			postImage.setImageBitmap(bitmap);
			postImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
			postImage.setBackgroundColor(Color.parseColor("#ffffff"));
			postImage.setPadding(5, 5, 5, 5);
			loadPosts.addView(postImage,new LinearLayout.LayoutParams( 
		            LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

			//bitmap.recycle();
			//Runtime.getRuntime().gc();
			
		}
		
		loader.setVisibility(View.INVISIBLE);
		loadBoard.setVisibility(View.VISIBLE);
		
		if(titles.size() == 0){
			button4.performClick();
		}else{Log.v("debug",adtimers + "");
			if(adtimers >= 10){
				adtimers=1;
				revmob.showPopup(this);
			}else{
				adtimers++;
			}
			
		}
	}
	
	public boolean isImageFormat(String filename){
		boolean isImage = false;
		
		String[] filenameSplit;
		filenameSplit = filename.split(".jpg");
		if(filenameSplit.length > 1){
			isImage = true;
		}else{
			filenameSplit = filename.split(".jpeg");
			if(filenameSplit.length > 1){
				isImage = true;
			}else{
				filenameSplit = filename.split(".gif");
				if(filenameSplit.length > 1){
					isImage = true;
				}else{
					filenameSplit = filename.split(".png");
					if(filenameSplit.length > 1){
						isImage = true;
					}
				}
			}
		}

		return isImage;
	}
	
	public String cleanImageFile(String filename){
		String cleanImageFile = filename;
		
		String[] filenameSplit;
		filenameSplit = filename.split(".jpg");
		if(filenameSplit.length > 1){
			cleanImageFile = filenameSplit[0] + ".jpg";
		}else{
			filenameSplit = filename.split(".jpeg");
			if(filenameSplit.length > 1){
				cleanImageFile = filenameSplit[0] + ".jpeg";
			}else{
				filenameSplit = filename.split(".gif");
				if(filenameSplit.length > 1){
					cleanImageFile = filenameSplit[0] + "gif";
				}else{
					filenameSplit = filename.split(".png");
					if(filenameSplit.length > 1){
						cleanImageFile = filenameSplit[0] + ".png";
					}
				}
			}
		}

		return cleanImageFile;
	}
	
	public void saveURLValue(){
		  SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	      SharedPreferences.Editor editor = settings.edit();
	      editor.putString("oldurl", currentURL);

	      editor.commit();
	}
	
	public void retriveURLValue(){
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		currentURL = settings.getString("oldurl", "");
	}
	
	public void setScanLoadWFT() {
        WFT.schedule(new TimerTask() {          
            @Override
            public void run() {
                WFTTimerMethod();
            }
        }, 500); // 4 seconds delay
    }

    private void WFTTimerMethod() {
        this.runOnUiThread(Timer_Tick);
    }

    private Runnable Timer_Tick = new Runnable() {
        public void run() {
        	pageCodes = getUrlContents( currentURL );
        	statusTxt.setText("Processing, please wait...");
        	setProcessLoadWFT();
        }
    };
    
    public void setProcessLoadWFT() {
        WFT.schedule(new TimerTask() {          
            @Override
            public void run() {
                WFTTimerMethodProcess();
            }
        }, 500); // 4 seconds delay
    }

    private void WFTTimerMethodProcess() {
        this.runOnUiThread(Timer_TickProcess);
    }

    private Runnable Timer_TickProcess = new Runnable() {
        public void run() {
        	editText1.setText(currentPageNo(currentURL).replaceAll("/", ""));
        	getPhotos( pageCodes );
        }
    };
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
