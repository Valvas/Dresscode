package fr.hexus.dresscode.classes;

import java.util.List;

public class Outfit
{
    private String name;
    private List<WardrobeElement> elements;

    public Outfit(String name, List<WardrobeElement> elements)
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
