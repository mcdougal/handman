package edu.neu.madcourse.numandroid.letter;

import java.util.LinkedList;

import android.graphics.Path;

/**
 * Custom class that overrides Path in order to create a useful toString()
 * method and a fromString() method.
 */
public class DrawPath extends Path {

	private static final String XY_DELIM = "a";
	private static final String LINE_DELIM = "b";
	private static final String POINT_DELIM = "c";
	
	private LinkedList<LinkedList<Float>> xLines = new LinkedList<LinkedList<Float>>();
	private LinkedList<LinkedList<Float>> yLines = new LinkedList<LinkedList<Float>>();
	
	private LinkedList<Float> currentXLine;
	private LinkedList<Float> currentYLine;
	
	@Override
	public void moveTo(float x, float y) {
		super.moveTo(x, y);
		
		currentXLine = new LinkedList<Float>();
		currentYLine = new LinkedList<Float>();
		
		currentXLine.add(x);
		currentYLine.add(y);
		
		xLines.add(currentXLine);
		yLines.add(currentYLine);
	}
	
	@Override
	public void lineTo(float x, float y) {
		super.lineTo(x, y);
		
		currentXLine.add(x);
		currentYLine.add(y);
	}
	
	@Override
	public String toString() {
		if (xLines.size() == 0)
			return "";
		
		StringBuilder sb = new StringBuilder();
		
		// for every x line
		int i = 0;
		for (LinkedList<Float> xLine : xLines) {
			if (i != 0)
				sb.append(LINE_DELIM);
			
			// for every x in the x line
			int j = 0;
			for (Float x : xLine) {
				if (j != 0)
					sb.append(POINT_DELIM);
				
				sb.append(x.toString());
				j++;
			}
			
			i++;
		}
		
		sb.append(XY_DELIM);
		
		// for every y line
		i = 0;
		for (LinkedList<Float> yLine : yLines) {
			if (i != 0)
				sb.append(LINE_DELIM);
			
			// for every x in the x line
			int j = 0;
			for (Float y : yLine) {
				if (j != 0)
					sb.append(POINT_DELIM);
				
				sb.append(y.toString());
				j++;
			}
			
			i++;
		}
		
		return sb.toString();
	}
	
	public static DrawPath fromString(String s) {
		DrawPath path = new DrawPath();
		
		if (s == null || s.equals(""))
			return path;
		
		String[] xy = s.split(XY_DELIM);
		
		if (xy == null || xy.length != 2)
			return path;
		
		String[] xLines = xy[0].split(LINE_DELIM);
		String[] yLines = xy[1].split(LINE_DELIM);
		
		if (xLines == null || yLines == null)
			return path;
		
		String[] xPoints;
		String[] yPoints;
		String yPoint;
		
		int i = 0;
		for (String line : xLines) {
			if (line == null || line.equals(""))
				continue;
			
			xPoints = line.split(POINT_DELIM);
			yPoints = yLines[i].split(POINT_DELIM);
			
			if (xPoints == null || yPoints == null)
				continue;
			
			int j = 0;
			for (String xPoint : xPoints) {
				yPoint = yPoints[j];
				
				if (xPoint == null || xPoint.equals("")
						|| yPoint == null || yPoint.equals(""))
					continue;
				
				if (j == 0)
					path.moveTo(Float.parseFloat(xPoint), Float.parseFloat(yPoint));
				else
					path.lineTo(Float.parseFloat(xPoint), Float.parseFloat(yPoint));
				
				j++;
			}
			
			i++;
		}
		
		return path;
	}
}









