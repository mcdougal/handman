package edu.neu.madcourse.numandroid.game;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import edu.neu.madcourse.numandroid.AlphabetAdapter;
import edu.neu.madcourse.numandroid.HandmanUtils;
import edu.neu.madcourse.numandroid.R;
import edu.neu.madcourse.numandroid.SoundManager;
import edu.neu.madcourse.numandroid.letter.LetterActivity;
import edu.neu.madcourse.numandroid.letter.game.LetterGameActivity;
import edu.neu.madcourse.numandroid.stats.StatsUtils;

public class GameActivity extends Activity implements OnClickListener, OnItemClickListener {

	private static final String TAG = "GameActivity";

	// INTENT EXTRAS
	
	public static final String INTENT_MODE = "intent:mode";
	
	// PREFERENCES KEYS
	
	private static final String PREF_WORD = "pref:word";
	private static final String PREF_REMAINING_LETTERS = "pref:remaining_letters";
	private static final String PREF_CORRECT_GUESSES = "pref:correct_guesses";
	private static final String PREF_INCORRECT_GUESSES = "pref:incorrect_guesses";
	private static final String PREF_GUESSES_LEFT = "pref:guesses_left";
	private static final String PREF_IN_LETTER_GAME = "pref:in_letter_game";
	
	// REQUEST CODES
	
	private static final int REQUEST_LETTER_INPUT = 0;
	private static final int REQUEST_GAME_LOST_CLOSED = 1;
	private static final int REQUEST_GAME_LOST_MODE = 2;
	private static final int REQUEST_GAME_WON_CLOSED = 3;
	private static final int REQUEST_GAME_WON_MODE = 4;
	private static final int REQUEST_NEW_GAME_MODE = 5;
	
	// GAME MODES
	
	public static final int MODE_UPPER = 0;
	public static final int MODE_LOWER = 1;
	
	// GAME PROPERTIES
	
	private static final int START_NUM_GUESSES = 6;
	public static final int ACCURACY_THRESHOLD = 80;

    
    //////////////////////////////////////////
    //
    //  INSTANCE VARS
    //
    //////////////////////////////////////////

	private int mode; // NOT stored in preferences

	private String word; // word being guessed
	private String remainingLetters; // letters that haven't been guesses	
	private String correctGuesses; // letters that were guessed correctly
	private String incorrectGuesses; // letters that were guessed wrong
	private int guessesLeft; // number of guesses remaining

	// views
	
	private ViewGroup wordLoaderView;
	private AlphabetAdapter remainingLettersAdapter;
	private GridView remainingLettersView;
	private ArrayList<LetterSlotView> correctGuessesViews = new ArrayList<LetterSlotView>();
	private TextView incorrectGuessesView;
	private View gallowsView;
	private Button guessLetterBtn;
	private Toast guessResultToast;
	
	private long lastButtonClick; // for button double-click prevention

	
	
