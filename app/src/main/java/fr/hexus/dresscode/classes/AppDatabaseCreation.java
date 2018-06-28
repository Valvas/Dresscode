package fr.hexus.dresscode.classes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import fr.hexus.dresscode.classes.Constants;

public class AppDatabaseCreation extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "dresscode.db";
    private static final int DATABASE_VERSION = 1;

    public AppDatabaseCreation(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String wardrobeTable = "CREATE TABLE " + Constants.WARDROBE_TABLE_NAME + " (id INTEGER PRIMARY KEY, " + Constants.WARDROBE_TABLE_COLUMNS_TYPE + " INTEGER NOT NULL, " + Constants.WARDROBE_TABLE_COLUMNS_PATH + " TEXT NOT NULL, " + Constants.WARDROBE_TABLE_COLUMNS_OUTFIT + " INTEGER)";
        String outfitTable = "CREATE TABLE " + Constants.OUTFIT_TABLE_NAME + " (id INTEGER PRIMARY KEY, " + Constants.OUTFIT_TABLE_COLUMNS_NAME + " TEXT NOT NULL)";
        String wardrobeElementColorsTable = "CREATE TABLE " + Constants.WARDROBE_ELEMENT_COLORS_TABLE_NAME + " (id, INTEGER PRIMARY KEY, " + Constants.WARDROBE_ELEMENT_COLORS_TABLE_COLUMNS_ELEMENT_ID + " INTEGER NOT NULL, " + Constants.WARDROBE_ELEMENT_COLORS_TABLE_COLUMNS_COLOR_ID + " INTEGER NOT NULL)";

        db.execSQL(wardrobeTable);
        db.execSQL(outfitTable);
        db.execSQL(wardrobeElementColorsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        String wardrobeTable = "DROP TABLE IF EXISTS " + Constants.WARDROBE_TABLE_NAME;
        String outfitTable = "DROP TABLE IF EXISTS " + Constants.OUTFIT_TABLE_NAME;
        String wardrobeElementColorsTable = "DROP TABLE IF EXISTS " + Constants.WARDROBE_ELEMENT_COLORS_TABLE_NAME;

        db.execSQL(wardrobeTable);
        db.execSQL(outfitTable);
        db.execSQL(wardrobeElementColorsTable);

        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        onUpgrade(db, oldVersion, newVersion);
    }
}
