package fr.hexus.dresscode.dresscode;

import java.util.List;

public class Outfit
{
    private String name;
    private List<WardrobeElement> elements;

    public Outfit(List<WardrobeElement> elements, String name)
    {
        this.elements = elements;
        this.name = name;
    }

    public List<WardrobeElement> getElements()
    {
        return elements;
    }

    public void setElements(List<WardrobeElement> elements)
    {
        this.elements = elements;
    }

    public void addElementToOutfit(WardrobeElement element)
    {
        this.elements.add(element);
    }

    public String getName()
    {
        return this.name;
    }
}
