package nfcf.BatteryStatus.Classes;

import java.io.IOException;
import java.util.Locale;

import nfcf.BatteryStatus.AppContext;
import nfcf.BatteryStatus.Utils.JSONUtils;

import org.acra.ErrorReporter;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Base64;
import android.util.Log;

public class PachubeAPI 
{
	public static final String PACHUBE_API_URL = "http://api.pachube.com/v2/";
	
	public static class PachubeResponse {
		private int statusCode;
		private String value;
		
		public int getStatusCode() { return statusCode; }
		public void setStatusCode(int statusCode) { this.statusCode = statusCode; }
		  
		public String getValue() { return value; }
		public void setValue(String value) { this.value = value; }
		
	}
	
	private static HttpParams getHTTPParams() {
		return getHTTPParams(6000);
	}
	
	private static HttpParams getHTTPParams(int timeout) {
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		int timeoutConnection = timeout;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		// Set the default socket timeout (SO_TIMEOUT) 
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = timeout;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		
		return httpParameters;
	}
	
	public static JSONObject getJSONLevelObject() {
		JSONObject jsonObject = new JSONObject();

		try {
			jsonObject.put("max_value", "100");
			jsonObject.put("min_value", "0");
			jsonObject.put("unit", new JSONObject("{\"type\": \"basicSI\",\"label\": \"Percentage\",\"symbol\": \"%\"}"));
			jsonObject.put("id", AppContext.LEVEL);
		} catch (JSONException e) {
			ErrorReporter.getInstance().handleException(e);
			jsonObject = null;
		}

		return jsonObject;
	}
	
	public static JSONObject getJSONPluggedObject() {
		JSONObject jsonObject = new JSONObject();

		try {
			jsonObject.put("max_value", "5");
			jsonObject.put("min_value", "0");
			jsonObject.put("id", AppContext.PLUGGED);
		} catch (JSONException e) {
			ErrorReporter.getInstance().handleException(e);
			jsonObject = null;
		}

		return jsonObject;
	}
	
	public static JSONObject getJSONTemperatureObject() {
		JSONObject jsonObject = new JSONObject();

		try {
			jsonObject.put("max_value", "100");
			jsonObject.put("min_value", "0");
			jsonObject.put("unit", new JSONObject("{\"type\": \"basicSI\",\"label\": \"Degrees Celsius\",\"symbol\": \"ºC\"}"));
			jsonObject.put("id", AppContext.TEMPERATURE);
		} catch (JSONException e) {
			ErrorReporter.getInstance().handleException(e);
			jsonObject = null;
		}

		return jsonObject;
	}
	
	public static JSONObject getJSONVoltageObject() {
		JSONObject jsonObject = new JSONObject();

		try {
			jsonObject.put("max_value", "5");
			jsonObject.put("min_value", "0");
			jsonObject.put("unit", new JSONObject("{\"type\": \"basicSI\",\"label\": \"Volt\",\"symbol\": \"V\"}"));
			jsonObject.put("id", AppContext.VOLTAGE);
		} catch (JSONException e) {
			ErrorReporter.getInstance().handleException(e);
			jsonObject = null;
		}

		return jsonObject;
	}
	
	public static JSONObject getJSONScreenObject() {
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonTagsArray = new JSONArray();

		try {
			jsonObject.put("max_value", "100");
			jsonObject.put("min_value", "0");
			jsonObject.put("id", AppContext.SCREEN);
			jsonObject.put("tags", jsonTagsArray);
			jsonTagsArray.put("Brightness");
		} catch (JSONException e) {
			ErrorReporter.getInstance().handleException(e);
			jsonObject = null;
		}

		return jsonObject;
	}
	
	public static JSONObject getJSONWifiObject() {
		JSONObject jsonObject = new JSONObject();

		try {
			jsonObject.put("max_value", "1");
			jsonObject.put("min_value", "0");
			jsonObject.put("id", AppContext.WIFI);
		} catch (JSONException e) {
			ErrorReporter.getInstance().handleException(e);
			jsonObject = null;
		}

		return jsonObject;
	}
	
