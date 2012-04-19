package edu.neu.madcourse.numandroid.stats;

import java.util.Arrays;
import java.util.Comparator;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import edu.neu.madcourse.numandroid.AlphabetAdapter;

public class StatsUtils {
	
	private static final String TAG = "StatsUtils";
	
	private static final String PREF_FILE_NAME = "Stats";
	
	private static final String PREF_GAMES_WON = "pref:games_won";
	private static final String PREF_GAMES_LOST = "pref:games_lost";
	
	// example letter preference key = "pref:A_scores"
	// example letter preference value = "72a91a63a5"
	private static final String PREF_LETTER_PREFIX = "pref:";
	private static final String PREF_LETTER_SUFFIX = "_scores";
	private static final String PREF_LETTER_DELIM = "a";
	
	
	
	public static void onGameWon(Context c) {
		Log.d(TAG, "onGameWon()");
		SharedPreferences pref = c.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
		
		int gamesWon = pref.getInt(PREF_GAMES_WON, 0);
		pref.edit().putInt(PREF_GAMES_WON, gamesWon+1).commit();

		Log.d(TAG, "gamesWon="+(gamesWon+1));
	}
	
	
	public static int getGamesWon(Context c) {
		Log.d(TAG, "getGamesWon()");
		SharedPreferences pref = c.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
		
		int gamesWon = pref.getInt(PREF_GAMES_WON, 0);
		Log.d(TAG, "gamesWon="+gamesWon);
		return gamesWon;
	}
	
	
	public static void onGameLost(Context c) {
		Log.d(TAG, "onGameLost()");
		SharedPreferences pref = c.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
		
		int gamesLost = pref.getInt(PREF_GAMES_LOST, 0);
		pref.edit().putInt(PREF_GAMES_LOST, gamesLost+1).commit();

		Log.d(TAG, "gamesLost="+(gamesLost+1));
	}
	
	
	public static int getGamesLost(Context c) {
		Log.d(TAG, "getGamesLost()");
		SharedPreferences pref = c.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
		
		int gamesLost = pref.getInt(PREF_GAMES_LOST, 0);
		Log.d(TAG, "gamesLost="+gamesLost);
		return gamesLost;
	}
	
	
	public static void onLetterDrawingSubmitted(Context c, char letter, int accuracy) {
		Log.d(TAG, "onLetterDrawingSubmitted()");
		SharedPreferences pref = c.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
		
		String key = constructLetterKey(letter);
		String value = pref.getString(key, "");
		
		if (value.equals(""))
			value = ""+accuracy;
		else
			value = value + PREF_LETTER_DELIM + accuracy;
		
		pref.edit().putString(key, value).commit();

		Log.d(TAG, "key="+key);
		Log.d(TAG, "value="+value);
	}
	
	
	public static int[][] getAvgHighLowForAllUppercaseLetters(Context c) {
		Log.d(TAG, "getAvgHighLowForAllUppercaseLetters()");
		
		return getAvgHighLowForAlphabet(c,
				AlphabetAdapter.ALPHABET.toUpperCase());
	}
	
	
	public static int[][] getAvgHighLowForAllLowercaseLetters(Context c) {
		Log.d(TAG, "getAvgHighLowForAllLowercaseLetters()");
		
		return getAvgHighLowForAlphabet(c,
				AlphabetAdapter.ALPHABET.toLowerCase());
	}
	
	
	public static int[][] getAvgHighLowForAlphabet(Context c, String alph) {
		int[][] results = new int[26][];
		
		char letter;
		for (int i = 0; i < alph.length(); i++) {
			letter = alph.charAt(i);
			results[i] = getAvgHighLowForLetter(c, letter);
		}
		
		Arrays.sort(results, new StatsUtils().new StatComparator());
		
		return results;
	}
	
	
	private static int[] getAvgHighLowForLetter(Context c, char letter) {
		SharedPreferences pref = c.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
		
		String key = constructLetterKey(letter);
		String value = pref.getString(key, "");
		
		if (value.equals(""))
			return new int[]{letter, -1, -1, -1};
		
		String[] allScores = value.split(PREF_LETTER_DELIM);
		
		int max = -1;
		int min = -1;
		int sum = 0;
		
		int score;
		for (String scoreStr : allScores) {
			score = Integer.parseInt(scoreStr);
			
			if (max == -1 || score > max)
				max = score;
			
			if (min == -1 || score < min)
				min = score;
			
			sum += score;
		}
		
		if (max == -1)
			return new int[]{letter, -1, -1, -1};
		
		int avg = sum / allScores.length;
		
		return new int[]{letter, avg, max, min};
	}
	
	
	private class StatComparator implements Comparator<int[]> {
		
		@Override
		public int compare(int[] object1, int[] object2) {
			if (object1[1] < object2[1])
				return 1;
			else if (object1[1] == object2[1])
				return 0;
			else
				return -1;
		}
		
	}
	
	
	private static String constructLetterKey(char letter) {
		return PREF_LETTER_PREFIX + letter + PREF_LETTER_SUFFIX;
	}

}
