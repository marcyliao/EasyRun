package ca.ece.utoronto.ece1780.runningapp.view.fragment.statistics;

import ca.ece.utoronto.ece1780.runningapp.data.ActivityRecord;
import android.support.v4.app.Fragment;

public class CaloriesVSTimeFragment extends Fragment  {
	private ActivityRecord record;

	public void setRecord(ActivityRecord record) {
		this.record = record;
	}
}
