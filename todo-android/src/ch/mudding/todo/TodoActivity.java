package ch.mudding.todo;


import ch.mudding.todo.R;
import ch.mudding.todo.provider.Columns.ToDos;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class TodoActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TODO_LIST_TOKEN = "triallist";
	private static final int TODO_LIST_ID = 0;
	
    private ListView listViewItems;

	private SimpleCursorAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        
        getLoaderManager().initLoader(0, null, this);
        
        listViewItems = (ListView) findViewById(R.id.listViewItems);
        itemsAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null, new String[] { ToDos.KEY_TEXT }, 
                new int[] { android.R.id.text1 }, 0);
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
             addTask(textField);
            }
        });
        
        textField.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
					addTask(textField);
					return true;
				}
				return false;
			}
		});
    }
    
    private void addTask(EditText textField) {
    	int myselfAsContactId = getMyselfAsContactId();
    	
    	ContentValues values = new ContentValues();
    	values.put(ToDos.KEY_TODO_LIST_ID, TODO_LIST_ID);
    	values.put(ToDos.KEY_TEXT, textField.getText().toString());
    	values.put(ToDos.KEY_CREATOR_ID, myselfAsContactId);

    	textField.setText("");
	}


    private int getMyselfAsContactId() {
		// TODO implement
		return 0;
	}


	private void setupDeleteItemListener() {
        listViewItems.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                    int position, long rowId) {
            	String where = ToDos._ID + "=" +itemsAdapter.getItemId(position);
            	getContentResolver().delete(ToDos.CONTENT_URI, where, null);
            	
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
		String selection = ToDos.KEY_TODO_LIST_ID + "=" + TODO_LIST_TOKEN;
		CursorLoader loader = new CursorLoader(this, ToDos.CONTENT_URI, null, null, null, null);
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