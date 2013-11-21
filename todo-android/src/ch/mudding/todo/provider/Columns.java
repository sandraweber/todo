package ch.mudding.todo.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public interface Columns extends BaseColumns {
	public static String AUTHORITY = TodoContentProvider.AUTHORITY;
	
	public static final class ToDos implements Columns {
		private ToDos() {}

		static final String TABLE_NAME = "todoItemsTable";
		static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.mudding.todo.todo";
		static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.mudding.todo.todo";
		
		public static final Uri CONTENT_URI = Uri.parse("content://"+ AUTHORITY +"/todoitems");
		public static final String KEY_TODO_LIST_ID = "todo_list_id";
		public static final String KEY_TEXT = "text";
		public static final String KEY_CREATION_DATE = "creation_date";
		public static final String KEY_CREATOR_ID = "creator_id";
		public static final String KEY_ASSIGNEE_ID = "assignee_id";
		public static final String KEY_STATUS = "status";

		public static final String STATUS_CREATED = "created";

		public static final String JOINED_CREATOR_NAME = "creator_full_name";
		public static final String JOINED_ASSIGNEE_NAME = "assignee_full_name";
		
		static final String SQL_TO_CREATE_TABLE = "create table " + TABLE_NAME + " ("
					+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ KEY_TODO_LIST_ID + " INTEGER, "
					+ KEY_TEXT + " TEXT, "
					+ KEY_STATUS + " TEXT,"
					+ KEY_CREATION_DATE + " INTEGER,"
					+ KEY_CREATOR_ID + " INTEGER,"
					+ KEY_ASSIGNEE_ID + " INTEGER"
					+ ");";
	}
	
	public static final class Contacts implements Columns {
		private Contacts() {}

		static final String TABLE_NAME = "contactsTable";
		static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.mudding.todo.contact";
		static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.mudding.todo.contact";
		
		public static final Uri CONTENT_URI = Uri.parse("content://"+ AUTHORITY +"/contacts");
		public static final String KEY_ID = "_id";
		public static final String KEY_FULL_NAME = "fullName";
		public static final String KEY_MD5_PHONE_NUMBER = "md5PhoneNumber";
		public static final String KEY_IS_OWNER = "isOwner";
		public static final String KEY_LOOKUP_KEY = "lookupKey";
		
		static final String SQL_TO_CREATE_TABLE = "create table " + TABLE_NAME + " ("
					+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ KEY_FULL_NAME + " TEXT, "
					+ KEY_MD5_PHONE_NUMBER + " TEXT,"
					+ KEY_IS_OWNER + " BOOLEAN,"
					+ KEY_LOOKUP_KEY + " TEXT"
					+ ");";
	}
	
	public static final class ToDoLists implements Columns {
		private ToDoLists() {}

		static final String TABLE_NAME = "todoListsTable";
		static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.mudding.todo.todoList";
		static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.mudding.todo.todoList";
		
		public static final Uri CONTENT_URI = Uri.parse("content://"+ AUTHORITY +"/todolists");
		public static final String KEY_ID = "_id";
		public static final String KEY_TOKEN = "token";
		
		static final String SQL_TO_CREATE_TABLE = "create table " + TABLE_NAME + " ("
					+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ KEY_TOKEN + " TEXT"
					+ ");";
	}
	
	
	public static final class Comments implements Columns {
		private Comments() {}

		static final String TABLE_NAME = "commentsTable";
		static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.mudding.todo.comment";
		static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.mudding.todo.comment";
		
		public static final Uri CONTENT_URI = Uri.parse("content://"+ AUTHORITY +"/comments");
		public static final String KEY_ID = "_id";
		public static final String KEY_TEXT = "text";
		public static final String KEY_CREATION_DATE = "creation_date";
		public static final String KEY_CREATOR_ID = "creator_id";
		
		static final String SQL_TO_CREATE_TABLE = "create table " + TABLE_NAME + " ("
					+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ KEY_TEXT + " TEXT, "
					+ KEY_CREATION_DATE + " INTEGER,"
					+ KEY_CREATOR_ID + " INTEGER"
					+ ");";
	}
}
