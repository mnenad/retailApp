package com.pivotal.bootcamp;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit.RestAdapter;

public class DetailActivity extends ActionBarActivity implements OnMapReadyCallback, LocationListener {

    /*
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
    private static final String urlSuffix = ",10))?format=json&apiKey=agbnsnx7rn5cegxxhv5z3dar&show=storeId,name,lat,lng,distance,phone";
    private LocationManager lManager;
    private MapFragment mMapFragment;
    //Latlng values for New York City - Best Buy API is States only
    private StoreLocation[] closestStoreLocations;
    private double latitude = 40.71;
    private double longitude = -74.0;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title("Your Current Location"));

        if (closestStoreLocations != null) {
            for (StoreLocation storeLocation : closestStoreLocations) {
                googleMap.addMarker(new MarkerOptions()
                        .position(storeLocation.location)
                        .title(storeLocation.name + " - " + String.valueOf(storeLocation.distanceFromCurrentLocation) + " miles away"));
            }
        }

        CameraUpdate camUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 11);
        googleMap.animateCamera(camUpdate);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Search");

        mMapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.map, mMapFragment);
        fragmentTransaction.commit();
        mMapFragment.getMapAsync(this);

        Intent intent = getIntent();
        String name = intent.getStringExtra("NAME");
        Double price = intent.getDoubleExtra("PRICE", 0);
        String imageUrl = intent.getStringExtra("LGIMG");
        String description = intent.getStringExtra("DESC");
        String sku = intent.getStringExtra("sku");

        TextView pName = (TextView) findViewById(R.id.productName);
        TextView pPrice = (TextView) findViewById(R.id.productPrice);
        TextView pDesc = (TextView) findViewById(R.id.productDesc);
        ImageView pImage = (ImageView) findViewById(R.id.productImage);

        pName.setText(name);
        pPrice.setText(price.toString());
        pDesc.setText(description);
        new ImageDownloader(pImage).execute(imageUrl);

        // For Location tracking - implement LocationListener in order to get location updates from the phone's hardware
        lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, (LocationListener) this);
        Location currentLocation = lManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


        if (currentLocation != null) {
            updateLatLng(currentLocation);
        }

        String url = urlPrefix + String.valueOf(latitude) + "," + String.valueOf(longitude) + urlSuffix;
        getBestBuyLocations(url);

    }

    private void updateLatLng(Location location) {
        latitude = Math.round(location.getLatitude());
        longitude = Math.round(location.getLongitude());
        Toast.makeText(this, latitude + " " + longitude, Toast.LENGTH_SHORT).show();

        String url = urlPrefix + String.valueOf(latitude) + "," + String.valueOf(longitude) + urlSuffix;
        getBestBuyLocations(url);
    }

    private void getBestBuyLocations(String url) {
        final RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            closestStoreLocations = new StoreLocation[3];
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("stores");
                            String closestStoreURL = "http://stores.bestbuy.com/";
                            for (int i = 0; i < closestStoreLocations.length; i++) {
                                JSONObject store = jsonArray.getJSONObject(i);
                                LatLng location = new LatLng(store.getDouble("lat"), store.getDouble("lng"));
                                String name = store.getString("name");
                                Double dist = store.getDouble("distance"); //Default is Miles, not km
                                closestStoreLocations[i] = new StoreLocation(location, name, dist);
                                //quick fix to send a message for nearest store
                                if (i == 0) {
                                    String storeId = store.getString("storeId");
                                    closestStoreURL = "http://stores.bestbuy.com/" + storeId;
                                    Log.d("Closest store URL:", closestStoreURL);
                                }
                            }
                            final String finalEndPoint = "http://pushsender.cfapps.io/push?deviceId=2fee7b91-0a08-45f8-968d-81d31cac4355&message=" + closestStoreURL;
                            mMapFragment.getMapAsync(DetailActivity.this);
                            mMapFragment.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(GoogleMap googleMap) {
                                    //todo call the proxy service
                                    googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                        @Override
                                        public boolean onMarkerClick(Marker marker) {
                                            Log.d("Push REST API Call", finalEndPoint);
                                            executeRest(finalEndPoint);
                                            return false;
                                        }
                                    });
                                }

                            });
                        } catch (Exception e) {
                            Log.d("Store API Call", e.toString());
                        }
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

    private void executeRest(String url) {
        BackgroundTask task = new BackgroundTask();
        task.setString(url);
        task.execute();
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

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        String url = urlPrefix + String.valueOf(latitude) + "," + String.valueOf(longitude) + urlSuffix;
        getBestBuyLocations(url);
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

    private class BackgroundTask extends AsyncTask<Void, Void, SomePOJO>{
         //   SomePOJO> {
        RestAdapter restAdapter;
        String mUrl = "";

        public void setString(String url){
            this.mUrl = url;
        }

        @Override
        protected void onPreExecute() {
            Log.d(getClass().getSimpleName(), "ON PREEXECUTE");
            restAdapter = new RestAdapter.Builder()
                    .setEndpoint(mUrl)
                    .build();
        }

        @Override
        protected SomePOJO doInBackground(Void... params) {
            IApiMethods methods = restAdapter.create(IApiMethods.class);
            SomePOJO curators = methods.getCurators(null);
            return curators;
        }

        @Override
        protected void onPostExecute(SomePOJO curators) {
            Log.d(getClass().getSimpleName(), "SUCCESS CALL " + curators.title + " : " + curators.dataset);
        }
    }
}
