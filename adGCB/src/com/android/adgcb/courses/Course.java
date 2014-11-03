package com.android.adgcb.courses;

/**
 * Class Couse
 * 
 * Information about course
 * 
 * @author Jose Antonio Espino Palomares
 *
 */
public class Course {
	public String name;
	public String address;
	public String id;

	/**
	 * Constructor
	 */
	public Course() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Constructor
	 */
	public Course(String name, String address, String id) {
		this.name = name;
		this.address = address;
		this.id = id;
	}
	
	/**
	 * Get name of course
	 * 
	 * @return course's name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Get address of course
	 * 
	 * @return course's address
	 */
	public String getAddress(){
		return this.address;
	}
	
	/**
	 * Get id of course
	 * 
	 * @return course's id
	 */
	public String getId(){
		return this.id;
	}
}