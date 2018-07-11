package fr.hexus.dresscode.classes;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class SyncAgent implements ISynchronizationObservable, IJobServiceObserver
{
    private int wardrobeElementsBrowserIndex;
    private int wardrobeOutfitsBrowserIndex;

    private String token;
    private Context applicationContext;

    private ArrayList<ISynchronizationObserver> observers;
    private ArrayList<WardrobeElement> wardrobeElements;
    private ArrayList<Outfit> wardrobeOutfits;

    public SyncAgent(String token, Context applicationContext)
    {
        this.wardrobeElementsBrowserIndex = 0;
        this.wardrobeOutfitsBrowserIndex = 0;
        this.observers = new ArrayList<>();

        this.token = token;

        this.applicationContext = applicationContext;

        this.wardrobeElements = getNonSyncWardrobeElements(applicationContext);
        this.wardrobeOutfits = getNonSyncWardrobeOutfits(applicationContext);
    }

    /******************************************************************************************/

    public ArrayList<WardrobeElement> getWardrobeElements()
    {
        return this.wardrobeElements;
    }

    /******************************************************************************************/

    public ArrayList<Outfit> getWardrobeOutfits()
    {
        return this.wardrobeOutfits;
    }

    /******************************************************************************************/
    // SEND THE DATA TO THE API
    /******************************************************************************************/

    public void synchronizeDataWithApi()
    {
        jobDone(false);
    }

    /******************************************************************************************/
    // CALLED WHEN A REQUEST IS OVER
    /******************************************************************************************/

    @Override
    public void jobDone(boolean rescheduleJob)
    {
        // EXECUTE REQUEST TO SEND NEXT WARDROBE ELEMENT

        if(wardrobeElementsBrowserIndex < wardrobeElements.size())
        {
            try{ notifyObservers(false, (rescheduleJob == false)); } catch(Exception e){ e.printStackTrace(); }

            wardrobeElements.get(wardrobeElementsBrowserIndex).addObserver(this);
            wardrobeElements.get(wardrobeElementsBrowserIndex).sendWardrobeElementToTheAPI(this.token, this.applicationContext);

            wardrobeElementsBrowserIndex += 1;
        }

        // EXECUTE REQUEST TO SEND NEXT WARDROBE OUTFIT

        else if(wardrobeOutfitsBrowserIndex < wardrobeOutfits.size())
        {
            try{ notifyObservers(false, (rescheduleJob == false)); } catch(Exception e){ e.printStackTrace(); }

            wardrobeOutfits.get(wardrobeOutfitsBrowserIndex).addObserver(this);
            wardrobeOutfits.get(wardrobeOutfitsBrowserIndex).sendOutfitToTheAPI(this.token, this.applicationContext);

            wardrobeOutfitsBrowserIndex += 1;
        }

        // NO MORE ELEMENTS OR OUTFITS TO SEND, NOTIFY OBSERVERS THAT SYNC IS DONE

        else
        {
            try{ notifyObservers(true, (rescheduleJob == false)); } catch(Exception e){ e.printStackTrace(); }
        }
    }

    /******************************************************************************************/
    // RETURNS AN ARRAY LIST WITH ALL WARDROBE ELEMENTS NON SYNCHRONIZED WITH THE API
    /******************************************************************************************/

    private ArrayList<WardrobeElement> getNonSyncWardrobeElements(Context applicationContext)
    {
        AppDatabaseCreation appDatabaseCreation = new AppDatabaseCreation(applicationContext);

        SQLiteDatabase database = appDatabaseCreation.getReadableDatabase();

        // GET ALL WARDROBE ELEMENTS FROM DATABASE WHICH ARE NOT SYNCHRONIZED WITH THE API

        Cursor wardrobeElementsCursor = database.query(Constants.WARDROBE_TABLE_NAME, new String[]{ "*" }, Constants.WARDROBE_TABLE_COLUMNS_STORED_ON_API + " = ?", new String[]{ "0" }, null, null, null);

        wardrobeElementsCursor.moveToFirst();

        ArrayList<WardrobeElement> wardrobeElements = new ArrayList<>();

        // BROWSE EACH ELEMENT

        for(int i = 0; i < wardrobeElementsCursor.getCount(); i++)
        {
            ArrayList<Integer> currentElementColors = new ArrayList<>();

            // GET ALL CURRENT ELEMENT'S COLORS

            Cursor currentWardrobeElementColorsCursor = database.query(Constants.WARDROBE_ELEMENT_COLORS_TABLE_NAME, new String[]{ Constants.WARDROBE_ELEMENT_COLORS_TABLE_COLUMNS_COLOR_ID }, Constants.WARDROBE_ELEMENT_COLORS_TABLE_COLUMNS_ELEMENT_ID + " = ?", new String[]{ String.valueOf(wardrobeElementsCursor.getInt(wardrobeElementsCursor.getColumnIndex("id"))) }, null, null, null);

            currentWardrobeElementColorsCursor.moveToFirst();

            // BROWSE EACH ELEMENT COLOR

            for(int j = 0; j < currentWardrobeElementColorsCursor.getCount(); j++)
            {
                currentElementColors.add(currentWardrobeElementColorsCursor.getInt(currentWardrobeElementColorsCursor.getColumnIndex(Constants.WARDROBE_ELEMENT_COLORS_TABLE_COLUMNS_COLOR_ID)));

                currentWardrobeElementColorsCursor.moveToNext();
            }

            currentWardrobeElementColorsCursor.close();

            // CREATE A NEW WARDROBE ELEMENT INSTANCE FOR THE CURRENT ELEMENT AND PUT IT IN AN ARRAY LIST

            wardrobeElements.add(new WardrobeElement(0, wardrobeElementsCursor.getInt(wardrobeElementsCursor.getColumnIndex(Constants.WARDROBE_TABLE_COLUMNS_TYPE)), wardrobeElementsCursor.getString(wardrobeElementsCursor.getColumnIndex(Constants.WARDROBE_TABLE_COLUMNS_UUID)), currentElementColors, wardrobeElementsCursor.getString(wardrobeElementsCursor.getColumnIndex(Constants.WARDROBE_TABLE_COLUMNS_PATH)), false));

            wardrobeElementsCursor.moveToNext();
        }

        wardrobeElementsCursor.close();

        database.close();

        return wardrobeElements;
    }

    /******************************************************************************************/
    // RETURNS AN ARRAY LIST WITH ALL WARDROBE OUTFITS NON SYNCHRONIZED WITH THE API
    /******************************************************************************************/

    private ArrayList<Outfit> getNonSyncWardrobeOutfits(Context applicationContext)
    {
        AppDatabaseCreation appDatabaseCreation = new AppDatabaseCreation(applicationContext);

        SQLiteDatabase database = appDatabaseCreation.getReadableDatabase();

        // GET ALL OUTFITS FROM DATABASE WHICH ARE NOT SYNCHRONIZED WITH THE API

        Cursor wardrobeOutfitsCursor = database.query(Constants.OUTFIT_TABLE_NAME, new String[]{ "*" }, Constants.OUTFIT_TABLE_COLUMNS_STORED_ON_API + " = ?", new String[]{ "0" }, null, null, null);

        wardrobeOutfitsCursor.moveToFirst();

        ArrayList<Outfit> wardrobeOutfits = new ArrayList<>();

        // BROWSE EACH OUTFIT

        for(int i = 0; i < wardrobeOutfitsCursor.getCount(); i++)
        {
            // GET ALL ELEMENTS FROM THE CURRENT OUTFIT

            ArrayList<WardrobeElement> currentOutfitElements = new ArrayList<>();

            Cursor currentOutfitElementsCursor = database.query(Constants.OUTFIT_ELEMENTS_TABLE_NAME, new String[]{ Constants.OUTFIT_ELEMENTS_TABLE_COLUMNS_ELEMENT_ID }, Constants.OUTFIT_ELEMENTS_TABLE_COLUMNS_OUTFIT_UUID + " = ?", new String[]{ wardrobeOutfitsCursor.getString(wardrobeOutfitsCursor.getColumnIndex(Constants.OUTFIT_TABLE_COLUMNS_UUID)) }, null, null, null);

            currentOutfitElementsCursor.moveToFirst();

            // BROWSE EACH ELEMENT TO GET ITS DATA

            for(int j = 0; j < currentOutfitElementsCursor.getCount(); j++)
            {
                ArrayList<Integer> currentElementColors = new ArrayList<>();

                // GET COLORS FOR THE CURRENT ELEMENT

                Cursor currentOutfitElementColorsCursor = database.query(Constants.WARDROBE_ELEMENT_COLORS_TABLE_NAME, new String[]{ Constants.WARDROBE_ELEMENT_COLORS_TABLE_COLUMNS_COLOR_ID }, Constants.WARDROBE_ELEMENT_COLORS_TABLE_COLUMNS_ELEMENT_ID + " = ?", new String[]{ String.valueOf(currentOutfitElementsCursor.getInt(currentOutfitElementsCursor.getColumnIndex("id"))) }, null, null, null);

                currentOutfitElementColorsCursor.moveToFirst();

                // BROWSE EACH COLOR

                for(int k = 0; k < currentOutfitElementColorsCursor.getCount(); k++)
                {
                    currentElementColors.add(currentOutfitElementColorsCursor.getInt(currentOutfitElementColorsCursor.getColumnIndex(Constants.WARDROBE_ELEMENT_COLORS_TABLE_COLUMNS_COLOR_ID)));

                    currentOutfitElementColorsCursor.moveToNext();
                }

                currentOutfitElementColorsCursor.close();

                currentOutfitElements.add(new WardrobeElement(0, currentOutfitElementsCursor.getInt(currentOutfitElementsCursor.getColumnIndex(Constants.WARDROBE_TABLE_COLUMNS_TYPE)), currentOutfitElementsCursor.getString(currentOutfitElementsCursor.getColumnIndex(Constants.WARDROBE_TABLE_COLUMNS_UUID)), currentElementColors, currentOutfitElementsCursor.getString(currentOutfitElementsCursor.getColumnIndex(Constants.WARDROBE_TABLE_COLUMNS_PATH)), false));

                currentOutfitElementsCursor.moveToNext();
            }

            currentOutfitElementsCursor.close();

            wardrobeOutfits.add(new Outfit(wardrobeOutfitsCursor.getString(wardrobeOutfitsCursor.getColumnIndex(Constants.OUTFIT_TABLE_COLUMNS_NAME)), currentOutfitElements, wardrobeOutfitsCursor.getString(wardrobeOutfitsCursor.getColumnIndex(Constants.OUTFIT_TABLE_COLUMNS_UUID)), false));

            wardrobeOutfitsCursor.moveToNext();
        }

        wardrobeOutfitsCursor.close();

        database.close();

        return wardrobeOutfits;
    }

    /******************************************************************************************/

    @Override
    public void addObserver(ISynchronizationObserver o)
    {
        this.observers.add(o);
    }

    /******************************************************************************************/

    @Override
    public void removeObserver(ISynchronizationObserver o)
    {
        this.observers.remove(o);
    }

    /******************************************************************************************/

    @Override
    public void notifyObservers(boolean isSyncOver, boolean lastTaskSucceeded) throws Exception
    {
        for(int i = 0; i < this.observers.size(); i++)
        {
            if(isSyncOver)
            {
                this.observers.get(i).syncDone(lastTaskSucceeded);
            }

            else
            {
                this.observers.get(i).syncProgress(lastTaskSucceeded);
            }
        }
    }

    /******************************************************************************************/
}
