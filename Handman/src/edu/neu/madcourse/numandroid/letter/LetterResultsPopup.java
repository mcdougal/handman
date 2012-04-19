package edu.neu.madcourse.numandroid.letter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import edu.neu.madcourse.numandroid.R;
import edu.neu.madcourse.numandroid.SoundManager;
import edu.neu.madcourse.numandroid.game.GameActivity;

public class LetterResultsPopup extends Activity implements OnClickListener {

	private static final String TAG = "Hangman_LetterResultsPopup";
	
	public static final String INTENT_RESULT = "intent:result";
	public static final String INTENT_EXTRA_MSG = "intent:extra_msg";
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()");
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.letter_results_popup);

		int accuracy = getIntent().getIntExtra(INTENT_RESULT, 0);
		String extraMsg = getIntent().getStringExtra(INTENT_EXTRA_MSG);
		
		Log.d(TAG, "accuracy="+accuracy);
		Log.d(TAG, "extraMsg="+extraMsg);

		if (accuracy < GameActivity.ACCURACY_THRESHOLD)
			SoundManager.badAcc();
		else
			SoundManager.goodAcc();
		
		String header;
		if (extraMsg != null) {
			header = extraMsg+" "+getString(R.string.letter_result_accuracy_lbl);
		}
		else {
			header = getString(R.string.letter_result_accuracy_lbl);
		}
		
		((TextView) findViewById(R.id.letter_input_result_popup_msg)).setText(header);
		((TextView) findViewById(R.id.letter_input_result_accuracy)).setText(accuracy+"%");
		((Button) findViewById(R.id.letter_input_result_ok_btn)).setOnClickListener(this);
		
		setResult(Activity.RESULT_OK);
	}

	@Override
	public void onClick(View v) {
		SoundManager.btnClick();
		
		finish();
	}
	
}
