package ch.mudding.todo;

import java.util.ArrayList;

import ch.mudding.todo.R;

import android.os.Bundle;
import android.app.Activity;
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
        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        listViewItems.setAdapter(itemsAdapter);
        
        initializeItems();
        setupListViewListener();
        setupButtonListener();
    }


    private void setupButtonListener() {
                Button btn = (Button) findViewById(R.id.btnAddNewItem);
                btn.setOnClickListener(new OnClickListener() {
                        
                        @Override
                        public void onClick(View v) {
                         EditText newItem = (EditText) findViewById(R.id.editTextNewItem);
                         itemsAdapter.add(newItem.getText().toString());
                         newItem.setText("");
                        }
                });
        }


        private void setupListViewListener() {
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