package generator;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;


public class Log {

    File filePath;
    FileWriter fileWriter;
    BufferedWriter bufferedWriter;
    ArrayList<String> logData = new ArrayList<>();

    public Log(File logPath) {
        this.filePath = logPath;
        if (filePath.exists()) {
            filePath.delete();
        }
    }

    public void log(String data) {
        logData.add(data);
        try {
            fileWriter = new FileWriter(filePath);
            bufferedWriter = new BufferedWriter(fileWriter);

            for (int i=0; i < logData.size(); i++){
                bufferedWriter.write(
                        getTime() + "   " + logData.get(i)
                );
                bufferedWriter.newLine();
            }
            bufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String getTime() {
        return (String.valueOf(new Date().getHours()) + ":" + String.valueOf(new Date().getMinutes()) + ":" + String.valueOf(new Date().getSeconds()) + ">>");
    }


}
