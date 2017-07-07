package com.feiliu.flgamesdk.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class AccountDBOpenHelper extends SQLiteOpenHelper {
	public AccountDBOpenHelper(Context context) {
		super(context, "flaccount.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//_id userId username password nickName phone email userType time
		db.execSQL("create table account (_id integer primary key autoincrement,userId varchar(20),username varchar(20),password varchar(20),nickName varchar(20),phone varchar(20),email varchar(20),userType varchar(20),quitState varchar(20),time varchar(20))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
}
