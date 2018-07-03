package fr.hexus.dresscode.classes;

public class WardrobeElementForm
{
    private int type;
    private int[] color;
    private String picture;

    public WardrobeElementForm(int type, int[] colors, String picture)
    {
        this.type = type;
        this.color = colors;
        this.picture = picture;
    }
}
