package com.example.android.wifidirect;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LandingActivity extends AppCompatActivity {

    Button sos, safe, volunteer, profile;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    String serverip;


    //SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        ed = sp.edit();

        //sp = PreferenceManager.getDefaultSharedPreferences(this);
        sos = (Button)findViewById(R.id.sos);
        safe = (Button)findViewById(R.id.safe);
        volunteer= (Button)findViewById(R.id.volunteer);
        profile= (Button)findViewById(R.id.viewProfile);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.edit().putString("Volunteer", "NO").commit();
                Intent i = new Intent(LandingActivity.this, ProfileView.class);
                startActivity(i);
            }
        });
        sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.edit().putString("Volunteer", "NO").commit();
                Intent i = new Intent(LandingActivity.this, PrepareMessage.class);
                i.putExtra("MessageType", "SOS");
                startActivity(i);
            }
        });

        safe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.edit().putString("Volunteer", "NO").commit();
                Intent i = new Intent(LandingActivity.this, PrepareMessage.class);
                i.putExtra("MessageType", "SAFE");
                startActivity(i);
            }
        });

        volunteer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.edit().putString("Volunteer", "YES").commit();

                Intent i = new Intent(LandingActivity.this, WiFiDirectActivity.class);
                i.putExtra("MessageType", "FromLanding");
                startActivity(i);
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.servermenuitem:
                showservermenuitem();
                return true;
            case R.id.phonemenuitem:
                showphonemenuitem();
                return true;
            case R.id.logout:
                ed.putString("username","NOUSER");
                ed.commit();
                Intent i = new Intent(LandingActivity.this, LoginScreen.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showservermenuitem() {
        LayoutInflater layoutInflater = LayoutInflater.from(LandingActivity.this);
        View promptView = layoutInflater.inflate(R.layout.server_input, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LandingActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.serverinput);
        editText.setText(sp.getString("serveraddress", "please set"));
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

        LayoutInflater layoutInflater = LayoutInflater.from(LandingActivity.this);
        View promptView = layoutInflater.inflate(R.layout.phone_input, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LandingActivity.this);
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


}
