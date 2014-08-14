package com.salesforce.samples.analyticsapp;

import com.salesforce.samples.analyticsapp.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
public class GraphActivity extends Activity {
	Button efforts;
	public static double[] percent_array; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.graph_main);

		Bundle b=this.getIntent().getExtras();
		percent_array=b.getDoubleArray("percentages");

	
		PieChart effort = new PieChart();
		Intent effortIntent = effort.getIntent(this);
		startActivity(effortIntent);


	}

}
