package fr.hexus.dresscode.dresscode;

import java.util.HashMap;
import java.util.Map;

public enum Types
{
    TOP (1), SHIRT (2), TROUSERS (3), SKIRT (4), SHORT (5), DRESS (6), SWIMWEAR (7), SPORTSWEAR (8), SOCKS (9), TIGHTS (10), COAT (11), SHOES (12), BELT (13), BAG (14);

    private int id;

    private static final Map<Integer, String> MY_MAP = new HashMap<Integer, String>();

    static
    {
        for(Types types : values())
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
