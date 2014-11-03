package com.android.adgcb.database;

import java.util.ArrayList;
import java.util.List;

import com.android.adgcb.viewnotification.Notification;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Class DbAdapter
 * 
 * Operation with database
 * 
 * @author Jose Antonio Espino Palomares
 * 
 */
public class DbAdapter {

	private SqLiteHelper dbHelper;
	private SQLiteDatabase db;

	private final Context contexto;

	/**
	 * Constructor
	 * 
	 * @param contexto
	 */
	public DbAdapter(Context contexto) {
		this.contexto = contexto;
	}

	/**
	 * Open database
	 * 
	 * @return
	 * @throws SQLException
	 */
	public SQLiteDatabase open() throws SQLException {
		dbHelper = new SqLiteHelper(contexto);
		db = dbHelper.getWritableDatabase();
		return db;
	}

	/**
	 * Close database
	 */
	public void close() {
		dbHelper.close();
	}

	/**
	 * Load course
	 * 
	 * @return ArrayList with courses
	 */
	public ArrayList<String> loadCourses() {

		ArrayList<String> resultados = new ArrayList<String>();
		Cursor c = db.rawQuery("SELECT nombre FROM Cursos", null);
		if (c.moveToFirst()) {
			int indexcolumna = c.getColumnIndexOrThrow("nombre");
			do {
				String valorColumna = c.getString(indexcolumna);
				resultados.add(valorColumna);
			} while (c.moveToNext());
		}
		c.close();
		return resultados;

	}

	/**
	 * Load Notification
	 * 
	 * @return List with notifications
	 */
	public List<Notification> loadNotification() {
		List<Notification> notificationList = new ArrayList<Notification>();

		String[] campos = new String[] { "titulo", "nombre", "mensaje",
				"estado", "fecha" };

		Cursor c = db.query("Notificaciones", campos, null, null, null, null,
				"_id DESC");
		if (c.moveToFirst()) {
			do {
				Notification notification = new Notification(c.getString(0),
						c.getString(1), c.getString(2), c.getString(3),
						c.getString(4));
				// Add to list
				notificationList.add(notification);

			} while (c.moveToNext());
		}

		return notificationList;

	}

	/**
	 * Store new course
	 * 
	 * @param nombre
	 * @param direccion
	 * @param identificador
	 * @return
	 */
	public long newCourse(String nombre, String direccion, String identificador) {

		ContentValues registro = new ContentValues();
		registro.put("nombre", nombre);
		registro.put("direccion", direccion);
		registro.put("identificador", identificador);
		return db.insert(SqLiteHelper.DATABASE_TABLE1, null, registro);
	}

	/**
	 * Store new notification
	 * 
	 * @param titulo
	 * @param nombre
	 * @param mensaje
	 * @param estado
	 * @param reminderDateTime
	 * @return
	 */
	public long newNotificacion(String titulo, String nombre, String mensaje,
			int estado, String reminderDateTime) {

		ContentValues registro = new ContentValues();
		registro.put("titulo", titulo);
		registro.put("nombre", nombre);
		registro.put("mensaje", mensaje);
		registro.put("estado", 0);
		registro.put("fecha", reminderDateTime);
		return db.insert(SqLiteHelper.DATABASE_TABLE2, null, registro);
	}

	/*
	 * public Cursor cargarNotificaciones() {
	 * 
	 * Cursor mCursor = db.query("Notificaciones", new String[] { "_id",
	 * "titulo", "nombre", "mensaje", "estado", "fecha" }, null, null, null,
	 * null, "_id DESC");
	 * 
	 * if (mCursor != null) { mCursor.moveToFirst(); } return mCursor; }
	 */

	/**
	 * Update state of notification
	 * 
	 * @param title
	 *            Title of notification to update
	 */
	public void updateState(String title, String msg) {
		// int filasAfectadas = 0;
		ContentValues values = new ContentValues();
		values.put("estado", 1);
		// filasAfectadas = (int)
		db.update("Notificaciones", values, "titulo = ? AND mensaje = ?",
				new String[] {title,msg});
	}

	/**
	 * Delete notification
	 * 
	 * @param title
	 *            Title of notification to delete
	 */
	public void deleteNotificacion(String title) {
		db.delete("Notificaciones", "titulo = ?", new String[] { title });
	}

	/**
	 * Check if course exits
	 * 
	 * @param nombre
	 *            Name of course to check
	 * @return true if course exits or false if course not exits
	 */
	public boolean checkCourse(String nombre) {
		String[] campos = new String[] { "nombre" };
		String[] args = new String[] { nombre };

		Cursor c = db.query("Cursos", campos, "nombre=?", args, null, null,
				null);

		if (c.getCount() <= 0) {
			return false;
		}
		return true;
	}

	/**
	 * Check if notification exits
	 * 
	 * @param nombre
	 *            Name of notification to check
	 * @return true if course exits or false if course not exits
	 */
	public boolean checkNofitication(String titulo, String msg) {
		String[] campos = new String[] { "titulo" };
		String[] args = new String[] { titulo };

		String[] campos2 = new String[] { "mensaje" };
		String[] args2 = new String[] { msg };

		Cursor c = db.query("Notificaciones", campos, "titulo=?", args, null,
				null, null);
		if (c.getCount() <= 0) {
			return false;
		} else {
			Cursor c2 = db.query("Notificaciones", campos2, "mensaje=?", args2,
					null, null, null);
			if (c2.getCount() <= 0) {
				return false;
			} else
				return true;
		}
	}

	/**
	 * Search notifications
	 * 
	 * @param inputText
	 *            String to search in notifications
	 * @return List with notifications
	 * @throws SQLException
	 */
	public List<Notification> searchNotifications(String inputText)
			throws SQLException {

		List<Notification> notificationList = new ArrayList<Notification>();
		Cursor c;
		if (inputText == null || inputText.length() == 0) {

			c = db.query("Notificaciones", new String[] { "titulo", "nombre",
					"mensaje", "estado", "fecha" }, null, null, null, null,
					"_id DESC");

			if (c.moveToFirst()) {
				// Recorremos el cursor hasta que no haya más registros
				do {
					Notification notification = new Notification(
							c.getString(0), c.getString(1), c.getString(2),
							c.getString(3), c.getString(4));
					// Add to list
					notificationList.add(notification);

				} while (c.moveToNext());
			}

		} else {
			String titulo = "titulo";
			c = db.query("Notificaciones", new String[] { "titulo", "nombre",
					"mensaje", "estado", "fecha" }, titulo + " like '%"
					+ inputText + "%'", null, null, null, "_id DESC");
			if (c.moveToFirst()) {
				// Recorremos el cursor hasta que no haya más registros
				do {
					Notification notification = new Notification(
							c.getString(0), c.getString(1), c.getString(2),
							c.getString(3), c.getString(4));
					// Add to list
					notificationList.add(notification);

				} while (c.moveToNext());
			}
		}

		return notificationList;

	}

	/**
	 * Delete configuration
	 * 
	 * @param nombre
	 *            String with configuration to delete
	 */
	public void deleteConfiguracion(String nombre) {
		db.delete("Cursos", "nombre = ?", new String[] { nombre });
	}

	/**
	 * Get course
	 * 
	 * @param nombre
	 *            String with name of course
	 * 
	 * @return
	 */
	public Cursor getCourse(String nombre) {

		String[] campos = new String[] { "direccion", "identificador" };
		String[] args = new String[] { nombre };

		Cursor c = db.query("Cursos", campos, "nombre=?", args, null, null,
				null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}

}
