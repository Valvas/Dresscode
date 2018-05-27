package fr.hexus.dresscode.dresscode;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppDatabaseCreation extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "dresscode.db";
    private static final int DATABASE_VERSION = 3;

    public AppDatabaseCreation(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String wardrobeTable = "CREATE TABLE " + Constants.WARDROBE_TABLE_NAME + " (id INTEGER PRIMARY KEY, " + Constants.WARDROBE_TABLE_COLUMNS_TYPE + " INTEGER NOT NULL, " + Constants.WARDROBE_TABLE_COLUMNS_COLOR + " INTEGER NOT NULL, " + Constants.WARDROBE_TABLE_COLUMNS_PATH + " TEXT NOT NULL)";

        db.execSQL(wardrobeTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        String wardrobeTable = "DROP TABLE IF EXISTS " + Constants.WARDROBE_TABLE_NAME;

        db.execSQL(wardrobeTable);

        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        onUpgrade(db, oldVersion, newVersion);
    }
}
