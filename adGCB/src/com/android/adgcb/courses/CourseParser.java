package com.android.adgcb.courses;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Class CourseParse
 * Parse data from xml file
 * 
 * @author Jose Antonio Espino Palomares
 *
 */
public class CourseParser {

	
	private DocumentBuilderFactory factory;
	private DocumentBuilder builder;
	private final List<Course> list;

	/**
	 * Constructor
	 */
	public CourseParser() {
		this.list = new ArrayList<Course>();
	}

	/**
	 * Get value of node
	 * @param map
	 * @param key
	 * @return string nodeValue
	 */
	private String getNodeValue(NamedNodeMap map, String key) {
		String nodeValue = null;
		Node node = map.getNamedItem(key);
		if (node != null) {
			nodeValue = node.getNodeValue();
		}
		return nodeValue;
	}

	/**
	 * Get list of courses
	 * 
	 * @return list of courses
	 */
	public List<Course> getList() {
		return this.list;
	}

	/**
	 * Parse XML file containing body part name/addresss/id
	 * 
	 * @param inStream
	 */
	public void parse(InputStream inStream) {
		try {
			// TODO: after we must do a cache of this XML!!!!
			this.factory = DocumentBuilderFactory.newInstance();
			this.builder = this.factory.newDocumentBuilder();
			this.builder.isValidating();
			Document doc = this.builder.parse(inStream, null);

			doc.getDocumentElement().normalize();

			NodeList countryList = doc.getElementsByTagName("course");
			final int length = countryList.getLength();

			for (int i = 0; i < length; i++) {
				final NamedNodeMap attr = countryList.item(i).getAttributes();
				final String courseName = getNodeValue(attr, "name");
				final String courseAddress = getNodeValue(attr, "address");
				final String courseId = getNodeValue(attr, "id");

				// Construct Country object
				Course course = new Course(courseName,courseAddress,courseId);
				
				// Add to list
				this.list.add(course);
				
			}
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
}