package com.android.adgcb.courses;

import java.util.ArrayList;
import java.util.List;

import com.android.adgcb.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/** Class CourseArrayAdapter
 * 
 * 	Arrays of courses
 * 
 * @author Jose Antonio Espino Palomares
 *
 */
public class CourseArrayAdapter extends ArrayAdapter<Course> {


	private TextView courseName;
	private List<Course> courses = new ArrayList<Course>();

	/**
	 * Constructor
	 * 
	 * @param context
	 * @param textViewResourceId
	 * @param objects
	 */
	public CourseArrayAdapter(Context context, int textViewResourceId,
			List<Course> objects) {
		super(context, textViewResourceId, objects);
		this.courses = objects;
	}

	/**
	 * Get number of courses
	 * 
	 * @return Courses arrays's size
	 */
	public int getCount() {
		return this.courses.size();
	}

	/**
	 * Get item from courses
	 */
	public Course getItem(int index) {
		return this.courses.get(index);
	}

	/**
	 * Get view to show
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			// ROW INFLATION
			LayoutInflater inflater = (LayoutInflater) this.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.course_listitem, parent, false);
		}

		// Get item
		Course course = getItem(position);
		
		// Get reference to TextView - course name
		courseName = (TextView) row.findViewById(R.id.textView1);
		
		//Set course name
		courseName.setText(course.name);
		
		return row;
	}
}