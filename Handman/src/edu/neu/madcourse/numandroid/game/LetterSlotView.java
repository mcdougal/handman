package edu.neu.madcourse.numandroid.game;

import edu.neu.madcourse.numandroid.HandmanUtils;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LetterSlotView extends LinearLayout {
	
	private TextView letterView;
	
	public LetterSlotView(Context context, char letter) {
		super(context);
		
		//	<LinearLayout
		//		android:layout_width="fill_parent"
		//		android:layout_height="60dp"
		//		android:layout_weight="1"
		//		android:orientation="vertical">
		
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT,
				HandmanUtils.getDpFromPixels(60f, context));
		
		lp.weight = 1;
		
		setLayoutParams(lp);
		setOrientation(VERTICAL);
		
		// <TextView
		// 		android:layout_width="35dp"
		// 		android:layout_height="55dp"
		// 		android:layout_gravity="center_horizontal"
		// 		android:gravity="center"
		// 		android:textColor="@android:color/white"
		//		android:textSize="45sp" />
		
		letterView = new TextView(context);
		
		lp = new LinearLayout.LayoutParams(
				HandmanUtils.getDpFromPixels(35f, context),
				HandmanUtils.getDpFromPixels(55f, context));
		
		lp.gravity = Gravity.CENTER_HORIZONTAL;
		
		letterView.setLayoutParams(lp);
		letterView.setGravity(Gravity.CENTER);
		letterView.setText(""+letter);
		letterView.setTextColor(Color.WHITE);
		letterView.setTextSize(45f);
		letterView.setVisibility(INVISIBLE);

		addView(letterView);

		// <View
		//		android:background="@android:color/white"
		//		android:layout_width="33dp"
		//		android:layout_height="3dp"
		//		android:layout_gravity="center_horizontal" />
		
		View underline = new View(context);
		
		lp = new LinearLayout.LayoutParams(
				HandmanUtils.getDpFromPixels(33f, context),
				HandmanUtils.getDpFromPixels(3f, context));
		
		lp.gravity = Gravity.CENTER_HORIZONTAL;
		
		underline.setLayoutParams(lp);
		underline.setBackgroundColor(Color.WHITE);
		
		addView(underline);
	}
	
	
	public void showLetter() {
		letterView.setVisibility(VISIBLE);
	}
}
