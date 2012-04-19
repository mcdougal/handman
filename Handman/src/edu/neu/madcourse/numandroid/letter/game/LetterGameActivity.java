package edu.neu.madcourse.numandroid.letter.game;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import edu.neu.madcourse.numandroid.R;
import edu.neu.madcourse.numandroid.SoundManager;
import edu.neu.madcourse.numandroid.game.GameActivity;
import edu.neu.madcourse.numandroid.letter.DrawPath;
import edu.neu.madcourse.numandroid.letter.LetterActivity;
import edu.neu.madcourse.numandroid.letter.LetterResultsPopup;
import edu.neu.madcourse.numandroid.stats.StatsUtils;

public class LetterGameActivity extends LetterActivity implements OnClickListener {

	private static final String TAG = "LetterGameActivity";
	
	private static final int REQUEST_BACK = 1;
	
	private static final int STATE_PRE_SHOW = 0;
	private static final int STATE_SHOW = 1;
	private static final int STATE_DRAW = 2;
	private static final int STATE_SUBMITTED = 3;
	private static final int STATE_DONE = 4;
	
	private static final int LETTER_SHOW_TIME = 2500; // millis
	
	
	//
	//  INSTANCE VARS
	//
	
	private ViewGroup showLetterView;
	private Button eraseButton;
	private Button submitButton;
	private Button continueButton;
	
	private long lastButtonClick;
	
	
	//
	//  INITIALIZATION
	//
	
	
	@Override
	protected void initializeViewVariables() {
		super.initializeViewVariables();
		
		SoundManager.guessWrong();
		
		// inflate and add the layout for housing the "Show Letter" button
		
		showLetterView = (ViewGroup) getLayoutInflater()
				.inflate(R.layout.letter_game_show_letter, null);
		
		((TextView) showLetterView.getChildAt(0)).setText(
				String.format(getString(R.string.letter_game_show_letter_msg),
						letter, ""+GameActivity.ACCURACY_THRESHOLD));
		
		((Button) showLetterView.getChildAt(1)).setOnClickListener(this);
		
		((ViewGroup) findViewById(R.id.letter_draw_root)).addView(showLetterView);

		
		// inflate and add the footer buttons
		
		ViewGroup buttons = (ViewGroup) getLayoutInflater()
				.inflate(R.layout.letter_game_buttons, null);
		
		eraseButton = (Button) buttons.getChildAt(0);
		submitButton = (Button) buttons.getChildAt(1);
		continueButton = (Button) buttons.getChildAt(2);
		
		eraseButton.setOnClickListener(this);
		submitButton.setOnClickListener(this);
		continueButton.setOnClickListener(this);
		
		((ViewGroup) findViewById(R.id.letter_footer)).addView(buttons, 0);
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
		inflater.inflate(R.menu.letter_game_menu, menu);
		return true;
	}

