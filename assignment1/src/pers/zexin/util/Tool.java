package pers.zexin.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Tool {
    /**
     * This method will write the content into the log file
     * @param p_content the content of the log message
     */
    public static void write2LogFile(String p_content, String directory, String fileName) {
        //create file writer
        FileWriter fw = null;
        try {
            File dir = new File(directory);
            if (!dir.isDirectory()) {
                dir.mkdir();
            }
            File f = new File(dir, fileName);
            if (!f.isFile()) {
                f.createNewFile();
            }
            fw = new FileWriter(f, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //create print writer
        PrintWriter l_pw = new PrintWriter(fw);
        l_pw.println(p_content);
        l_pw.flush();
        try {
            fw.flush();
            l_pw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
