package ch.mudding.todo.provider;

import ch.mudding.todo.provider.Columns.Comments;
import ch.mudding.todo.provider.Columns.Contacts;
import ch.mudding.todo.provider.Columns.ToDoLists;
import ch.mudding.todo.provider.Columns.ToDos;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

public class TodoContentProvider extends ContentProvider {

	public static String AUTHORITY = "ch.mudding.todo.provider";
	private ToDoSQLiteHelper databaseHelper;
	
	private static final int URI_ROOT = 0;
	private static final int URI_TODO_LISTS_ALL = 1;
	private static final int URI_TODO_LISTS_ITEM = 2; 
	private static final int URI_TODOS_ALL = 3;
	private static final int URI_TODOS_ITEM = 4; 
	private static final int URI_CONTACTS_ALL = 5;
	private static final int URI_CONTACTS_ITEM = 6; 
	private static final int URI_COMMENTS_ALL = 7;
	private static final int URI_COMMENTS_ITEM = 8; 
	
	private static final UriMatcher matcher = new UriMatcher(URI_ROOT);
	
	static {
		matcher.addURI(AUTHORITY, "todolists", URI_TODO_LISTS_ALL);
		matcher.addURI(AUTHORITY, "todolists/#", URI_TODO_LISTS_ITEM);
		matcher.addURI(AUTHORITY, "todoitems", URI_TODOS_ALL);
		matcher.addURI(AUTHORITY, "todoitems/#", URI_TODOS_ITEM);
		matcher.addURI(AUTHORITY, "contacts", URI_CONTACTS_ALL);
		matcher.addURI(AUTHORITY, "contacts/#", URI_CONTACTS_ITEM);
		matcher.addURI(AUTHORITY, "comments", URI_COMMENTS_ALL);
		matcher.addURI(AUTHORITY, "comments/#", URI_COMMENTS_ITEM);
	}

	
	@Override
	public boolean onCreate() {
		databaseHelper = new ToDoSQLiteHelper(getContext());
		return true;
	} 

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		int result;

		if (!isItemUri(uri) && TextUtils.isEmpty(selection)) {
			throw new IllegalArgumentException("Please provide a selection to delete.");
		}
		selection = getSelectionWithId(uri, selection);
		
