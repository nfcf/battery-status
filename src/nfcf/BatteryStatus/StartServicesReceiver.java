package nfcf.BatteryStatus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartServicesReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		if ( intent.getAction().equals(AppContext.START_SERVICES_COMPLETED) ){
        	AppContext.startServices();
        } else { //BOOT_COMPLETED and PACKAGE_REPLACED
        	//Don't do anything. Just starting the process (AppContext) will make sure the services are running
        }
		
	}
}