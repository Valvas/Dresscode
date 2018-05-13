package fr.hexus.dresscode.dresscode;

public class WardrobeElementType
{
    private int id;
    private String value;

    public WardrobeElementType(int id, String value)
    {
        this.id = id;
        this.value = value;
    }

    public int getId()
    {
        return this.id;
    }

    public String getValue()
    {
        return value;
    }

    public String toString()
    {
        return "ID : " + this.id + " -> Value : " + this.value;
    }
}
