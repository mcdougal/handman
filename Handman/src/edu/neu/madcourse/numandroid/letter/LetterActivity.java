package edu.neu.madcourse.numandroid.letter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;
import edu.neu.madcourse.numandroid.HandmanUtils;
import edu.neu.madcourse.numandroid.R;

public abstract class LetterActivity extends Activity {
	
	private static final String TAG = "LetterActivity";

	public static final String INTENT_LETTER = "intent:letter";
	
	public static final String RESULT_LETTER = "result:letter";
	public static final String RESULT_ACCURACY = "result:accuracy";
	public static final String RESULT_TO_MAIN_MENU = "result:to_main_menu";
	
	private static final String PREF_LETTER = "pref:letter";
	private static final String PREF_STATE = "pref:state";
	private static final String PREF_PATH = "pref:path";
	private static final String PREF_ACCURACY = "pref:accuracy";
	
	protected static final int REQUEST_RESULT_POPUP_CLOSED = 0;
	
	private static final int STATE_DEFAULT = 0;
	
	protected static final int NO_ACCURACY = -1;
	
	
	//
	//  INSTANCE VARS
	//

	
	private AsyncTask<Void, Void, Void> calculateAccuracyTask;
	
	protected char letter;
	protected DrawPath path;
	protected int state;
	protected int accuracy;
	
	protected boolean blockDrawing;
	protected boolean showLetter;
	protected boolean returnResult;
	protected boolean returnToMenu;
	
	private DrawingView drawingView;
	private ViewGroup calcView;
	
	
	//
	//  ABSTRACT METHODS
	//
	
	
	protected abstract void setCurrentState();
	
	protected abstract String getExtraResultMsg(int result);
	
