package com.android.adgcb.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Class SqLiteHelper
 * 
 * Create database
 * 
 * @author Jose Antonio Espino Palomares
 *
 */
class SqLiteHelper extends SQLiteOpenHelper{
	
    // Database name.
    public static final String DATABASE_NAME = "datos.db";
    // Database version.
    public static final int DATABASE_VERSION = 1;
    
    public static final String DATABASE_TABLE1 = "Cursos";
    public static final String DATABASE_TABLE2 = "Notificaciones";


    public static final String TablaCursos = "create table Cursos(nombre TEXT PRIMARY KEY, direccion TEXT, identificador TEXT)";
    public static final String TablaNotificaciones = "create table Notificaciones(_id integer PRIMARY KEY autoincrement, titulo TEXT, nombre TEXT, mensaje TEXT, estado INTEGER,fecha DATETIME)";

    public SqLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
    
  	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TablaCursos);
		db.execSQL(TablaNotificaciones);
	}
	
	
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE1);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE2);
        onCreate(db);
	}
	

}
