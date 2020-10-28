package com.jwohlleben.datarecorder;

import android.content.Context;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class CSVWriter
{
    private static final String CSV_HEAD = "x_acc,y_acc,z_acc,x_gyro,y_gyro,z_gyro\n";

    private Context context;
    private String directoryPath;

    public CSVWriter(Context context, String directoryPath)
    {
        this.context = context;
        this.directoryPath = directoryPath;
    }

    public void setDirectoryPath(String directoryPath)
    {
        this.directoryPath = directoryPath;
    }

    public String getDirectoryPath()
    {
        return directoryPath;
    }

    public boolean createFile(int id, List<float[]> values)
    {
        if (values == null)
            return false;

        StringBuilder sb = new StringBuilder();

        sb.append(CSV_HEAD);

        String newLine = "";

        for (int i = 0; i < values.size(); i++)
        {
            newLine = i + ",";

            for (int j = 0; j < values.get(i).length - 1; j++)
            {
                newLine += values.get(i)[j] + ",";
            }

            newLine += values.get(i)[values.get(i).length - 1] + "\n";

            sb.append(newLine);
        }

        File file;
        FileWriter writer;

        file = new File(directoryPath, DateTime.getTimestamp() +  '_' + id + ".csv");

        try
        {
            writer = new FileWriter(file);
            writer.append(sb.toString());
            writer.flush();
            writer.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
