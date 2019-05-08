package com.lunamint.wallet.utils;

import java.math.BigDecimal;

public class BigDecimalUtil {
    private static final BigDecimal DECIMAL_NANO = new BigDecimal(0.000001);
    private static final BigDecimal DEFAULT_DECIMAL = new BigDecimal(1000000);

    public static final String getNumberNano(String amount, String digit) {
        try {
            //double mAmount = Math.floor(Double.parseDouble(amount)* 10000) / 10000.0d;
            BigDecimal result = new BigDecimal(amount);
            result = result.multiply(DECIMAL_NANO);

            if (result.compareTo(BigDecimal.ZERO) == 0) {
                return "0";
            } else {
                return String.format("%." + digit + "f", result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    public static final String getNumberOrigin(String amount, String digit) {
        try {
            //double mAmount = Math.floor(Double.parseDouble(amount)* 10000) / 10000.0d;
            BigDecimal result = new BigDecimal(amount);
            result = result.multiply(DEFAULT_DECIMAL);

            if (result.compareTo(BigDecimal.ZERO) == 0) {
                return "0";
            } else {
                return String.format("%." + digit + "f", result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }
}

