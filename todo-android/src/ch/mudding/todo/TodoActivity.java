package ch.mudding.todo;

import java.util.ArrayList;

import ch.mudding.todo.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class TodoActivity extends Activity {

    private ListView listViewItems;
	private ArrayList<String> items;
	private ArrayAdapter<String> itemsAdapter;


        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        
        listViewItems = (ListView) findViewById(R.id.listViewItems);
        items = new ArrayList<String>();
        itemsAdapter = new ArrayAdapter<String>(this, R.layout.activity_todo, items);
        listViewItems.setAdapter(itemsAdapter);
        
        initializeItems();
        setupDeleteItemListener();
        setupAddItemListener();
    }


    private void setupAddItemListener() {
        Button btn = (Button) findViewById(R.id.btnAddNewItem);
        final EditText textField = (EditText) findViewById(R.id.editTextNewItem);
        
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
             addItem(textField);
            }
        });
        
        textField.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
					addItem(textField);
					return true;
				}
				return false;
			}
		});
    }
    
    private void addItem(EditText textField) {
		 itemsAdapter.add(textField.getText().toString());
		 textField.setText("");
	}


    private void setupDeleteItemListener() {
        listViewItems.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                    int position, long rowId) {
                items.remove(position);
                itemsAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    private void initializeItems() {
        items.add("Äpfel");
        items.add("Tomate");
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu; this adds items to the action bar if it is present.
	    getMenuInflater().inflate(R.menu.todo, menu);
	    return true;
	}
    
}