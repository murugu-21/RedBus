package com.example.redbus;

import java.util.ArrayList;
import java.util.LinkedList;

public class booked {
    public ArrayList<Integer> pos;
    public booked(){
        // Default constructor required for calls to DataSnapshot.getValue(booked.class)
    }
    public booked(ArrayList<Integer> pos) {
        this.pos = pos;
    }
}
