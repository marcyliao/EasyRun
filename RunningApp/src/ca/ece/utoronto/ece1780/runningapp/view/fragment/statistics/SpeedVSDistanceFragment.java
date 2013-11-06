package ca.ece.utoronto.ece1780.runningapp.view.fragment.statistics;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.PointStyle;
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

public class SpeedVSDistanceFragment extends Fragment {

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
		mRenderer.setYAxisMax(20);
		mRenderer.setYTitle("Speed (km/h)");
		mRenderer.setXTitle("distance (m)");
        // create a new series of data
		XYSeries series = new XYSeries("Speed VS Distance");
		
		double distance = 0;
		Long lastTime = record.getLocationPointsTime().get(0).getTime();
		int lastUnit = 1;
		for(int i=1; i<record.getSpeedRecords().size(); i+=1) {
			if(distance > (lastUnit)*50){
				Long currentTime = record.getLocationPointsTime().get(i).getTime();
				Long length = currentTime - lastTime;
				double speed = (distance-(lastUnit-1)*50)/(length/3600);
				series.add(lastUnit*50, speed);
				Log.d("spead",Math.round(speed*10)/10+"");
				lastUnit = (int)(distance/50)+1;
				lastTime = currentTime;
			}
			
			if(i != record.getSpeedRecords().size()-1) {
				distance += (record.getLocationPoints().get(i+1).distanceTo(record.getLocationPoints().get(i)));
			}
		}
		mDataset.clear();
		mDataset.addSeries(series);

        // create a new renderer for the new series
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setDisplayChartValues(true);
        renderer.setColor(Color.YELLOW);
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