	/**
	 * Handles selection of menu options.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		
		case R.id.letter_game_menu_main:
			returnToMenu = true;
			finish();
			return true;
		
		case R.id.letter_game_menu_help:
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
		
		if (System.currentTimeMillis() - lastButtonClick < 750) {
			Log.d(TAG, "onClick(): double-click detected; aborting");
			return;
		}

		lastButtonClick = System.currentTimeMillis();
		
		switch(v.getId()) {
		
		case R.id.letter_game_show_letter_btn:
		case R.id.letter_game_submit_button:
		case R.id.letter_game_continue_button:
			SoundManager.btnClick();
			setNextState();
			break;
			
		case R.id.letter_game_erase_button:
			SoundManager.erase();
			path = new DrawPath();
			break;
		
		}
	}
	
	
	@Override
	public void onBackPressed() {
		Log.d(TAG, "onBackPressed()");
		
		if (state != STATE_SUBMITTED && state != STATE_DONE) {
			showBackPopup();
		}
		else {
			super.onBackPressed();
		}
	}
	
	
	//
	//  POPUPS
	//
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
		
        if (requestCode == REQUEST_RESULT_POPUP_CLOSED) {
            if (resultCode == RESULT_OK) {
            	onResultPopupClosed();
            }
        }
        else if (requestCode == REQUEST_BACK) {
            if (resultCode == RESULT_OK) {
            	onRequestBackPopupClosed(data);
            }
        }
    }
	
	
	private void showBackPopup() {
		Intent i = new Intent(this, LetterGameBackPopup.class);
		startActivityForResult(i, REQUEST_BACK);
	}
	
	
	private void onRequestBackPopupClosed(Intent data) {
		boolean goBack = data.getBooleanExtra(LetterGameBackPopup.RESULT_ABORT, false);
        
        if (goBack) {
    		returnResult = true;
    		accuracy = 0;
    		finish();
        }
	}

	
	protected void showHelpPopup() {
		Intent i = new Intent(this, LetterGameHelpPopup.class);
		startActivity(i);
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
		
		case STATE_PRE_SHOW: 	setPreShowState(); break;
		case STATE_SHOW: 		setShowState(); break;
		case STATE_DRAW: 		setDrawState(); break;
		case STATE_SUBMITTED: 	setSubmittedState(); break;
		case STATE_DONE: 		setDoneState(); break;
		
		}
	}

	private void setNextState() {
		switch(state) {
		
		case STATE_PRE_SHOW: 	setShowState(); break;
		case STATE_SHOW: 		setDrawState(); break;
		case STATE_DRAW: 		setSubmittedState(); break;
		case STATE_SUBMITTED: 	setDoneState(); break;
		case STATE_DONE:	 	finish(); break;
		
		}
	}
	
	
	//
	//  PRE-SHOW STATE
	//
	
	
	private void setPreShowState() {
		Log.d(TAG, "setPreShowState()");
		
		state = STATE_PRE_SHOW;
		
		blockDrawing = true;
		showLetter = false;
		
		showLetterView.setVisibility(View.VISIBLE); // next state
		eraseButton.setVisibility(View.GONE);
		submitButton.setVisibility(View.GONE);
		continueButton.setVisibility(View.GONE);
	}
	
	
	//
	//  SHOW STATE
	//
	
	
	private void setShowState() {
		Log.d(TAG, "setShowState()");
		
		state = STATE_SHOW;
		
		blockDrawing = true;
		showLetter = true;
		
		showLetterView.setVisibility(View.GONE);
		eraseButton.setVisibility(View.GONE);
		submitButton.setVisibility(View.GONE);
		continueButton.setVisibility(View.GONE);
		
		new ShowLetterTask().execute(); // next state
	}
	
	
	private class ShowLetterTask extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected Void doInBackground(Void... empty) {
			Log.d(TAG, "ShowLetterTask.doInBackground(): time="+LETTER_SHOW_TIME);
			
			try {
				Thread.sleep(LETTER_SHOW_TIME);
			}
			catch (Exception e) {
				Log.d(TAG, "ShowLetterTask.doInBackground(): interrupted");
			}
			
			Log.d(TAG, "ShowLetterTask.doInBackground(): finished");
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			setNextState();
		}
	}
	
	
	//
	//  DRAW STATE
	//
	
	
	private void setDrawState() {
		Log.d(TAG, "setDrawState()");
		
		state = STATE_DRAW;
		
		blockDrawing = false;
		showLetter = false;
		
		showLetterView.setVisibility(View.GONE);
		eraseButton.setVisibility(View.VISIBLE);
		submitButton.setVisibility(View.VISIBLE); // next state
		continueButton.setVisibility(View.GONE);
		
		Toast.makeText(this, getString(R.string.letter_game_draw_instructions),
				Toast.LENGTH_LONG).show();
	}
	
	
	//
	//  SUBMITTED STATE
	//
	
	
	private void setSubmittedState() {
		Log.d(TAG, "setSubmittedState()");
		
		state = STATE_SUBMITTED;
		
		blockDrawing = true;
		showLetter = true;
		
		showLetterView.setVisibility(View.GONE);
		eraseButton.setVisibility(View.GONE);
		submitButton.setVisibility(View.GONE);
		continueButton.setVisibility(View.GONE);
		
		if (accuracy == NO_ACCURACY)
			calculateAccuracy(); // triggers the below callback
		else
			finishSettingSubmittedState();
	}


	@Override
	protected String getExtraResultMsg(int result) {
		if (result > GameActivity.ACCURACY_THRESHOLD + 10)
			return getString(R.string.letter_game_result_great_feedback);
		else if (result > GameActivity.ACCURACY_THRESHOLD)
			return getString(R.string.letter_game_result_good_feedback);
		else if (result == GameActivity.ACCURACY_THRESHOLD)
			return getString(R.string.letter_game_result_good_exact_feedback);
		else if (result >= GameActivity.ACCURACY_THRESHOLD - 10)
			return getString(R.string.letter_game_result_bad_close_feedback);
		else if (result >= GameActivity.ACCURACY_THRESHOLD - 30)
			return getString(R.string.letter_game_result_bad_far_feedback);
		else if (result > 0)
			return getString(R.string.letter_game_result_terrible_feedback);
		else
			return getString(R.string.letter_game_result_0_feedback);
	}


	@Override
	protected void accuracyCalculatedCallback() {
		returnToMenu = false;
		returnResult = true;
		
		if (accuracy != 0) // TODO
			StatsUtils.onLetterDrawingSubmitted(this, letter, accuracy);
		
		finishSettingSubmittedState();
	}
	
	
	private void finishSettingSubmittedState() {		
		Intent i = new Intent(this, LetterResultsPopup.class);
		i.putExtra(LetterResultsPopup.INTENT_RESULT, accuracy);
		i.putExtra(LetterResultsPopup.INTENT_EXTRA_MSG, getExtraResultMsg(accuracy));
		startActivityForResult(i, REQUEST_RESULT_POPUP_CLOSED);
	}
	
	
	private void onResultPopupClosed() {
		setNextState();
	}
	
	
	//
	//  DONE STATE
	//
	
	
	private void setDoneState() {
		Log.d(TAG, "setDoneState()");
		
		returnToMenu = false;
		returnResult = true;
		
		state = STATE_DONE;
		
		blockDrawing = false;
		showLetter = true;

		eraseButton.setText(getString(R.string.letter_game_practice_btn_lbl));
		
		showLetterView.setVisibility(View.GONE);
		eraseButton.setVisibility(View.VISIBLE);
		submitButton.setVisibility(View.GONE);
		continueButton.setVisibility(View.VISIBLE);
	}
	
}
