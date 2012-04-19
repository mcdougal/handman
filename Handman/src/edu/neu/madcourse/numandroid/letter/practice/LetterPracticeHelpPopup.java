package edu.neu.madcourse.numandroid.letter.practice;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import edu.neu.madcourse.numandroid.R;
import edu.neu.madcourse.numandroid.SoundManager;

public class LetterPracticeHelpPopup extends Activity implements OnClickListener {

	private static final String TAG = "LetterPracticeHelpPopup";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.letter_practice_help_popup);
		
		((Button) findViewById(
				R.id.letter_practice_help_popup_ok_btn)).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		SoundManager.btnClick();
		
		finish();
	}
	
}