	public static JSONObject getJSONNetworkObject() {
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonTagsArray = new JSONArray();

		try {
			jsonObject.put("max_value", "5");
			jsonObject.put("min_value", "0");
			jsonObject.put("id", AppContext.NETWORK);
			jsonObject.put("tags", jsonTagsArray);
			jsonTagsArray.put("Network Gen");
		} catch (JSONException e) {
			ErrorReporter.getInstance().handleException(e);
			jsonObject = null;
		}

		return jsonObject;
	}
	
	public static JSONObject getJSONPhoneCallObject() {
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonTagsArray = new JSONArray();

		try {
			jsonObject.put("max_value", "1");
			jsonObject.put("min_value", "0");
			jsonObject.put("id", AppContext.PHONECALL);
			jsonObject.put("tags", jsonTagsArray);
			jsonTagsArray.put("Call State");
		} catch (JSONException e) {
			ErrorReporter.getInstance().handleException(e);
			jsonObject = null;
		}

		return jsonObject;
	}
	
	public static JSONObject getJSONBluetoothObject() {
		JSONObject jsonObject = new JSONObject();

		try {
			jsonObject.put("max_value", "1");
			jsonObject.put("min_value", "0");
			jsonObject.put("id", AppContext.BLUETOOTH);
		} catch (JSONException e) {
			ErrorReporter.getInstance().handleException(e);
			jsonObject = null;
		}

		return jsonObject;
	}
	
	public static JSONObject getJSONRxTxObject() {
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonTagsArray = new JSONArray();

		try {
			//jsonObject.put("max_value", "1");
			jsonObject.put("min_value", "0");
			jsonObject.put("unit", new JSONObject("{\"type\": \"basicSI\",\"label\": \"kiloBytes\",\"symbol\": \"kB\"}"));
			jsonObject.put("id", AppContext.RXTX);
			jsonObject.put("tags", jsonTagsArray);
			jsonTagsArray.put("Data transfered");
		} catch (JSONException e) {
			ErrorReporter.getInstance().handleException(e);
			jsonObject = null;
		}

		return jsonObject;
	}
	
	//Check if the desired key exists and if it has the desired access status
	public static PachubeResponse checkKey(String key, Boolean privateAccess) {
		PachubeResponse returnValue = null; 
        
    	try {
    		HttpClient hc = new DefaultHttpClient(getHTTPParams());
    		HttpGet get= new HttpGet(PACHUBE_API_URL + "keys/" + key);
    		//get.addHeader("Authorization", "Basic " + Base64.encodeToString((user + ":" + pass).getBytes(), Base64.NO_WRAP));
    		get.addHeader("X-PachubeApiKey", key);
    		
    		HttpResponse rp = hc.execute(get);

    		returnValue = new PachubeResponse();
    		returnValue.setStatusCode(rp.getStatusLine().getStatusCode());
    		Log.d("Check Key", "Status Code: " + rp.getStatusLine().getStatusCode());
    		if (rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

    			JSONObject jo = JSONUtils.getJSONfromEntity(rp.getEntity());
    			if (jo.getJSONObject("key").getBoolean("private_access") == privateAccess){
    				returnValue.setValue(jo.getJSONObject("key").getString("api_key"));
    			}

    		} else {
    			ErrorReporter.getInstance().handleSilentException(new Exception("Check Key, Status Code:" + rp.getStatusLine().getStatusCode()));
    		}

    	} catch (IOException e) {
    		Log.e("checkKey",e.toString());
    	} catch (Exception e) {
    		//Log.e("findKey",e.toString());
    		ErrorReporter.getInstance().handleException(e);
    		returnValue = null;
    	} 
    	
    	return returnValue;
	}
	
