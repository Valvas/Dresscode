package fr.hexus.dresscode.dresscode;

import java.io.Serializable;

public class WardrobeElement implements Serializable
{
    private String name;
    private int id;
    private int type;
    private int material;
    private int color;

    public WardrobeElement(int id, int type, int material, int color, String name)
    {
        this.id         = id;
        this.type       = type;
        this.name       = name;
        this.color      = color;
        this.material   = material;
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

    public int getMaterial()
    {
        return this.material;
    }

    public String getName()
    {
        return this.name;
    }

    public String toString()
    {
        return "Name : " + this.name + "\nType : " + this.type + "\nMaterial : " + this.material + "\nColor : " + this.color;
    }
}
