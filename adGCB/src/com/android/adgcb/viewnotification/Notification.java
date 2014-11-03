package com.android.adgcb.viewnotification;

/**
 * Class notification
 * 
 * Information about notification
 * 
 * @author Jose Antonio Espino Palomares
 *
 */
public class Notification {

	public String title;
	public String nameCourse;
	public String message;
	public String state;
	public String date;

	/**
	 * Constructor
	 */
	public Notification() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructor
	 */
	public Notification(String title, String nameCourse, String message,
			String state, String date) {
		this.title = title;
		this.nameCourse = nameCourse;
		this.message = message;
		this.state = state;
		this.date = date;
	}

	/**
	 * Get title of notification
	 * 
	 * @return notification's title
	 */
	public String getTitle() {
		return this.title;
	}
	
	/**
	 * Get course name of notification
	 * 
	 * @return notification's course name
	 */
	public String getNameCourse() {
		return this.nameCourse;
	}

	/**
	 * Get message of notification
	 * 
	 * @return notification's message
	 */
	public String getMessage() {
		return this.message;
	}
	
	/**
	 * Get state of notification
	 * 
	 * @return notification's state
	 */
	public String getState() {
		return this.state;
	}
	
	/**
	 * Get date of notification
	 * 
	 * @return notification's date
	 */
	public String getDate() {
		return this.date;
	}
}
