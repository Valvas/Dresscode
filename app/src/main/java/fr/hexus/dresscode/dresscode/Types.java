package fr.hexus.dresscode.dresscode;

import java.util.HashMap;
import java.util.Map;

public enum Types
{
    TOP (0), SHIRT (1), TROUSERS (2), SKIRT (3), SHORT (4), DRESS (5), SWIMWEAR (6), SPORTSWEAR (7), SOCKS (8), TIGHTS (9), COAT (10), SHOES (11), BELT (12), BAG (13);

    private int id;

    private static final Map<Integer, String> MY_MAP = new HashMap<Integer, String>();

    static
    {
        for (Types types : values())
        {
            MY_MAP.put(types.getId(), String.valueOf(types));
        }
    }

    Types(int id)
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
