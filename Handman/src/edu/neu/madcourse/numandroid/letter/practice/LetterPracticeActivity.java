package edu.neu.madcourse.numandroid.letter.practice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import edu.neu.madcourse.numandroid.R;
import edu.neu.madcourse.numandroid.SoundManager;
import edu.neu.madcourse.numandroid.letter.DrawPath;
import edu.neu.madcourse.numandroid.letter.LetterActivity;
import edu.neu.madcourse.numandroid.letter.LetterResultsPopup;

public class LetterPracticeActivity extends LetterActivity implements OnClickListener {

	private static final String TAG = "LetterPracticeActivity";
	
	private static final String PREF_LAST_STATE = "pref:last_state";
	
	private static final int STATE_TRACE = 0;
	private static final int STATE_FREE = 1;
	private static final int STATE_SHOW = 2;
	
	
	//
	//  INSTANCE VARS
	//

	private int lastState;

	private Button eraseButton;
	private Button checkAccuracyButton;
	private Button freeDrawButton;
	private Button traceButton;
	private Button continueButton;
	
	private long lastButtonClick;
	
	
	//
	//  SHARED PREFERENCES
	//
	
	
	@Override
	protected void setDefaults() {
		super.setDefaults();
		lastState = STATE_TRACE;
	}
	
	
	@Override
	protected void loadPreferences() {
		super.loadPreferences();
		SharedPreferences pref = getPreferences(MODE_PRIVATE);
		
		lastState = pref.getInt(PREF_LAST_STATE, STATE_TRACE);
		
		Log.d(TAG, "lastState="+lastState);
	}

	
	@Override
	protected void savePreferences() {
		super.savePreferences();
		SharedPreferences pref = getPreferences(MODE_PRIVATE);
		
		pref.edit().putInt(PREF_LAST_STATE, lastState).commit();

		Log.d(TAG, "lastState="+lastState);
	}
	
	
	//
	//  INITIALIZATION
	//
	
	
	@Override
	protected void initializeViewVariables() {
		super.initializeViewVariables();
		
		ViewGroup buttons = 
			(ViewGroup) getLayoutInflater().inflate(R.layout.letter_practice_buttons, null);

		eraseButton = (Button) buttons.getChildAt(0);
		checkAccuracyButton = (Button) buttons.getChildAt(1);
		freeDrawButton = (Button) buttons.getChildAt(2);
		traceButton = (Button) buttons.getChildAt(3);
		continueButton = (Button) buttons.getChildAt(4);
		
		eraseButton.setOnClickListener(this);
		checkAccuracyButton.setOnClickListener(this);
		freeDrawButton.setOnClickListener(this);
		traceButton.setOnClickListener(this);
		continueButton.setOnClickListener(this);

		ViewGroup footer = (ViewGroup) findViewById(R.id.letter_footer);
		
		footer.addView(buttons, 0);
	}
	
	
	//
	//  MENU
	//
	
	
	/**
	 * Creates the options menu.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.letter_practice_menu, menu);
		return true;
	}

	/**
	 * Handles selection of menu options.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		
		case R.id.letter_practice_menu_help:
			showHelpPopup();
			return true;
			
		}
		
		return false;
	}
	
	
	//
	//  CLICK HANDLING
	//


	@Override
	public void onClick(View v) {
		Log.d(TAG, "onClick()");
		
		if (System.currentTimeMillis() - lastButtonClick < 500) {
			Log.d(TAG, "onClick(): double-click detected; aborting");
			return;
		}

		lastButtonClick = System.currentTimeMillis();
		
		switch(v.getId()) {
			
		case R.id.letter_practice_erase_button:
			SoundManager.erase();
			path = new DrawPath();
			break;
		
		case R.id.letter_practice_check_accuracy_button:
			SoundManager.btnClick();
			setShowState();
			break;
		
		case R.id.letter_practice_free_draw_button:
			SoundManager.deepClick();
			path = new DrawPath();
			setFreeDrawState();
			break;
			
		case R.id.letter_practice_trace_button:
			SoundManager.deepClick();
			path = new DrawPath();
			setTraceState();
			break;
			
		case R.id.letter_practice_continue_button:
			SoundManager.btnClick();
			if (lastState == STATE_SHOW)
				state = STATE_TRACE;
			else
				state = lastState;
			
			setCurrentState();
			break;
		
		}
	}
	
	
	//
	//  POPUPS
	//

	
	protected void showHelpPopup() {
		Intent i = new Intent(this, LetterPracticeHelpPopup.class);
		startActivity(i);
	}


	@Override
	protected String getExtraResultMsg(int result) {
		return "";
	}
	
	
	
	/***************************************************************************
	 * 
	 * 
	 *   STATES
	 * 
	 * 
	 ***************************************************************************/
	
	
	@Override
	protected void setCurrentState() {
		switch(state) {
		
		case STATE_TRACE: 	setTraceState(); break;
		case STATE_FREE: 	setFreeDrawState(); break;
		case STATE_SHOW: 	setShowState(); break;
		
		}
	}
	
	
	//
	//  TRACE STATE
	//
	
	
	private void setTraceState() {
		Log.d(TAG, "setTraceState()");
		
		state = STATE_TRACE;

		blockDrawing = false;
		showLetter = true;
		
		eraseButton.setVisibility(View.VISIBLE);
		checkAccuracyButton.setVisibility(View.VISIBLE);
		freeDrawButton.setVisibility(View.VISIBLE);
		traceButton.setVisibility(View.GONE);
		continueButton.setVisibility(View.GONE);
	}
	
	
	//
	//  FREE DRAW STATE
	//
	
	
	private void setFreeDrawState() {
		Log.d(TAG, "setFreeDrawState()");
		
		state = STATE_FREE;

		blockDrawing = false;
		showLetter = false;
		
		eraseButton.setVisibility(View.VISIBLE);
		checkAccuracyButton.setVisibility(View.VISIBLE);
		freeDrawButton.setVisibility(View.GONE);
		traceButton.setVisibility(View.VISIBLE);
		continueButton.setVisibility(View.GONE);
	}
	
	
	//
	//  SHOW STATE
	//
	
	
	private void setShowState() {
		Log.d(TAG, "setShowState()");
		
		lastState = state;
		state = STATE_SHOW;
		
		blockDrawing = true;
		showLetter = true;
		
		eraseButton.setVisibility(View.GONE);
		checkAccuracyButton.setVisibility(View.GONE);
		freeDrawButton.setVisibility(View.GONE);
		traceButton.setVisibility(View.GONE);
		continueButton.setVisibility(View.GONE);
		
		if (!isCalculatingAccuracy())
			calculateAccuracy();
	}
	
	
	//
	//  ACCURACY CHECK
	//
	

	@Override
	protected void accuracyCalculatedCallback() {
		Intent i = new Intent(this, LetterResultsPopup.class);
		i.putExtra(LetterResultsPopup.INTENT_RESULT, accuracy);
		i.putExtra(LetterResultsPopup.INTENT_EXTRA_MSG, getExtraResultMsg(accuracy));
		startActivityForResult(i, REQUEST_RESULT_POPUP_CLOSED);
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
		
        if (requestCode == REQUEST_RESULT_POPUP_CLOSED) {
            if (resultCode == RESULT_OK) {
        		continueButton.setVisibility(View.VISIBLE); // next state
            }
        }
    }
	
}
