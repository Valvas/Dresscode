package fr.hexus.dresscode.dresscode;

import java.util.List;

public class Outfit
{
    private List<WardrobeElement> elements;

    public Outfit(List<WardrobeElement> elements)
    {
        this.elements = elements;
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
}