	/***************************************************************************
	 * 
	 * 
	 *   INITIALIZATION
	 * 
	 * 
	 ***************************************************************************/
	
	
	//////////////////////////////////////
	//
	//  ACTIVITY METHODS
	//
	//////////////////////////////////////
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game);
		
		if (getIntentExtras()) {
			setDefaults();
		}
		else {
			loadPreferences();
		}
		
		if (word == null) {
			new LoadWordTask().execute();
		}
		else {
			initializeViewVariables();
		}
	}
	
	@Override
	protected void onPause() {
		Log.d(TAG, "Paused");
		super.onPause();
		
		savePreferences();
	}
	
	
	//////////////////////////////////////
	//
	//  SHARED PREFERENCES / INTENT EXTRAS
	//
	//////////////////////////////////////
	
	
	private boolean getIntentExtras() {
		Log.d(TAG, "getIntentExtras()");
		
		mode = getIntent().getIntExtra(INTENT_MODE, -1);
		getIntent().removeExtra(INTENT_MODE);
		
		return mode != -1;
	}
	
	
	private void setDefaults() {
		Log.d(TAG, "setDefaults()");
		
		if (mode == MODE_UPPER) {
			remainingLetters = AlphabetAdapter.ALPHABET.toUpperCase();
		}
		else {
			remainingLetters = AlphabetAdapter.ALPHABET.toLowerCase();
		}
		
		word = null;
		correctGuesses = "";
		incorrectGuesses = "";
		guessesLeft = START_NUM_GUESSES;
	}
	
	
	private void loadPreferences() {
		Log.d(TAG, "loadPreferences()");
		SharedPreferences pref = getPreferences(MODE_PRIVATE);
		
		word = pref.getString(PREF_WORD, null);
		remainingLetters = pref.getString(PREF_REMAINING_LETTERS, AlphabetAdapter.ALPHABET.toUpperCase());
		correctGuesses = pref.getString(PREF_CORRECT_GUESSES, "");
		incorrectGuesses = pref.getString(PREF_INCORRECT_GUESSES, "");
		guessesLeft = pref.getInt(PREF_GUESSES_LEFT, START_NUM_GUESSES);
		
		Log.d(TAG, "word="+word);
		Log.d(TAG, "remainingLetters="+remainingLetters);
		Log.d(TAG, "correctGuesses="+correctGuesses);
		Log.d(TAG, "incorrectGuesses="+incorrectGuesses);
		Log.d(TAG, "guessesLeft="+guessesLeft);

		if (pref.getBoolean(PREF_IN_LETTER_GAME, false)) {
			Intent i = new Intent(this, LetterGameActivity.class);
			startActivityForResult(i, REQUEST_LETTER_INPUT);
			return;
		}
	}

	
	private void savePreferences() {
		Log.d(TAG, "savePreferences()");
		SharedPreferences pref = getPreferences(MODE_PRIVATE);
		
		pref.edit().putString(PREF_WORD, word).commit();
		pref.edit().putString(PREF_REMAINING_LETTERS, remainingLetters).commit();
		pref.edit().putString(PREF_CORRECT_GUESSES, correctGuesses).commit();
		pref.edit().putString(PREF_INCORRECT_GUESSES, incorrectGuesses).commit();
		pref.edit().putInt(PREF_GUESSES_LEFT, guessesLeft).commit();

		Log.d(TAG, "word="+word);
		Log.d(TAG, "remainingLetters="+remainingLetters);
		Log.d(TAG, "correctGuesses="+correctGuesses);
		Log.d(TAG, "incorrectGuesses="+incorrectGuesses);
		Log.d(TAG, "guessesLeft="+guessesLeft);
	}
	
	
	//////////////////////////////////////
	//
	//  WORD RETRIEVAL
	//
	//////////////////////////////////////
	
	
	private class LoadWordTask extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected Void doInBackground(Void... params) {
			Log.d(TAG, "LoadWordTask.doInBackground(): starting");
			
			long time = System.currentTimeMillis();
			
			word = HandmanUtils.getRandomWord(GameActivity.this);
			
			if (mode == MODE_UPPER)
				word = word.toUpperCase();
			else
				word = word.toLowerCase();
			
			while (System.currentTimeMillis() - time < 2000) {
				// wait so user can't figure out first letter from load time
			}
			
			Log.d(TAG, "LoadWordTask.doInBackground(): finished");
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			Log.d(TAG, "LoadWordTask.onPostExecute()");
			
			initializeViewVariables();
		}
	}
	
	
	//////////////////////////////////////
	//
	//  VIEW VAR INITIALIZATION
	//
	//////////////////////////////////////
	
	
	private void initializeViewVariables() {
		
		// the loading progress indicator when finding a word

		wordLoaderView = (ViewGroup) findViewById(R.id.game_loader);
		wordLoaderView.setVisibility(View.GONE);
		
		// set remaining letters view
		
		remainingLettersAdapter = new AlphabetAdapter(this, remainingLetters);
		
		remainingLettersView = (GridView) findViewById(R.id.game_alphabet_grid);
		remainingLettersView.setAdapter(remainingLettersAdapter);
		remainingLettersView.setOnItemClickListener(this);
		remainingLettersView.setVisibility(View.GONE);

		// set correct guesses view

		ViewGroup letterSlotParent = (ViewGroup) findViewById(R.id.game_letter_container);
		
		LetterSlotView letterSlot;
		char letter;
		for (int i = 0; i < word.length(); i++) {
			letter = word.charAt(i);
			letterSlot = new LetterSlotView(this, letter);
			if (correctGuesses.indexOf(letter) != -1)
				letterSlot.showLetter();
			
			letterSlotParent.addView(letterSlot);
			correctGuessesViews.add(letterSlot);
		}
		
		// set other vies
		
		incorrectGuessesView = (TextView) findViewById(R.id.game_attempted_view);
		gallowsView = (View) findViewById(R.id.game_gallows_image);

		incorrectGuessesView.setText(incorrectGuesses);
		updateGallowsView();
		
		guessLetterBtn = (Button) findViewById(R.id.game_guess_letter_btn);
		guessLetterBtn.setOnClickListener(this);
		
		// the toast for telling the user if they were right or wrong
		
		TextView tv = new TextView(this);
		tv.setTextSize(25f);
		tv.setTextColor(Color.BLACK);
		tv.setBackgroundColor(Color.WHITE);
		tv.setWidth(HandmanUtils.getDpFromPixels(300, this));
		tv.setGravity(Gravity.CENTER);
		
		guessResultToast = new Toast(this);
		guessResultToast.setView(tv);
		guessResultToast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,
				0, HandmanUtils.getDpFromPixels(75, this));
		guessResultToast.setDuration(Toast.LENGTH_SHORT);

		if (guessesLeft == 0)
			handleGameLost();
		else if (isGameWon())
			showGameWonPopup();
	}
	
	
	/***************************************************************************
	 * 
	 * 
	 *   RUNTIME
	 * 
	 * 
	 ***************************************************************************/
	
	
	//////////////////////////////////////
	//
	//  ACTIVITY RESULT
	//
	//////////////////////////////////////
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
		
		switch(requestCode) {
			
		case REQUEST_LETTER_INPUT:
			onLetterActivityFinished(resultCode, data); break;
		
		case REQUEST_GAME_LOST_CLOSED:
			onGameLostPopupClosed(resultCode, data); break;
		case REQUEST_GAME_LOST_MODE:
			onGameLostModeChoicePopupClosed(resultCode, data); break;
		
		case REQUEST_GAME_WON_CLOSED:
			onGameWonPopupClosed(resultCode, data); break;
		case REQUEST_GAME_WON_MODE:
			onGameWonModeChoicePopupClosed(resultCode, data); break;
		
		case REQUEST_NEW_GAME_MODE:
			onNewGameModeChoicePopupClosed(resultCode, data); break;
			
		}
    }
	
	
	//////////////////////////////////////
	//
	//  MENU
	//
	//////////////////////////////////////
	
	
	/**
	 * Creates the options menu.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.game_menu, menu);
		return true;
	}

	/**
	 * Handles selection of menu options.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		
		case R.id.game_menu_new_game:
			onNewGameMenuItemSelected();
			return true;
			
		}
		
		return false;
	}
	
	
	private void onNewGameMenuItemSelected() {
		Intent i = new Intent(this, ModeChoicePopup.class);
    	startActivityForResult(i, REQUEST_NEW_GAME_MODE);
	}
	
	
	private void onNewGameModeChoicePopupClosed(int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;
		
		int mode = data.getIntExtra(ModeChoicePopup.RESULT_MODE, -1);
        
        if (mode == -1) {
        	Log.d(TAG, "ERROR: could not retrieve mode from result intent");
        	return;
        }
        
		Intent i = new Intent(this, GameActivity.class);
		i.putExtra(GameActivity.INTENT_MODE, mode);
		
		finish();
		startActivity(i);
	}
	
	
	//////////////////////////////////////
	//
	//  CLICK HANDLING
	//
	//////////////////////////////////////

	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		
		case R.id.game_guess_letter_btn:
			SoundManager.openGuess();
			remainingLettersView.setVisibility(View.VISIBLE);
			break;
		
		}
	}
	
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view,
			int position, long id) {
		
		if (System.currentTimeMillis() - lastButtonClick < 500) {
			Log.d(TAG, "Double-click detected on letter. Aborting click.");
			return;
		}

		lastButtonClick = System.currentTimeMillis();

		ViewGroup listItem = (ViewGroup) view;
		TextView letterView = (TextView) listItem.getChildAt(0);
		char letter = letterView.getText().toString().charAt(0);

		remainingLettersView.setVisibility(View.GONE);
		makeGuess(letter);
	}
	
	
	@Override
	public void onBackPressed() {
		Log.d(TAG, "onBackPressed()");
		
		if (remainingLettersView != null
				&& remainingLettersView.getVisibility() == View.VISIBLE) {
			SoundManager.closeGuess();
			remainingLettersView.setVisibility(View.GONE);
		}
		else {
			super.onBackPressed();
		}
	}
	
	
	//////////////////////////////////////
	//
	//  GUESSING
	//
	//////////////////////////////////////
	
	
	private void makeGuess(char guess) {
		Log.d(TAG, "makeGuess(): char="+guess);
		
		if (word.indexOf(guess) != -1) {
			SoundManager.guessRight();
			addCorrectGuess(guess);
		}
		else {
			startBadGuessActivity(guess);
		}
	}
	
	
	private void addCorrectGuess(char guess) {
		Log.d(TAG, "addCorrectGuess(): guess="+guess);
		
		correctGuesses = correctGuesses+guess;
		removeRemainingLetter(guess);
		
		int numCorrect = 0;
		int index = word.indexOf(guess);
		
		while (index != -1) {
			numCorrect++;
			correctGuessesViews.get(index).showLetter();
			index = word.indexOf(guess, index+1);
		}
		
		String msg; 
		if (numCorrect == 1) {
			msg = String.format(getString(R.string.game_guessed_right_txt),
					getString(R.string.game_guessed_right_txt_singular));
		}
		else {
			msg = String.format(getString(R.string.game_guessed_right_txt),
					String.format(getString(R.string.game_guessed_right_txt_plural),
							numCorrect));
		}

		((TextView) guessResultToast.getView()).setText(msg);
		guessResultToast.show();
		
		if (isGameWon()) {
			StatsUtils.onGameWon(this);
			SoundManager.gameWon();
			showGameWonPopup();
		}
	}
	
	
	private void startBadGuessActivity(char letter) {
		Log.d(TAG, "startBadGuessActivity(): letter="+letter);
		
		Intent i = new Intent(this, LetterGameActivity.class);
		i.putExtra(LetterGameActivity.INTENT_LETTER, letter);
		startActivityForResult(i, REQUEST_LETTER_INPUT);
	}
	
	
	private void onLetterActivityFinished(int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;
		
		boolean toMainMenu = data.getBooleanExtra(
				LetterActivity.RESULT_TO_MAIN_MENU, false);
		
		if (toMainMenu) {
			getPreferences(MODE_PRIVATE).edit().putBoolean(
					PREF_IN_LETTER_GAME, true).commit();
			finish();
			return;
		}
		else {
			getPreferences(MODE_PRIVATE).edit().putBoolean(
					PREF_IN_LETTER_GAME, false).commit();
		}
		
		char letter = data.getCharExtra(LetterActivity.RESULT_LETTER, ' ');
        int accuracy = data.getIntExtra(LetterActivity.RESULT_ACCURACY, -1);
        
        if (letter == ' ') {
        	Log.d(TAG, "ERROR: could not retrieve letter from result intent");
        	return;
        }
        if (accuracy == -1) {
        	Log.d(TAG, "ERROR: could not retrieve accuracy from result intent");
        	return;
        }
        
        Log.d(TAG, "onActivityResult(): letter="+letter+", accuracy="+accuracy);

        if (accuracy < ACCURACY_THRESHOLD) {
        	guessesLeft--;
    		
        	updateGallowsView();
        	
        	if (guessesLeft == 0) {
    			StatsUtils.onGameLost(this);
    			handleGameLost();
        	}
        }

    	addIncorrectGuess(letter);
	}
	
	
	private void addIncorrectGuess(char guess) {
		Log.d(TAG, "addIncorrectGuess(): guess="+guess);
		
		if (incorrectGuesses.equals(""))
			incorrectGuesses = ""+guess;
		else
			incorrectGuesses = incorrectGuesses+"   "+guess;
		
		incorrectGuessesView.setText(""+incorrectGuesses);
		removeRemainingLetter(guess);
	}
	
	
	private void removeRemainingLetter(char letterToRemove) {
		Log.d(TAG, "removeRemainingLetter(): letterToRemove="+letterToRemove);
		
		remainingLetters = remainingLetters.replace(""+letterToRemove, "");
		remainingLettersAdapter.updateLetters(remainingLetters);
	}
	
	
	//////////////////////////////////////
	//
	//  GAME OVER
	//
	//////////////////////////////////////
	
	
	private void handleGameLost() {
		guessLetterBtn.setVisibility(View.INVISIBLE);
		// wait to show popup so user can see the hangman with all limbs
		Timer timer = new Timer();
		timer.schedule(new ShowGameListPopupTimer(), 1500);
	}
	
	
	private class ShowGameListPopupTimer extends TimerTask {
		@Override
		public void run() {
			showGameLostPopup();
		}
	}
	
	
	private void showGameLostPopup() {
		Intent i = new Intent(this, GameLostPopup.class);
		i.putExtra(GameLostPopup.INTENT_WORD, word);
		startActivityForResult(i, REQUEST_GAME_LOST_CLOSED);
	}
	
	
	private void onGameLostPopupClosed(int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
        	finish();
		
		boolean playAgain = data.getBooleanExtra(GameLostPopup.RESULT_PLAY_AGAIN, false);
        
        if (playAgain) {
    		Intent i = new Intent(this, ModeChoicePopup.class);
        	startActivityForResult(i, REQUEST_GAME_LOST_MODE);
        }
        else {
        	finish();
        }
	}
	
	
	private void onGameLostModeChoicePopupClosed(int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			showGameLostPopup();
			return;
		}
		
		int mode = data.getIntExtra(ModeChoicePopup.RESULT_MODE, -1);
        
        if (mode == -1) {
        	Log.d(TAG, "ERROR: could not retrieve mode from result intent");
        	return;
        }
        
		Intent i = new Intent(this, GameActivity.class);
		i.putExtra(GameActivity.INTENT_MODE, mode);
		
		finish();
		startActivity(i);
	}
	
	
	private boolean isGameWon() {
		if (word == null || guessesLeft == 0)
			return false;
		
		for (int i = 0; i < word.length(); i++) {
			if (correctGuesses.indexOf(word.charAt(i)) == -1)
				return false;
		}
		
		return true;
	}
	
	
	private void showGameWonPopup() {
		guessLetterBtn.setVisibility(View.INVISIBLE);
		Intent i = new Intent(this, GameWonPopup.class);
		startActivityForResult(i, REQUEST_GAME_WON_CLOSED);
	}
	
	
	private void onGameWonPopupClosed(int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
        	finish();
		
		boolean playAgain = data.getBooleanExtra(GameLostPopup.RESULT_PLAY_AGAIN, false);
        
        if (playAgain) {
    		Intent i = new Intent(this, ModeChoicePopup.class);
        	startActivityForResult(i, REQUEST_GAME_WON_MODE);
        }
        else {
        	finish();
        }
	}
	
	
	private void onGameWonModeChoicePopupClosed(int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			showGameWonPopup();
			return;
		}
		
		int mode = data.getIntExtra(ModeChoicePopup.RESULT_MODE, -1);
        
        if (mode == -1) {
        	Log.d(TAG, "ERROR: could not retrieve mode from result intent");
        	return;
        }
        
		Intent i = new Intent(this, GameActivity.class);
		i.putExtra(GameActivity.INTENT_MODE, mode);
		
		finish();
		startActivity(i);
	}
	
	
	//////////////////////////////////////
	//
	//  VIEW UPDATING
	//
	//////////////////////////////////////
	
	
	private void updateGallowsView() {
		Log.d(TAG, "Updating the gallows view");
		
		switch(guessesLeft) {
		
		case 6:
			gallowsView.setBackgroundResource(R.drawable.hangman_0); break;
		case 5:
			gallowsView.setBackgroundResource(R.drawable.hangman_1); break;
		case 4:
			gallowsView.setBackgroundResource(R.drawable.hangman_2); break;
		case 3:
			gallowsView.setBackgroundResource(R.drawable.hangman_3); break;
		case 2:
			gallowsView.setBackgroundResource(R.drawable.hangman_4); break;
		case 1:
			gallowsView.setBackgroundResource(R.drawable.hangman_5); break;
		case 0:
			gallowsView.setBackgroundResource(R.drawable.hangman_6); break;
		
		}
	}
	
	
}
