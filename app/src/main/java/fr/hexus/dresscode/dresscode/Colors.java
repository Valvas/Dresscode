package fr.hexus.dresscode.dresscode;

import java.util.HashMap;
import java.util.Map;

public enum Colors
{
    BLACK (1), WHITE (2), BLUE (3), RED (4), PINK (5), GREEN (6), BROWN (7), ORANGE (8), YELLOW (9), PURPLE (10), PATTERNED (11);

    private int id;

    private static final Map<Integer, String> MY_MAP = new HashMap<Integer, String>();

    static
    {
        for (Colors colors : values())
        {
            MY_MAP.put(colors.getId(), String.valueOf(colors));
        }
    }

    Colors(int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return this.id;
    }

    public static String getKey(int value)
    {
        return MY_MAP.get(value);
    }
}
