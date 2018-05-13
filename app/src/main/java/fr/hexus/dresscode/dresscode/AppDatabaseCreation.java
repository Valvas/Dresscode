package fr.hexus.dresscode.dresscode;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppDatabaseCreation extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "dresscode";
    private static final int DATABASE_VERSION = 1;

    public AppDatabaseCreation(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String wardrobeTable = "CREATE TABLE wardrobe (id INTEGER PRIMARY KEY, name TEXT NOT NULL, type INTEGER NOT NULL, color INTEGER NOT NULL, picture TEXT NOT NULL)";

        db.execSQL(wardrobeTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }
}
