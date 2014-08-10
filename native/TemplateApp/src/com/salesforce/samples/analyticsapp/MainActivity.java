/*lab
 * Copyright (c) 2012, salesforce.com, inc.
 * All rights reserved.
 * Redistribution and use of this software in source and binary forms, with or
 * without modification, are permitted provided that the following conditions
 * are met:
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * - Neither the name of salesforce.com, inc. nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission of salesforce.com, inc.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.salesforce.samples.analyticsapp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestClient.AsyncRequestCallback;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;
import com.salesforce.androidsdk.ui.sfnative.SalesforceActivity;

/**
 * Main activity
 */
public class MainActivity extends SalesforceActivity {

	private RestClient client;
	private ArrayAdapter<String> listAdapter;
	private String finalResult = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Setup view
		setContentView(R.layout.main);
	}



	@Override 
	public void onResume() {
		// Hide everything until we are logged in
		findViewById(R.id.root).setVisibility(View.INVISIBLE);

		// Create list adapter
		listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
		((ListView) findViewById(R.id.contacts_list)).setAdapter(listAdapter);				

		super.onResume();
	}		

	@Override
	public void onResume(RestClient client) {
		// Keeping reference to rest client
		this.client = client; 

		// Show everything
		findViewById(R.id.root).setVisibility(View.VISIBLE);
	}

	/**
	 * Called when "Logout" button is clicked. 
	 * 
	 * @param v
	 */
	public void onLogoutClick(View v) {
		SalesforceSDKManager.getInstance().logout(this);
	}

	/**
	 * Called when "Clear" button is clicked. 
	 * 
	 * @param v
	 */
	public void onClearClick(View v) {
		listAdapter.clear();
	}	

	/**
	 * Called when "Fetch Contacts" button is clicked
	 * 
	 * @param v
	 * @throws UnsupportedEncodingException 
	 */
	public void onFetchContactsClick(View v) throws UnsupportedEncodingException {
		//sendRequest("SELECT Name FROM Contact");
		//sendRequest("SELECT Name From Report");
		//sendRequest("SELECT id From Report");
		RestRequest feedRequest = generateRequest("GET", "analytics/reports/00OF0000005q9Jx?includeDetails=true", null);
		sendRequest(feedRequest);

		//		PostFetcher fetcher = new PostFetcher();
		//		fetcher.execute();

		//Read the server response and attempt to parse it as JSON

	}



	private void writeToFile(String data) {
		try {
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("config.txt", Context.MODE_PRIVATE));
			outputStreamWriter.write(data);
			outputStreamWriter.close();

			TextView tv = (TextView) findViewById(R.id.textView1);
			tv.setText(data);
		}
		catch (IOException e) {
			Log.e("Exception", "File write failed: " + e.toString());
		} 
	}


	private String readFromFile() {

		String ret = "";

		try {
			InputStream inputStream = openFileInput("config.txt");

			if ( inputStream != null ) {
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String receiveString = "";
				StringBuilder stringBuilder = new StringBuilder();

				while ( (receiveString = bufferedReader.readLine()) != null ) {
					stringBuilder.append(receiveString);
				}

				inputStream.close();
				ret = stringBuilder.toString();
			}
		}
		catch (FileNotFoundException e) {
			Log.e("login activity", "File not found: " + e.toString());
		} catch (IOException e) {
			Log.e("login activity", "Can not read file: " + e.toString());
		}

		return ret;
	}


	public String parseagain(String jsonLine) throws FileNotFoundException {



		try {

			JSONObject reader = new JSONObject(jsonLine);

			String factMaps = reader.getString("factMap");
			TextView tv = (TextView) findViewById(R.id.textView1);

			tv.setText(factMaps);

			JSONObject maps = new JSONObject(factMaps); //creates new json object for all maps

			
			//GET TYPE (0!T)
			String T_0_full = maps.getString("0!T");
			System.out.println("full string 0t : "+ T_0_full);
			JSONObject T_0_object = new JSONObject(T_0_full);
			
			JSONArray T_0_label = T_0_object.getJSONArray("aggregates");
			String T_0_amt = T_0_label.getJSONObject(0).getString("label");
			System.out.println("T_0_amt: " + T_0_amt);
			
			String T_0_rev = T_0_label.getJSONObject(1).getString("label");
			System.out.println("T_0_rev: " + T_0_rev);
			
			String T_0_age = T_0_label.getJSONObject(2).getString("label");
			System.out.println("T_0_age: " + T_0_age);
			
			//GET EXISTING CUSTOMER - UPGRADE (1!T)
			String T_1_full = maps.getString("1!T");
			System.out.println("full string 1t : "+ T_1_full);
			JSONObject T_1_object = new JSONObject(T_1_full);
			
			JSONArray T_1_label = T_1_object.getJSONArray("aggregates");
			String T_1_amt = T_1_label.getJSONObject(0).getString("label");
			System.out.println("T_1_amt: " + T_1_amt);
			
			String T_1_rev = T_1_label.getJSONObject(1).getString("label");
			System.out.println("T_1_rev: " + T_1_rev);
			
			String T_1_age = T_1_label.getJSONObject(2).getString("label");
			System.out.println("T_1_age: " + T_1_age);
			
			//GET EXISTING CUSTOMER - REPLACEMENT (2!T)
			String T_2_full = maps.getString("2!T");
			System.out.println("full string 2t : "+ T_2_full);
			JSONObject T_2_object = new JSONObject(T_2_full);
			
			JSONArray T_2_label = T_2_object.getJSONArray("aggregates");
			String T_2_amt = T_2_label.getJSONObject(0).getString("label");
			System.out.println("T_2_amt: " + T_2_amt);
			
			String T_2_rev = T_2_label.getJSONObject(1).getString("label");
			System.out.println("T_2_rev: " + T_2_rev);
			
			String T_2_age = T_2_label.getJSONObject(2).getString("label");
			System.out.println("T_2_age: " + T_2_age);
			
			
		
			//GET EXISTING CUSTOMER - DOWNGRADE (3!T)
			String T_3_full = maps.getString("3!T");
			System.out.println("full string 3t : "+ T_3_full);
			JSONObject T_3_object = new JSONObject(T_3_full);
			
			JSONArray T_3_label = T_3_object.getJSONArray("aggregates");
			String T_3_amt = T_3_label.getJSONObject(0).getString("label");
			System.out.println("T_3_amt: " + T_3_amt);
			
			String T_3_rev = T_3_label.getJSONObject(1).getString("label");
			System.out.println("T_3_rev: " + T_3_rev);
			
			String T_3_age = T_3_label.getJSONObject(2).getString("label");
			System.out.println("T_3_age: " + T_3_age);
			//NEW CUSTOMER (4!T)
			String T_4_full = maps.getString("4!T");
			System.out.println("full string 4t : "+ T_4_full);
			JSONObject T_4_object = new JSONObject(T_4_full);
			
			JSONArray T_4_label = T_4_object.getJSONArray("aggregates");
			String T_4_amt = T_4_label.getJSONObject(0).getString("label");
			System.out.println("T_4_amt: " + T_4_amt);
			
			String T_4_rev = T_4_label.getJSONObject(1).getString("label");
			System.out.println("T_4_rev: " + T_4_rev);
			
			String T_4_age = T_4_label.getJSONObject(2).getString("label");
			System.out.println("T_4_age: " + T_4_age);
			
			//GET TOTALS (T!T)
			String T_T_full = maps.getString("T!T");
			System.out.println("full string tt : "+ T_T_full);
			JSONObject T_T_object = new JSONObject(T_T_full);
			
			JSONArray T_T_label = T_T_object.getJSONArray("aggregates");
			String T_amt = T_T_label.getJSONObject(0).getString("label");
			System.out.println("T_amt: " + T_amt);
			
			String T_rev = T_T_label.getJSONObject(1).getString("label");
			System.out.println("T_rev: " + T_rev);
			
			String T_age = T_T_label.getJSONObject(2).getString("label");
			System.out.println("T_age: " + T_age);
		
		} catch (Exception e) {
			Toast.makeText(getBaseContext(), e.getMessage(),
					Toast.LENGTH_SHORT).show();
		}


		return null;
	}

	public String parse(String jsonLine) throws FileNotFoundException {
		System.out.println("got full line: "+jsonLine + "LJWELJFLWEJWLEJFLEWJFLWE LFJWEL FJWOE FJOWEIJF LJWELFJLWEJFLWJELJEFWLJWELF JWLKEJFWFLJLWEJ"+
				"OEUWOEIUFOUEOFUOWE");

		//writeToFile(jsonLine);

		//		 JsonParser jp = new JsonParser();
		//	      JsonElement je = jp.parse(jsonLine);
		//	      JsonElement je2 = je.getAsJsonObject().get("factMap");
		//	      //JsonElement je3 = je2.getAsJsonObject().get("rows");
		//	     // JsonElement je4 = je3.getAsJsonObject().get("dataCells");
		//	      
		//	      String testing = je2.getAsString();





		try {

			JSONObject reader = new JSONObject(jsonLine);

			//			JSONObject fm = reader.getJSONObject("factMap");
			String factMaps = reader.getString("factMap");
			TextView tv = (TextView) findViewById(R.id.textView1);


			//		        int len = factMaps.length();

			//		        factMaps = factMaps.substring(1, len-1); //remove first and last curly braces

			//		        String[] rowsList = factMaps.split("{*},");
			// JSONArray arr = new JSONArray(rowsList);
			tv.setText(factMaps);

			JSONObject maps = new JSONObject(factMaps); //creates new json object for all maps

			//JSONArray rows = maps.getJSONArray("dataCells");


			/* for (int i = 0; i<rowsList.length; i++){
//		    	  JSONObject n = rows.getJSONObject(i);
		    	  System.out.println("addy: " +i + " : "+rowsList[i].toString());
//		    	  JSONArray curr = n.getJSONArray("rows");



		    	 for (int k=0; k< curr.length(); k++){
		    		  JSONObject currData = curr.getJSONObject(k);
		    		  String datacell =currData.getString("dataCells");
		    		  tv.setText(datacell);
				      Toast.makeText(getBaseContext(), datacell,
			                    Toast.LENGTH_LONG).show();
		    	  }

		      } */

			//            File myFile = new File("/mnt/mysdfile.txt");
			//            myFile.createNewFile();
			//            FileOutputStream fOut = new FileOutputStream(myFile);
			//            OutputStreamWriter myOutWriter = 
			//                                    new OutputStreamWriter(fOut);
			//            myOutWriter.append(jsonLine);
			//            myOutWriter.close();
			//            fOut.close();
			//            Toast.makeText(getBaseContext(),
			//                    jsonLine + "LJWELJFLWEJWLEJFLEWJFLWE LFJWEL FJWOE FJOWEIJF LJWELFJLWEJFLWJELJEFWLJWELF JWLKEJFWFLJLWEJ"+
			//                            "OEUWOEIUFOUEOFUOWE",
			//                    Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(getBaseContext(), e.getMessage(),
					Toast.LENGTH_SHORT).show();
		}

		//	    JsonElement jelement = new JsonParser().parse(jsonLine);
		//	    JsonObject  jobject = jelement.getAsJsonObject();
		//	    jobject = jobject.getAsJsonObject("factMap");
		//	    JsonArray jarray = jobject.getAsJsonArray("rows");
		//	    jobject = jarray.get(0).getAsJsonObject();
		//	    
		//	    String result = jobject.get("dataCells").toString();
		//	    return result;


		//	      Type mapType = new TypeToken<Map<String, Item>>() {}.getType();
		//
		//	      Map<String, Item> testing = new Gson().fromJson(je4, mapType);

		return null;
	}

	/**
	 * Called when "Fetch Accounts" button is clicked
	 * 
	 * @param v
	 * @throws UnsupportedEncodingException 
	 */
	public void onFetchAccountsClick(View v) throws UnsupportedEncodingException {
		sendRequest("SELECT Name FROM Account");
	}	

	private void sendRequest(String soql) throws UnsupportedEncodingException {
		RestRequest restRequest = RestRequest.getRequestForQuery(getString(R.string.api_version), soql);

		client.sendAsync(restRequest, new AsyncRequestCallback() {
			@Override
			public void onSuccess(RestRequest request, RestResponse result) {
				try {


					listAdapter.clear();
					JSONArray records = result.asJSONObject().getJSONArray("records");
					for (int i = 0; i < records.length(); i++) {
						listAdapter.add(records.getJSONObject(i).getString("Name"));
					}		


					//print response 



				} catch (Exception e) {
					onError(e);
				}
			}

			@Override
			public void onError(Exception exception) {
				Toast.makeText(MainActivity.this,
						MainActivity.this.getString(SalesforceSDKManager.getInstance().getSalesforceR().stringGenericError(), exception.toString()),
						Toast.LENGTH_LONG).show();
			}
		});
	}


	private RestRequest generateRequest(String httpMethod, String resource, String jsonPayloadString) {
		RestRequest request = null;

		if (jsonPayloadString == null) {
			jsonPayloadString = "";
		}
		String url = String.format("/services/data/%s/" + resource, getString(R.string.api_version)); // The IDE might highlight this line as having an error. This is a bug, the code will compile just fine.
		try {
			HttpEntity paramsEntity = getParamsEntity(jsonPayloadString);
			RestRequest.RestMethod method = RestRequest.RestMethod.valueOf(httpMethod.toUpperCase());
			request = new RestRequest(method, url, paramsEntity);
			return request;
		} catch (UnsupportedEncodingException e) {
			Log.e("ERROR", "Could not build request");
			e.printStackTrace();
		}
		return request;
	}


	private HttpEntity getParamsEntity(String requestParamsText)
			throws UnsupportedEncodingException {
		Map<String, Object> params = parseFieldMap(requestParamsText);
		if (params == null) {
			params = new HashMap<String, Object>();
		}
		List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
		for (Map.Entry<String, Object> param : params.entrySet()) {
			paramsList.add(new BasicNameValuePair(param.getKey(),
					(String) param.getValue()));
		}
		return new UrlEncodedFormEntity(paramsList);
	}

	private Map<String, Object> parseFieldMap(String jsonText) {
		String fieldsString = jsonText;
		if (fieldsString.length() == 0) {
			return null;
		}

		try {
			JSONObject fieldsJson = new JSONObject(fieldsString);
			Map<String, Object> fields = new HashMap<String, Object>();
			JSONArray names = fieldsJson.names();
			for (int i = 0; i < names.length(); i++) {
				String name = (String) names.get(i);
				fields.put(name, fieldsJson.get(name));
			}
			return fields;

		} catch (Exception e) {
			Log.e("ERROR", "Could not build request");
			e.printStackTrace();
			return null;
		}
	}

	private void sendRequest(RestRequest restRequest) {
		client.sendAsync(restRequest, new RestClient.AsyncRequestCallback() {

			@Override
			public void onSuccess(RestRequest request, RestResponse result) {
				try {

					//Do something with JSON result.
					System.out.println(result);  //Use our helper function, to print our JSON response.

					System.out.println("got here");
					String data = parseagain(result.toString());
					System.out.println("data: " +data);

				} catch (Exception e) {
					e.printStackTrace();
				}

				//EventsObservable.get().notifyEvent(EventsObservable.EventType.RenditionComplete);
			}

			@Override
			public void onError(Exception e) {
				e.printStackTrace();
				//EventsObservable.get().notifyEvent(EventsObservable.EventType.RenditionComplete);
			}
		});
	}
}
