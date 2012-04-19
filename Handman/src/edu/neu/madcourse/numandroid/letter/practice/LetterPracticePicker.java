package edu.neu.madcourse.numandroid.letter.practice;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import edu.neu.madcourse.numandroid.AlphabetAdapter;
import edu.neu.madcourse.numandroid.R;
import edu.neu.madcourse.numandroid.SoundManager;
import edu.neu.madcourse.numandroid.letter.LetterActivity;

public class LetterPracticePicker
extends Activity
implements OnClickListener, OnItemClickListener {
	
	private static final String TAG = "LetterPracticePicker";
	
	private static final String PREF_UPPERCASE = "pref:uppercase";
	
	
	//
	//  INSTANCE VARS
	//
	
	private boolean upperCase;
	
	private long lastButtonClick;

	private AlphabetAdapter alphabetAdapter;
	private Button caseButton;
	
	
	//
	//  ACTIVITY METHODS
	//
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.letter_practice_picker);
		
		loadPreferences();
		initializeViewVariables();
	}
	
	
	@Override
	protected void onPause() {
		Log.d(TAG, "onPause()");
		super.onPause();
		
		savePreferences();
	}
	
	
	//
	//  SHARED PREFERENCES
	//
	
	
	protected void loadPreferences() {
		Log.d(TAG, "loadPreferences()");
		SharedPreferences pref = getPreferences(MODE_PRIVATE);
		
		upperCase = pref.getBoolean(PREF_UPPERCASE, true);
		
		Log.d(TAG, "upperCase="+upperCase);
	}

	
	protected void savePreferences() {
		Log.d(TAG, "savePreferences()");
		SharedPreferences pref = getPreferences(MODE_PRIVATE);
		
		pref.edit().putBoolean(PREF_UPPERCASE, upperCase).commit();
		
		Log.d(TAG, "upperCase="+upperCase);
	}
	
	
	//
	//  VIEW VAR INITIALIZATION
	//
	
	
	protected void initializeViewVariables() {
		Log.d(TAG, "initializeViewVariables()");
		
		caseButton = (Button) findViewById(R.id.letter_practice_picker_case_btn);
		
		if (upperCase)
			caseButton.setText(getString(R.string.letter_practice_lower_btn_lbl));
		else
			caseButton.setText(getString(R.string.letter_practice_upper_btn_lbl));
		
		caseButton.setOnClickListener(this);
		
		if (upperCase)
			alphabetAdapter = new AlphabetAdapter(this,
					AlphabetAdapter.ALPHABET.toUpperCase());
		else
			alphabetAdapter = new AlphabetAdapter(this,
					AlphabetAdapter.ALPHABET.toLowerCase());
		
		GridView alphabetView = (GridView) findViewById(
				R.id.letter_practice_picker_alphabet_grid);
		
		alphabetView.setAdapter(alphabetAdapter);
		alphabetView.setOnItemClickListener(this);
	}
	
	
	//
	//  CLICK HANDLING
	//
	
	
	@Override
	public void onClick(View v) {
		Log.d(TAG, "onClick()");
		
		switch(v.getId()) {
		
		case R.id.letter_practice_picker_case_btn:
			SoundManager.deepClick();
			switchCase();
			break;
		
		}
	}
	
	
	private void switchCase() {
		Log.d(TAG, "switchCase(): set upperCase="+!upperCase);
		
		upperCase = !upperCase;
		
		if (upperCase) {
			alphabetAdapter.toUpperCase();
			caseButton.setText(getString(R.string.letter_practice_lower_btn_lbl));
		}
		else {
			alphabetAdapter.toLowerCase();
			caseButton.setText(getString(R.string.letter_practice_upper_btn_lbl));
		}
	}
	
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view,
			int position, long id) {

		Log.d(TAG, "onItemClick()");
		
		if (System.currentTimeMillis() - lastButtonClick < 500) {
			Log.d(TAG, "onItemClick(): double-click prevented");
			return;
		}

		lastButtonClick = System.currentTimeMillis();
		
		SoundManager.deepClick();

		ViewGroup listItem = (ViewGroup) view;
		TextView letterView = (TextView) listItem.getChildAt(0);
		char letter = letterView.getText().toString().charAt(0);

		Intent i = new Intent(this, LetterPracticeActivity.class);
		i.putExtra(LetterActivity.INTENT_LETTER, letter);
		startActivity(i);
	}
}
