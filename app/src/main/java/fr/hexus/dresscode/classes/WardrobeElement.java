package fr.hexus.dresscode.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;
import java.util.ArrayList;

public class WardrobeElement implements Serializable
{
    private String path;
    private int id;
    private int type;
    private ArrayList<Integer> colors;

    public WardrobeElement(int id, int type, ArrayList colors, String path)
    {
        this.id         = id;
        this.type       = type;
        this.path       = path;
        this.colors     = colors;
    }

    public int getId()
    {
        return this.id;
    }

    public int getType()
    {
        return this.type;
    }

    public ArrayList<Integer> getColors()
    {
        return this.colors;
    }

    public String getPath()
    {
        return this.path;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public void setColors(ArrayList<Integer> colors)
    {
        this.colors = colors;
    }

    public String toString()
    {
        StringBuilder stringToReturn = new StringBuilder();
        stringToReturn.append("\n[Element]\n- Type : " + this.type + "\n- Path : " + this.path + "\n- Colors : ");

        for(int x = 0; x < this.colors.size(); x++)
        {
            stringToReturn.append(" " + colors.get(x));
        }

        return String.valueOf(stringToReturn);
    }

    /****************************************************************************************************/
    // SAVE ELEMENT IN DATABASE
    /****************************************************************************************************/

    public boolean saveWardrobeElementInDatabase(Context context)
    {
        AppDatabaseCreation appDatabaseCreation = new AppDatabaseCreation(context);

        SQLiteDatabase db = appDatabaseCreation.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.WARDROBE_TABLE_COLUMNS_TYPE, this.type);
        values.put(Constants.WARDROBE_TABLE_COLUMNS_PATH, this.path);

        long insertedRowId = db.insert(Constants.WARDROBE_TABLE_NAME, null, values);

        if(insertedRowId > 0)
        {
            for(int x = 0; x < this.colors.size(); x++)
            {
                ContentValues colorValues = new ContentValues();
                colorValues.put(Constants.WARDROBE_ELEMENT_COLORS_TABLE_COLUMNS_ELEMENT_ID, insertedRowId);
                colorValues.put(Constants.WARDROBE_ELEMENT_COLORS_TABLE_COLUMNS_COLOR_ID, this.colors.get(x));

                long insertedColorId = db.insert(Constants.WARDROBE_ELEMENT_COLORS_TABLE_NAME, null, colorValues);

                if(insertedColorId < 0) return false;
            }
        }

        else
        {
            return false;
        }

        appDatabaseCreation.close();

        return true;
    }

    /****************************************************************************************************/
    // SAVE ELEMENT IN DATABASE
    /****************************************************************************************************/

    public boolean updateWardrobeElementInDatabase(Context context)
    {
        AppDatabaseCreation appDatabaseCreation = new AppDatabaseCreation(context);

        SQLiteDatabase db = appDatabaseCreation.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.WARDROBE_TABLE_COLUMNS_TYPE, this.type);
        values.put(Constants.WARDROBE_TABLE_COLUMNS_PATH, this.path);

        long updatedRows = db.update(Constants.WARDROBE_TABLE_NAME, values, "id = ?", new String[]{ String.valueOf(this.id) });

        if(updatedRows > 0)
        {
            long deletedRows = db.delete(Constants.WARDROBE_ELEMENT_COLORS_TABLE_NAME, Constants.WARDROBE_ELEMENT_COLORS_TABLE_COLUMNS_ELEMENT_ID + " = ?", new String[]{ String.valueOf(this.id) });

            for(int x = 0; x < this.colors.size(); x++)
            {
                ContentValues colorValues = new ContentValues();
                colorValues.put(Constants.WARDROBE_ELEMENT_COLORS_TABLE_COLUMNS_ELEMENT_ID, this.id);
                colorValues.put(Constants.WARDROBE_ELEMENT_COLORS_TABLE_COLUMNS_COLOR_ID, this.colors.get(x));

                long insertedColorId = db.insert(Constants.WARDROBE_ELEMENT_COLORS_TABLE_NAME, null, colorValues);

                if(insertedColorId < 0) return false;
            }
        }

        else
        {
            return false;
        }

        appDatabaseCreation.close();

        return true;
    }
}
