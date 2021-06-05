package com.our;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Stopwords {
    static void loadStopwords(ArrayList<String> stopwords) throws FileNotFoundException {
        File fileObj = new File("stopwords.txt");
        Scanner myReader = new Scanner(fileObj);
        while (myReader.hasNextLine()) {
            stopwords.add(myReader.nextLine().toLowerCase());
        }
    }
}
