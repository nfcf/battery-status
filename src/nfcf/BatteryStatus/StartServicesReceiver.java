package nfcf.BatteryStatus;

import nfcf.BatteryStatus.Classes.Settings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartServicesReceiver extends BroadcastReceiver { 
	
	@Override
	public void onReceive(Context context, Intent intent) {
        
        if (( Settings.getServiceStarted() && intent.getAction().contains("android.intent.action.BOOT_COMPLETED") ) || 
        		( Settings.getServiceStarted() && intent.getAction().contains("android.intent.action.PACKAGE_REPLACED") && intent.getDataString().contains(AppContext.getContext().getPackageName()) ) ||
        		( intent.getAction().equals(AppContext.START_SERVICES_COMPLETED) )){

        	AppContext.startServices();

        }
	}
}