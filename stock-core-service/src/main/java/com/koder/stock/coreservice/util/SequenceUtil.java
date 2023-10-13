package com.koder.stock.coreservice.util;

import java.util.Date;

public class SequenceUtil {

    public static String genTradeOrderNum() {
        Date date = new Date();
        String prefix = "TO" + String.format("%tY", date) + String.format("%tm", date) + String.format("%td", date);
        prefix += String.format("%tH", date) + String.format("%tM", date) + String.format("%tS", date);
        int random = (int) (Math.random() * (10000 - 1000) + 1000);
        return prefix + random;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            System.out.println(genTradeOrderNum());
        }
    }

}
