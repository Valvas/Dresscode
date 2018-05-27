package fr.hexus.dresscode.dresscode;

import java.io.Serializable;

public class WardrobeElement implements Serializable
{
    private String path;
    private int id;
    private int type;
    private int color;

    public WardrobeElement(int id, int type, int color, String path)
    {
        this.id         = id;
        this.type       = type;
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

    public String getPath()
    {
        return this.path;
    }
    public void setType(int type)
    {
        this.type = type;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public void setColor(int color)
    {
        this.color = color;
    }
}
