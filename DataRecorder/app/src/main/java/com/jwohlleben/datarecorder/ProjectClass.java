package com.jwohlleben.datarecorder;

public class ProjectClass
{
    private static int globalId = 1;

    private int id;
    private String label;
    private String description;

    ProjectClass(String label, String description)
    {
        this.id = globalId;
        this.label = label;
        this.description = description;

        globalId++;
    }

    ProjectClass(int id, String label, String description)
    {
        this.id = id;
        this.label = label;
        this.description = description;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return id;
    }

    public static void resetGlobalId()
    {
        globalId = 1;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getLabel()
    {
        return label;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }

    @Override
    public String toString()
    {
        return label + " - " + description;
    }
}
