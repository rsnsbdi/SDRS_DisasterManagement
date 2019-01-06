package com.example.android.wifidirect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

public class ProfileView extends AppCompatActivity {
    String emailid="";
    ProgressDialog pDialog;
    String serverip;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        ed = sp.edit();
        emailid = sp.getString("username", "NOUSER");
        serverip=sp.getString("serveraddress","localhost:8080");
        new CallInternet().execute();
        Button editProfile = (Button)findViewById(R.id.editProfile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileView.this, Profile.class);
                startActivity(i);
            }
        });
    }

    class CallInternet extends AsyncTask<String, Void, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        String username , pwd;
        String email;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            email = ProfileView.this.emailid;
            pDialog = new ProgressDialog(ProfileView.this);
            pDialog.setMessage("Contacting Servers. please wait");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            String url = "http://" + serverip + "/MobileHealthNetwork/profileview?u="+ URLEncoder.encode(email) ;
            Log.d(WiFiDirectActivity.TAG, "");
            String reply= InternetHelper.get(url);
            return reply;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String reply) {
            // dismiss the dialog after getting all products

            pDialog.dismiss();
            TextView tv = (TextView)findViewById(R.id.profileInformation);
            try {

//                json.put("email", email);
//                json.put("name", resultSet.getString("name"));
//                json.put("phone", resultSet.getString("phone"));
//                json.put("age", resultSet.getString("age"));
//                json.put("gender", resultSet.getString("gender"));
//                json.put("bloodgroup", resultSet.getString("bloodgroup"));
//                json.put("emergency", resultSet.getString("emergency"));
//                json.put("add", resultSet.getString("add"));
//                json.put("officeaddress", resultSet.getString("officeaddress"));
//                json.put("allergy", resultSet.getString("allergy"));
//                json.put("weight", resultSet.getString("weight"));
//                json.put("height", resultSet.getString("height"));

                JSONObject root = new JSONObject(reply);
                String email = root.getString("email");

                String name= root.getString("name");
                ed.putString("profilename", name);
                ed.commit();
                String phone = root.getString("phone");
                ed.putString("profilephone", phone);
                ed.commit();
                String age = root.getString("age");
                ed.putString("profileage", age);
                ed.commit();
                String gender = root.getString("gender");
                ed.putString("profilegender", gender);
                ed.commit();
                String bloodgroup = root.getString("bloodgroup");
                ed.putString("profilebloodgroup", bloodgroup);
                ed.commit();
                String emergency = root.getString("emergency");
                ed.putString("profileemergency", emergency);
                ed.commit();
                String add = root.getString("add");
                ed.putString("profileadd", add);
                ed.commit();
                String officeaddress = root.getString("officeaddress");
                ed.putString("profileofficeaddress", officeaddress);
                ed.commit();
                String allergy = root.getString("allergy");
                ed.putString("profileallergy", allergy);
                ed.commit();
                String weight = root.getString("weight");
                ed.putString("profileweight", weight);
                ed.commit();
                String height = root.getString("height");
                ed.putString("profileheight", height);
                ed.commit();

                String finalInfo = "Email :" + email + "<br>" +
                        "Name : " + name + "<br>"  +
                        "Phone : " + phone + "<br>"  +
                        "Age : " + age + "<br>"  +
                        "Gender : " + gender + "<br>"  +
                        "Blood Group : " + bloodgroup + "<br>" +
                        "Emergency Contact : " + emergency + "<br>"  +
                        "Home Address : " + add + "<br>"  +
                        "Office Address : " + officeaddress + "<br>"  +
                        "Allergy : " + allergy + "<br>"  +
                        "Weight : " + weight + "<br>"  +
                        "Height : " + height + "<br>"  ;
                tv.setText(Html.fromHtml(finalInfo));

            } catch (Exception ex){
                ex.printStackTrace();
            }
        }

    }


}
