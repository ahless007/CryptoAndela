package com.example.android.crytoandela;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    public CurrencyRate ratesLedger;
    LinearLayout mainView;
    SwipeRefreshLayout SwipeRefreshLayout;
    ListView ratesListView;
    RequestQueue rQueue;
    String requestUrl;
    SharedPreferences settings;
    int orderMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainView = (LinearLayout) findViewById(R.id.main_view);
        SwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.reload_activity);
        ratesListView = (ListView) findViewById(R.id.currency_list_view);
        ratesListView.setDivider(null); //to remove dividers from the list view

        ratesLedger = new CurrencyRate();

        rQueue = Volley.newRequestQueue(getApplicationContext());

        settings = getSharedPreferences("mSettings", MODE_PRIVATE);
        orderMode = settings.getInt("orderMode", RateConstants.ORDER_ALPHABETICAL);


        requestUrl = "https://min-api.cryptocompare.com/data/pricemulti?fsyms=BTC,ETH&tsyms=" + "NGN,USD,EUR,JPY,GBP,AUD,CAD,CHF,CNY,KES,GHS,UGX,ZAR,XAF,NZD,MYR,BND,GEL,RUB,INR";
        //Currencies will NOT be displayed in this here order

        downloadRates();

        SwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                downloadRates();
            }
        });


    }

    //Method downloads the rates of the 20 currencies in the URL, receives JSON response, parses response and displays rates
    public void downloadRates() {
        ratesLedger = new CurrencyRate();

        JsonObjectRequest requestNameAvatar = new JsonObjectRequest(Request.Method.GET, requestUrl, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject btc_rates = response.getJSONObject("BTC".trim());
                            JSONObject eth_rates = response.getJSONObject("ETH".trim());

                            Iterator<?> keysBTC = btc_rates.keys();
                            Iterator<?> keysETH = eth_rates.keys();

                            while (keysBTC.hasNext() && keysETH.hasNext()) {
                                String keyBTC = (String) keysBTC.next();
                                String keyETH = (String) keysETH.next();

                                ratesLedger.add(keyBTC, btc_rates.getDouble(keyBTC), eth_rates.getDouble(keyETH));
                            }

                            SwipeRefreshLayout.setRefreshing(false); //to remove the progress bar for refresh
                            ratesLedger.orderList(orderMode);
                            ratesListView.setAdapter(new MyAdapter(ratesLedger.currencyArrayList));


                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {


            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(mainView, RateConstants.REFRESH_ERROR, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                SwipeRefreshLayout.setRefreshing(false);
            }


        });

        rQueue.add(requestNameAvatar);
    }

    @Override
    protected void onStop() {
        super.onStop();
        rQueue.cancelAll(this);
    }

    private static class ViewHolder {
        private TextView currencyTextView;
        private TextView btcTextView;
        private TextView ethTextView;
    }

    private class MyAdapter extends BaseAdapter {
        ArrayList<CurrencyRate.MoneyRate> rates;

        private MyAdapter(ArrayList<CurrencyRate.MoneyRate> ratesInstance) {
            rates = ratesInstance;
        }

        public int getCount() {
            return rates.size();
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("DefaultLocale")
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.custom_list, parent, false);
                holder = new ViewHolder();
                holder.currencyTextView = (TextView) convertView.findViewById(R.id.currency_name);
                holder.btcTextView = (TextView) convertView.findViewById(R.id.btc_rate);
                holder.ethTextView = (TextView) convertView.findViewById(R.id.eth_rate);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final CurrencyRate.MoneyRate rateRow = rates.get(position);
            final String crossCurrency = rateRow.getCurrency();
            final double crossBtc = rateRow.getBtcRate();
            final double crossEth = rateRow.getEthRate();

            holder.currencyTextView.setText(crossCurrency);
            holder.btcTextView.setText(String.format("%1$,.2f", crossBtc));
            holder.ethTextView.setText(String.format("%1$,.2f", crossEth));

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, ConversionActivity.class);
                    intent.putExtra(RateConstants.EXTRA_CURRENCY, crossCurrency);
                    intent.putExtra(RateConstants.EXTRA_BTC_RATE, crossBtc);
                    intent.putExtra(RateConstants.EXTRA_ETH_RATE, crossEth);
                    startActivity(intent);
                }
            });

            return convertView;

        }
    }


}
