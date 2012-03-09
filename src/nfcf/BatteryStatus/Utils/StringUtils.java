package nfcf.BatteryStatus.Utils;

public class StringUtils {

	public static final String EMPTY_STRING = "";
	
	public static boolean isNullOrBlank(String param) { 
	    return param == null || param.trim().length() == 0;
	}
	
}
