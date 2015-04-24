package ca.ece.utoronto.ece1780.runningapp.utility;

public class UtilityCaculator {
	private static final double K = 0.001036F;
	
	// Weight: kg, distance: m
	public static double computeColories(int weight, double distance) {
		return weight*distance*K;
	}
	
	// Duration: second
	public static String getFormatStringFromDuration(int duration) {
		if(duration<3600) {
			return String.format("%02d:%02d", (duration%3600)/60, (duration%60));
		}
		else {
			return String.format("%d:%02d:%02d", duration/3600, (duration%3600)/60, (duration%60));
		}
	}
	
	public static float square(float a){
		
		return a * a;
	}
	
	//
	public static float sumOFSquare(float a, float b){
		
		return a * a + b * b;
	}
}
