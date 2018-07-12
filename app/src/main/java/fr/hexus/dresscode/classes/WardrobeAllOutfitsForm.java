package fr.hexus.dresscode.classes;

import java.util.ArrayList;

public class WardrobeAllOutfitsForm
{
    private ArrayList<OutfitForm> outfits;

    public WardrobeAllOutfitsForm(ArrayList<OutfitForm> outfits)
    {
        this.outfits = outfits;
    }

    public ArrayList<OutfitForm> getOutfits()
    {
        return this.outfits;
    }
}
