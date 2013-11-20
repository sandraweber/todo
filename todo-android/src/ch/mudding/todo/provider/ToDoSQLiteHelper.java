package ch.mudding.todo.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ToDoSQLiteHelper extends SQLiteOpenHelper {
	
	private static int DATABASE_VERSION = 1;
	private static String DATABASE_NAME = "todoDatabase.db";
	
	
	public ToDoSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(Columns.ToDos.SQL_TO_CREATE_TABLE);
		db.execSQL(Columns.ToDoLists.SQL_TO_CREATE_TABLE);
		db.execSQL(Columns.Contacts.SQL_TO_CREATE_TABLE);
		db.execSQL(Columns.Comments.SQL_TO_CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
	
	
}
