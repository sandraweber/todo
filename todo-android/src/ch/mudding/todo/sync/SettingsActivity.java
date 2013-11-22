package ch.mudding.todo.sync;

import ch.mudding.todo.R;
import android.app.Activity;
import android.os.Bundle;

public class SettingsActivity extends Activity {
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		setContentView(R.layout.activity_accountsettings);
	}
}
