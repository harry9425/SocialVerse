package com.harry9425.yaari;

import java.util.Comparator;

public class allchatsorter implements Comparator<chatmodel>
{
    @Override
    public int compare(chatmodel o1, chatmodel o2) {
        return o2.getTime().compareTo(o1.getTime());
    }
}