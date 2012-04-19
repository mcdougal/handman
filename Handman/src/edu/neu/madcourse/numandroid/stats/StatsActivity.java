package edu.neu.madcourse.numandroid.stats;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableRow.LayoutParams;
import edu.neu.madcourse.numandroid.R;

public class StatsActivity extends Activity {
	
	private static final String TAG = "StatsActivity";

	int gamesPlayed;
	int gamesWon;
	int gamesLost;
	int[][] upperStats;
	int[][] lowerStats;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stats);

		new GetStatsTask().execute();
	}
	
	
	private void showStats() {
		
		((TextView) findViewById(R.id.stats_games_played_view)).setText(
				String.format(getString(R.string.stats_games_played), gamesPlayed));
		
		((TextView) findViewById(R.id.stats_games_won_view)).setText(
				String.format(getString(R.string.stats_games_won), gamesWon));
		
		((TextView) findViewById(R.id.stats_games_lost_view)).setText(
				String.format(getString(R.string.stats_games_lost), gamesLost));
		
		TableLayout table = (TableLayout) findViewById(R.id.stats_table);
		
		TableRow.LayoutParams lp = new TableRow.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		lp.weight = 1;
		
		addHeader(lp, table);
		
		addBlankRow(table);
		
		for (int[] stat : upperStats) {
			addRow(lp, table, stat);
		}
		
		addBlankRow(table);
		addBlankRow(table);

		for (int[] stat : lowerStats) {
			addRow(lp, table, stat);
		}
		
		// if no rows were added besides the header and blank rows
		if (table.getChildCount() == 4) {
			table.setVisibility(View.GONE);
		}
	}
	
	
	private void addHeader(LayoutParams lp, TableLayout table) {

		TableRow row = new TableRow(this);
		
		TextView  cell = new TextView(this);
		cell.setLayoutParams(lp);
		cell.setText(" ");
		
		row.addView(cell);
		
		// average
		
		cell = new TextView(this);
		cell.setLayoutParams(lp);
		cell.setGravity(Gravity.CENTER);
		cell.setText(getString(R.string.stats_avg_header));
		cell.setTextColor(Color.WHITE);
		cell.setTextSize(20f);
		
		row.addView(cell);
		
		// high
		
		cell = new TextView(this);
		cell.setLayoutParams(lp);
		cell.setGravity(Gravity.CENTER);
		cell.setText(getString(R.string.stats_high_header));
		cell.setTextColor(Color.WHITE);
		cell.setTextSize(20f);
		
		row.addView(cell);
		
		// low
		
		cell = new TextView(this);
		cell.setLayoutParams(lp);
		cell.setGravity(Gravity.CENTER);
		cell.setText(getString(R.string.stats_low_header));
		cell.setTextColor(Color.WHITE);
		cell.setTextSize(20f);
		
		row.addView(cell);
		
		table.addView(row);
	}
	
	
	private void addBlankRow(TableLayout table) {
		TableRow row = new TableRow(this);
		
		TextView  cell = new TextView(this);
		cell.setText(" ");
		row.addView(cell);
		
		table.addView(row);
	}
	
	
	private void addRow(LayoutParams lp, TableLayout table, int[] stats) {
		
		if (stats[1] == -1)
			return;
		
		TableRow row = new TableRow(this);
		
		// letter
		
		TextView cell = new TextView(this);
		cell.setLayoutParams(lp);
		cell.setGravity(Gravity.LEFT);
		cell.setText(""+(char)stats[0]);
		cell.setTextColor(Color.WHITE);
		cell.setTextSize(25f);
		
		row.addView(cell);
		
		// average
		
		cell = new TextView(this);
		cell.setLayoutParams(lp);
		cell.setGravity(Gravity.CENTER);
		
		if (stats[1] == -1)
			cell.setText("");
		else
			cell.setText(stats[1]+"%");
		
		cell.setTextColor(Color.WHITE);
		cell.setTextSize(25f);
		
		row.addView(cell);
		
		// high
		
		cell = new TextView(this);
		cell.setLayoutParams(lp);
		cell.setGravity(Gravity.CENTER);

		if (stats[2] == -1)
			cell.setText("");
		else
			cell.setText(stats[2]+"%");
		
		cell.setTextColor(Color.WHITE);
		cell.setTextSize(25f);
		
		row.addView(cell);
		
		// low
		
		cell = new TextView(this);
		cell.setLayoutParams(lp);
		cell.setGravity(Gravity.CENTER);

		if (stats[3] == -1)
			cell.setText("");
		else
			cell.setText(stats[3]+"%");
		
		cell.setTextColor(Color.WHITE);
		cell.setTextSize(25f);
		
		row.addView(cell);

		table.addView(row);
	}
	
	
	private class GetStatsTask extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected void onPreExecute() {
			Log.d(TAG, "GetStatsTask.onPreExecute()");
			
			((ViewGroup) findViewById(R.id.stats_loader)).setVisibility(View.VISIBLE);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			Log.d(TAG, "GetStatsTask.doInBackground(): starting");
			
			gamesWon = StatsUtils.getGamesWon(StatsActivity.this);
			gamesLost = StatsUtils.getGamesLost(StatsActivity.this);
			gamesPlayed = gamesWon + gamesLost;
			
			upperStats = StatsUtils.getAvgHighLowForAllUppercaseLetters(StatsActivity.this);
			lowerStats = StatsUtils.getAvgHighLowForAllLowercaseLetters(StatsActivity.this);
			
			Log.d(TAG, "GetStatsTask.doInBackground(): finished");
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			Log.d(TAG, "GetStatsTask.onPostExecute()");

			((ViewGroup) findViewById(R.id.stats_loader)).setVisibility(View.GONE);
			
			showStats();
		}
	}
	
}
