package com.cryptoconverter.crytomxn;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class home extends AppCompatActivity {

    private String TAG = home.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;

    // URL to get contacts JSON
    private static String url = "https://api.bitso.com/v3/ticker/";

    public String coinsP[] = new String[10];
    Spinner s2;
    TextView res, bt, et, ri, bh, xrpb, ethb;
    EditText monto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        new getCoins().execute();

        res = (TextView) findViewById(R.id.res);
        monto = (EditText) findViewById(R.id.monto);
        bt = (TextView) findViewById(R.id.Tbtc);
        et = (TextView) findViewById(R.id.Teth);
        ri = (TextView) findViewById(R.id.Txrp);
        bh = (TextView) findViewById(R.id.Tbch);
        xrpb = (TextView) findViewById(R.id.Txrpb);
        ethb = (TextView) findViewById(R.id.Tethb);

        String[] coins = {"BTC", "ETH", "XRP", "BCH"};
        s2 = (Spinner) findViewById(R.id.coins);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, coins);
        s2.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater findMenuItems = getMenuInflater();
        findMenuItems.inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.refresh:
                cargaValores(this.getCurrentFocus());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void toast(String s){
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    public void cargaValores(View v){
        new getCoins().execute();
    }

    public void precios(){

    }

    public void convierte(View v){
        if(bt.getText().toString().length() <= 3 || monto.getText().toString().length() == 0){
            toast("Faltan datos");
        }
        else {
            double resp = 0;
            double m = Double.parseDouble(monto.getText().toString());
            String q = s2.getSelectedItem().toString();
            double coin = 0;
            switch (q) {
                case "BTC":
                    coin = Double.parseDouble(coinsP[0]);
                    break;
                case "ETH":
                    coin = Double.parseDouble(coinsP[1]);
                    break;
                case "XRP":
                    coin = Double.parseDouble(coinsP[3]);
                    break;
                case "BCH":
                    coin = Double.parseDouble(coinsP[5]);
                    coin *= Double.parseDouble(coinsP[0]);
            }
            coin = Math.round(coin * 100.0) / 100.0;
            resp = m * coin;
            res.setText(Double.toString(resp) + " MXN");
        }
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class getCoins extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(home.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray coins = jsonObj.getJSONArray("payload");

                    // looping through All Contacts
                    for (int i = 0; i < coins.length(); i++) {
                        JSONObject c = coins.getJSONObject(i);
                        coinsP[i] = c.getString("last");
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            String[] coins = {"1 BTC = " + coinsP[0] + " MXN",
                    "1 ETH = " + coinsP[1] + " MXN",
                    "1 XRP = " + coinsP[3] + " MXN",
                    "1 BCH = " + coinsP[5] + " BTC",
                    "1 ETH = " + coinsP[4] + " BTC",
                    "1 XRP = " + coinsP[2] + " BTC"};

            bt.setText(coins[0]);
            et.setText(coins[1]);
            ri.setText(coins[2]);
            bh.setText(coins[3]);
            xrpb.setText(coins[4]);
            ethb.setText(coins[5]);
        }


    }
}