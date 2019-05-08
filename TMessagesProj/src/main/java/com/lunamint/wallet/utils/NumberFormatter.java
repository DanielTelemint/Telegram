package com.lunamint.wallet.utils;

import java.text.DecimalFormat;

public class NumberFormatter {
    public static final String getNumber(String origin) {
        try {
            double num = Double.parseDouble(origin);
            DecimalFormat format = new DecimalFormat("#,###.####");
            return format.format(num);
        } catch (Exception e) {
            e.printStackTrace();
            return origin;
        }
    }

    public static final String getNumberWithFixedDecimal(String origin) {
        try {
            double num = Double.parseDouble(origin);
            DecimalFormat format = new DecimalFormat("#,##0.0000");
            return format.format(num);
        } catch (Exception e) {
            e.printStackTrace();
            return origin;
        }
    }
}

