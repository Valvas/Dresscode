package fr.hexus.dresscode.dresscode;

import java.util.HashMap;
import java.util.Map;

public enum Colors
{
    BLACK (0), WHITE (1), BLUE (2), RED (3), PINK (4), GREEN (5), BROWN (6), ORANGE (7), YELLOW (8), PURPLE (9), PATTERNED (10);

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
