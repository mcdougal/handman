package edu.neu.madcourse.numandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AlphabetAdapter extends BaseAdapter {
	
	public static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

	
	private Context context;
	private String letters;

	
	public AlphabetAdapter(Context context, String letters) {
		this.context = context;
		this.letters = letters;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;

		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);

			v = vi.inflate(R.layout.alphabet_item, null);
		}
		
		((TextView) v.findViewById(R.id.alphabet_item_txt)).setText(""+getItem(position));

		return v;
	}


	@Override
	public int getCount() {
		return letters.length();
	}


	@Override
	public Object getItem(int position) {
		return letters.charAt(position);
	}


	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public void toUpperCase() {
		updateLetters(letters.toUpperCase());
	}
	
	public void toLowerCase() {
		updateLetters(letters.toLowerCase());
	}
	
	public void updateLetters(String letters) {
		this.letters = letters;
		notifyDataSetChanged();
	}

}
