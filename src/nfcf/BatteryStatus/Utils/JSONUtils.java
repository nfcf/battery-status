package nfcf.BatteryStatus.Utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONUtils {

	public static JSONObject getJSONfromEntity(HttpEntity ent)
	{
		String result = "";
		JSONObject jArray = null;

		//convert response to string
		try{
			InputStream is = ent.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			result=sb.toString();
		}catch(Exception e){
			Log.e("getJSONfromEntity", "Error converting result "+e.toString());
		}

		//try parse the string to a JSON object
		try{
			jArray = new JSONObject(result);
		}catch(JSONException e){
			Log.e("getJSONfromEntity", "Error parsing data "+e.toString());
		}

		return jArray;
	}

}
