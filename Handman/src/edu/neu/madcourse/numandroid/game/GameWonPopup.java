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

public class GameWonPopup extends Activity implements OnClickListener {

	private static final String TAG = "GameWonPopup";
	
	public static final String RESULT_PLAY_AGAIN = "result:play_again";
	
	private long lastButtonClick;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_won_popup);
		
		((Button) findViewById(R.id.game_won_menu_btn)).setOnClickListener(this);
		((Button) findViewById(R.id.game_won_play_again_btn)).setOnClickListener(this);
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
		
		case R.id.game_won_menu_btn:
			resultIntent.putExtra(RESULT_PLAY_AGAIN, false);
			break;
		
		case R.id.game_won_play_again_btn:
			resultIntent.putExtra(RESULT_PLAY_AGAIN, true);
			break;
		
		}
		
		setResult(RESULT_OK, resultIntent);
		
		finish();
	}
	
	
	@Override
	public void onBackPressed() {
		Log.d(TAG, "onBackPressed()");
		
		Intent resultIntent = new Intent();
		resultIntent.putExtra(RESULT_PLAY_AGAIN, false);
		setResult(RESULT_OK, resultIntent);
		
		super.onBackPressed();
	}
	
}
