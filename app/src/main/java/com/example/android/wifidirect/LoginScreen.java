package com.example.android.wifidirect;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginScreen extends AppCompatActivity {

    EditText login, password;
    Button loginBt, registerBt;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    ProgressDialog pDialog;
    String serverip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        ed = sp.edit();
        serverip=sp.getString("serveraddress","localhost:8080");
        login = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);

        loginBt = (Button)findViewById(R.id.login);
        registerBt = (Button)findViewById(R.id.register);
        if (! "NOUSER".equals(sp.getString("username", "NOUSER"))){
            Intent i = new Intent(LoginScreen.this, LandingActivity.class);
            startActivity(i);
            finish();
        }
        loginBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (login.getText().toString() == null || "".equals(login.getText().toString())) {
                    Toast.makeText(LoginScreen.this, "Wrong credentials. Login Failed!", Toast.LENGTH_LONG).show();
                    return;
                } else if (password.getText() == null || "".equals(password.getText())){
                    Toast.makeText(LoginScreen.this, "Wrong credentials. Login Failed!", Toast.LENGTH_LONG).show();
                    return;
                }

                new CallInternet().execute("SomeURL");
            }
        });

        registerBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginScreen.this, RegistrationActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_login_settings, menu);
        return true;
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.servermenuitem:
                    showservermenuitem();
                return true;
            case R.id.phonemenuitem:
                    showphonemenuitem();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showservermenuitem() {
        LayoutInflater layoutInflater = LayoutInflater.from(LoginScreen.this);
        View promptView = layoutInflater.inflate(R.layout.server_input, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginScreen.this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.serverinput);
        editText.setText(sp.getString("serveraddress","please set"));
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ed.putString("serveraddress", editText.getText().toString());
                        serverip=editText.getText().toString();
                        ed.commit();
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void showphonemenuitem(){

        LayoutInflater layoutInflater = LayoutInflater.from(LoginScreen.this);
        View promptView = layoutInflater.inflate(R.layout.phone_input, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginScreen.this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.phoneinput);
        editText.setText(sp.getString("rescuephone","please set"));
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ed.putString("rescuephone", editText.getText().toString());

                        ed.commit();
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();


    }
    class CallInternet extends AsyncTask<String, Void, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        String username , pwd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            username = login.getText().toString();
            pwd = password.getText().toString();

            pDialog = new ProgressDialog(LoginScreen.this);
            pDialog.setMessage("Contacting Servers. please wait");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            // getting JSON string from URL
            String url = "http://" + serverip + "/MobileHealthNetwork/loginservlet?u="+ username + "&p=" + pwd;
            Log.d(WiFiDirectActivity.TAG, "");
            String reply= InternetHelper.get(url);
            //Log.d("printMyValue", reply);
            try {
                JSONObject root = new JSONObject(reply);
                String error = root.getString("error");
                reply = error;
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return reply;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String reply) {
            // dismiss the dialog after getting all products

            pDialog.dismiss();
            if ("false".equals(reply)) {
                Toast.makeText(LoginScreen.this, reply, Toast.LENGTH_SHORT).show();
                ed.putString("username", login.getText().toString());
                ed.commit();
                /// only if successful go to landing activity
                Intent i = new Intent(LoginScreen.this, LandingActivity.class);
                startActivity(i);
            } else {
                Toast.makeText(LoginScreen.this, "Login Failed. Please check your username or password.", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
