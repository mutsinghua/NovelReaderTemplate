package com.win16.utils.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public interface TableData{
	
	void checkStructure(SQLiteDatabase db);

	TableData readFrom(Cursor cursor);

    long insertTo(SQLiteDatabase db);
}