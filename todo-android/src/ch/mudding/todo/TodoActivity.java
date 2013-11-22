package ch.mudding.todo;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import ch.mudding.todo.R;
import ch.mudding.todo.provider.Columns.Contacts;
import ch.mudding.todo.provider.Columns.ToDos;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

public class TodoActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {
	
	private static final String TAG = "TodoActivity";
	private static final int TODO_LIST_ID = 0;
	
    private ListView listViewItems;

	private SimpleCursorAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        
        getLoaderManager().initLoader(0, null, this);
        
        listViewItems = (ListView) findViewById(R.id.listViewItems);
        itemsAdapter = new SimpleCursorAdapter(this, R.layout.listview_row_todo, null, new String[] { ToDos.KEY_TEXT, ToDos.JOINED_CREATOR_NAME, ToDos.JOINED_ASSIGNEE_NAME, ToDos.KEY_STATUS }, 
                new int[] { R.id.todo_row_name, R.id.todo_row_creator, R.id.todo_row_assignee, R.id.status }, 0);
        itemsAdapter.setViewBinder(new ViewBinder() {
			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				String columnName = cursor.getColumnName(columnIndex);
				if (columnName.equals(ToDos.JOINED_CREATOR_NAME)) {
					String creator = getResources().getString(R.string.created_by) + " " + cursor.getString(columnIndex);
					setText(view, creator);
					return true;
				} else if (columnName.equals(ToDos.JOINED_ASSIGNEE_NAME)) {
					String assignee = cursor.getString(columnIndex);
					if (assignee != null && !assignee.isEmpty()) {
						assignee = " / " + getResources().getString(R.string.assigned_to) + " " + assignee;
						setText(view, assignee);
					}
					return true;
				} else if (columnName.equals(ToDos.KEY_STATUS)) {
					RelativeLayout viewParent = (RelativeLayout) view.getParent();
					viewParent.setAlpha((float) 0.5);
					setText(view, cursor.getString(columnIndex));
					return true;
				}
				return false;
			}

			private void setText(View view, String text) {
				TextView textView = (TextView) view;
				textView.setText(text);
			}
		});
        listViewItems.setAdapter(itemsAdapter);
        
        setupDeleteItemListener();
        setupAddItemListener();
    }


    private void setupAddItemListener() {
        Button btn = (Button) findViewById(R.id.btnAddNewItem);
        final EditText textField = (EditText) findViewById(R.id.editTextNewItem);
        
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
             addToDoItem(textField);
            }
        });
        
        textField.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
					addToDoItem(textField);
					return true;
				}
				return false;
			}
		});
    }
    
    private void addToDoItem(EditText textField) {
    	int myselfAsContactId = getMyselfAsContactId();
    	
    	ContentValues values = new ContentValues();
    	values.put(ToDos.KEY_TODO_LIST_ID, TODO_LIST_ID);
    	values.put(ToDos.KEY_TEXT, textField.getText().toString());
    	values.put(ToDos.KEY_CREATOR_ID, myselfAsContactId);

    	getContentResolver().insert(ToDos.CONTENT_URI, values);
    	getLoaderManager().restartLoader(0, null, this);
    	
    	textField.setText("");
	}


    private int getMyselfAsContactId() {
    	Cursor cursor = getContentResolver().query(Contacts.CONTENT_URI, null, Contacts.KEY_IS_OWNER + "=1" , null, null);
    	
    	if (cursor.getCount()>0) {
    		cursor.moveToFirst();
    		return cursor.getInt(cursor.getColumnIndex(ToDos._ID)); 
    	} else {
	    	return createMyselfAsContact();
    	}
	}


	private int createMyselfAsContact() {
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String ownPhoneNumber = telephonyManager.getLine1Number();

		Cursor ownProfile = this.getContentResolver().query(
		        ContactsContract.Profile.CONTENT_URI, null, null, null,
		        null);
		String lookupKey = null;
		String fullName = null;
		if (ownProfile.getCount() != 0) {
			
			ownProfile.moveToFirst();
			lookupKey = ownProfile.getString(ownProfile.getColumnIndex(ContactsContract.Profile.LOOKUP_KEY));
			fullName = ownProfile.getString(ownProfile.getColumnIndex(ContactsContract.Profile.DISPLAY_NAME));
		}
		ownProfile.close();
		
		ContentValues values = new ContentValues();
		values.put(Contacts.KEY_MD5_PHONE_NUMBER, md5(ownPhoneNumber));
		values.put(Contacts.KEY_FULL_NAME, fullName);
		values.put(Contacts.KEY_LOOKUP_KEY, lookupKey);
		values.put(Contacts.KEY_IS_OWNER, 1);
		
		Log.d(TAG, "Created myself as contact: "+values.toString());
		
		Uri result = getContentResolver().insert(Contacts.CONTENT_URI, values);
		
		return Integer.parseInt(result.getPathSegments().get(1));
	}
	
	public static final String md5(final String s) {
	    try {
	        // Create MD5 Hash
	        MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
	        digest.update(s.getBytes());
	        byte messageDigest[] = digest.digest();

	        // Create Hex String
	        StringBuffer hexString = new StringBuffer();
	        for (int i = 0; i < messageDigest.length; i++) {
	            String h = Integer.toHexString(0xFF & messageDigest[i]);
	            while (h.length() < 2)
	                h = "0" + h;
	            hexString.append(h);
	        }
	        return hexString.toString();

	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    }
	    return "";
	}


	private void setupDeleteItemListener() {
        listViewItems.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                    int position, long rowId) {
            	String where = ToDos._ID + "=" +itemsAdapter.getItemId(position);
            	
            	Cursor cursor = (Cursor) itemsAdapter.getItem(position);
            	ContentValues values = new ContentValues();
            	DatabaseUtils.cursorRowToContentValues(cursor, values);
            	values.put(ToDos.KEY_STATUS, ToDos.STATUS_COMPLETED);
            	values.remove(ToDos.JOINED_ASSIGNEE_NAME);
            	values.remove(ToDos.JOINED_CREATOR_NAME);
            	getContentResolver().update(ToDos.CONTENT_URI, values, where, null);
            	
                itemsAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu; this adds items to the action bar if it is present.
	    getMenuInflater().inflate(R.menu.todo, menu);
	    return true;
	}

    @Override
    protected void onResume() {
    	super.onResume();
    	getLoaderManager().restartLoader(0, null, this);
    }

    @Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// TODO use the real todo list token
		String selection = ToDos.KEY_TODO_LIST_ID + "=" + TODO_LIST_ID;
		// TODO only look for open todos
		CursorLoader loader = new CursorLoader(this, ToDos.CONTENT_URI, null, selection, null, null);
		return loader;
	}


	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		itemsAdapter.swapCursor(cursor);
	}


	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		itemsAdapter.swapCursor(null);
	}
    
}