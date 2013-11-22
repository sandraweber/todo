package ch.mudding.todo.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class TodoSyncAdapterService extends Service {
	private static final String TAG = "TodoSyncAdapterService";
	private static SyncAdapter syncAdapter;
	private static final Object syncAdapterLock = new Object();
		
	@Override
	public void onCreate() {
		synchronized (syncAdapterLock) {
			if (syncAdapter == null)
				syncAdapter = new SyncAdapter(this);
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return syncAdapter.getSyncAdapterBinder();
	}
	
	private static class SyncAdapter extends AbstractThreadedSyncAdapter {
		private Context context;
		
		public SyncAdapter(Context context) {
			super(context, true);
			this.context = context; 
		}

		@Override
		public void onPerformSync(Account account, Bundle extras,
				String authority, ContentProviderClient provider,
				SyncResult syncResult) {
			
			Log.i(TAG, "Syncing Account: " + account.toString());
			
			AccountManager manager = AccountManager.get(context);
			final String accountProvider = manager.getUserData(account, "provider");
			
			Log.i(TAG, "Account has provider: " + accountProvider);
						
			ContentResolver resolver = context.getContentResolver();
			// TODO: actually implement sync ;)
		}
	}
}
