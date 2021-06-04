package com.our;

public class Crawler {
    public static void main(String[] args) {
        DBController DB = new DBController();
        DB.loadInitSeed();
    }
}
