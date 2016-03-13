package com.example.FileProcess;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by PC on 13/03/2016.
 */
public class FileProcess {

    public static Set<String> ReaderIter(String PathFile) throws IOException {
        //Pattern pattern = Pattern.compile("(?=.{17}$)97(?:8|9)([ -])\\d{1,5}\\1\\d{1,7}\\1\\d{1,6}\\1\\d$");
        //Pattern pattern = Pattern.compile("ISBN(-1(?:(0)|3))?:?\\x20(\\s)*[0-9]+[- ][0-9]+[- ][0-9]+[- ][0-9]*[- ]*[xX0-9]");
        Pattern pattern = Pattern.compile("(ISBN[-]*(1[03])*[ ]*(: ){0,1})*(([0-9Xx][- ]*){13}|([0-9Xx][- ]*){10})");
        BufferedReader reader = new BufferedReader(new FileReader(PathFile));

        Set<String> list = new HashSet<>();

        String line;
        while((line = reader.readLine()) != null)
        {
            Matcher matcher = pattern.matcher(line);
            while(matcher.find()) {
                int start = matcher.start(0);
                int end = matcher.end(0);
                list.add(line.substring(start,end));
                //System.out.println(line.substring(start,end));
            }
        }
        return list;
    }
}
