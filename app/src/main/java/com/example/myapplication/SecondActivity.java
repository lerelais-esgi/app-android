package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SecondActivity extends AppCompatActivity {
    private FloatingActionButton cam;
    private Button bt3;
    private Button request;
    private ListView status_verbose;
    public static TextView barcode;
    public static String API_URL = "https://api.lerelais.cf/product/getInfo/";
    private ProgressBar loading;
    private EditText qt;
    private ImageView product_pic;
    private Bitmap bitmap;
    ArrayList<Product> arrayList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        cam = (FloatingActionButton) findViewById(R.id.cam);
        bt3 = (Button) findViewById(R.id.bt3);
        request = (Button) findViewById(R.id.request);
        barcode = (TextView) findViewById(R.id.barcode);
        status_verbose = (ListView) findViewById(R.id.status_verbose);
        arrayList = new ArrayList<>();
        loading = (ProgressBar) findViewById(R.id.loading);
        qt = (EditText) findViewById(R.id.et4);
        Bundle bundle = getIntent().getExtras();
        product_pic = (ImageView) findViewById(R.id.product_pic);
        final String token = bundle.getString("token");
        product_pic.setVisibility(View.GONE);




        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ScanCodeActivity.class));
            }
        });

        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String FINAL_URL = API_URL + barcode.getText();
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, FINAL_URL, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {

                                    //Toast.makeText(SecondActivity.this, response.toString(), Toast.LENGTH_LONG).show();
                                    final String URL_pic = response.getString("image");
                                    new DownLoadImageTask(product_pic).execute(URL_pic);
                                    product_pic.setVisibility(View.VISIBLE);

                                    arrayList.add(new Product(
                                            response.getString("name"),
                                            response.getString("expiration_date")
                                    ));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                CustomListAdapter adapter = new CustomListAdapter(
                                        getApplicationContext(),R.layout.item,arrayList
                                );

                                status_verbose.setAdapter(adapter);

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SecondActivity.this, "Erreur" + error.toString(), Toast.LENGTH_SHORT).show();
                        error.printStackTrace();

                    }
                }) {

                    /**
                     * Passing some request headers
                     */
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        //headers.put("Content-Type", "application/json");
                        headers.put("X-Auth-key", token);
                        return headers;
                    }
                };

                Singleton.getInstance(SecondActivity.this).addToRequestQueue(jsonObjectRequest);


            }
        });


        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.setVisibility(View.VISIBLE);
                request.setVisibility(View.GONE);
                arrayList.add((Product) qt.getText());
                String json = new Gson().toJson(arrayList);
                String API_REQUEST ="https://api.lerelais.cf/";
                try {
                    JSONObject jsonObject = new JSONObject(json);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, API_REQUEST, jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Toast.makeText(SecondActivity.this,
                                        "Requête envoyée! C'est un petit pas pour l'homme, mais un grand pas pour la lutte anti-gaspi!", Toast.LENGTH_SHORT).show();
                                loading.setVisibility(View.GONE);
                                request.setVisibility(View.VISIBLE);
                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(SecondActivity.this,
                                "Erreur" + error.toString(), Toast.LENGTH_SHORT).show();
                        loading.setVisibility(View.GONE);
                        request.setVisibility(View.VISIBLE);
                    }
                })
                {

                    /**
                     * Passing some request headers
                     */
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        //headers.put("Content-Type", "application/json");
                        headers.put("X-Auth-key", token);
                        return headers;
                    }
                };

                    Singleton.getInstance(SecondActivity.this).addToRequestQueue(jsonObjectRequest);

                } catch(JSONException e) {
                e.printStackTrace();
            }



            }
        });


    }

    private class DownLoadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        /*
            doInBackground(Params... params)
                Override this method to perform a computation on a background thread.
         */
        protected Bitmap doInBackground(String... urls) {
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try {
                InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                logo = BitmapFactory.decodeStream(is);
            } catch (Exception e) { // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        /*
            onPostExecute(Result result)
                Runs on the UI thread after doInBackground(Params...).
         */
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }


    }


}
