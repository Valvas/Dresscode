package fr.hexus.dresscode.classes;

public class WardrobeElementForm
{
    private int type;
    private int[] color;
    private String uuid;
    private String picture;

    public WardrobeElementForm(int type, int[] colors, String uuid, String picture)
    {
        this.type = type;
        this.uuid = uuid;
        this.color = colors;
        this.picture = picture;
    }

    public String toString()
    {
        StringBuilder stringToReturn = new StringBuilder();

        stringToReturn.append("[WARDROBE ELEMENT FORM]\n\n- Type : " + this.type + "\n- UUID : " + this.uuid + "\n- Picture : " + this.picture.substring(0, 32) + "...." + "\n- Colors : ");

        for(int i = 0; i < this.color.length; i++)
        {
            stringToReturn.append(this.color[i] + " ");
        }

        return String.valueOf(stringToReturn);
    }
}
