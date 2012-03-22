package nfcf.BatteryStatus.Activities;

import nfcf.BatteryStatus.AppContext;
import nfcf.BatteryStatus.Classes.PachubeAPI;
import nfcf.BatteryStatus.Classes.Settings;
import nfcf.BatteryStatus.Services.ServOtherData;
import nfcf.BatteryStatus.Services.ServPachube;
import nfcf.BatteryStatus.Utils.PhoneUtils;
import nfcf.BatteryStatus.Utils.StringUtils;

import nfcf.BatteryStatus.R;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class ActMain extends Activity {
	
	private ImageView ivBattery = null;
	private static ActMain instance = null;


    /** Called when the activity is first created. */
    @Override 
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        instance = this;
        ivBattery = (ImageView) findViewById(R.id.ivBattery);
        
    }
    
    @Override
    public void onStart() 
    {
    	super.onStart();
    	
    	IntentFilter filter1 = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(actMainReceiver, filter1);
        IntentFilter filter2 = new IntentFilter(AppContext.FORCE_SYNC_COMPLETED);
        registerReceiver(actMainReceiver, filter2);
    	
    	updateControls();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	AppContext.tracker.trackPageView("Main Screen");
    }
    
    @Override
    public void onDestroy() {
    	AppContext.tracker.stopSession(); 
    	unregisterReceiver(actMainReceiver);
    	super.onDestroy();
    }
    
    public static ActMain getInstance() {
    	return instance;
    }
    
    public void updateControls() {
    	
    	Button btnStartStopServices = (Button) findViewById(R.id.btnStartStopService);
    	Button btnForceSync = (Button) findViewById(R.id.btnForceSync);
    	if (Settings.getServiceStarted()) {
    		//btnStartStopServices.setText(R.string.btnStopServices);
    		btnStartStopServices.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.stop_selector,0,0);
    		btnForceSync.setVisibility(View.VISIBLE);
    	} else {
    		//btnStartStopServices.setText(R.string.btnStartServices);
    		btnStartStopServices.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.start_selector,0,0);
    		btnForceSync.setVisibility(View.GONE);
    	}
    	
    	TextView tvInfoFeed = (TextView) findViewById(R.id.tvInfoFeed);
    	TextView tvTitleFeed = (TextView) findViewById(R.id.tvTitleFeed);  
    	if (StringUtils.isNullOrBlank(Settings.getFeed())) {
    		tvInfoFeed.setVisibility(View.GONE);
    		tvTitleFeed.setText("");
    	} else {
    		tvInfoFeed.setVisibility(Settings.getPrivate() ? View.VISIBLE : View.GONE);
    		tvInfoFeed.setText(Html.fromHtml(String.format(getText(R.string.infoPrivateFeed).toString(), Settings.getUser())));
    		tvInfoFeed.setMovementMethod(LinkMovementMethod.getInstance());
    		tvTitleFeed.setText(Html.fromHtml(String.format(getText(R.string.titleViewFeed).toString(), Settings.getFeed(), Settings.getFeed(), (Settings.getPrivate() ? " *" : ""))));
    		tvTitleFeed.setMovementMethod(LinkMovementMethod.getInstance());
    	}
    }
 
    public void btnForceSync_onClick(View v) {
    	
    	AppContext.tracker.trackEvent("Button Pressed", "Force Sync", null, 0);
    	
    	if (Settings.getServiceStarted()) {
    		final ProgressDialog dialog = ProgressDialog.show(this, "", getString(R.string.pleaseWait), true);
        	
    		Thread t = new Thread(new Runnable(){
    			public void run() {
    				String msg = ServPachube.sendDataPoints();
    				dialog.dismiss();
    				
    				Intent i = new Intent();
            		i.setAction(AppContext.FORCE_SYNC_COMPLETED);
            		i.putExtra("msgToDisplay", msg);
            		AppContext.getContext().sendBroadcast(i);
    			}
            });
            t.start();
    	}
    }
    
    public void btnStartStopServices_onClick(View v) {

    	if (Settings.getServiceStarted()) {

    		AppContext.tracker.trackEvent("Button Pressed", "Stop Services", null, 0);
    		stopService();
    		
    	} else {
    		if (StringUtils.isNullOrBlank(Settings.getKey())){
    			AppContext.tracker.trackEvent("Button Pressed", "Settings", null, 0);
    			Intent myIntent = new Intent(v.getContext(), ActSettings.class);
    			startActivityForResult(myIntent, 0);
    		} else {
    			new AlertDialog.Builder(this)
    			.setTitle(getString(R.string.settings))
				.setMessage(Html.fromHtml(getString(R.string.questionText)))
				.setCancelable(false)
				.setNegativeButton(getString(R.string.changeSettings),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								AppContext.tracker.trackEvent("Button Pressed", "Go To Settings", null, 0);
								Intent myIntent = new Intent(AppContext.getContext(), ActSettings.class);
								startActivityForResult(myIntent, 0);
							}
						})
				.setPositiveButton(getString(R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								AppContext.tracker.trackEvent("Button Pressed", "Start Services", null, 0);
								launchServices();
							}
						}).show();
    		}
    		
    	}
    	
    }   
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
        	if (resultCode == RESULT_OK) {
        		launchServices();
        	}
        }
    }
    
    final Handler guiHandler = new Handler(){
    	/** Gets called on every message that is received */
    	// @Override
    	public void handleMessage(Message msg) {
    		Context ctx = AppContext.getContext();

    		switch (msg.what) {
    		case AppContext.STATUS_WRONG_KEY:
    			Toast.makeText(ctx, ctx.getString(R.string.msgWrongKey), Toast.LENGTH_LONG).show();
    			break;
    		case AppContext.STATUS_WRONG_FEED:
    			Toast.makeText(ctx, ctx.getString(R.string.msgWrongFeed), Toast.LENGTH_LONG).show();
    			break;
    		case AppContext.STATUS_WRONG_KEY_AND_FEED:
    			Toast.makeText(ctx, ctx.getString(R.string.msgWrongKeyAndFeed), Toast.LENGTH_LONG).show();
    			break;
    		default:
    			break;
    		}
    		super.handleMessage(msg);
    	}
    };


