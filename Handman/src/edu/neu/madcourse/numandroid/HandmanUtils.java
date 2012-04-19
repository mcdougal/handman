package edu.neu.madcourse.numandroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

import android.content.Context;
import android.util.Log;

public class HandmanUtils {
	
	private static final String TAG = "HangmanUtils";
	
	public static int getDpFromPixels(float pixels, Context context) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pixels * scale + 0.5f);
	}
	
	
	public static String getRandomWord(Context context) {
        Log.d(TAG, "getRandomWord()");
		
		int index = new Random().nextInt(27560); // number of lines in word list
		
		Log.d(TAG, "index="+index);
		
		String line = "";
		
		try {
            InputStream stream = context.getResources().openRawResource(R.raw.wordlist);
            InputStreamReader reader = new InputStreamReader(stream);
            BufferedReader br = new BufferedReader(reader);
            
            int count = 0;
            line = br.readLine();
            
            while (count < index) {
            	line = br.readLine();
            	count++;
            }
            
            Log.d(TAG, "line="+line);
            
            stream.close();
        }
        catch (IOException e) {
            Log.e(TAG, "An IO error occurred reading the word list");
            e.printStackTrace();
        }
        catch (Exception e) {
            Log.e(TAG, "An unknown error occurred reading the word list: "+e.getMessage());
            e.printStackTrace();
        }

        return line;
	}


	public static int getLetterDrawableResId(char letter) {
		switch(letter) {

		case 'A':
			return R.drawable.uppera;
		case 'B':
			return R.drawable.upperb;
		case 'C':
			return R.drawable.upperc;
		case 'D':
			return R.drawable.upperd;
		case 'E':
			return R.drawable.uppere;
		case 'F':
			return R.drawable.upperf;
		case 'G':
			return R.drawable.upperg;
		case 'H':
			return R.drawable.upperh;
		case 'I':
			return R.drawable.upperi;
		case 'J':
			return R.drawable.upperj;
		case 'K':
			return R.drawable.upperk;
		case 'L':
			return R.drawable.upperl;
		case 'M':
			return R.drawable.upperm;
		case 'N':
			return R.drawable.uppern;
		case 'O':
			return R.drawable.uppero;
		case 'P':
			return R.drawable.upperp;
		case 'Q':
			return R.drawable.upperq;
		case 'R':
			return R.drawable.upperr;
		case 'S':
			return R.drawable.uppers;
		case 'T':
			return R.drawable.uppert;
		case 'U':
			return R.drawable.upperu;
		case 'V':
			return R.drawable.upperv;
		case 'W':
			return R.drawable.upperw;
		case 'X':
			return R.drawable.upperx;
		case 'Y':
			return R.drawable.uppery;
		case 'Z':
			return R.drawable.upperz;

		case 'a':
			return R.drawable.lowera;
		case 'b':
			return R.drawable.lowerb;
		case 'c':
			return R.drawable.lowerc;
		case 'd':
			return R.drawable.lowerd;
		case 'e':
			return R.drawable.lowere;
		case 'f':
			return R.drawable.lowerf;
		case 'g':
			return R.drawable.lowerg;
		case 'h':
			return R.drawable.lowerh;
		case 'i':
			return R.drawable.loweri;
		case 'j':
			return R.drawable.lowerj;
		case 'k':
			return R.drawable.lowerk;
		case 'l':
			return R.drawable.lowerl;
		case 'm':
			return R.drawable.lowerm;
		case 'n':
			return R.drawable.lowern;
		case 'o':
			return R.drawable.lowero;
		case 'p':
			return R.drawable.lowerp;
		case 'q':
			return R.drawable.lowerq;
		case 'r':
			return R.drawable.lowerr;
		case 's':
			return R.drawable.lowers;
		case 't':
			return R.drawable.lowert;
		case 'u':
			return R.drawable.loweru;
		case 'v':
			return R.drawable.lowerv;
		case 'w':
			return R.drawable.lowerw;
		case 'x':
			return R.drawable.lowerx;
		case 'y':
			return R.drawable.lowery;
		case 'z':
			return R.drawable.lowerz;

		}

		return -1; // error
	}
}
