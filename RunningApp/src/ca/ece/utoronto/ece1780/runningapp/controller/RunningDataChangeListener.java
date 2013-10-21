package ca.ece.utoronto.ece1780.runningapp.controller;

import ca.ece.utoronto.ece1780.runningapp.data.ActivityRecord;

public abstract class RunningDataChangeListener {
	public abstract void onDataChange(ActivityRecord record);
	public abstract void onLocationAdded(ActivityRecord record);
	public abstract void onGoalAchieved();
}
