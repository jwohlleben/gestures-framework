package com.jwohlleben.datarecorder;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class XMLReader
{
    private String filePath;
    private String name;
    private String description;

    private ArrayList<ProjectClass> classes;

    private Document document;

    public XMLReader()
    {
        classes = new ArrayList<>();
    }

    public void setFilePath(String filePath)
    {
        this.filePath = filePath;
    }

    public String getFilePath()
    {
        return filePath;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public ProjectClass[] getClasses()
    {
        if (classes.size() == 0)
            return null;

        ProjectClass[] temp = new ProjectClass[classes.size()];

        for (int i = 0; i < classes.size(); i++)
            temp[i] = classes.get(i);

        return temp;
    }

    public boolean read()
    {
        try
        {
            File file = new File(filePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringElementContentWhitespace(true);
            DocumentBuilder builder = factory.newDocumentBuilder();

            document = builder.parse(file);
        }
        catch (Exception e)
        {
            Log.d("XMLReader", "Could not build document");
            return false;
        }

        name = document.getElementsByTagName("name").item(0).getTextContent();
        description = document.getElementsByTagName("description").item(0).getTextContent();

        NodeList classNodes = document.getElementsByTagName("class");

        for (int i = 0; i < classNodes.getLength(); i++)
        {
            Node currentNode = classNodes.item(i);

            int id = Integer.parseInt(currentNode.getAttributes().getNamedItem("id").getTextContent());
            String label = document.getElementsByTagName("label").item(i).getTextContent();
            String description = document.getElementsByTagName("description").item(i + 1).getTextContent();

            classes.add(new ProjectClass(id, label, description));
        }

        return true;
    }
}