	protected abstract void accuracyCalculatedCallback();
	
	
	//
	//  ACTIVITY METHODS
	//
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.letter);
		
		if (getIntentExtras()) {
			setDefaults();
		}
		else {
			loadPreferences();
		}

		initializeViewVariables();

		setCurrentState();
	}
	
	@Override
	protected void onPause() {
		Log.d(TAG, "onPause()");
		super.onPause();
		
		savePreferences();
	}
	
	
	//
	//  SHARED PREFERENCES / INTENT EXTRAS
	//
	
	
	private boolean getIntentExtras() {
		Log.d(TAG, "getIntentExtras()");

		letter = getIntent().getCharExtra(INTENT_LETTER, ' ');
		getIntent().removeExtra(INTENT_LETTER);
		
		Log.d(TAG, "letter="+letter);
		
		return letter != ' ';
	}
	
	
	protected void setDefaults() {
		Log.d(TAG, "setDefaults()");
		
		path = new DrawPath();
		state = STATE_DEFAULT;
		accuracy = NO_ACCURACY;
	}
	
	
	protected void loadPreferences() {
		Log.d(TAG, "loadPreferences()");
		SharedPreferences pref = getPreferences(MODE_PRIVATE);
		
		letter = pref.getString(PREF_LETTER, "?").charAt(0);
		path = DrawPath.fromString(pref.getString(PREF_PATH, null));
		state = pref.getInt(PREF_STATE, STATE_DEFAULT);
		accuracy = pref.getInt(PREF_ACCURACY, NO_ACCURACY);
		
		Log.d(TAG, "letter="+letter);
		Log.d(TAG, "state="+state);
		Log.d(TAG, "accuracy="+accuracy);
	}

	
	protected void savePreferences() {
		Log.d(TAG, "savePreferences()");
		SharedPreferences pref = getPreferences(MODE_PRIVATE);
		
		pref.edit().putString(PREF_LETTER, ""+letter).commit();
		pref.edit().putString(PREF_PATH, path.toString()).commit();
		pref.edit().putInt(PREF_STATE, state).commit();
		pref.edit().putInt(PREF_ACCURACY, accuracy).commit();
		
		Log.d(TAG, "letter="+letter);
		Log.d(TAG, "state="+state);
		Log.d(TAG, "accuracy="+accuracy);
	}
	
	
	//
	//  VIEW VAR INITIALIZATION
	//
	
	
	protected void initializeViewVariables() {
		Log.d(TAG, "initializeViewVariables()");
		
		drawingView = new DrawingView(this);
		((ViewGroup) findViewById(R.id.letter_draw_root)).addView(drawingView);
		
		calcView = (ViewGroup) findViewById(R.id.letter_calc_view);
		calcView.setVisibility(View.GONE);
		
		blockDrawing = false;
		showLetter = true;
		returnResult = false;
		returnToMenu = false;
	}
	
	
	//
	//  FINISHING
	//
	
	
	@Override
	public void finish() {
		Log.d(TAG, "finish(): returnToMenu="+returnToMenu);
		Log.d(TAG, "finish(): returnResult="+returnResult);
		
		if (isCalculatingAccuracy()) {
			Toast.makeText(this,getString(R.string.letter_wait_for_calc_msg),
					Toast.LENGTH_LONG).show();
			
			return;
		}
		
		if (returnToMenu) {
			Intent resultIntent = new Intent();
			resultIntent.putExtra(RESULT_TO_MAIN_MENU, true);
			setResult(RESULT_OK, resultIntent);
		}
		else if (returnResult) {
			Intent resultIntent = new Intent();
			resultIntent.putExtra(RESULT_LETTER, letter);
			resultIntent.putExtra(RESULT_ACCURACY, accuracy);
			setResult(RESULT_OK, resultIntent);
		}
		else {
			setResult(RESULT_CANCELED);
		}
		
		super.finish();
	}
	
	
	/***************************************************************************
	 * 
	 * 
	 *   ACCURACY TASK
	 * 
	 * 
	 ***************************************************************************/
	
	
	protected boolean isCalculatingAccuracy() {
		return calculateAccuracyTask != null
			&& calculateAccuracyTask.getStatus() == AsyncTask.Status.RUNNING;
	}
	
	
	protected void calculateAccuracy() {
		Log.d(TAG, "calculateAccuracy()");
		
		calculateAccuracyTask = new CalculateAccuracyTask();
		calculateAccuracyTask.execute();
	}
	
	
	private class CalculateAccuracyTask extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected void onPreExecute() {
			Log.d(TAG, "CalculateAccuracyTask.onPreExecute()");
			
			calcView.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			Log.d(TAG, "CalculateAccuracyTask.doInBackground(): starting");
			accuracy = drawingView.getAccuracy();
			Log.d(TAG, "CalculateAccuracyTask.doInBackground(): finished");
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			Log.d(TAG, "CalculateAccuracyTask.onPostExecute()");
			
			calcView.setVisibility(View.GONE);
			
			accuracyCalculatedCallback();
		}
	}
	
	
	/***************************************************************************
	 * 
	 * 
	 *   DRAWING VIEW
	 * 
	 * 
	 ***************************************************************************/
	
	
	private class DrawingView extends SurfaceView implements SurfaceHolder.Callback {
		
		public DrawingThread thread; // calls the view's onDraw() method
		
		private Paint paint; // paint used to draw user's line

		private Rect viewRect; // boundary rect for bitmaps
		private Bitmap guidelinesImg; // guidelines for writing letter on
		private Bitmap letterImg; // the image of the letter
		private Bitmap drawLockImg; // indicates that drawing is blocked
		
		
		//
		//  INITIALIZATION
		//
		
		
		public DrawingView(Context context) {
			super(context);
			Log.d(TAG, "new DrawingView()");

			initializeViewProperties();
			initializeViewVariables();
			initializeThread();
		}
		
		
		private void initializeViewProperties() {
			Log.d(TAG, "DrawingView.initializeViewProperties()");
			
			setLayoutParams(new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			setFocusable(true);
			getHolder().addCallback(this);
		}
		
		
		private void initializeViewVariables() {
			Log.d(TAG, "DrawingView.initializeViewVariables()");

			guidelinesImg = BitmapFactory.decodeResource(getResources(),
					R.drawable.guidelines);
			
			letterImg = BitmapFactory.decodeResource(getResources(),
					HandmanUtils.getLetterDrawableResId(letter));
			
			drawLockImg = BitmapFactory.decodeResource(getResources(),
					R.drawable.drawing_locked);
			
			paint = new Paint();
			paint.setColor(Color.RED);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeJoin(Paint.Join.ROUND);
			paint.setStrokeCap(Paint.Cap.ROUND);
			
			int strokeWidth = 47;
			if (Character.isLowerCase(letter))
				strokeWidth = 43;
			
			paint.setStrokeWidth(
					HandmanUtils.getDpFromPixels(strokeWidth, getContext()));
		}
		
		
		private void initializeThread() {
			Log.d(TAG, "DrawingView.initializeThread()");
			thread = new DrawingThread(getHolder());
		}
		
		
		//
		//  DRAWING
		//
		
		
		@Override
		protected void onDraw(Canvas canvas) {
			
			
			if (viewRect == null)
				viewRect = new Rect(0, 0, getWidth(), getHeight());
			
			canvas.drawBitmap(guidelinesImg, null, viewRect, null);
			
			if (showLetter)
				canvas.drawBitmap(letterImg, null, viewRect, null);

			canvas.drawPath(path, paint);
			
			if (blockDrawing)
				canvas.drawBitmap(drawLockImg, null, viewRect, null);
		}
		
		
		//
		//  TOUCH EVENT
		//
		
		
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			if (path == null) {
				Log.d(TAG, "DrawingView.onTouchEvent() cancelled: path is null");
				return true;
			}
			
			synchronized (thread.surfaceHolder) {
				
				switch(event.getAction()) {
				
				case MotionEvent.ACTION_DOWN:
					Log.d(TAG, "Touch down event");
					path.moveTo(event.getX(), event.getY());
					break;
					
				case MotionEvent.ACTION_MOVE:
				case MotionEvent.ACTION_UP:
					if (!blockDrawing)
						path.lineTo(event.getX(), event.getY());
					break;
				
				}

				return true;
			}
		}
		
		
		//
		//  SURFACE METHODS
		//

		
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			if (thread.getState() == Thread.State.TERMINATED) {
				Log.d(TAG, "DrawingView.surfaceCreated(): thread restarted");
				thread = new DrawingThread(getHolder());
				thread.running = true;
				thread.start();
			}
			else {
				Log.d(TAG, "DrawingView.surfaceCreated(): thread started");
				thread.running = true;
				thread.start();
			}
		}

		
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			Log.d(TAG, "DrawingView.surfaceDestroyed(): stopping thread");
			
			boolean retry = true;
			thread.running = false;
			while (retry) {
				try {
					thread.join();
					retry = false;
					Log.d(TAG, "DrawingView.surfaceDestroyed(): thread stopped");
				}
				catch (InterruptedException e) {
					// do nothing
				}
			}
		}

		
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			
			// do nothing
		}
		
		
		//
		//  BLACK PIXEL PERCENTAGE
		//
		
		
		public int getAccuracy() {
			Log.d(TAG, "DrawingView.getAccuracy()");
			
			if (letterImg == null) {
				Log.d(TAG, "DrawingView.getAccuracy(): ERROR! letterImg is null");	
				return -1;
			}
			
			int width = getWidth();
			int height = getHeight();
			
			Bitmap tempBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			Canvas tempCanvas = new Canvas(tempBitmap);

			tempCanvas.drawBitmap(letterImg, null, viewRect, null);
			
			int numBlackPixelsStart = countBlackPixels(tempBitmap, width, height);
			
			tempCanvas.drawPath(path, paint);

			int numBlackPixelsEnd = countBlackPixels(tempBitmap, width, height);
			
			int resultAccuracy = 0;
			if (numBlackPixelsStart != 0)
				resultAccuracy = (int)((1.0f-((float)numBlackPixelsEnd/(float)numBlackPixelsStart))*100);
			
			Log.d(TAG, "DrawingView.getAccuracy(): result="+resultAccuracy);
			return resultAccuracy;
		}
		
		
		private int countBlackPixels(Bitmap b, int width, int height) {
			Log.d(TAG, "countBlackPixels()");
			
			int numPixels = width * height;
			
			int[] pixels = new int[numPixels];
			b.getPixels(pixels, 0, width, 0, 0, width, height);
			
			int numBlackPixels = 0;
			
			for (int pixel : pixels) {
				if (pixel == Color.BLACK)
					numBlackPixels++;
			}
			
			if (numBlackPixels == 0)
				Log.d(TAG, "countBlackPixels(): WARNING! 0 black pixels found");
			else
				Log.d(TAG, "countBlackPixels(): numBlackPixels="+numBlackPixels);
			
			return numBlackPixels;
		}
	}
	
	
	
	/***************************************************************************
	 * 
	 * 
	 *   DRAWING THREAD
	 * 
	 * 
	 ***************************************************************************/
	
	
	private class DrawingThread extends Thread {

		private SurfaceHolder surfaceHolder;
		private Canvas c;
		private boolean running = false;
		
		public DrawingThread(SurfaceHolder surfaceHolder) {
			this.surfaceHolder = surfaceHolder;
		}
		
		@Override
		public void run() {
			while (running) {
				if (drawingView != null) {
					c = null;
					try {
						c = surfaceHolder.lockCanvas();
						synchronized (surfaceHolder) {
							drawingView.onDraw(c);
						}
					}
					finally {
						if (c != null) {
							surfaceHolder.unlockCanvasAndPost(c);
						}
					}
				}
			}
		}
	}
}
