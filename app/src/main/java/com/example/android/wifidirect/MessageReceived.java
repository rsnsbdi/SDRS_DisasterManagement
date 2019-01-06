package com.example.android.wifidirect;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

public class MessageReceived extends AppCompatActivity {

    Button forward, discard, sms, internet;
    String message="dummy message";
    TextView messageFromClient;
    SharedPreferences sp ;
    SharedPreferences.Editor ed;
    ProgressDialog pDialog;
    String serverip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_received);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        serverip = sp.getString("serveraddress", "localhost:8080");
        ed = sp.edit();
        Bundle b = getIntent().getExtras();
        messageFromClient = (TextView)findViewById(R.id.messageFromClient);
        if (b !=null) {
            message = b.getString("Message");
        }
        messageFromClient.setText(message);
        ed.putString("Message", message);
        ed.commit();

        forward = (Button)findViewById(R.id.forward);
        discard = (Button)findViewById(R.id.discard);
        sms = (Button)findViewById(R.id.sendThroughSMS);
        internet = (Button)findViewById(R.id.sendThroughInternet);
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ed.putString("Message", message);
                ed.commit();
                Intent i = new Intent(MessageReceived.this, WiFiDirectActivity.class);
                startActivity(i);
            }
        });
        discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MessageReceived.this, WiFiDirectActivity.class);
                startActivity(i);
            }
        });
        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmsManager smsManager = SmsManager.getDefault();
                String phone = sp.getString("rescuephone", "9972740962");
                smsManager.sendTextMessage(phone, null, message, null, null);
                Toast.makeText(MessageReceived.this, "SMS Successfullly sent to " + phone, Toast.LENGTH_LONG).show();
            }
        });
        internet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager cm =
                        (ConnectivityManager)MessageReceived.this.getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                if (isConnected){
                    new CallInternet().execute("some url");
                } else {
                    Toast.makeText(MessageReceived.this, "Internet not available. Please try Forward", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    class CallInternet extends AsyncTask<String, Void, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(MessageReceived.this);
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

            //Log.d("printMyValue", reply);
            String reply= null;
            try {

                reply = InternetHelper.get("http://" + serverip + "/MobileHealthNetwork/message?m="
                        + URLEncoder.encode(message, "UTF-8"));

                JSONObject root = new JSONObject(reply);
                String error = root.getString("error");
                reply = error;
            } catch (JSONException e) {
                e.printStackTrace();
                return "error";
            } catch (Exception ex){
                ex.printStackTrace();
                return "error";
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
                Toast.makeText(MessageReceived.this, "Successfully sent", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(MessageReceived.this, "Could not send", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
