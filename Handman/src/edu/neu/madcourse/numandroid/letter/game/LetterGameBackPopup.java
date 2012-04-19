package edu.neu.madcourse.numandroid.letter.game;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import edu.neu.madcourse.numandroid.R;
import edu.neu.madcourse.numandroid.SoundManager;

public class LetterGameBackPopup extends Activity implements OnClickListener {

	private static final String TAG = "LetterGameBackPopup";
	
	public static final String RESULT_ABORT = "result:abort";
	
	private long lastButtonClick;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.letter_game_back_popup);
		
		SoundManager.openGuess();
		
		((Button) findViewById(R.id.letter_game_back_abort_btn)).setOnClickListener(this);
		((Button) findViewById(R.id.letter_game_back_continue_btn)).setOnClickListener(this);
	}

	
	@Override
	public void onClick(View v) {
		
		Log.d(TAG, "onClick()");
		
		if (System.currentTimeMillis() - lastButtonClick < 750) {
			Log.d(TAG, "onClick(): double-click detected; aborting");
			return;
		}

		lastButtonClick = System.currentTimeMillis();
		
		SoundManager.btnClick();
		
		Intent resultIntent = new Intent();
		
		switch(v.getId()) {
		
		case R.id.letter_game_back_abort_btn:
			resultIntent.putExtra(RESULT_ABORT, true);
			break;
		
		case R.id.letter_game_back_continue_btn:
			resultIntent.putExtra(RESULT_ABORT, false);
			break;
		
		}
		
		setResult(RESULT_OK, resultIntent);
		
		finish();
	}
	
}