		switch (matcher.match(uri)) {
		case URI_TODO_LISTS_ALL:
		case URI_TODO_LISTS_ITEM:
			result = db.delete(ToDoLists.TABLE_NAME, selection, selectionArgs);
			break;
		case URI_TODOS_ALL:
		case URI_TODOS_ITEM:
			result = db.delete(ToDos.TABLE_NAME, selection, selectionArgs);
			break;
		case URI_CONTACTS_ALL:
		case URI_CONTACTS_ITEM:
			result = db.delete(Contacts.TABLE_NAME, selection, selectionArgs);
			break;
		case URI_COMMENTS_ALL:
		case URI_COMMENTS_ITEM:
			result = db.delete(Comments.TABLE_NAME, selection, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return result;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		long rowID;
		
		switch (matcher.match(uri)) {
		case URI_TODO_LISTS_ALL:
			validateMandatoryFields(values, new String[] { ToDoLists.KEY_TOKEN });
			rowID = db.insert(ToDoLists.TABLE_NAME, null, values);
			break;
		case URI_TODOS_ALL:
			validateMandatoryFields(values, new String[] { ToDos.KEY_TEXT, ToDos.KEY_CREATOR_ID, ToDos.KEY_TODO_LIST_ID });
			setToDefaultIfEmpty(values, ToDos.KEY_STATUS, ToDos.STATUS_CREATED);
			setToCurrentTimeIfEmpty(values, ToDos.KEY_CREATION_DATE);
			rowID = db.insert(ToDos.TABLE_NAME, null, values);
			break;
		case URI_CONTACTS_ALL:
			validateMandatoryFields(values, new String[] { Contacts.KEY_FULL_NAME, Contacts.KEY_LOOKUP_KEY });
			setToDefaultIfEmpty(values, Contacts.KEY_IS_OWNER, 0);
			rowID = db.insert(Contacts.TABLE_NAME, null, values);
			break;
		case URI_COMMENTS_ALL:
			validateMandatoryFields(values, new String[] { Comments.KEY_TEXT, Comments.KEY_CREATOR_ID });
			setToCurrentTimeIfEmpty(values, Comments.KEY_CREATION_DATE);
			rowID = db.insert(Comments.TABLE_NAME, null, values);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		if (rowID > 0){
			Uri result = ContentUris.withAppendedId(uri, rowID);
			getContext().getContentResolver().notifyChange(result, null);
			return result;
		} else
			throw new SQLException("Failed to insert row into: " + uri);
	}

	private void setToDefaultIfEmpty(ContentValues values, String key,
			int defaultValue) {
		if (!values.containsKey(key)) {
			values.put(key, defaultValue);
		}
	}

	private void setToDefaultIfEmpty(ContentValues values, String key,
			String defaultValue) {
		if (!values.containsKey(key)) {
			values.put(key, defaultValue);
		}
	}

	private void setToCurrentTimeIfEmpty(ContentValues values,
			String creationDate) {
		if (!values.containsKey(creationDate)) {
			values.put(creationDate, System.currentTimeMillis());
		}
	}

	private void validateMandatoryFields(ContentValues values, String[] mandatoryFields) {
		for (String mandatoryField: mandatoryFields) {
			if (!values.containsKey(mandatoryField)) {
				throw new IllegalArgumentException("Missing mandatory field: " + mandatoryField);
			}
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		SQLiteDatabase db = databaseHelper.getReadableDatabase();

		switch (matcher.match(uri)) {
		case URI_TODO_LISTS_ALL:
			queryBuilder.setTables(ToDoLists.TABLE_NAME);
			break;
		case URI_TODO_LISTS_ITEM:
			queryBuilder.setTables(ToDoLists.TABLE_NAME);
			queryBuilder.appendWhere(ToDoLists._ID + "=" + getId(uri));
			break;
		case URI_TODOS_ALL:
			queryBuilder.setTables(ToDos.TABLE_NAME);
			break;
		case URI_TODOS_ITEM:
			queryBuilder.setTables(ToDos.TABLE_NAME);
			queryBuilder.appendWhere(ToDos._ID + "=" + getId(uri));
			break;
		case URI_CONTACTS_ALL:
			queryBuilder.setTables(Contacts.TABLE_NAME);
			break;
		case URI_CONTACTS_ITEM:
			queryBuilder.setTables(Contacts.TABLE_NAME);
			queryBuilder.appendWhere(Contacts._ID + "=" + getId(uri));
			break;
		case URI_COMMENTS_ALL:
			queryBuilder.setTables(Comments.TABLE_NAME);
			break;
		case URI_COMMENTS_ITEM:
			queryBuilder.setTables(Comments.TABLE_NAME);
			queryBuilder.appendWhere(Comments._ID + "=" + getId(uri));
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	private String getId(Uri uri) {
		return uri.getPathSegments().get(1);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		int result;

		if (!isItemUri(uri) && TextUtils.isEmpty(selection)) {
			throw new IllegalArgumentException("Please provide a selection to update.");
		}
		selection = getSelectionWithId(uri, selection);
		
		switch (matcher.match(uri)) {
		case URI_TODO_LISTS_ALL:
		case URI_TODO_LISTS_ITEM:
			result = db.update(ToDoLists.TABLE_NAME, values, selection, selectionArgs);
			break;
		case URI_TODOS_ALL:
		case URI_TODOS_ITEM:
			result = db.update(ToDos.TABLE_NAME, values, selection, selectionArgs);
			break;
		case URI_CONTACTS_ALL:
		case URI_CONTACTS_ITEM:
			result = db.update(Contacts.TABLE_NAME, values, selection, selectionArgs);
			break;
		case URI_COMMENTS_ALL:
		case URI_COMMENTS_ITEM:
			result = db.update(Comments.TABLE_NAME, values, selection, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return result;
	}

	private String getSelectionWithId(Uri uri, String selection) {
		if (isItemUri(uri)) {
			return BaseColumns._ID + "=" + getId(uri) + " AND ("+ selection +")";
		} else {
			return selection;
		}
	}

	private boolean isItemUri(Uri uri) {
		return uri.getPathSegments().size()==2;
	}

	@Override
	public String getType(Uri uri) {
		switch (matcher.match(uri)) {
		case URI_TODO_LISTS_ALL:
			return ToDoLists.CONTENT_TYPE;
		case URI_TODO_LISTS_ITEM:
			return ToDoLists.CONTENT_ITEM_TYPE;
		case URI_TODOS_ALL:
			return ToDos.CONTENT_TYPE;
		case URI_TODOS_ITEM:
			return ToDos.CONTENT_ITEM_TYPE;
		case URI_CONTACTS_ALL:
			return Contacts.CONTENT_TYPE;
		case URI_CONTACTS_ITEM:
			return Contacts.CONTENT_ITEM_TYPE;
		case URI_COMMENTS_ALL:
			return Comments.CONTENT_TYPE;
		case URI_COMMENTS_ITEM:
			return Comments.CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}

}
