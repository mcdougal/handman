package edu.neu.madcourse.numandroid.letter.game;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import edu.neu.madcourse.numandroid.R;

public class LetterGameHelpPopup extends Activity implements OnClickListener {

	private static final String TAG = "LetterGameHelpPopup";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.letter_game_help_popup);
		
		((Button) findViewById(R.id.letter_game_help_popup_ok_btn)).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Log.d(TAG, "onClick()");
		
		finish();
	}
	
}
