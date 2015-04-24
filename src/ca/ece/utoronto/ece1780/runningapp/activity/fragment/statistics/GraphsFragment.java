package ca.ece.utoronto.ece1780.runningapp.activity.fragment.statistics;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import ca.ece.utoronto.ece1780.runningapp.data.ActivityRecord;
import ca.ece.utoronto.ece1780.runningapp.utility.FormatProcessor;
import ca.ece.utoronto.ece1780.runningapp.activity.R;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class GraphsFragment extends Fragment  {

	private ActivityRecord record;
	private View rootView;
	
	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer mRenderer = getBasicRender();
	private GraphicalView mChartView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_statistic_graphs,
				container, false);

		record = (ActivityRecord) getArguments().getSerializable("record");
		prepareWidgets();

		return rootView;
	}

	private void prepareWidgets() {
		prepareChart();
	}

	private void prepareChart() {

		mRenderer.setYAxisMin(0,0);
		mRenderer.setYAxisMin(0,1);
		
		FormatProcessor fp = new FormatProcessor(getActivity());
		
		mRenderer.setYTitle("distance ("+fp.getDistanceUnit()+")",0);
		mRenderer.setYTitle("speed ("+fp.getSpeedUnit()+")",1);
		
		mRenderer.setYLabelsColor(1, Color.YELLOW);
		mRenderer.setYLabelsColor(0, Color.GREEN);

		mRenderer.setYLabelsColor(0, Color.GREEN);
		
		mRenderer.setYAxisAlign(Align.RIGHT, 0);
		mRenderer.setYAxisAlign(Align.LEFT, 1);
		
		mRenderer.setYAxisMax(25,1);
		
		mRenderer.setXTitle("time (min)");
        // create a new series of data
		XYSeries seriesDistance = new XYSeries("Distance VS Time",0);
		XYSeries seriesSpeed = new XYSeries("Speed VS Time",1);
		
		
		double distance = 0;
		
		long lastTime = 0;
		double lastDistance = 0;
		
		seriesDistance.add(0, 0);
		int size = record.getLocationPointsTime().size();
		for(int i=1; i<size-1; i+=1) {
			long currentTime = record.getLocationPointsTime().get(i).getTime()-record .getTime();
			distance += (record.getLocationPoints().get(i+1).distanceTo(record.getLocationPoints().get(i)));
			
			if(i%10 == 0) {
				seriesDistance.add(Float.valueOf(currentTime)/60000, fp.getDistanceValue(distance));
			}
			
			if(i%10 == 0) {
				float speed = (float)(distance - lastDistance)/((currentTime-lastTime)/3600.0f);
				seriesSpeed.add(Float.valueOf(lastTime)/60000, speed);
				Log.v("speed",""+speed);
				
				lastTime = currentTime;
				lastDistance = distance;
			}
		}

		if(record.getLocationPointsTime().size()>0){
			seriesDistance.add(Float.valueOf(record.getLocationPointsTime().get(size-1).getTime()-record .getTime())/60000, fp.getDistanceValue(distance));
		}
		
		mDataset.clear();
		mDataset.addSeries(seriesDistance);
		mDataset.addSeries(seriesSpeed);

        // create a new renderer for the new series
        XYSeriesRenderer rendererDistance = new XYSeriesRenderer();
        rendererDistance.setDisplayChartValues(true);
        rendererDistance.setColor(Color.GREEN);
        rendererDistance.setDisplayChartValues(false);
        rendererDistance.setLineWidth(5f);
        
        XYSeriesRenderer rendererSpeed = new XYSeriesRenderer();
        rendererSpeed.setDisplayChartValues(true);
        rendererSpeed.setColor(Color.YELLOW);
        rendererSpeed.setDisplayChartValues(false);
        rendererSpeed.setLineWidth(5f);
        
        mRenderer.removeAllRenderers();
        mRenderer.addSeriesRenderer(rendererDistance);
        mRenderer.addSeriesRenderer(rendererSpeed);
        
        // paint
        LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.chart);
	    mChartView = ChartFactory.getCubeLineChartView(rootView.getContext(), mDataset, mRenderer,0.5f);
        layout.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
        mChartView.repaint();
	}
	
	private XYMultipleSeriesRenderer getBasicRender() {
		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer(2);
		// set up simple chart render
		mRenderer.setBackgroundColor(Color.argb(100, 50, 50, 50));
		mRenderer.setChartTitleTextSize(20);
		mRenderer.setLabelsTextSize(15);
		mRenderer.setLegendTextSize(15);
		mRenderer.setMargins(new int[] { 40, 40, 40, 40 });
		mRenderer.setInScroll(false);
		mRenderer.setShowGridX(true);
		mRenderer.setXLabels(10);
		mRenderer.setLabelsTextSize(22f);
		mRenderer.setYLabels(10);
		mRenderer.setAxesColor(Color.WHITE);
		mRenderer.setAxisTitleTextSize(22f);
		mRenderer.setClickEnabled(false);
		mRenderer.setYLabelsPadding(10);
		mRenderer.setYLabelsPadding(-30f);
		mRenderer.setPanEnabled(false);
		mRenderer.setAxisTitleTextSize(22f);
		mRenderer.setLegendTextSize(22f); 
		
		return mRenderer;
	}

	
}
