package ca.ece.utoronto.ece1780.runningapp.view.listener;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public abstract class OnGestureListener implements View.OnTouchListener {

	ArrayList<Pointer> pointers;
	final String DEBUG_TAG = "TouchListener";
	Context context;
	
	public OnGestureListener(Context context){
		
		this.context = context;
		
		pointers = new ArrayList<Pointer>();
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		// get the action type of one touch action
		int actionType = MotionEventCompat.getActionMasked(event);

		switch (actionType) {
		// if the prime pointer press down
		case MotionEvent.ACTION_DOWN: {
			Pointer pointer = new Pointer();
			// get the index of the pointer
			int pointerIndex = MotionEventCompat.getActionIndex(event);
			// initialize the variable for primePointer
			pointer.Id = MotionEventCompat.getPointerId(event, pointerIndex);
			pointer.startX = MotionEventCompat.getX(event, pointerIndex);
			pointer.startY = MotionEventCompat.getY(event, pointerIndex);
			pointer.startTime = event.getDownTime();
			//add the pointer into the pointer array list
			pointers.add(pointer);
			
			
			Log.d(DEBUG_TAG, "Prime Down " + pointer.Id + "  " + pointer.startX + "  " + pointer.startY);
			
			break;
		}
		// if the second pointer pree down
		case MotionEvent.ACTION_POINTER_DOWN: {
			Pointer pointer = new Pointer();

			// get the index of the pointer
			int pointerIndex = MotionEventCompat.getActionIndex(event);
			// initialize the variable for the secondPointer
			pointer.Id = MotionEventCompat.getPointerId(event, pointerIndex);
			pointer.startX = MotionEventCompat.getX(event, pointerIndex);
			pointer.startY = MotionEventCompat.getY(event, pointerIndex);
			pointer.startTime = event.getDownTime();
			//add the pointer into the pointer array list
			pointers.add(pointer);
			
			Log.d(DEBUG_TAG, "Second Down " + pointer.Id + "  " + pointer.startX + "  " + pointer.startY);
			break;
		}
		
		case MotionEvent.ACTION_MOVE:{
			
			Pointer pointer = pointers.get(0);
			
			int historySize = event.getHistorySize();
			
			for(int i = 0; i < historySize; i++)
				System.out.println(i + "  " + event.getHistoricalX(i) + " : " + event.getHistoricalY(i));
			
			System.out.println("e  " +  MotionEventCompat.getX(event, 0) + "  " + MotionEventCompat.getY(event, 0));				
			System.out.println();
			
			Log.d(DEBUG_TAG, "Pointer Move " + pointer.Id + "  " + pointer.startX + "  " + pointer.startY);
			break;
		}

		case MotionEvent.ACTION_POINTER_UP: {
			
			// get the index of the pointer which triggers the action
			int pointerIndex = event.getActionIndex();
			//get the pointer id for the pointer above
			int pointerId = MotionEventCompat.getPointerId(event, pointerIndex);
			//set pointer endX and endY
			Pointer pointer = finaPointerById(pointerId);
			if (pointer != null){
				
				pointer.endX = MotionEventCompat.getX(event, pointerIndex);
				pointer.endY = MotionEventCompat.getY(event, pointerIndex);
				pointer.endTime = event.getEventTime();
			}
			break;
		}
		
		case MotionEvent.ACTION_UP: {
			
			//get the pointer id for the last pointer
			int pointerId = MotionEventCompat.getPointerId(event, 0);
			Pointer pointer = finaPointerById(pointerId);
			pointer.endX = MotionEventCompat.getX(event, 0);
			pointer.endY = MotionEventCompat.getY(event, 0);
			pointer.endTime = event.getEventTime();
			
			//if it is a multi finger Gesture
			if(isDoubleFingerGesture()){

				DoubleFingerGesture(pointers.get(0), pointers.get(1));
			}
			//if it is a single finger Gesture
			else {
				
				SingleFingerGesture(pointers.get(0));
			}
			

			//reset all the variables to invariable states.
			init();
		
			break;
		}
		
		case MotionEvent.ACTION_CANCEL : {
				init();
				break;
		}


		default : 
			Log.d(DEBUG_TAG, "The Action is " + actionType);
			break;
		}// end of switch

		return true;
	}// end of onTouch
	
	private boolean isDoubleFingerGesture(){
		
		if (pointers.size() == 2)
			return true;
		
		return false;
	}
	
	private Pointer finaPointerById (int pointerId){
		
		for (Pointer pointer : pointers){
			
			if (pointer.Id == pointerId)
				return pointer;
		}
		
		return null;
	}
	
	private void SingleFingerGesture(Pointer primePointer) {
		
		float primeDistanceX = primePointer.endX - primePointer.startX;
		float primeDistanceY = primePointer.endY - primePointer.startY;
	

		Log.d(DEBUG_TAG, "duration : " + (primePointer.endTime - primePointer.startTime));

		if( square(primeDistanceX) < square(primeDistanceY)){
			
			if(primeDistanceY > Pointer.Y_TRAVEL_BENCHEMARK){
				oneFingerTop2Bottom();
			}
			else if(primeDistanceY < 0 - Pointer.Y_TRAVEL_BENCHEMARK){
				oneFingerBottom2Top();
			}
			else if(primePointer.endTime - primePointer.startTime > Pointer.LONGPRESS_BENCHEMARK){
				oneFingerLongPress();
			}
				
			else{
				oneFingerSingleClick();
			}
		}
		// if the main movement is from X
		else{
			
			if(primeDistanceX > Pointer.X_TRAVEL_BENCHEMARK){
				oneFingerLeft2Right();
			}
			else if(primeDistanceX < 0 - Pointer.X_TRAVEL_BENCHEMARK){
				oneFingerRight2Left();
			}
			else if(primePointer.endTime - primePointer.startTime > Pointer.LONGPRESS_BENCHEMARK){
				oneFingerLongPress();
			}
			else{
				oneFingerSingleClick();
			}
						
		}
	}
	
	public void oneFingerTop2Bottom(){};
	
	public void oneFingerBottom2Top(){};
	
	public void oneFingerLeft2Right(){};
	
	public void oneFingerRight2Left(){};
	
	public void oneFingerSingleClick(){};
	
	public void oneFingerLongPress(){};
	
	public void twoFingersIncreaseDistance(){};
	
	public void twoFingersDecreaseDistance(){};
	
	public void twoFingersTop2Bottom(){};
	
	public void twoFingersBottom2Top(){};
	
	public void twoFingersLeft2Right(){};
	
	public void twoFingersRight2Left(){};
	
	public void twoFingersSingleClick(){};

	private void DoubleFingerGesture(Pointer primePointer, Pointer secondPointer){
		
		
		float startDistance = (float) Math.sqrt(square(primePointer.startX - secondPointer.startX) + square(primePointer.startY - secondPointer.startY));
		float endDistance   = (float) Math.sqrt(square(primePointer.endX - secondPointer.endX)     + square(primePointer.endY - secondPointer.endY));
		
		
		if(endDistance > startDistance + Pointer.DISTANCE_BENCHEMARK){
			twoFingersIncreaseDistance();
			return;
		}
		else if (endDistance < startDistance - Pointer.DISTANCE_BENCHEMARK){
			twoFingersDecreaseDistance();
			return;
		}
		
		float primeDistanceX  = primePointer.endX - primePointer.startX;
		float primeDistanceY  = primePointer.endY - primePointer.startY;
		float secondDistanceX = secondPointer.endX - secondPointer.startX;
		float secondDistanceY = secondPointer.endY - secondPointer.startY;
		
		// if the main movement is from Y
		if( sumOfSquare(primeDistanceX, secondDistanceX)  < sumOfSquare(primeDistanceY, secondDistanceY) ){

			if(primeDistanceY > Pointer.Y_TRAVEL_BENCHEMARK && secondDistanceY > Pointer.Y_TRAVEL_BENCHEMARK)
				twoFingersTop2Bottom();
			
			else if (primeDistanceY < 0 - Pointer.Y_TRAVEL_BENCHEMARK && secondDistanceY < 0 - Pointer.Y_TRAVEL_BENCHEMARK)
				twoFingersBottom2Top();
			
			else
				twoFingersSingleClick();
		} 
		//if the main movement is from X	
		else {

			if(primeDistanceX > Pointer.X_TRAVEL_BENCHEMARK && secondDistanceX > Pointer.X_TRAVEL_BENCHEMARK)
				twoFingersLeft2Right();
			
			else if (primeDistanceX < 0 - Pointer.X_TRAVEL_BENCHEMARK && secondDistanceX < 0 - Pointer.X_TRAVEL_BENCHEMARK)
				twoFingersLeft2Right();
			
			else
				twoFingersSingleClick();
		}
		
	}//end of MultiFingerGesture

	void init(){
		pointers.clear();
	}
	
	private float square (float a){
		return a * a;
	}
	
	private float sumOfSquare(float a, float b){
		return square(a) + square(b);
	}
	
	private class Pointer{
		
		int Id;
		
		float startX, startY;
		
		float endX, endY;
		
		long startTime, endTime;
		
		//set some benchmarks to compare variables
		static final float X_TRAVEL_BENCHEMARK = 100f;
		static final float Y_TRAVEL_BENCHEMARK = 100f;
		static final float DISTANCE_BENCHEMARK = 100f;
		static final long  LONGPRESS_BENCHEMARK = 800;

		//set some constant to represent invalid states
		static final int INVALID_POINTER_ID = -1;
		static final float INVALID_POINTER_POSITION = -0.1f;
		
		private Pointer(){
			
			init();
		}
		
		private void init(){
			
			Id = INVALID_POINTER_ID;
			
			startX = INVALID_POINTER_POSITION;
			startY = INVALID_POINTER_POSITION;
			endX = INVALID_POINTER_POSITION;
			endY = INVALID_POINTER_POSITION;
		}
	}// end of Pointer
	
}// end of TouchListener



