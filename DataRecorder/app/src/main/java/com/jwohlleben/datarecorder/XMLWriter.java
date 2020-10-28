package com.jwohlleben.datarecorder;

import android.util.Log;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;

public class XMLWriter
{
    private String filePath;
    private String name;
    private String description;
    private ArrayList<ProjectClass> classes;

    public XMLWriter()
    {
        classes = new ArrayList<>();
    }

    public String getFilePath()
    {
        return filePath;
    }

    public void setFilePath(String filePath)
    {
        this.filePath = filePath;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public void addClass(String label, String description)
    {
        classes.add(new ProjectClass(label, description));
    }

    public void removeClass(int index)
    {
        if (index >= classes.size())
        {
            Log.w("XMLWriter", "Cant remove class. Index >= size!");
            return;
        }

        classes.remove(index);
    }

    public void write()
    {
        ProjectClass currentClass;
        StringBuilder sb = new StringBuilder();

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");

        sb.append("<project>\n");

        sb.append("\t<name>");
        sb.append(name);
        sb.append("</name>\n");

        sb.append("\t<description>");
        sb.append(description);
        sb.append("</description>\n");

        for (int i = 0; i < classes.size(); i++)
        {
            currentClass = classes.get(i);

            sb.append("\t<class id=\"");
            sb.append(currentClass.getId());
            sb.append("\">\n");

            sb.append("\t\t<label>");
            sb.append(currentClass.getLabel());
            sb.append("</label>\n");

            sb.append("\t\t<description>");
            sb.append(currentClass.getDescription());
            sb.append("</description>\n");

            sb.append("\t</class>\n");
        }

        sb.append("</project>");

        File file = new File(filePath);
        file.getParentFile().mkdirs();

        try
        {
            PrintWriter writer = new PrintWriter(file.getAbsoluteFile(), "UTF-8");
            writer.println(sb.toString());
            writer.close();
        }
        catch (Exception e)
        {
            Log.d("XMLWriter", "Could not write file!");
            e.printStackTrace();
        }
    }
}
