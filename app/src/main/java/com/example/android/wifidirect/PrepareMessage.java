package com.example.android.wifidirect;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PrepareMessage extends AppCompatActivity implements LocationListener, SensorEventListener {

    Button d2d, sms, internet;
    TextView status, message, sensordata;
    String messageText, messageType = "";
    String serverip;
    ProgressDialog pDialog;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    private LocationManager locationManager;
    private SensorManager mgr;
    private Map<String, String> values = new HashMap<String, String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_prepare_message);
        status = (TextView) findViewById(R.id.status);
        message = (TextView) findViewById(R.id.message);

        sensordata = (TextView) findViewById(R.id.sensordata);
        mgr = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors = mgr.getSensorList(Sensor.TYPE_ALL);
        Iterator<Sensor> sensorIt = sensors.iterator();
        int count=5;
        while (sensorIt.hasNext()) {
            Sensor s = sensorIt.next();
            mgr.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
            count--;
            if (count <0) break;
        }

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        ed = sp.edit();
        serverip = sp.getString("serveraddress", "localhost:8080");
        messageText = "";
        setMessageInitial();
        message.setText(messageText);
        status.setText("Message Preparation Done");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 10, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 10, this);


        d2d = (Button) findViewById(R.id.d2d);
        sms = (Button) findViewById(R.id.sms);
        internet = (Button) findViewById(R.id.internet);
        Button addToMessage = (Button) findViewById(R.id.addToMessage);
        addToMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(PrepareMessage.this);
                View promptView = layoutInflater.inflate(R.layout.message_input, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PrepareMessage.this);
                alertDialogBuilder.setView(promptView);
                final EditText editText = (EditText) promptView.findViewById(R.id.messageaddition);

                // setup a dialog window
                alertDialogBuilder.setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                messageText = messageText + editText.getText();
                                message.setText(messageText);
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
        });
        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmsManager smsManager = SmsManager.getDefault();
                String phone = sp.getString("rescuephone", "9972740962");
                smsManager.sendTextMessage(phone, null, messageText + sensordata.getText(), null, null);
                Toast.makeText(PrepareMessage.this, "SMS Successfullly sent to " + phone, Toast.LENGTH_LONG).show();
            }
        });


        internet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager cm =
                        (ConnectivityManager) PrepareMessage.this.getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                if (isConnected) {
                    new CallInternet().execute("some url");
                } else {
                    Toast.makeText(PrepareMessage.this, "Internet not available. Please try D2D", Toast.LENGTH_LONG).show();
                }

            }
        });

        d2d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PrepareMessage.this, WiFiDirectActivity.class);
                i.putExtra("MessageType", "fromPrepareMessage");
                i.putExtra("Message", messageText + sensordata.getText());
                ed.putString("Message", messageText + sensordata.getText());

                ed.commit();
                startActivity(i);
            }
        });


        // get sensor data
        // get GPS data
        // 2 buttons 1. send through internet
        // 2. send to D2D
        // 3. send SMS
    }

    private void setMessageInitial() {

        messageText = messageText + "Username=" + sp.getString("username", "not set") + ":";
        Bundle b = getIntent().getExtras();
        if (b != null) {
            messageType = b.getString("MessageType");
            if (messageType != null)
                messageText = messageText + "Message Type=" + messageType + ":";
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        String str = "Latitude: " + location.getLatitude() + "Longitude: " + location.getLongitude();
        Toast.makeText(getBaseContext(), str, Toast.LENGTH_LONG).show();
        messageText = messageText + str;
        message.setText(messageText);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    class CallInternet extends AsyncTask<String, Void, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        String text = "";
        String mess = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            text = sensordata.getText().toString();
            mess = message.getText().toString();
            pDialog = new ProgressDialog(PrepareMessage.this);
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
            String reply= InternetHelper.get("http://" + serverip + "/MobileHealthNetwork/message?m="+
                    URLEncoder.encode(mess + text) + "&t=" + messageType);
            //Log.d("printMyValue", reply);
            try {
                JSONObject root = new JSONObject(reply);
                String error = root.getString("error");
                reply = error;
            } catch (JSONException e) {
                e.printStackTrace();
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
                Toast.makeText(PrepareMessage.this, "Successfully sent", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(PrepareMessage.this, "Could not send", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    protected void onResume() {
        List<Sensor> sensors = mgr.getSensorList(Sensor.TYPE_ALL);
        Iterator<Sensor> sensorIt = sensors.iterator();
        String name = "";
        while (sensorIt.hasNext()){
            Sensor s = sensorIt.next();
            mgr.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        List<Sensor> sensors = mgr.getSensorList(Sensor.TYPE_ALL);
        Iterator<Sensor> sensorIt = sensors.iterator();
        String name = "";
        while (sensorIt.hasNext()){
            Sensor s = sensorIt.next();
            mgr.unregisterListener(this, s);
        }
        super.onPause();
    }
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onSensorChanged(SensorEvent event) {
        String data = event.sensor.getName() + " " + event.values[0];
        values.put(event.sensor.getName(), Float.toString(event.values[0]));
        sensordata.setText(rebuildText(values));
        sensordata.invalidate();
        mgr.unregisterListener(this, event.sensor);
    }

    private String rebuildText(Map<String, String> m){
        Set<String> set =m.keySet();
        String str = "";
        Iterator<String> it = set.iterator();
        while(it.hasNext()){
            String key = it.next();
            str = str + key + " " + m.get(key);
        }
        return str;
    }
}