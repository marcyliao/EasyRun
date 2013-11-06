package ca.ece.utoronto.ece1780.runningapp.view.fragment.statistics;

import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.graphics.Color;

public class StaticsRenderConfig {

	public static XYMultipleSeriesRenderer getBasicRender() {
		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
		// set up simple chart render
		mRenderer.setBackgroundColor(Color.argb(100, 50, 50, 50));
		mRenderer.setChartTitleTextSize(20);
		mRenderer.setLabelsTextSize(15);
		mRenderer.setLegendTextSize(15);
		mRenderer.setMargins(new int[] { 20, 30, 15, 0 });
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
		
		return mRenderer;
	}
}
