package org.maximko.kabinetbalance;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import java.io.IOException;

public class MainActivity extends Activity {
    
    private final String TAG = "KABINET balance fetcher";
    private final int PREF_ID = 0;
    private final int INFO_ID = 1;
    
    BalanceFetcher balance;
    SharedPreferences settings;
    private String login;
    private String password;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        balance = new BalanceFetcher();
        settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        getData();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        getData();
    }
    
    public void refreshData(View view) {
        new getBalance().execute(login, password);
    }
    
    public void getData() {
        login = settings.getString("login", "nd");
        password = settings.getString("password", "nd");
        if (login.equals("nd") || password.equals("nd")) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else {
            new getBalance().execute(login, password);
        }
    }
    
    private class getBalance extends AsyncTask<String, Void, String> {

        ProgressDialog dialog;
        
        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(MainActivity.this, null, 
                                         MainActivity.this.getString(R.string.wait), true);
        }
        
        @Override
        protected void onPostExecute(String result) {
            String balance;
            if (result.equals("")) { balance = "Ошибка."; }
            else { balance = result + "р"; }
            TextView balancetext =  (TextView)findViewById(R.id.balancetext);
            balancetext.setText(balance);
            dialog.dismiss();
        }
        
        @Override
        protected String doInBackground(String... paramss) {
            String result = "Ошибка";
            try { result =  balance.get(paramss[0], paramss[1]); } 
            catch (IOException ex) { Log.e(TAG, ex.toString()); }
            return result;
        }
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, PREF_ID, 0, R.string.settings).setIcon(android.R.drawable.ic_menu_preferences);
        menu.add(0, INFO_ID, 0, R.string.info).setIcon(android.R.drawable.ic_menu_info_details);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case PREF_ID:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case INFO_ID:
                new AlertDialog.Builder(this).setMessage(R.string.infomsg)
                                             .setNeutralButton(R.string.ok, null)
                                             .setIcon(android.R.drawable.ic_dialog_info)
                                             .setTitle(R.string.info).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
