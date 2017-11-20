package com.cryptoconverter.crytomxn;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;

public class home extends AppCompatActivity {

    public String prices = "";
    public String BTC = "";
    public String XRP = "";
    public String BCH = "";
    public String ETH = "";
    Spinner s2;
    TextView res, bt, et, ri, bh;
    EditText monto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        res = (TextView) findViewById(R.id.res);
        monto = (EditText) findViewById(R.id.monto);
        bt = (TextView) findViewById(R.id.Tbtc);
        et = (TextView) findViewById(R.id.Teth);
        ri = (TextView) findViewById(R.id.Txrp);
        bh = (TextView) findViewById(R.id.Tbch);

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

    private class MyTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... params) {
            Document doc;
            try {
                doc = Jsoup.connect("https://bitso.com/fees").get();
                prices = doc.html();
                Element s = doc.select("#lastPrice").first();
                if(s != null)
                    prices = s.text();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void cargaValores(View v){
        try {
            MyTask t = new MyTask();
            t.execute();
            BTC = prices.substring(12, 21);
            ETH = prices.substring(34, 41);
            XRP = prices.substring(77, 81);
            BCH = prices.substring(117, 127);
            toast("Correcto");
            precios();
            t.cancel(true);
        } catch (Exception e){
            toast("Error de conexión");
        }
    }

    public void precios(){
        String[] coins = {"1 BTC = " + BTC + " MXN", "1 ETH = " + ETH + " MXN", "1 XRP = " + XRP + " MXN", "1 BCH = " + BCH + " BTC"};
        bt.setText(coins[0]);
        et.setText(coins[1]);
        ri.setText(coins[2]);
        bh.setText(coins[3]);
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
                    coin = Double.parseDouble(BTC);
                    break;
                case "ETH":
                    coin = Double.parseDouble(ETH);
                    break;
                case "XRP":
                    coin = Double.parseDouble(XRP);
                    break;
                case "BCH":
                    coin = Double.parseDouble(BCH);
                    coin *= Double.parseDouble(BTC);
            }
            coin = Math.round(coin * 100.0) / 100.0;
            resp = m * coin;
            res.setText(Double.toString(resp) + " MXN");
        }
    }
}