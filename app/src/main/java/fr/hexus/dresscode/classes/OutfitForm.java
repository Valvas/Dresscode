package fr.hexus.dresscode.classes;

public class OutfitForm
{
    private String name;
    private WardrobeElementForm[] elements;

    public OutfitForm(String name, WardrobeElementForm[] elements)
    {
        this.name = name;
        this.elements = elements;
    }

    public String toString()
    {
        StringBuilder stringToReturn = new StringBuilder();

        stringToReturn.append("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n\n");

        stringToReturn.append("[OUTFIT FORM]\n\n- Name : " + this.name + "\n- Elements : \n");

        for(int i = 0; i < this.elements.length; i++)
        {
            stringToReturn.append("\n" + i + ". " + this.elements[i].toString());
        }

        stringToReturn.append("\n\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        return String.valueOf(stringToReturn);
    }
}
