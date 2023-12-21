package xyz.lebalex.mysongs;

import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import static androidx.core.app.ActivityCompat.startActivityForResult;

public class FileHelper {
    public static String getFileContext(String fileName) {
        StringBuilder text = new StringBuilder();
        if (fileName != null) {
            File file = new File(fileName);
            if (file.exists()) {
                try {
                    //text.append("<html><body bgcolor=\"black\"><font color=\"white\">");
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;

                    while ((line = br.readLine()) != null) {
                        text.append(line);
                        text.append('\n');
                        //text.append("<br>");
                    }
                    //text.append("</font></body></html>");
                    br.close();
                } catch (Exception e) {
                }

                return text.toString();
            } else {
                return "";
            }
        } else return "";
    }


}
