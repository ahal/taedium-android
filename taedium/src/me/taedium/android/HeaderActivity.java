package me.taedium.android;

import me.taedium.android.add.AddName;
import me.taedium.android.api.Caller;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class HeaderActivity extends Activity {
    protected View vHeader;
   
    private static final int ACTIVITY_CREATE = 50;
    private static final int ACTIVITY_REGISTER = 60;
    private static final int DIALOG_LOGIN = 200;
    protected static final String LOGGED_IN_KEY = "loggedIn";
    protected static final String USER_PASS_KEY = "userpass";

    public void initializeHeader() {
        vHeader = findViewById(R.id.vHeader);
        vHeader.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(HeaderActivity.this, AddName.class);
                startActivityForResult(i, ACTIVITY_CREATE);                  
            }
        });
    }
    
    public void initializeHeader(View.OnClickListener callback) {
        // Initialize bAdd
        vHeader = findViewById(R.id.vHeader);
        vHeader.setOnClickListener(callback);
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        final Dialog dialog;
        dialog = new Dialog(this, R.style.Dialog);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.0f;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        switch(id) {
            case DIALOG_LOGIN:
                dialog.setContentView(R.layout.login);
                dialog.setTitle("Login");
                
                // Create new account button
                Button bRegister = (Button)dialog.findViewById(R.id.bCreateAccount);
                bRegister.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View v) {
						dismissDialog(DIALOG_LOGIN);
		                register();
					}
				});
                
                // Login
                Button bLogin = (Button)dialog.findViewById(R.id.bLogin);
                bLogin.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v) {
                        //Authenticate user
                    	EditText userText = (EditText)dialog.findViewById(R.id.etUserName);
                    	EditText passText = (EditText)dialog.findViewById(R.id.etPassword);                    	                    	
                        boolean is_authenticated = Caller.getInstance(getApplicationContext()).checkLogin(
                        		userText.getText().toString(), passText.getText().toString());
                        if (is_authenticated) {
                            Toast.makeText(HeaderActivity.this, R.string.msgLoginSuccess, Toast.LENGTH_LONG).show();
                        }
                        else {
                        	Toast.makeText(HeaderActivity.this, R.string.msgLoginFailed, Toast.LENGTH_LONG).show();
                        }
                        dismissDialog(DIALOG_LOGIN);
                    }
                });
                break;
            default:
                // This dialog not known to this activity
                return super.onCreateDialog(id);
        }
        return dialog;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	boolean result = super.onCreateOptionsMenu(menu);
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.taedium_menu, menu);   	
    	return result;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	if (ApplicationGlobals.getInstance().isLoggedIn(getApplicationContext())) {
    		menu.findItem(R.id.mnuLogin).setVisible(false);
    		menu.findItem(R.id.mnuLogout).setVisible(true);
    		menu.findItem(R.id.mnuRegister).setVisible(false);
    	}
    	else {
    		menu.findItem(R.id.mnuLogin).setVisible(true);
    		menu.findItem(R.id.mnuLogout).setVisible(false);
    		menu.findItem(R.id.mnuRegister).setVisible(true);
    	}
    	return super.onPrepareOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    	case R.id.mnuLogin:
    		showDialog(DIALOG_LOGIN);
    		return true;
    	case R.id.mnuLogout:
    		logout();
    		return true;
    	case R.id.mnuRegister:
    		register();
    		return true;
    	}   		
    	return super.onOptionsItemSelected(item);
    }    
    // Logout helper
    private void logout() {
    	ApplicationGlobals globals = ApplicationGlobals.getInstance();
    	globals.setUserpass("", getApplicationContext());
		globals.setLoggedIn(false, getApplicationContext());
		Toast.makeText(this, getString(R.string.msgLoggedOut), Toast.LENGTH_LONG).show();
		
		// return to main screen
		Intent i = new Intent(HeaderActivity.this, FirstStart.class);
		startActivityForResult(i, ACTIVITY_REGISTER);
	}
    
    // Register helper
    private void register() {
    	Intent i = new Intent(HeaderActivity.this, Register.class);
        startActivityForResult(i, ACTIVITY_REGISTER);
    }
}