	public static PachubeResponse findKey(String user, String pass, String labelToFind) {
		PachubeResponse returnValue = null; 
        
		//List keys with the desirable label
    	try {
    		HttpClient hc = new DefaultHttpClient(getHTTPParams());
    		HttpGet get= new HttpGet(PACHUBE_API_URL + "keys");
    		get.addHeader("Authorization", "Basic " + Base64.encodeToString((user + ":" + pass).getBytes(), Base64.NO_WRAP));

    		HttpResponse rp = hc.execute(get);

    		returnValue = new PachubeResponse();
    		returnValue.setStatusCode(rp.getStatusLine().getStatusCode());
    		Log.d("Find Key", "Status Code: " + rp.getStatusLine().getStatusCode());
    		if (rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
    		{
    			JSONObject jo = JSONUtils.getJSONfromEntity(rp.getEntity());
    			JSONArray ja = jo.getJSONArray("keys");
    			for (int i = 0; i < ja.length(); i++) {
    				//before the API changed
    				//if (ja.getJSONObject(i).getJSONObject("key").getString("label").equalsIgnoreCase(labelToFind)){
    				
    				if (ja.getJSONObject(i).getString("label").equalsIgnoreCase(labelToFind)){
    					//ja.getJSONObject(i).getBoolean("private_access");
    					returnValue.setValue(ja.getJSONObject(i).getString("api_key"));
    				}
    			}
    		} else {
    			ErrorReporter.getInstance().handleSilentException(new Exception("Find Key, Status Code:" + rp.getStatusLine().getStatusCode()));
    		}

    	} catch (IOException e) {
    		Log.e("findKey",e.toString());
    	} catch (Exception e) {
    		//Log.e("findKey",e.toString());
    		ErrorReporter.getInstance().handleException(e);
    		returnValue = null;
    	} 
    	
    	return returnValue;
	}
	
	public static PachubeResponse createKey(String user, String pass, String labelToCreate, Boolean privateAccess) {
		PachubeResponse returnValue = null; 
		
		//Create key with the desirable label
		JSONObject jsonCreateKeyObject = new JSONObject();
		JSONObject jsonKeyObject = new JSONObject();
		JSONArray jsonPermissionsArray = new JSONArray();
		JSONObject jsonPermissionsObject = new JSONObject();
		JSONArray jsonAccessMethodsArray = new JSONArray();

		try {
			jsonCreateKeyObject.put("key", jsonKeyObject);
			jsonKeyObject.put("label", labelToCreate);
			jsonKeyObject.put("private_access", privateAccess.toString());
			jsonKeyObject.put("permissions", jsonPermissionsArray);
			jsonPermissionsArray.put(jsonPermissionsObject);
			jsonPermissionsObject.put("access_methods",jsonAccessMethodsArray);
			jsonAccessMethodsArray.put("post");
			jsonAccessMethodsArray.put("get");	

		} catch (JSONException e) {
			ErrorReporter.getInstance().handleException(e);
		}

		try {
			HttpClient hc = new DefaultHttpClient(getHTTPParams());
			HttpPost post = new HttpPost(PACHUBE_API_URL + "keys");
			post.addHeader("Authorization", "Basic " + Base64.encodeToString((user + ":" + pass).getBytes(), Base64.NO_WRAP));

			StringEntity se;
			se = new StringEntity(jsonCreateKeyObject.toString(),HTTP.UTF_8);
			se.setContentType("application/json");
			post.setEntity(se);
			HttpResponse rp = hc.execute(post);

			returnValue = new PachubeResponse();
    		returnValue.setStatusCode(rp.getStatusLine().getStatusCode());
			Log.d("Create Key", "Status Code: " + String.valueOf(rp.getStatusLine().getStatusCode()));
			if (rp.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED)
			{
				//Before the API changed
				//JSONObject jo = JSONUtils.getJSONfromEntity(rp.getEntity());
				//returnValue.setValue(jo.getJSONObject("key").getString("api_key"));
				
				String key = rp.getHeaders("Location")[0].getValue();
				key = key.substring(key.lastIndexOf("/") + 1);
				returnValue.setValue(key);
			} else {
				ErrorReporter.getInstance().handleSilentException(new Exception("Create Key, Status Code:" + rp.getStatusLine().getStatusCode()));
			}

		} catch (IOException e) {
    		Log.e("createKey",e.toString());
    	} catch (Exception e) {
    		//Log.e("createKey",e.toString());
    		ErrorReporter.getInstance().handleException(e);
    		returnValue = null;
    	} 
		
		return returnValue;
	}
	
	//Check if the desired feed exists and if it has the desired access status
	public static PachubeResponse checkFeed(String user, String pass, String feed, Boolean privateAccess) {
		PachubeResponse returnValue = null; 

		ErrorReporter.getInstance().handleSilentException(new Exception(user + "-" + pass + "-" + feed + "-" + privateAccess));
		
		JSONObject jsonCheckFeedObject = new JSONObject();

		try {
			jsonCheckFeedObject.put("version", "1.0.0");
			jsonCheckFeedObject.put("private", privateAccess.toString());
		} catch (JSONException e) {
			ErrorReporter.getInstance().handleException(e);
		}
		
		try {
			HttpClient hc = new DefaultHttpClient(getHTTPParams());
			HttpPut put= new HttpPut(PACHUBE_API_URL + "feeds/" + feed);
			put.addHeader("Authorization", "Basic " + Base64.encodeToString((user + ":" + pass).getBytes(), Base64.NO_WRAP));
			//put.addHeader("X-PachubeApiKey", key);

			StringEntity se;
			se = new StringEntity(jsonCheckFeedObject.toString(),HTTP.UTF_8);
			se.setContentType("application/json");
			put.setEntity(se);
			HttpResponse rp = hc.execute(put);
			
			returnValue = new PachubeResponse();
			returnValue.setStatusCode(rp.getStatusLine().getStatusCode());
			Log.d("Check Feed", "Status Code: " + rp.getStatusLine().getStatusCode());
			if (rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				returnValue.setValue(feed);
			} else {
				returnValue.setValue(null);
				ErrorReporter.getInstance().handleSilentException(new Exception("Check Feed, Status Code:" + rp.getStatusLine().getStatusCode()));
			}

		} catch (IOException e) {
			Log.e("checkFeed",e.toString());
		} catch (Exception e) {
			ErrorReporter.getInstance().handleException(e);
			returnValue = null;
		} 

		return returnValue;
	}

	//List Feeds and see if yours already exist
	public static PachubeResponse findFeed(String user, String key, String titleToFind) {
		PachubeResponse returnValue = null; 
		
    	try {
    		
    		HttpClient hc = new DefaultHttpClient(getHTTPParams());
    		HttpGet get= new HttpGet(PACHUBE_API_URL + "feeds?user=" + user + "&content=summary"); //&q=" + URLEncoder.encode(titleToFind, "utf-8"));
    		//post.addHeader("Authorization", "Basic " + Base64.encodeToString((user + ":" + pass).getBytes(), Base64.NO_WRAP));
    		get.addHeader("X-PachubeApiKey", key);

    		HttpResponse rp = hc.execute(get);

    		returnValue = new PachubeResponse();
    		returnValue.setStatusCode(rp.getStatusLine().getStatusCode());
    		Log.d("Find Feed", "Status Code: " + rp.getStatusLine().getStatusCode());
    		if (rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
    		{
    			JSONObject jo = JSONUtils.getJSONfromEntity(rp.getEntity());
    			JSONArray ja = jo.getJSONArray("results");
    			for (int i = 0; i < ja.length(); i++) {
    				if (ja.getJSONObject(i).getString("title").equalsIgnoreCase(titleToFind) && 
    						!ja.getJSONObject(i).getString("status").equalsIgnoreCase("deleted")){
    					String feed = ja.getJSONObject(i).getString("feed");
    					feed = feed.substring(feed.lastIndexOf("/") + 1);
    					if (feed.endsWith(".json")) feed = feed.substring(0,feed.indexOf(".json"));
    					returnValue.setValue(feed);
    				}
    			}	
    		} 	else {
    			ErrorReporter.getInstance().handleSilentException(new Exception("Find Feed, Status Code:" + rp.getStatusLine().getStatusCode()));
    		}

    	} catch (IOException e) {
    		Log.e("findFeed",e.toString());
    	} catch (Exception e) {
    		//Log.e("findFeed",e.toString());
    		ErrorReporter.getInstance().handleException(e);
    		returnValue = null;
    	} 
    	
    	return returnValue;
	}
	
	public static PachubeResponse createFeed(String key, String titleToCreate, String description, Boolean privateAccess) {
		PachubeResponse returnValue = null; 
		
		JSONObject jsonCreateFeedObject = new JSONObject();
		JSONArray jsonTagsArray = new JSONArray();
		JSONObject jsonLocationObject = new JSONObject();
		JSONArray jsonDataStreamsArray = new JSONArray();

		try {
			jsonCreateFeedObject.put("title", titleToCreate);
			jsonCreateFeedObject.put("version", "1.0.0");
			jsonCreateFeedObject.put("private", privateAccess.toString());
			jsonCreateFeedObject.put("description", description);
			jsonCreateFeedObject.put("tags", jsonTagsArray);
			jsonTagsArray.put("Battery");
			jsonTagsArray.put("Battery Status");
			jsonTagsArray.put("Android");

			jsonCreateFeedObject.put("location", jsonLocationObject);  
			jsonLocationObject.put("disposition", "Mobile");
			jsonLocationObject.put("name", AppContext.getContext().getResources().getConfiguration().locale.getDisplayCountry(Locale.getDefault()));
			jsonLocationObject.put("exposure", "Outdoor");
			jsonLocationObject.put("domain", "Physical");

			jsonCreateFeedObject.put("datastreams", jsonDataStreamsArray);
			jsonDataStreamsArray.put(getJSONLevelObject());
			jsonDataStreamsArray.put(getJSONPluggedObject());
			jsonDataStreamsArray.put(getJSONTemperatureObject());
			jsonDataStreamsArray.put(getJSONVoltageObject());
			jsonDataStreamsArray.put(getJSONScreenObject());
			jsonDataStreamsArray.put(getJSONNetworkObject());
			jsonDataStreamsArray.put(getJSONWifiObject());
			jsonDataStreamsArray.put(getJSONPhoneCallObject());
			jsonDataStreamsArray.put(getJSONBluetoothObject());
			jsonDataStreamsArray.put(getJSONRxTxObject());

		} catch (JSONException e) {
			ErrorReporter.getInstance().handleException(e);
		}

		try {
			HttpClient hc = new DefaultHttpClient(getHTTPParams());
			HttpPost post = new HttpPost(PACHUBE_API_URL + "feeds");
			post.addHeader("X-PachubeApiKey", key);

			StringEntity se;
			se = new StringEntity(jsonCreateFeedObject.toString(),HTTP.UTF_8);
			se.setContentType("application/json");
			post.setEntity(se);
			HttpResponse rp = hc.execute(post);

			returnValue = new PachubeResponse();
    		returnValue.setStatusCode(rp.getStatusLine().getStatusCode());
			Log.d("Create Feed", "Status Code: " + String.valueOf(rp.getStatusLine().getStatusCode()));
			if (rp.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED)
			{
				Log.d("Create Feed",rp.getHeaders("Location")[0].getValue());
				String feed = rp.getHeaders("Location")[0].getValue();
				feed = feed.substring(feed.lastIndexOf("/") + 1);
				if (feed.endsWith(".json")) feed = feed.substring(0,feed.indexOf(".json"));
				returnValue.setValue(feed);
			} else {
				ErrorReporter.getInstance().handleSilentException(new Exception("Create Feed, Status Code:" + rp.getStatusLine().getStatusCode()));
			}

		} catch (IOException e) {
    		Log.e("createFeed",e.toString());
    	} catch (Exception e) {
    		ErrorReporter.getInstance().handleException(e);
    		returnValue = null;
    	} 
		
		return returnValue;
	}

	public static Boolean createDataStream(String feed, String key, JSONObject jsonDataStreamObject) {
		
		JSONObject jsonCreateDataStreamObject = new JSONObject();
		JSONArray jsonDataStreamsArray = new JSONArray();

		try {
			jsonCreateDataStreamObject.put("version", "1.0.0");
			jsonCreateDataStreamObject.put("datastreams", jsonDataStreamsArray);
			jsonDataStreamsArray.put(jsonDataStreamObject);
		} catch (JSONException e) {
			ErrorReporter.getInstance().handleException(e);
		}

		try {
			HttpClient hc = new DefaultHttpClient(getHTTPParams());
			HttpPost post = new HttpPost(PACHUBE_API_URL + "feeds/" + feed + "/datastreams");
			post.addHeader("X-PachubeApiKey", key);

			StringEntity se;
			se = new StringEntity(jsonCreateDataStreamObject.toString(),HTTP.UTF_8);
			se.setContentType("application/json");
			post.setEntity(se);
			HttpResponse rp = hc.execute(post);

			Log.d("Create DataStream", "Status Code: " + String.valueOf(rp.getStatusLine().getStatusCode()));
			if (rp.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED)
			{
				return true;
			} 

		} catch (IOException e) {
    		Log.e("createDataStream",e.toString());
    	} catch (Exception e) {
    		ErrorReporter.getInstance().handleException(e);
    	} 
		
		return false;
	}
	
	public static boolean sendDataPoints(String feed, String datastream, String key, String jsonContent) {
		return sendDataPoints(feed, datastream, key, jsonContent, true);
	}
	public static boolean sendDataPoints(String feed, String datastream, String key, String jsonContent, Boolean mayCreateDataStream) {
		Boolean returnValue = false;
		try
		{
			HttpClient hc = new DefaultHttpClient(getHTTPParams(20000));
			HttpPost post = new HttpPost(PACHUBE_API_URL + "feeds/" + feed + "/datastreams/" + datastream + "/datapoints");
			post.addHeader("X-PachubeApiKey", key);

			StringEntity se = new StringEntity(jsonContent,HTTP.UTF_8);
			se.setContentType("application/json");
			post.setEntity(se);
			HttpResponse rp = hc.execute(post);
			//Log.d("coiso", jsonContent);
			if(rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				returnValue = true;
			} else {
				Log.w("Sending datapoints","Status Code: " + rp.getStatusLine().getStatusCode());
				//if the datastream doesn't exist, create it and try to re-send the datapoints
				if (rp.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
					if (mayCreateDataStream) {
						JSONObject joDataStream=null;
						if (datastream.equals(AppContext.LEVEL)) {
							joDataStream = getJSONLevelObject();
						} else if (datastream.equals(AppContext.PLUGGED)) {
							joDataStream = getJSONPluggedObject();
						} else if (datastream.equals(AppContext.VOLTAGE)) {
							joDataStream = getJSONVoltageObject();
						} else if (datastream.equals(AppContext.TEMPERATURE)) {
							joDataStream = getJSONTemperatureObject();
						} else if (datastream.equals(AppContext.SCREEN)) {
							joDataStream = getJSONScreenObject();
						} else if (datastream.equals(AppContext.WIFI)) {
							joDataStream = getJSONWifiObject();
						} else if (datastream.equals(AppContext.NETWORK)) {
							joDataStream = getJSONNetworkObject();
						} else if (datastream.equals(AppContext.PHONECALL)) {
							joDataStream = getJSONPhoneCallObject();
						} else if (datastream.equals(AppContext.BLUETOOTH)) {
							joDataStream = getJSONBluetoothObject();
						} else if (datastream.equals(AppContext.RXTX)) {
							joDataStream = getJSONRxTxObject();
						}
						if (createDataStream(feed,key, joDataStream)) returnValue = sendDataPoints(feed,datastream,key,jsonContent, false);
					}
				}
			}
		} catch (IOException e) {
			Log.e("sendDataPoints",e.toString());
		} catch (Exception e) {
			ErrorReporter.getInstance().putCustomData("feed", feed);
			ErrorReporter.getInstance().handleException(e);
		} 

		return returnValue;
	}
	
}
