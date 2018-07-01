package fr.hexus.dresscode.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class Outfit
{
    private int id;
    private String name;
    private List<WardrobeElement> elements;

    public Outfit(int id, String name, List<WardrobeElement> elements)
    {
        this.id = id;
        this.elements = elements;
        this.name = name;
    }

    public List<WardrobeElement> getElements()
    {
        return elements;
    }

    public void setElements(List<WardrobeElement> elements)
    {
        this.elements = elements;
    }

    public void addElementToOutfit(WardrobeElement element)
    {
        this.elements.add(element);
    }

    public String getName()
    {
        return this.name;
    }

    public String toString()
    {
        return "\n[Outfit]\n- ID : " + this.id + "\n- Name : " + this.name + "\n- Elements : " + this.elements.size() + "\n";
    }

    /****************************************************************************************************/
    // SAVE ELEMENT IN DATABASE
    /****************************************************************************************************/

    public boolean saveOutfitInDatabase(Context context)
    {
        AppDatabaseCreation appDatabaseCreation = new AppDatabaseCreation(context);

        SQLiteDatabase db = appDatabaseCreation.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.OUTFIT_TABLE_COLUMNS_NAME, this.name);

        long insertedRowId = db.insert(Constants.OUTFIT_TABLE_NAME, null, values);

        if(insertedRowId > 0)
        {
            for(int i = 0; i < this.elements.size(); i++)
            {
                ContentValues outfitCurrentElementValues = new ContentValues();
                outfitCurrentElementValues.put(Constants.OUTFIT_ELEMENTS_TABLE_COLUMNS_OUTFIT_ID, insertedRowId);
                outfitCurrentElementValues.put(Constants.OUTFIT_ELEMENTS_TABLE_COLUMNS_ELEMENT_ID, this.elements.get(i).getId());

                long insertedElementId = db.insert(Constants.OUTFIT_ELEMENTS_TABLE_NAME, null, outfitCurrentElementValues);

                if(insertedElementId < 0) return false;
            }
        }

        else
        {
            return false;
        }

        return true;
    }
}
