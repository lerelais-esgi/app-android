package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

        private Button bt1;
        private EditText et1,et2;
        private TextView tv2;
        private int counter = 3;
        private static String URL_LOGIN = "https://api.lerelais.cf/login";
        private ProgressBar loading;



        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);


            bt1 = (Button) findViewById(R.id.bt1);
            et1 = (EditText) findViewById(R.id.et1);
            et2 = (EditText) findViewById(R.id.et2);
            tv2 = (TextView) findViewById(R.id.tv2);
            loading = (ProgressBar) findViewById(R.id.loading);


            bt1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String mEmail = et1.getText().toString();
                    String mPassword = et2.getText().toString().trim();
                    if(!mEmail.isEmpty() || !mPassword.isEmpty())
                    {
                        Login(mEmail,mPassword);
                    }else{
                        et1.setError("Entrez un mail valide");
                        et2.setError("Entrez un mot de passe valide");

                    }


                }
            });

            tv2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://lerelais.cf/register"));
                    startActivity(browserIntent);

                }
            });


        }

// format response:  {"logged":"true", token:"........."}
        private void Login(final String email,final String password) {

            loading.setVisibility(View.VISIBLE);
            bt1.setVisibility(View.GONE);


            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.trim().contains("true")){

                                try {

                                     //cast le String de response en JSONObject
                                    JSONObject obj = new JSONObject(response);

                                    //Save SharedPreferences
                                    SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
                                    sharedPreferences.edit().putString("token",obj.getString("token")).apply();
                                    Toast.makeText(MainActivity.this,
                                        response,
                                        Toast.LENGTH_SHORT).show();


                                loading.setVisibility(View.GONE);
                                bt1.setVisibility(View.VISIBLE);

                                //Récupérer SharedPreferences
                                String token = sharedPreferences.getString("token","");


                                 Bundle bundle = new Bundle();
                                 bundle.putString("token", token);
                                 Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                                 intent.putExtras(bundle);
                                 startActivity(intent);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                            }else{
                                Toast.makeText(MainActivity.this,
                                        response,
                                        Toast.LENGTH_SHORT).show();
                                loading.setVisibility(View.GONE);
                                bt1.setVisibility(View.VISIBLE);
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            loading.setVisibility(View.GONE);
                            bt1.setVisibility(View.VISIBLE);

                            Toast.makeText(MainActivity.this,
                                    "Erreur" + error.toString(),
                                    Toast.LENGTH_SHORT).show();

                        }
                    })

            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("email", email);
                    params.put("password",password);
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);

        }



    }
