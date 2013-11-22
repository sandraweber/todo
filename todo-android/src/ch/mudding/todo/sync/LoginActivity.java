package ch.mudding.todo.sync;

import java.io.IOException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.KeyAgreement;
import javax.crypto.spec.DHParameterSpec;

import ch.mudding.todo.R;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Base64DataException;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AccountAuthenticatorActivity {
	private static final String TAG = "TodoActivity";
	public static final String ACTION_LOGIN = "ch.mudding.todo.sync.LOGIN";
	
	private Button btnLogin;
	private EditText editProvider;
	private EditText editEmail;
	private EditText editPassword;
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		setContentView(R.layout.activity_login);
		
		btnLogin = (Button)findViewById(R.id.btnLogin);
		editProvider = (EditText)findViewById(R.id.editProvider);
		editEmail = (EditText)findViewById(R.id.editEmail);
		editPassword = (EditText)findViewById(R.id.editPassword);
		
		btnLogin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onLoginButtonClick();
			}
		});
	}
	
	private void onLoginButtonClick() {
		final String provider = editProvider.getText().toString();
				
		if (provider.startsWith("http://")) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Login Insecure");
			alert.setMessage("Using http:// is highly discouraged since it makes stealing credentials easy. Switch to https:// and use encryption?");
			
			alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					doLogin("https://" + provider.substring("http://".length()));
				}
			});
			
			alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					doLogin(provider);
				}
			});
			
			alert.show();
		} else {
			if (!provider.startsWith("https://"))
				doLogin("https://" + provider);
			else
				doLogin(provider);
		}
	}
	
	private void doLogin(String provider)
	{
		Logger.getLogger(TAG).log(Level.INFO, "Provider is " + provider);
		final Context context = this;
		
		final ProgressDialog progressDialog = ProgressDialog.show(context, "Login", "Contacting Provider...", true, false);
		
		try {			
			final URL url = new URL(provider);
			final String username = editEmail.getText().toString();
			final String password = editPassword.getText().toString();
			final String authHeader = "Basic " + Base64.encodeToString((username + ":" + password).getBytes(), Base64.NO_WRAP);
			
			(new AsyncTask<Void, Void, Boolean>() {
				private Exception exception;
				protected Boolean doInBackground(Void... params) {
					try {
						HttpURLConnection connection = (HttpURLConnection)url.openConnection();
						connection.setConnectTimeout(10000);
						Logger.getLogger(TAG).log(Level.INFO, "Auth: " + authHeader);
						connection.setRequestProperty("Authorization", authHeader);
						Logger.getLogger(TAG).log(Level.INFO, "Getting response code");
						int code = connection.getResponseCode();
						Logger.getLogger(TAG).log(Level.INFO, "Got code: " + code);
						return code == 200;
					} catch (IOException e) {
						Logger.getLogger(TAG).log(Level.INFO, "Exception :(  " + e.getMessage());
						exception = e;
						return false;
					}
				}
				
				protected void onPostExecute(Boolean result) {
					Logger.getLogger(TAG).log(Level.INFO, "In result: " + result);
					progressDialog.cancel();
					
					if (exception != null)
						(new AlertDialog.Builder(context)).setTitle("Error").setMessage("Unable to connect to provider").show();
					else {
						if (result) {
							onLoginSuccessful(url.toString(), username, password);
						} else {
							(new AlertDialog.Builder(context)).setTitle("Nope").setMessage(":(").show();
						}
					}
				}
			}).execute();
		} catch (MalformedURLException e) {
			(new AlertDialog.Builder(this)).setTitle("Error").setMessage("Invalid URL").show();
		}
	}
	
	private void onLoginSuccessful(String provider, String user, String password) {
		Logger.getLogger(TAG).log(Level.INFO, "Provider for account is " + provider);
		
		Bundle userdata = new Bundle();
		userdata.putString("provider", provider);
		
		Account account = new Account(user, "ch.mudding.todo.account");
		AccountManager manager = AccountManager.get(this);
		boolean created = manager.addAccountExplicitly(account, password, userdata);
		
		Bundle result = new Bundle();
		result.putString(AccountManager.KEY_ACCOUNT_NAME, user);
		result.putString(AccountManager.KEY_ACCOUNT_TYPE, "ch.mudding.todo.account");
		
		if (created)
			setAccountAuthenticatorResult(result);
		else
			setAccountAuthenticatorResult(null);
		
		finish();
	}
	
	/*
	Example code that could be extended to generate a device key with diffie hellman,
	providing perfect forward secrecy.
	
	private void getDeviceKey(String provider)
	{
		DHParameterSpec dhparams = new DHParameterSpec(1, 1);
		
		KeyPairGenerator keygen = KeyPairGenerator.getInstance("DH");
		keygen.initialize(dhparams);
		KeyPair keypair = keygen.generateKeyPair();
		
		KeyAgreement agreement = KeyAgreement.getInstance("DH");
		agreement.init(keypair.getPrivate());
		
		// TODO: send key  keypair.getPublic().getEncoded();
		// TODO: get key
		
		KeyFactory keyfactory = KeyFactory.getInstance("DH");
		X509EncodedKeySpec x509spec = new X509EncodedKeySpec("".getBytes()); // TODO key from remote
		PublicKey receivedKey = keyfactory.generatePublic(x509spec);
		
		agreement.doPhase(receivedKey, true);
		byte[] secret = agreement.generateSecret();
	}*/
	
	private static final View.OnClickListener loginButtonClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			
		}
	};
}
