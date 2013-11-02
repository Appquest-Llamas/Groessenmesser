package ch.appquest.groessenmesser4500;


import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MeasureActivity extends Activity implements
		SurfaceHolder.Callback, Camera.PictureCallback, SensorEventListener {

	private Camera cam;
	private Camera.PictureCallback camCallbackVorschau;
	private Camera.ShutterCallback camCallbackVerschluss;
	private SurfaceHolder camViewHolder;

	private final float[] magneticFieldData = new float[3];
	private final float[] accelerationData = new float[3];

	double currentRotationValue;
	double alpha;
	double beta;
	boolean isBeta = false;

	SensorManager sensorManager;
	Sensor magneticSen;
	Sensor accelerationSen;
	
	int camId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_measure);
		try {
			sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
			magneticSen = sensorManager
					.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
			accelerationSen = sensorManager
					.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.getMessage(),
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.measure, menu);
		return true;
	}

	@Override
	public void onPictureTaken(byte[] arg0, Camera arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int format, int width,
			int height) {
		// TODO Auto-generated method stub

		try {
			cam.stopPreview();
			setCameraDisplayOrientation();
			cam.setPreviewDisplay(camViewHolder);
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.getMessage(),
					Toast.LENGTH_LONG).show();
		}
		cam.startPreview();
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		try {
			camId=getCameraGingerbread();
			if(camId!=-1){
				cam=Camera.open(camId);
				setCameraDisplayOrientation();
			}
			else{
				Toast.makeText(getApplicationContext(), "No Camera detected", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), e.getMessage(),
					Toast.LENGTH_LONG).show();
		}
	}
	private int getCameraGingerbread() {
	    int cameraCount = 0;
	    int cam=-1;
	    Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
	    cameraCount = Camera.getNumberOfCameras();
	    for ( int camIdx = 0; camIdx < cameraCount; camIdx++ ) {
	        Camera.getCameraInfo( camIdx, cameraInfo );
	        if ( cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK  )
	                cam =camIdx;
	        else if(cameraInfo.facing==Camera.CameraInfo.CAMERA_FACING_FRONT)
	        		cam =camIdx;
	    }

	    return cam;
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub

	}

	public boolean onTouchEvent(MotionEvent event) {
		// Bei Berührung des Bildschirms
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (!isBeta) {
				alpha = getCurrentRotationValue();
				Toast.makeText(getApplicationContext(), "Alpha: "+alpha, Toast.LENGTH_SHORT).show();
				isBeta = true;
			} else {
				beta = getCurrentRotationValue() - alpha;
				Toast.makeText(getApplicationContext(), "Beta: " +beta, Toast.LENGTH_SHORT).show();
				Intent output = new Intent();
				output.putExtra("alpha", alpha);
				output.putExtra("beta", beta);
				setResult(RESULT_OK, output);
				finish();
			}
			return true;
		} else {
			return super.onTouchEvent(event);
		}
	}

	protected void onPause() {
		super.onPause();

		if (cam != null) {
			cam.stopPreview();
			cam.release();
		}

		sensorManager.unregisterListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		SurfaceView cameraView = (SurfaceView) findViewById(R.id.camSurfaceView);
		camViewHolder = cameraView.getHolder();
		camViewHolder.addCallback(this);

		camCallbackVorschau = new Camera.PictureCallback() {
			public void onPictureTaken(byte[] data, Camera c) {
			}
		};

		camCallbackVerschluss = new Camera.ShutterCallback() {
			public void onShutter() {
			}
		};

		if (magneticSen != null) {
			sensorManager.registerListener(this, magneticSen,
					SensorManager.SENSOR_DELAY_NORMAL);
		}
		if (accelerationSen != null) {
			sensorManager.registerListener(this, accelerationSen,
					SensorManager.SENSOR_DELAY_NORMAL);
		}

	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		if (event.sensor == accelerationSen) {
			System.arraycopy(event.values, 0, accelerationData, 0, 3);
		}

		if (event.sensor == magneticSen) {
			System.arraycopy(event.values, 0, magneticFieldData, 0, 3);
		}
	}

	private double getCurrentRotationValue() {
		float[] rotationMatrix = new float[16];

		if (SensorManager.getRotationMatrix(rotationMatrix, null,
				accelerationData, magneticFieldData)) {

			float[] orientation = new float[4];
			SensorManager.getOrientation(rotationMatrix, orientation);

			double neigung = Math.toDegrees(orientation[2]);

			return Math.abs(neigung);
		}

		return 0;
	}

	public void setCameraDisplayOrientation()
	{
	    android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
	    android.hardware.Camera.getCameraInfo(camId, info);
	    int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
	    int degrees = 0;
	    switch (rotation)
	    {
	    case Surface.ROTATION_0:
	        degrees = 0;
	        break;
	    case Surface.ROTATION_90:
	        degrees = 90;
	        break;
	    case Surface.ROTATION_180:
	        degrees = 180;
	        break;
	    case Surface.ROTATION_270:
	        degrees = 270;
	        break;
	    }

	    int result;
	    if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
	    {
	        result = (info.orientation + degrees) % 360;
	        result = (360 - result) % 360; // compensate the mirror
	    }
	    else
	    { // back-facing
	        result = (info.orientation - degrees + 360) % 360;
	    }
	    cam.setDisplayOrientation(result);
	}
}
