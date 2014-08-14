package com.salesforce.samples.analyticsapp;

import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;

public class PieChart {
	public Intent getIntent(Context context){


		double []Performance = GraphActivity.percent_array; 
		CategorySeries series = new CategorySeries("pie"); // adding series to charts. //collect 3 value in array. therefore add three series.
		series.add("--------",Performance[0]);            
		series.add("Existing Customer - Upgrade",Performance[1]);
		series.add("Existing Customer - Replacement",Performance[2]);
		series.add("Existing Customer - Downgrade",Performance[3]);
		series.add("New Customer",Performance[4]);
		// add three colors for three series respectively           
		int []colors = new int[]{Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE, Color.GRAY};
		// set style for series
		DefaultRenderer renderer = new DefaultRenderer();

		for(int color : colors){
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(color);
			r.setDisplayBoundingPoints(true);
			r.setDisplayChartValuesDistance(5);
			r.setDisplayChartValues(true);
			r.setChartValuesTextSize(20);
			renderer.addSeriesRenderer(r);
		}
		renderer.setInScroll(true);

		int[] margs = {10,10,10,10};
		renderer.setMargins(margs);
		renderer.setAntialiasing(false);
		renderer.setZoomButtonsVisible(true);   //set zoom button in Graph
		renderer.setApplyBackgroundColor(true);
		renderer.setBackgroundColor(Color.WHITE); //set background color
		renderer.setChartTitle("Salesforce Analytics API Graph");
		renderer.setAxesColor(Color.BLACK);
		renderer.setChartTitleTextSize((float) 40);
		renderer.setTextTypeface(null, Typeface.BOLD);
		renderer.setShowLabels(true);  
		renderer.setLabelsColor(Color.BLACK);
		renderer.setLabelsTextSize(30);
		renderer.setShowLegend(false);
		renderer.setDisplayValues(true);
		return ChartFactory.getPieChartIntent(context, series, renderer, "PieChart");
	}
}
