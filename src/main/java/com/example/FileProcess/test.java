package com.example.FileProcess;

import java.io.IOException;
import java.util.Set;

/**
 * Created by PC on 13/03/2016.
 */
public class test {
    public static void main(String[] args)
    {
        try {
            Set<String> list = FileProcess.ReaderIter("C:\\Users\\PC\\Desktop\\test.csv");
            for (int i=0;i<list.size();i++) {
                System.out.println(list.toArray()[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
