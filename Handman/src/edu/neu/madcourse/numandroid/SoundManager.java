package edu.neu.madcourse.numandroid;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class SoundManager {
	

	private static final int BTN_CLICK = 1;
	private static final int BACK = 2;

	private static final int OPEN_GUESS = 3;
	private static final int CLOSE_GUESS = 4;

	private static final int GUESS_RIGHT = 5;
	private static final int GUESS_WRONG = 6;

	private static final int ERASE = 7;
	private static final int CALC_ACC = 8;
	private static final int GOOD_ACC = 9;
	private static final int BAD_ACC = 10;

	private static final int GAME_WON = 11;
	private static final int GAME_LOST = 12;
	
	private static final int DEEP_CLICK = 13;
	
	private static final int PRACTICE_LETTER = 14;

	private static SoundPool soundPool;
	private static HashMap<Integer, Integer> soundPoolMap;
	private static AudioManager mgr;
	
	
	public static void create(Context c) {
		soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
		soundPoolMap = new HashMap<Integer, Integer>();
		
		soundPoolMap.put(BTN_CLICK, soundPool.load(c, R.raw.deep_click, 1));
		soundPoolMap.put(BACK, soundPool.load(c, R.raw.back, 1));

		soundPoolMap.put(OPEN_GUESS, soundPool.load(c, R.raw.open_guess, 1));
		soundPoolMap.put(CLOSE_GUESS, soundPool.load(c, R.raw.close_guess, 1));

		soundPoolMap.put(GUESS_RIGHT, soundPool.load(c, R.raw.guess_right, 1));
		soundPoolMap.put(GUESS_WRONG, soundPool.load(c, R.raw.guess_wrong, 1));

		soundPoolMap.put(ERASE, soundPool.load(c, R.raw.erase, 1));
		soundPoolMap.put(CALC_ACC, soundPool.load(c, R.raw.deep_click, 1));
		soundPoolMap.put(GOOD_ACC, soundPool.load(c, R.raw.guess_right, 1));
		soundPoolMap.put(BAD_ACC, soundPool.load(c, R.raw.guess_wrong, 1));
		
		soundPoolMap.put(DEEP_CLICK, soundPool.load(c, R.raw.deep_click, 1));
		soundPoolMap.put(GAME_WON, soundPool.load(c, R.raw.btn_click, 1));
		soundPoolMap.put(GAME_LOST, soundPool.load(c, R.raw.guess_wrong, 1));
		
		soundPoolMap.put(PRACTICE_LETTER, soundPool.load(c, R.raw.deep_click, 1));
		
		mgr = (AudioManager) c.getSystemService(Context.AUDIO_SERVICE);
	}

	
	public static void btnClick() { playSound(BTN_CLICK, true); }
	public static void back() { playSound(BACK, true); }

	public static void openGuess() { playSound(OPEN_GUESS, true); }
	public static void closeGuess() { playSound(CLOSE_GUESS, true); }

	public static void guessRight() { playSound(GUESS_RIGHT, true); }
	public static void guessWrong() { playSound(GUESS_WRONG, true); }

	public static void erase() { playSound(ERASE, true); }
	public static void calcAcc() { playSound(CALC_ACC, true); }
	public static void goodAcc() { playSound(GOOD_ACC, true); }
	public static void badAcc() { playSound(BAD_ACC, true); }

	public static void deepClick() { playSound(DEEP_CLICK, true); }

	public static void gameWon() { }
	public static void gameLost() { playSound(GAME_LOST, true); }
	
	public static void practiceLetter() { playSound(PRACTICE_LETTER, true); }

	
	
	private static void playSound(final int sound, final boolean loud) {
		new Thread() {
			@Override
			public void run() {
				if (soundPool == null || soundPoolMap == null || mgr == null)
					return;
				
				if (mgr.getRingerMode() == AudioManager.RINGER_MODE_SILENT
						|| mgr.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE)
					return;
				
				float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
				float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
				float volume = streamVolumeCurrent / streamVolumeMax;

				if (loud) {
					volume = volume * 2;
					
					if (volume > streamVolumeMax)
						volume = streamVolumeMax;
				}
				
				// Play the sound with the correct volume
				soundPool.play(soundPoolMap.get(sound), volume, volume, 1, 0, 1f);
			}
		}.start();
	}
}
