package fr.hexus.dresscode.classes;

public class WardrobeElementForm
{
    private int type;
    private int[] colors;
    private String picture;

    public WardrobeElementForm(int type, int[] colors, String picture)
    {
        this.type = type;
        this.colors = colors;
        this.picture = picture;
    }
}
