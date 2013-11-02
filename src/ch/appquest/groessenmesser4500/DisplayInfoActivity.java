package ch.appquest.groessenmesser4500;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayInfoActivity extends Activity {

	Button btnMeasure;
	Button btnCalc;
	EditText aEditText;
	EditText bEditText;
	EditText alphaEditText;
	EditText betaEditText;
	double alpha;
	double beta;
	int CAMERA_INTENT_REQUEST_CODE=0;
	boolean measured=false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_info);
		btnMeasure=(Button)findViewById(R.id.buttonMeasure);
		btnMeasure.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(getApplicationContext(), MeasureActivity.class);
				try{
					startActivityForResult(intent, CAMERA_INTENT_REQUEST_CODE);
				}
				catch(Exception e){
					Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
				}
				
			}
			
		});
		btnCalc=(Button)findViewById(R.id.buttonCalc);
		btnCalc.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				aEditText=(EditText)findViewById(R.id.EditTextA);
				if(!aEditText.getText().toString().equalsIgnoreCase(""))
					calcRes();
			}
			
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuItem logMenuItem = menu.add(R.string.logtext);
		logMenuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				TextView bTextView = (TextView) findViewById(R.id.EditTextB);
				try {
					log(bTextView.getText().toString());
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), "Could not log.",
							Toast.LENGTH_LONG).show();
				}
				return false;
			}
		});
		super.onCreateOptionsMenu(menu);
		return true;
	}
	
	public void onActivityResult(int RequestCode,int ResultCode,Intent intent){
		if(RequestCode==CAMERA_INTENT_REQUEST_CODE){
			if(ResultCode==RESULT_OK){
				TextView alphatextview=(TextView)findViewById(R.id.EditTextAlpha);
				TextView betatextview=(TextView)findViewById(R.id.EditTextBeta);
				alpha=intent.getDoubleExtra("alpha", 0);
				beta=intent.getDoubleExtra("beta", 0);
				alphatextview.setText(Double.toString(alpha));
				betatextview.setText(Double.toString(beta));
				measured=true;
			}
		}
	}
	
	private void log(String result) {
		Intent intent = new Intent("ch.appquest.intent.LOG");

		if (getPackageManager().queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY).isEmpty()) {
			Toast.makeText(this, "Logbook App not Installed", Toast.LENGTH_LONG)
					.show();
			return;
		}

		intent.putExtra("ch.appquest.taskname", "Groessenmesser");
		intent.putExtra("ch.appquest.logmessage", result);

		startActivity(intent);
	}
	
	private void calcRes(){
		bEditText=(EditText)findViewById(R.id.EditTextB);
		alphaEditText=(EditText)findViewById(R.id.EditTextAlpha);
		betaEditText=(EditText)findViewById(R.id.EditTextBeta);
		try{
			double distance=Double.parseDouble(aEditText.getText().toString());
			double res=(distance/Math.tan(Math.toRadians(alpha)))+(distance/Math.tan(Math.toRadians(180-(alpha+beta))));
			bEditText.setText(Double.toString(res));
		}
		catch(Exception e){
			Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
		}
		
	}

}
