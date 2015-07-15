package com.pivotal.bootcamp;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class DetailActivity extends ActionBarActivity implements OnMapReadyCallback {

    /*TODO:
    1. in onCreate, access location using hardware GPS (there's documention on the Google page for this)
    2. Extract that information and get it in lat, lng format
    3. Update the apiUrl in store(areax,y) line
    4. In the response, extract formation regarding store lat,lng similar to what we did in MainActivity
    5. Add markers like we did as the Nigeria example in onMapReady()

    Setting map view to current location:
    1. Repeat step 1, 2 from above
    2. Follow Google Maps documentation on camera zoom and initialization regarding current location
     */

    private static final String urlPrefix = "http://api.remix.bestbuy.com/v1/stores(area(";
    private static final String urlSuffix = ",10))?format=json&apiKey=agbnsnx7rn5cegxxhv5z3dar&show=storeId,name,lat,lng";
    private LocationManager lManager;
    private MapFragment mMapFragment;
    //Latlng values for New York City - Best Buy API is States only
    private double latitude = 40.71;
    private double longitude = -74.0;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title("Your Current Location"));

        CameraUpdate camUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 9);
        googleMap.animateCamera(camUpdate);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Search");
//*
        mMapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.map, mMapFragment);
        fragmentTransaction.commit();
        mMapFragment.getMapAsync(this);
//*/
        Intent intent = getIntent();
        String name = intent.getStringExtra("NAME");
        Double price = intent.getDoubleExtra("PRICE", 0);
        String imageUrl = intent.getStringExtra("LGIMG");
        String description = intent.getStringExtra("DESC");

        TextView pName = (TextView) findViewById(R.id.productName);
        TextView pPrice = (TextView) findViewById(R.id.productPrice);
        TextView pDesc = (TextView) findViewById(R.id.productDesc);
        ImageView pImage = (ImageView) findViewById(R.id.productImage);

        pName.setText(name);
        pPrice.setText(price.toString());
        pDesc.setText(description);
        new ImageDownloader(pImage).execute(imageUrl);

        //For Location tracking - implement LocationListener in order to get location updates from the phone's hardware
//        lManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//        lManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 0, this);
//        Location currentLocation = lManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//
//        if (currentLocation != null) {
//            updateLatLng(currentLocation);
//        }

        String url = urlPrefix+String.valueOf(latitude)+","+String.valueOf(longitude)+urlSuffix;
        getBestBuyLocations(url);

    }

    private void updateLatLng(Location location) {
        latitude = Math.round(location.getLatitude());
        longitude = Math.round(location.getLongitude());
        Toast.makeText(this, latitude+" "+longitude, Toast.LENGTH_SHORT).show();

        String url = urlPrefix+String.valueOf(latitude)+","+String.valueOf(longitude)+urlSuffix;
        getBestBuyLocations(url);
    }

    private void getBestBuyLocations(String url) {
        final RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Store API Call", response);
                        Toast.makeText(DetailActivity.this, response, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast toast = Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG);
                        toast.show();
                    }
                });

        queue.add(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
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
        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
