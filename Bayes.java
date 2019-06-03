package com.pp2.myapplication;

import android.os.Environment;
import android.util.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class Bayes {

    private List<List<String>> records = new ArrayList<>();

    public void loadDataset() {
        try (BufferedReader br = new BufferedReader(new FileReader(Environment.getExternalStoragePublicDirectory("/") + "/Documents/data.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                records.add(Arrays.asList(values));
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public String inferir(String a, String b, String c) {
        int countLigado=0;
        int countDesligado=0;
        int countA=0;
        int countB=0;
        int countC=0;

        int n = records.size();
        for (int i=0; i<n; i++) {
            if(records.get(i).get(1).equals("ligado")) {
                countLigado++;
            }
        }

        for (int i=0; i<n; i++) {

            if(records.get(i).get(1).equals("desligado")) {
                countDesligado++;
            }
        }

        for (int i=0; i<n-1; i++) {
            if(records.get(i).get(0).equals(a) && records.get(i).get(1).equals("ligado")) {
                countA++;
            }
        }

        for (int i=0; i<n-1; i++) {
            if(records.get(i).get(2).substring(0,5).equals(b.substring(0,5)) && records.get(i).get(1).equals("ligado")) {
                countB++;
            }
        }

        for (int i=0; i<n-1; i++) {
            if(records.get(i).get(3).substring(0,5).equals(c.substring(0,5)) && records.get(i).get(1).equals("ligado")) {

                countC++;
            }
        }

        double probS = ((double)countA/countLigado)*((double)countB/countLigado)*((double)countC/countLigado)*((double)countLigado/(n-1));

        countA=0;
        countB=0;
        countC=0;

        for (int i=0; i<n-1; i++) {
            if(records.get(i).get(0).equals(a) && records.get(i).get(1).equals("desligado")) {
                countA++;
            }
        }

        for (int i=0; i<n-1; i++) {
            if(records.get(i).get(2).substring(0,5).equals(b.substring(0,5)) && records.get(i).get(1).equals("desligado")) {
                countB++;
            }
        }

        for (int i=0; i<n-1; i++) {
            if(records.get(i).get(3).substring(0,5).equals(c.substring(0,5)) && records.get(i).get(1).equals("desligado")) {
                countC++;
            }
        }

        double probN = ((double)countA/countDesligado)*((double)countB/countDesligado)*((double)countC/countDesligado)*((double)countDesligado/(n-1));

        double chanceS = probS/(probN+probS);
        double chanceN = probN/(probN+probS);
        if((probS == 0)&& (probN == 0)) return "null";
        else if(chanceS > chanceN) return "ligado";
        else return "desligado";
    }
}