protected void launchServices() {
    	
    	final ProgressDialog dialog = ProgressDialog.show(this, "", getString(R.string.pleaseWait), true);
    	
        Thread t = new Thread(new Runnable(){
        	public void run() {
        		AppContext.lastStatusCode = 0;
        		
        		PachubeAPI.PachubeResponse key = null;
        		if (PhoneUtils.isOnline(ActMain.this) && !StringUtils.isNullOrBlank(Settings.getKey())) {
        			//Check if the key in settings exist and has valid permissions (private_access)
        			key = PachubeAPI.checkKey(Settings.getKey(), Settings.getPrivate());
        			if (key != null) {
        				//Overwrites the key value on the Settings. If the key is valid, it stays the same, if it isn't then erase it and create a new one.
        				Settings.setKey(key.getValue());
        				if (StringUtils.isNullOrBlank(key.getValue())) {
        					Message msg = new Message();
        					msg.what = AppContext.STATUS_WRONG_KEY;
                            guiHandler.sendMessage(msg);
        				}
        			}
        		}
        		if (PhoneUtils.isOnline(ActMain.this) && !StringUtils.isNullOrBlank(Settings.getUser()) && !StringUtils.isNullOrBlank(Settings.getPass()) && StringUtils.isNullOrBlank(Settings.getKey())) {
        			//Check if a key already exists
        			key = PachubeAPI.findKey(Settings.getUser(), Settings.getPass(), "Battery Status - Pachube key - " + (Settings.getPrivate() ? "Private" : "Public"));
        			//Create Key if necessary. If key = null, then probably a network error occurred and so there's no need to continue
        			if (key != null && StringUtils.isNullOrBlank(key.getValue())) {
        				key = PachubeAPI.createKey(Settings.getUser(), Settings.getPass(), "Battery Status - Pachube key - " + (Settings.getPrivate() ? "Private" : "Public"), Settings.getPrivate());        		
        			} 
        			if (key != null) {
        				AppContext.lastStatusCode = key.getStatusCode();
        				if (!StringUtils.isNullOrBlank(key.getValue())) Settings.setKey(key.getValue());
        			}
        		} 

        		PachubeAPI.PachubeResponse feed = null;
        		if (PhoneUtils.isOnline(ActMain.this) && !StringUtils.isNullOrBlank(Settings.getFeed())) {
        			//Check if the key in settings exist and has valid permissions (private_access)
        			feed = PachubeAPI.checkFeed(Settings.getUser(), Settings.getPass(), Settings.getFeed(), Settings.getPrivate());
        			if (feed != null) {
        				//Overwrites the feed value on the Settings. If the feed exists, it stays the same, if it isn't then erase it and create a new one.
        				Settings.setFeed(feed.getValue());
        				if (StringUtils.isNullOrBlank(feed.getValue())) {
        					Message msg = new Message();
        					msg.what = AppContext.STATUS_WRONG_FEED;
                            guiHandler.sendMessage(msg);
        				}
        			}
        		}
        		if (PhoneUtils.isOnline(ActMain.this) && !StringUtils.isNullOrBlank(Settings.getKey()) && StringUtils.isNullOrBlank(Settings.getFeed())){
        			//Check if a feed already exists. If feed = null, then probably a network error occurred and so there's no need to continue
        			feed = PachubeAPI.findFeed(Settings.getUser(), Settings.getKey(), "Battery Status - " + Build.MODEL + " - " + Settings.getUser());
        			//Create Feed if necessary
        			if (feed != null && StringUtils.isNullOrBlank(feed.getValue())) {
        				feed = PachubeAPI.createFeed(Settings.getKey(), "Battery Status - " + Build.MODEL + " - " + Settings.getUser(), "Battery information from my Android device", Settings.getPrivate());        		
        			} 
        			if (feed != null) {
        				AppContext.lastStatusCode = feed.getStatusCode();
        				if (!StringUtils.isNullOrBlank(feed.getValue())) Settings.setFeed(feed.getValue());
        			}
        		}

        		dialog.dismiss();
        		
        		Intent i = new Intent();
        		i.setAction(AppContext.START_SERVICES_COMPLETED);
        		AppContext.getContext().sendBroadcast(i);
        		
        	}
        });
        t.start();

    }
 
    
    
    protected void stopService() {   	
    	AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
		alarmManager.cancel(AppContext.pendingBatteryIntent);
		if (Settings.getPachubeInterval() > 0) {
			alarmManager.cancel(AppContext.pendingPachubeIntent);
		} else {
			stopService(new Intent(this, ServPachube.class));
		}
		stopService(new Intent(this, ServOtherData.class));
		
		Settings.setServiceStarted(false);
		Toast.makeText(ActMain.this, R.string.msgStopServices, Toast.LENGTH_LONG).show();
		updateControls();
    }

    private BroadcastReceiver actMainReceiver = new BroadcastReceiver() {

    	@Override
    	public void onReceive(Context context, Intent intent) {
    		Context ctx = AppContext.getContext();
    		
    		if (intent.hasExtra("msgToDisplay")) {
    			Toast.makeText(ctx, intent.getStringExtra("msgToDisplay"), Toast.LENGTH_LONG).show();	
    		} else {
    			int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
    			if (level > 80) {
    				ivBattery.setImageDrawable(ctx.getResources().getDrawable(R.drawable.battery5));
    			} else if (level > 60) {
    				ivBattery.setImageDrawable(ctx.getResources().getDrawable(R.drawable.battery4));
    			} else if (level > 40) {
    				ivBattery.setImageDrawable(ctx.getResources().getDrawable(R.drawable.battery3));
    			} else if (level > 20) {
    				ivBattery.setImageDrawable(ctx.getResources().getDrawable(R.drawable.battery2));
    			} else {
    				ivBattery.setImageDrawable(ctx.getResources().getDrawable(R.drawable.battery1));
    			}
    		}

    	}
    };
    
    
}