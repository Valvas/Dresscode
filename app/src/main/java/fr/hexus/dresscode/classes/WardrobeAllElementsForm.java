package fr.hexus.dresscode.classes;

import java.util.ArrayList;

public class WardrobeAllElementsForm
{
    private ArrayList<WardrobeElementForm> elements;

    public WardrobeAllElementsForm(ArrayList<WardrobeElementForm> elements)
    {
        this.elements = elements;
    }

    public ArrayList<WardrobeElementForm> getElements()
    {
        return this.elements;
    }
}
