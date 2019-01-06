package com.example.android.wifidirect;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

public class Profile extends AppCompatActivity {

    TextView profileEmail;
    EditText editName, editAge, editPhone, editAddress, editEmergencyPhone,
            editOfficeAddress, editBloodGroup, editAllergy, editWeight, editHeight;
    Button update;
    Spinner spinnerGender;
    ProgressDialog pDialog;
    SharedPreferences sp;
    String serverip;

    String email, name, age, phone, address, emergencyPhone, officeAddress,
            bloodGroup, allergy, weight, height, gender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        sp = PreferenceManager.getDefaultSharedPreferences(this);
        email = sp.getString("username", "NOUSER");
        profileEmail = (TextView)findViewById(R.id.profileEmail);
        profileEmail.setText(email);


        serverip = sp.getString("serveraddress", "localhost:8080");



        editName = (EditText) findViewById(R.id.editName);
        editName.setText(sp.getString("profilename", "NA"));

        editAge = (EditText) findViewById(R.id.editAge);
        editAge.setText(sp.getString("profileage", "NA"));

        editPhone= (EditText) findViewById(R.id.editPhone);
        editPhone.setText(sp.getString("profilephone", "NA"));

        editAddress = (EditText) findViewById(R.id.editAddress);
        editAddress.setText(sp.getString("profileadd", "NA"));

        editEmergencyPhone = (EditText) findViewById(R.id.editEmergencyPhone);
        editEmergencyPhone.setText(sp.getString("profileemergency", "NA"));

        editOfficeAddress= (EditText) findViewById(R.id.editOfficeAddress);
        editOfficeAddress.setText(sp.getString("profileofficeaddress", "NA"));

        editBloodGroup= (EditText) findViewById(R.id.editBloodGroup);
        editBloodGroup.setText(sp.getString("profilebloodgroup", "NA"));


        editAllergy= (EditText) findViewById(R.id.editAllergy);
        editAllergy.setText(sp.getString("profileallergy", "NA"));

        editWeight= (EditText) findViewById(R.id.editWeight);
        editWeight.setText(sp.getString("profileweight", "NA"));

        editHeight= (EditText) findViewById(R.id.editHeight);
        editHeight.setText(sp.getString("profileheight", "NA"));

        update = (Button)findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = editName.getText().toString();
                age = editAge.getText().toString();
                phone = editPhone.getText().toString();
                address = editAddress.getText().toString();
                emergencyPhone= editEmergencyPhone.getText().toString();
                officeAddress= editOfficeAddress.getText().toString();
                bloodGroup = editBloodGroup.getText().toString();
                allergy = editAllergy.getText().toString();
                weight = editWeight.getText().toString();
                height = editHeight.getText().toString();

                new CallInternet().execute("somestring");
            }
        });

        spinnerGender = (Spinner)findViewById(R.id.spinnerGender);
        gender = spinnerGender.getSelectedItem().toString();

    }


    class CallInternet extends AsyncTask<String, Void, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Profile.this);
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

//            String email, name, age, phone, address, emergencyPhone, officeAddress,
//                    bloodGroup, allergy, weight, height, gender;

            String url = "http://" + serverip + "/MobileHealthNetwork/profileview?"+
                    "u=" + URLEncoder.encode(email) +
                    "&name=" + URLEncoder.encode(name) +
                    "&age="+ URLEncoder.encode(age) +
                    "&phone="+ URLEncoder.encode(phone) +
                    "&address="+ URLEncoder.encode(address) +
                    "&emergencyPhone="+ URLEncoder.encode(emergencyPhone) +
                    "&officeAddress="+ URLEncoder.encode(officeAddress) +
                    "&bloodGroup="+ URLEncoder.encode(bloodGroup) +
                    "&allergy="+ URLEncoder.encode(allergy) +
                    "&weight="+ URLEncoder.encode(weight) +
                    "&height="+ URLEncoder.encode(height) +
                    "&gender="+ URLEncoder.encode(gender) +
                    "&action="+ URLEncoder.encode("update");

            String reply= InternetHelper.get(url);

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
                Toast.makeText(Profile.this, "Profile Successfully Update", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(Profile.this, "Could not update! Try Again or contact syadmin", Toast.LENGTH_LONG).show();
            }
        }
    }
}
