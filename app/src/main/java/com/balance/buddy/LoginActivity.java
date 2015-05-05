package com.balance.buddy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by israe_000 on 3/29/2015.
 */
public class LoginActivity extends Activity {
    Login  login;
    Context ctx;

    Button loginButton;
    Button skip;
    EditText username;
    EditText password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Links java to XMl
        setContentView(R.layout.activity_login);
        ctx = getApplicationContext();

        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);

        //Links button to XML
        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                login = new Login(username.getText().toString(), password.getText().toString());

                //Begins Task
                login.execute();


            }
        });

        skip = (Button)findViewById(R.id.skip_button);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx, MainActivity.class));
                finish();
            }
        });


    }

    //ASYNC BACKGROUND TASK
    class Login extends AsyncTask<String,String,String> {
        boolean found;
        String mUsername, mPassword;
        JSONParser jsonParser = new JSONParser();
        private static final String TAG_SUCCESS = "success";
        private String Login = "http://youngstroke.org/Genius/AndroidBuddy/BuddyFindUser.php";

        Login(String username, String password){
            mUsername = username;
            mPassword = password;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("Username",mUsername));
            params.add(new BasicNameValuePair("Password",mPassword));

            // getting JSON OBJECT
            //Note that create product url accpts POST method
            JSONObject json = jsonParser.makeHttpRequest(Login,"POST",params);

            // Check log cat for response
            Log.d("Create Response", json.toString());

            //Check for success tag

            found = false;

            try {

                int success = json.getInt(TAG_SUCCESS);

                if (success == 1 )
                {
                    found = true;
                }

                else
                    found = false;

            }

            catch(JSONException e) {

                e.printStackTrace();
            }


            return null;
        }

        // This method runs on the UI thread after the background thread is complete
        protected void onPostExecute(String file_url) {

            startActivity(new Intent(ctx, MainActivity.class));
            finish();

        }
    }


}
