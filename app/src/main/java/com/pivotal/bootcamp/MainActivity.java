package com.pivotal.bootcamp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {
    String key = "agbnsnx7rn5cegxxhv5z3dar";
    String value = "";
    String urlPrefix = "http://api.remix.bestbuy.com/v1/products(longDescription=";
    String urlPostfix="*)?show=sku,name,regularPrice&pageSize=15&page=5&format=json&apiKey=";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText searchField = (EditText) findViewById(R.id.editText);
        Button searchBtn = (Button) findViewById(R.id.button);
        final RequestQueue queue = Volley.newRequestQueue(this);
        final ListView searchResultList = (ListView) findViewById(R.id.listView);

        final ArrayList<String> list = new ArrayList<String>();
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        searchResultList.setAdapter(arrayAdapter);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                value = searchField.getText().toString();
                String url = urlPrefix+value+urlPostfix+key;

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONArray jsonArray = jsonObject.getJSONArray("products");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject obj = jsonArray.getJSONObject(i);
//                                        Log.d("Retail App", obj.toString());
                                        String listString = obj.getString("name") + " - " + obj.getString("regularPrice");
                                        list.add(listString);
                                    }

                                    arrayAdapter.notifyDataSetChanged();
                                } catch (Exception e) {
                                    Toast toast = Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG);
                                    toast.show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast toast = Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG);
                                toast.show();
                            }
                        }
                );

                queue.add(stringRequest);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
