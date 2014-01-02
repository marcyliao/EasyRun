package ca.ece.utoronto.ece1780.runningapp.utility;

import java.util.Date;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.view.View;
public class ShareUtility {
	public void share(Activity activity, String content, Uri uri){  
	    Intent shareIntent = new Intent(Intent.ACTION_SEND);   
	    if(uri!=null){  
	        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);  
	        shareIntent.setType("image/*");   
	        shareIntent.putExtra("sms_body", content);  
	    }else{  
	        shareIntent.setType("text/plain");   
	    }  
	    shareIntent.putExtra(Intent.EXTRA_TEXT, content);
	    activity.startActivity(shareIntent);  
	}
	
	public void share(Activity activity, String content, Bitmap image){  
	    Intent shareIntent = new Intent(Intent.ACTION_SEND);   
	    if(image!=null){   

	    	String path = Images.Media.insertImage(activity.getContentResolver(), image, ""+new Date().getTime(), null);
	    	Uri screenshotUri = Uri.parse(path);
	    	
	        shareIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);  
	        shareIntent.setType("image/*");   
	        shareIntent.putExtra("sms_body", content);  
	    }else{  
	        shareIntent.setType("text/plain");   
	    }  
	    shareIntent.putExtra(Intent.EXTRA_TEXT, content);
	    activity.startActivity(shareIntent);  
	}
	
	public Bitmap screenShot(final Activity activity, final View mView, GoogleMap map) {
		
		final Bitmap bitmap = null;
		
		SnapshotReadyCallback callback = new SnapshotReadyCallback() {
			 @Override
	         public void onSnapshotReady(Bitmap snapshot) {

				 Bitmap bitmap = Bitmap.createBitmap(mView.getWidth(),
				    	mView.getHeight(), Config.ARGB_8888);
				 
				 Canvas canvas = new Canvas(bitmap);
				 canvas.drawBitmap(snapshot, new Matrix(), null);
				 mView.draw(canvas);

			 }
		};
		
		map.snapshot(callback);
		
	    return bitmap;
	}
	
	public void share(final Activity activity, final String content, final View view, GoogleMap map) {
		
		SnapshotReadyCallback callback = new SnapshotReadyCallback() {
			 @Override
	         public void onSnapshotReady(Bitmap snapshot) {

				 view.setDrawingCacheEnabled(true);
				 Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
						 view.getHeight(), Config.ARGB_8888);
				 
				 Canvas canvas = new Canvas(bitmap);
				 canvas.drawBitmap(snapshot, new Matrix(), null);
				 canvas.drawBitmap(view.getDrawingCache(), 0, 0, null);
				 
				 share(activity,content,bitmap);
			 }
		};
		
		map.snapshot(callback);
		
	}
}
