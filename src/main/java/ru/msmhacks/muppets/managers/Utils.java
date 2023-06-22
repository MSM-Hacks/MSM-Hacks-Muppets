package ru.msmhacks.muppets.managers;

import com.smartfoxserver.v2.entities.data.SFSObject;
import ru.msmhacks.muppets.MuppetsExtension;
import ru.msmhacks.muppets.entities.Player.PlayerIsland;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static java.lang.Math.round;

public class Utils {
    public static String ReadFile(File file) {
        try{
            BufferedReader br = new BufferedReader(new FileReader(file.getPath()));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                line = line.replaceAll("\"last_changed\":\\s*(\\d+),", "\"last_changed\": \"$1\",");
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String everything = sb.toString();
            return everything;
        } catch (IOException e) {
            return null;
        }
    }

    public static SFSObject getSFSFromJson(File file){
        String data = ReadFile(file);

        if (data == null) {return null;}

        return (SFSObject) SFSObject.newFromJsonData(data);
    }

    public static String[] getAllFilesInDir(String path) {
        File dir = new File(path);
        File[] files = dir.listFiles();
        assert files != null;
        String[] fileNames = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            fileNames[i] = files[i].getAbsolutePath();
        }
        return fileNames;
    }

    public static void log(String text) {
        if (MuppetsExtension.extension == null) {
            System.out.println(text);
        } else {
            MuppetsExtension.extension.trace(text);
        }
    }
    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    public static int getSpeedupCost(long start, long end) {
        if (start > end) {
            return 0;
        }

        return round((end-start)/1800000) + 1;
    }

}
