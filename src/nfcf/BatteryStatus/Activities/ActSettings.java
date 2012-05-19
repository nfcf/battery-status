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
	Spinner spBatteryInterval = null;
	Spinner spCosmInterval = null;
	CheckBox chkPrivate = null;
	
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
    	spBatteryInterval = (Spinner) findViewById(R.id.spBatteryInterval);
    	spCosmInterval = (Spinner) findViewById(R.id.spCosmInterval);
    	chkPrivate = (CheckBox) findViewById(R.id.chkPrivate);
    }

    @Override
    public void onStart() 
    {
    	super.onStart();
    	
    	etUser.setText(Settings.getUser());
    	etPass.setText(Settings.getPass());
    	etKey.setText(Settings.getKey());
    	etFeed.setText(Settings.getFeed()); 
    	chkPrivate.setChecked(Settings.getPrivate());
    	
    	// Array of choices
    	String spBatteryValues[] = {"5","10","15","30"};
    	String spCosmValues[] = {"Only when the phone is awake","5","10","15","30","60","120"};

    	// Application of the Array to the Spinners
    	ArrayAdapter<String> adapterBattery = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, spBatteryValues);
    	adapterBattery.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
    	spBatteryInterval.setAdapter(adapterBattery);
    	spBatteryInterval.setSelection(adapterBattery.getPosition(String.valueOf(Settings.getBatteryInterval())));
    	
    	ArrayAdapter<String> adapterCosm = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, spCosmValues);
    	adapterCosm.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
    	spCosmInterval.setAdapter(adapterCosm);
    	spCosmInterval.setSelection(adapterCosm.getPosition(String.valueOf(Settings.getCosmInterval())));
    	
    	
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	//AppContext.tracker.trackPageView("Settings Screen");
    }
    
    public void btnSaveSettings_onClick(View v)
    {
    	if (isActivityValid()) {
    		//AppContext.tracker.trackEvent("Button Pressed", "Save Settings", null, 0);
    		//AppContext.tracker.trackEvent("Settings", "Collect Data Interval", null, Settings.getBatteryInterval());
			//AppContext.tracker.trackEvent("Settings", "Send Data Interval", null, Settings.getCosmInterval());
    		
    		Settings.setUser(etUser.getText().toString());
    		Settings.setPass(etPass.getText().toString());
    		Settings.setKey(etKey.getText().toString());
    		Settings.setFeed(etFeed.getText().toString());
    		Settings.setPrivate(chkPrivate.isChecked());

    		if (spBatteryInterval.getSelectedItem() != null) {
    			try {
    				Settings.setBatteryInterval(Integer.parseInt(spBatteryInterval.getSelectedItem().toString()));	
				} catch (Exception e) {
					Settings.setBatteryInterval(0);
				}
    		}
    		
    		if (spCosmInterval.getSelectedItem() != null) {
    			try {
    				Settings.setCosmInterval(Integer.parseInt(spCosmInterval.getSelectedItem().toString()));	
				} catch (Exception e) {
					Settings.setCosmInterval(0);
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
