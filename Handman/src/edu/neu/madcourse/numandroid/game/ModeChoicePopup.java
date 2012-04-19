package edu.neu.madcourse.numandroid.game;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import edu.neu.madcourse.numandroid.R;
import edu.neu.madcourse.numandroid.SoundManager;

public class ModeChoicePopup extends Activity implements OnClickListener {

	private static final String TAG = "ModeChoicePopup";
	
	public static final String RESULT_MODE = "result:mode";
	
	private long lastButtonClick; // for button double-click prevention

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "Creating popup");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mode_choice_popup);

		((Button) findViewById(R.id.mode_choice_upper_btn)).setOnClickListener(this);
		((Button) findViewById(R.id.mode_choice_lower_btn)).setOnClickListener(this);
	}

	
	@Override
	public void onClick(View v) {
		if (System.currentTimeMillis() - lastButtonClick < 500) {
			Log.d(TAG, "Double-click detected. Aborting click.");
			return;
		}

		lastButtonClick = System.currentTimeMillis();

		SoundManager.btnClick();
		
		Intent resultIntent = new Intent();
		
		if (v.getId() == R.id.mode_choice_upper_btn)
			resultIntent.putExtra(RESULT_MODE, GameActivity.MODE_UPPER);
		else
			resultIntent.putExtra(RESULT_MODE, GameActivity.MODE_LOWER);
		
		setResult(RESULT_OK, resultIntent);
		
		finish();
	}
	
	
	@Override
	public void onBackPressed() {
		Log.d(TAG, "onBackPressed()");
		
		setResult(RESULT_CANCELED);
		
		super.onBackPressed();
	}
}
