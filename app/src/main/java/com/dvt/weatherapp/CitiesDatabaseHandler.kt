package com.dvt.weatherapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.json.JSONArray
import org.json.JSONObject

class CitiesDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "cities.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_NAME = "cities"
        private const val COLUMN_ID = "id"
        private const val COLUMN_CITY_NAME = "city_name"

        private const val CREATE_TABLE_SQL = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CITY_NAME TEXT NOT NULL
            )
        """
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_SQL)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Insert a city name
    fun insertCity(cityName: String): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_CITY_NAME, cityName)
        }
        return db.insert(TABLE_NAME, null, contentValues).also {
            db.close()
        }
    }

    // Get all cities as JSON
    fun getAllCitiesAsJson(): String {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, null)
        val citiesArray = JSONArray()

        if (cursor.moveToFirst()) {
            do {
                val cityObject = JSONObject().apply {
                    put(COLUMN_ID, cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)))
                    put(COLUMN_CITY_NAME, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CITY_NAME)))
                }
                citiesArray.put(cityObject)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return citiesArray.toString()
    }
}
