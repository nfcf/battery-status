package nfcf.BatteryStatus.Activities;

import nfcf.BatteryStatus.R;
import nfcf.BatteryStatus.Classes.Settings;
import nfcf.BatteryStatus.Utils.StringUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class ActSettings extends Activity {
	
	EditText etKey = null;
	EditText etUser = null;
	EditText etPass = null;
	EditText etFeed = null;
	EditText etPebble = null;
	Spinner spCollectDataInterval = null;
	Spinner spSendDataInterval = null;
	CheckBox chkPrivate = null;
	CheckBox chkNotification = null;
	
	/** Called when the activity is first created. */
    @Override 
    public void onCreate(Bundle savedInstanceState) 
    {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.settings); 

    	TextView tvSignup = (TextView) findViewById(R.id.tvSignup);
    	tvSignup.setText(Html.fromHtml(getString(R.string.settingsInfo)));
    	tvSignup.setMovementMethod(LinkMovementMethod.getInstance());
    		
    	etUser = (EditText) findViewById(R.id.etUser);
    	etPass = (EditText) findViewById(R.id.etPass);
    	etKey = (EditText) findViewById(R.id.etKey);
    	etFeed = (EditText) findViewById(R.id.etFeed);
    	etPebble = (EditText) findViewById(R.id.etPebble);
    	spCollectDataInterval = (Spinner) findViewById(R.id.spCollectDataInterval);
    	spSendDataInterval = (Spinner) findViewById(R.id.spSendDataInterval);
    	chkPrivate = (CheckBox) findViewById(R.id.chkPrivate);
    	chkNotification = (CheckBox) findViewById(R.id.chkNotification);
    }

    @Override
    public void onStart() 
    {
    	super.onStart();
    	
    	etUser.setText(Settings.getUser());
    	etPass.setText(Settings.getPass());
    	etKey.setText(Settings.getKey());
    	etFeed.setText(Settings.getFeed());
    	etPebble.setText(Settings.getPebbleNotificationLevels()); 
    	chkPrivate.setChecked(Settings.getPrivate());
    	chkNotification.setChecked(Settings.getShowNotification());
    	
    	// Array of choices
    	String spCollectIntervalValues[] = {"1","3","5","10","15","30"};
    	String spSendIntervalValues[] = {"Only when the phone is awake","5","10","15","30","60","120"};

    	// Application of the Array to the Spinners
    	ArrayAdapter<String> adapterBattery = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, spCollectIntervalValues);
    	adapterBattery.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
    	spCollectDataInterval.setAdapter(adapterBattery);
    	spCollectDataInterval.setSelection(adapterBattery.getPosition(String.valueOf(Settings.getCollectInterval())));
    	
    	ArrayAdapter<String> adapterCosm = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spSendIntervalValues);
    	adapterCosm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
    	spSendDataInterval.setAdapter(adapterCosm);
    	spSendDataInterval.setSelection(adapterCosm.getPosition(String.valueOf(Settings.getSendInterval())));
    	
    	
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	//AppContext.tracker.trackPageView("Settings Screen");
    }
    
    public void btnSaveSettings_onClick(View v)
    {
    	if (isActivityValid()) {
    		Settings.setUser(etUser.getText().toString().trim());
    		Settings.setPass(etPass.getText().toString().trim());
    		Settings.setKey(etKey.getText().toString().trim());
    		Settings.setFeed(etFeed.getText().toString().trim());
    		Settings.setPebbleNotificationLevels(etPebble.getText().toString().trim());
    		Settings.setPrivate(chkPrivate.isChecked());
    		Settings.setShowNotification(chkNotification.isChecked());

    		if (spCollectDataInterval.getSelectedItem() != null) {
    			try {
    				Settings.setCollectInterval(Integer.parseInt(spCollectDataInterval.getSelectedItem().toString()));	
				} catch (Exception e) {
					Settings.setCollectInterval(0);
				}
    		}
    		
    		if (spSendDataInterval.getSelectedItem() != null) {
    			try {
    				Settings.setSendInterval(Integer.parseInt(spSendDataInterval.getSelectedItem().toString()));	
				} catch (Exception e) {
					Settings.setSendInterval(0);
				}
    		}

    		setResult(RESULT_OK);
    		finish();
    	}
    }   
    
    public void btnCancelSettings_onClick(View v)
    {
    	//AppContext.tracker.trackEvent("Button Pressed", "Cancel Settings", null, 0);
    	setResult(RESULT_CANCELED);
    	finish();
    }   
    
    public boolean isActivityValid() {
    	boolean returnValue = false;
    	if (StringUtils.isNullOrBlank(etUser.getText().toString())) {
    		new AlertDialog.Builder(this)
			.setMessage(Html.fromHtml(getString(R.string.userMissing)))
			.setCancelable(false)
			.setNeutralButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						}).show();
    	} else if (StringUtils.isNullOrBlank(etPass.getText().toString()) && StringUtils.isNullOrBlank(etKey.getText().toString())) {
    		new AlertDialog.Builder(this)
			.setMessage(Html.fromHtml(getString(R.string.passOrKeyMissing)))
			.setCancelable(false)
			.setNeutralButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						}).show();
    	} else {
    		returnValue = true;
    	}
    	return returnValue;
    }
}
