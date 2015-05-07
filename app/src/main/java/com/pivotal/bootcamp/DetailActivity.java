package com.pivotal.bootcamp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;


public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Search");

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
