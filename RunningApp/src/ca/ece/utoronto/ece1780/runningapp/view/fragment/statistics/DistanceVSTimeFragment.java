package ca.ece.utoronto.ece1780.runningapp.view.fragment.statistics;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import ca.ece.utoronto.ece1780.runningapp.data.ActivityRecord;
import ca.ece.utoronto.ece1780.runningapp.view.R;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class DistanceVSTimeFragment extends Fragment  {

	private ActivityRecord record;
	private View rootView;
	
	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer mRenderer = StaticsRenderConfig.getBasicRender();
	private GraphicalView mChartView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_statistic_speed,
				container, false);

		record = (ActivityRecord) getArguments().getSerializable("record");
		prepareWidgets();

		return rootView;
	}

	private void prepareWidgets() {
		prepareChart();
	}

	private void prepareChart() {

		mRenderer.setYAxisMin(0);
		mRenderer.setYTitle("distance (m)");
		mRenderer.setXTitle("time (min)");
        // create a new series of data
		XYSeries series = new XYSeries("Distance VS Time");
		
		
		double distance = 0;
		series.add(0, 0);
		int size = record.getLocationPointsTime().size();
		for(int i=0; i<size-1; i+=1) {
			distance += (record.getLocationPoints().get(i+1).distanceTo(record.getLocationPoints().get(i)));
			if(i%10 == 0) {
				series.add(Float.valueOf(record.getLocationPointsTime().get(i).getTime()-record .getTime())/60000, distance);
			}
		}

		series.add(Float.valueOf(record.getLocationPointsTime().get(size-1).getTime()-record .getTime())/60000, distance);
		
		/*
		double distance = 0;
		for(int i=0; i<record.getSpeedRecords().size(); i+=10) {
			series.add(record.getLocationPointsTime().get(i).getTime()-record .getTime(), distance);
			if(i != record.getSpeedRecords().size()-1) {
				distance += (record.getLocationPoints().get(i+1).distanceTo(record.getLocationPoints().get(i)));
			}
		}
		*/
		/*
		double distance = 0;
		double speed = 0;
		for(int i=1; i<record.getSpeedRecords().size(); i+=1) {
			speed += record.getSpeedRecords().get(i);
			if(i%10 == 0) {
				series.add(distance, speed/10);
				speed = 0;
			}
			
			if(i != record.getSpeedRecords().size()-1) {
				distance += (record.getLocationPoints().get(i+1).distanceTo(record.getLocationPoints().get(i)));
			}
		}
		
		mDataset.addSeries(series);
		*/
		/*
		double distance = 0;
		Long lastTime = record.getLocationPointsTime().get(0).getTime();
		int lastUnit = 1;
		for(int i=1; i<record.getSpeedRecords().size(); i+=1) {
			if(distance > (lastUnit)*100){
				Long currentTime = record.getLocationPointsTime().get(i).getTime();
				Long length = currentTime - lastTime;
				double speed = (distance-(lastUnit-1)*100)/(length/3600);
				if(speed>200)
					speed = 0;
				series.add(lastUnit, speed);
				Log.d("spead",Math.round(speed*10)/10+"");
				lastUnit = (int)(distance/100)+1;
				lastTime = currentTime;
			}
			
			if(i != record.getSpeedRecords().size()-1) {
				distance += (record.getLocationPoints().get(i+1).distanceTo(record.getLocationPoints().get(i)));
			}
		}
		*/
		mDataset.clear();
		mDataset.addSeries(series);

        // create a new renderer for the new series
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setDisplayChartValues(true);
        renderer.setColor(Color.GREEN);
        renderer.setDisplayChartValues(false);
        renderer.setLineWidth(5f);
        
        mRenderer.removeAllRenderers();
        mRenderer.addSeriesRenderer(renderer);
        
        // paint
        LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.chart);
	    mChartView = ChartFactory.getCubeLineChartView(rootView.getContext(), mDataset, mRenderer,0.5f);
        layout.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
        mChartView.repaint();
	}

	
}
