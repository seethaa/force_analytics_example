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

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;
import com.salesforce.androidsdk.ui.sfnative.SalesforceActivity;

/**
 * Main activity
 * Starts an activity with a options to fetch reports from Salesforce Analytics API, and graph the average amounts as a pie chart
 * This can be easily adapted to create remaining graphs
 */
public class MainActivity extends SalesforceActivity {

	public static double[] percentages = new double[5];
	private RestClient client;
	private ArrayAdapter<String> listAdapter;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Setup view
		setContentView(R.layout.main);

		Button button = (Button) findViewById(R.id.fetch_graphs);

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Bundle b=new Bundle();
				b.putDoubleArray("percentages", percentages);
				Intent i = new Intent(MainActivity.this, GraphActivity.class);
				i.putExtras(b);
				startActivity(i);

			}

		});
	}



	@Override 
	public void onResume() {
		// Hide everything until we are logged in
		findViewById(R.id.root).setVisibility(View.INVISIBLE);


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
	 * Called when "Fetch Reports" button is clicked
	 * 
	 * @param v
	 * @throws UnsupportedEncodingException 
	 */
	public void onFetchReportsClick(View v) throws UnsupportedEncodingException {

		RestRequest feedRequest = generateRequest("GET", "analytics/reports/00OF0000005q9Jx?includeDetails=true", null);
		sendRequest(feedRequest);

	}

	/**
	 * Helper function to round doubles up to two decimal places. 
	 * @param d
	 * @return double result
	 */
	public double roundTwoDecimals(double d) {
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		return Double.valueOf(twoDForm.format(d));
	}

	/**
	 * Main parse function which reads the JSON response from server and parses data 
	 * to retrieve all amounts. 
	 * This parse function will only work for Opportunity type reports.
	 * _rev and _age variables can be used for graphing revenue and age components.
	 * @param jsonLine
	 * @return
	 */
	public String parse(String jsonLine) {

		try {

			JSONObject reader = new JSONObject(jsonLine);

			String factMaps = reader.getString("factMap");
			TextView tv = (TextView) findViewById(R.id.textView1);

			tv.setText(jsonLine);

			JSONObject maps = new JSONObject(factMaps); //creates new json object for all maps

		//GET TOTALS (T!T)
			String T_T_full = maps.getString("T!T");
			JSONObject T_T_object = new JSONObject(T_T_full);

			JSONArray T_T_label = T_T_object.getJSONArray("aggregates");
			String T_amt = T_T_label.getJSONObject(0).getString("label");
			System.out.println("T_amt: " + T_amt);

			T_amt = T_amt.substring(1);

			int T_T_amt_int = NumberFormat.getNumberInstance(java.util.Locale.US).parse(T_amt).intValue();

			System.out.println("T int: " + T_T_amt_int);

			String T_rev = T_T_label.getJSONObject(1).getString("label");
			System.out.println("T_rev: " + T_rev);

			String T_age = T_T_label.getJSONObject(2).getString("label");
			System.out.println("T_age: " + T_age);

		//GET TYPE (0!T)
			String T_0_full = maps.getString("0!T");
			JSONObject T_0_object = new JSONObject(T_0_full);

			JSONArray T_0_label = T_0_object.getJSONArray("aggregates");
			String T_0_amt = T_0_label.getJSONObject(0).getString("label");
			System.out.println("T_0_amt: " + T_0_amt);

			T_0_amt = T_0_amt.substring(1);

			int T_0_amt_int = NumberFormat.getNumberInstance(java.util.Locale.US).parse(T_0_amt).intValue();

			System.out.println("0 int: " + T_0_amt_int);

			double T_0_amt_avg = ( ((double) T_0_amt_int) / ((double) T_T_amt_int)) * 100; 

			T_0_amt_avg = roundTwoDecimals(T_0_amt_avg);

			percentages[0] = T_0_amt_avg;
			System.out.println("0 avg: " + T_0_amt_avg);

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

			T_1_amt = T_1_amt.substring(1);

			int T_1_amt_int = NumberFormat.getNumberInstance(java.util.Locale.US).parse(T_1_amt).intValue();
			System.out.println("1 int: " + T_1_amt_int);

			double T_1_amt_avg = ( ((double) T_1_amt_int) / ((double) T_T_amt_int)) * 100; 

			T_1_amt_avg = roundTwoDecimals(T_1_amt_avg);

			percentages[1] = T_1_amt_avg;

			System.out.println("1 avg: " + T_1_amt_avg);

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

			T_2_amt = T_2_amt.substring(1);

			int T_2_amt_int = NumberFormat.getNumberInstance(java.util.Locale.US).parse(T_2_amt).intValue();

			System.out.println("2 int: " + T_2_amt_int);


			double T_2_amt_avg = ( ((double) T_2_amt_int) / ((double) T_T_amt_int)) * 100; 

			T_2_amt_avg = roundTwoDecimals(T_2_amt_avg);

			percentages[2] = T_2_amt_avg;
			System.out.println("2 avg: " + T_2_amt_avg);

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

			T_3_amt = T_3_amt.substring(1);

			int T_3_amt_int = NumberFormat.getNumberInstance(java.util.Locale.US).parse(T_3_amt).intValue();


			double T_3_amt_avg = ( ((double) T_3_amt_int) / ((double) T_T_amt_int)) * 100; 

			T_3_amt_avg = roundTwoDecimals(T_3_amt_avg);
			percentages[3] =  T_3_amt_avg;

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

			T_4_amt = T_4_amt.substring(1);

			int T_4_amt_int = NumberFormat.getNumberInstance(java.util.Locale.US).parse(T_4_amt).intValue();

			double T_4_amt_avg = ( ((double) T_4_amt_int) / ((double) T_T_amt_int)) * 100; 

			T_4_amt_avg = roundTwoDecimals(T_4_amt_avg);
			percentages[4] = T_4_amt_avg;

			String T_4_rev = T_4_label.getJSONObject(1).getString("label");
			System.out.println("T_4_rev: " + T_4_rev);

			String T_4_age = T_4_label.getJSONObject(2).getString("label");
			System.out.println("T_4_age: " + T_4_age);


			for (int i=0; i< percentages.length; i++){
				System.out.println("%PS: " + percentages[i]);
			}

		} catch (Exception e) {
			Toast.makeText(getBaseContext(), e.getMessage(),
					Toast.LENGTH_SHORT).show();
		}


		return null;
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
					String data = parse(result.toString());
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
