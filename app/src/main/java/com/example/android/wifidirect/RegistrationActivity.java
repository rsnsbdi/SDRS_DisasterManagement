package com.example.android.wifidirect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

public class RegistrationActivity extends AppCompatActivity {


    EditText emailid, password, phone, postaladdress, age, name;
    Spinner gender;
    Button register;
    ProgressDialog pDialog;
    SharedPreferences sp;
    SharedPreferences.Editor ed ;
    String serverip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        ed = sp.edit();
        serverip = sp.getString("serveraddress", "localhost:8080");
        emailid = (EditText)findViewById(R.id.emailid);
        password = (EditText)findViewById(R.id.password);
        phone = (EditText)findViewById(R.id.phone);
        postaladdress =(EditText)findViewById(R.id.postaladdress);
        age = (EditText)findViewById(R.id.age);
        name = (EditText)findViewById(R.id.name);
        gender= (Spinner)findViewById(R.id.gender);
        register = (Button)findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CallInternet().execute("Somewhere");
                // upon successful registration go to login screen
//                Intent i = new Intent(RegistrationActivity.this, LoginScreen.class);
//                startActivity(i);
            }
        });
    }

    class CallInternet extends AsyncTask<String, Void, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        String un, p, ph, add, a, n,g;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            un= emailid.getText().toString();
            p = password.getText().toString();
            ph = phone.getText().toString();
            add = postaladdress.getText().toString();
            a = age.getText().toString();
            n = name.getText().toString();
            g = gender.getSelectedItem().toString();

            pDialog = new ProgressDialog(RegistrationActivity.this);
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
            String reply= InternetHelper.get("http://"+serverip+"/MobileHealthNetwork/registerservlet?u="
                    + URLEncoder.encode(un) + "&p=" + URLEncoder.encode(p) + "&ph=" + URLEncoder.encode(ph) + "&add="+URLEncoder.encode(add) + "&a=" +URLEncoder.encode(a) + "&n=" + URLEncoder.encode(n) + "&g=" + URLEncoder.encode(g));

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
                Toast.makeText(RegistrationActivity.this, reply, Toast.LENGTH_SHORT).show();
                //ed.putString("username", login.getText().toString());
                //ed.commit();
                /// only if successful go to landing activity
                Intent i = new Intent(RegistrationActivity.this, LoginScreen.class);
                startActivity(i);
            } else {
                Toast.makeText(RegistrationActivity.this, reply, Toast.LENGTH_SHORT).show();
            }


        }

    }
}
