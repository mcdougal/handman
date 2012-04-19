package edu.neu.madcourse.numandroid;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import edu.neu.madcourse.numandroid.R;
import edu.neu.madcourse.numandroid.game.GameActivity;
import edu.neu.madcourse.numandroid.game.ModeChoicePopup;
import edu.neu.madcourse.numandroid.letter.practice.LetterPracticePicker;
import edu.neu.madcourse.numandroid.stats.StatsActivity;

public class MainActivity extends Activity implements OnClickListener {
	
	private static final String TAG = "MainActivity";
	
	// startActivityForRequest() keys
	private static final int REQUEST_MODE = 0;

    
    //////////////////////////////////////////
    //
    //  INSTANCE VARS
    //
    //////////////////////////////////////////
	
	// for button double-click prevention on buttons
	private long lastButtonClick;

	
	
	/***************************************************************************
	 * 
	 * 
	 *   INITIALIZATION
	 * 
	 * 
	 ***************************************************************************/
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        initializeAnimation();
        initializeSoundManager();
        setOnClickListeners();
    }
    
    
    private void initializeAnimation() {
        
    	Resources res = getResources();
    	
    	final AnimationDrawable animation = new AnimationDrawable();
        animation.addFrame(res.getDrawable(R.drawable.hangman_main_0), 300);
        animation.addFrame(res.getDrawable(R.drawable.hangman_main_1), 300);
        animation.setOneShot(false);
        
        ImageView animationView = (ImageView) findViewById(R.id.main_anim);
        animationView.setBackgroundDrawable(animation);
        
        animationView.post(
        	new Runnable() {
                public void run() {
                	try { Thread.sleep(500); }
                	catch (Exception e) {}
                    animation.start();
                }
        	}
        );	
    }
    
    
    private void initializeSoundManager() {
        new Thread() {
        	@Override
        	public void run() {
        		SoundManager.create(MainActivity.this);
        	}
        }.start();
    }
    
    
    private void setOnClickListeners() {
        ((Button) findViewById(R.id.main_new_game_btn)).setOnClickListener(this);
        ((Button) findViewById(R.id.main_continue_btn)).setOnClickListener(this);
        ((Button) findViewById(R.id.main_practice_btn)).setOnClickListener(this);
        ((Button) findViewById(R.id.main_stats_btn)).setOnClickListener(this);
    }

    
		
	/***************************************************************************
	 * 
	 * 
	 *   RUNTIME
	 * 
	 * 
	 ***************************************************************************/
    
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
		
    	switch(requestCode) {
    	
    	case REQUEST_MODE: onModeChoice(resultCode, data); break;
    	
    	}
    }

    
	@Override
	public void onClick(View v) {
		if (System.currentTimeMillis() - lastButtonClick < 500) {
			Log.d(TAG, "Double-click detected on letter. Aborting click.");
			return;
		}

		lastButtonClick = System.currentTimeMillis();
		
		SoundManager.btnClick();
		
		switch (v.getId()) {
		
		case R.id.main_new_game_btn: newGame(); break;
		case R.id.main_continue_btn: continueGame(); break;
		case R.id.main_practice_btn: practice(); break;
		case R.id.main_stats_btn: stats(); break;
		
		}
	}
    
	
    private void newGame() {
    	Intent i = new Intent(this, ModeChoicePopup.class);
    	startActivityForResult(i, REQUEST_MODE);
    }
    
    
    private void onModeChoice(int resultCode, Intent data) {
    	if (resultCode != RESULT_OK)
    		return;
    	
		int mode = data.getIntExtra(ModeChoicePopup.RESULT_MODE, -1);
        
        if (mode == -1) {
        	Log.d(TAG, "ERROR: could not retrieve mode from result intent");
        	return;
        }
        
		Intent i = new Intent(this, GameActivity.class);
		i.putExtra(GameActivity.INTENT_MODE, mode);
		startActivity(i);
    }
    
    
    private void continueGame() {
		Intent i = new Intent(this, GameActivity.class);
		startActivity(i);
    }
    
    
    private void practice() {
    	Intent i = new Intent(this, LetterPracticePicker.class);
		startActivity(i);
    }
    
    
    private void stats() {
    	Intent i = new Intent(this, StatsActivity.class);
		startActivity(i);
    }
    
}