package fr.hexus.dresscode.dresscode;

import java.io.Serializable;

public class WardrobeElement implements Serializable
{
    private String name;
    private String path;
    private int id;
    private int type;
    private int color;

    public WardrobeElement(int id, int type, int color, String name, String path)
    {
        this.id         = id;
        this.type       = type;
        this.name       = name;
        this.path       = path;
        this.color      = color;
    }

    public int getId()
    {
        return this.id;
    }

    public int getType()
    {
        return this.type;
    }

    public int getColor()
    {
        return this.color;
    }

    public String getName()
    {
        return this.name;
    }

    public String getPath()
    {
        return this.path;
    }

    public String toString()
    {
        return "Name : " + this.name + "\nType : " + this.type + "\nColor : " + this.color + "\nPicture path : " + this.path;
    }
}
