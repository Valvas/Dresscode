package fr.hexus.dresscode.classes;

public class OutfitForm
{
    private String uuid;
    private String name;
    private WardrobeElementForm[] elements;

    public OutfitForm(String uuid, String name, WardrobeElementForm[] elements)
    {
        this.uuid = uuid;
        this.name = name;
        this.elements = elements;
    }

    public String getUuid()
    {
        return this.uuid;
    }

    public String getName()
    {
        return this.name;
    }

    public WardrobeElementForm[] getElements()
    {
        return this.elements;
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
