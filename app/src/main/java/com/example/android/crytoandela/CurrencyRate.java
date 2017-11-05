package com.example.android.crytoandela;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Garuba Alex Samuel on 05-11-2017.
 */

class CurrencyRate {

    ArrayList<MoneyRate> currencyArrayList = new ArrayList<>();
    //contains all 20 monies and their btc & eth rates

    CurrencyRate() {}

    //Method to populate the ratesArrayList
    void add(String currency, Double btcRate, Double ethRate) {
        MoneyRate moneyRate = new MoneyRate(currency, btcRate, ethRate);
        currencyArrayList.add(moneyRate);
    }

    //This method sorts the ratesArrayList alphabetically OR by the currencies' values in BTC
    //...since BTC and ETH maintain a constant rate across all currencies
    void orderList(final int mode) {
        Collections.sort(currencyArrayList, new Comparator<MoneyRate>() {
            @Override
            public int compare(MoneyRate lhs, MoneyRate rhs) {
                if(mode == RateConstants.ORDER_ALPHABETICAL)
                    return lhs.getCurrency().compareTo(rhs.getCurrency());

                else
                    return Double.compare(lhs.getBtcRate(), rhs.getBtcRate());
            }
        });
    }


    //Custom class carries name of currency and its BTC and ETH rates and methods to return these properties
    class MoneyRate{
        private String currency;
        private double btcRate;
        private double ethRate;

        MoneyRate(String currency, double btcRate, double ethRate) {
            this.currency = currency;
            this.btcRate = btcRate;
            this.ethRate = ethRate;
        }

        public String getCurrency() {
            return currency;
        }

        public double getBtcRate() {
            return btcRate;
        }

        public double getEthRate() {
            return ethRate;
        }
    }
    
}
