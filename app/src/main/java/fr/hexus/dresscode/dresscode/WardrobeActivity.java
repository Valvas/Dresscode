package fr.hexus.dresscode.dresscode;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class WardrobeActivity extends ListActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wardrobe);

        AppDatabaseCreation appDatabaseCreation = new AppDatabaseCreation(this);

        SQLiteDatabase database = appDatabaseCreation.getReadableDatabase();

        Cursor wardrobeElementsCursor = database.rawQuery("SELECT * FROM wardrobe", null);

        if(wardrobeElementsCursor.getCount() == 0)
        {
            TextView emptyWardrobe = findViewById(R.id.emptyWardrobe);
            emptyWardrobe.setText(R.string.wardrobe_no_entries);
        }

        else
        {
            List<WardrobeElement> wardrobeElements = new ArrayList<>();

            wardrobeElementsCursor.moveToFirst();

            for(int i = 0; i < wardrobeElementsCursor.getCount(); i++)
            {
                wardrobeElements.add(new WardrobeElement(wardrobeElementsCursor.getInt(wardrobeElementsCursor.getColumnIndex("id")), wardrobeElementsCursor.getInt(wardrobeElementsCursor.getColumnIndex("type")),wardrobeElementsCursor.getInt(wardrobeElementsCursor.getColumnIndex("color")), wardrobeElementsCursor.getString(wardrobeElementsCursor.getColumnIndex("name")), wardrobeElementsCursor.getString(wardrobeElementsCursor.getColumnIndex("path"))));

                wardrobeElementsCursor.moveToNext();
            }

            /*wardrobeElements.add(new WardrobeElement(1, 1, 1, "T-shirt bleu", "/Dresscode/blue.jpg"));
            wardrobeElements.add(new WardrobeElement(2, 1, 1, "T-shirt vert", "/Dresscode/green.jpg"));
            wardrobeElements.add(new WardrobeElement(3, 1, 1, "T-shirt rouge", "/Dresscode/red.jpg"));
            wardrobeElements.add(new WardrobeElement(4, 1, 1, "T-shirt jaune", "/Dresscode/yellow.jpg"));
            wardrobeElements.add(new WardrobeElement(5, 1, 1, "T-shirt noir", "/Dresscode/black.jpg"));
            wardrobeElements.add(new WardrobeElement(6, 1, 1, "T-shirt blanc", "/Dresscode/white.jpg"));*/

            WardrobeElementAdapter wardrobeElementAdapter = new WardrobeElementAdapter(this, wardrobeElements);
            setListAdapter(wardrobeElementAdapter);
        }
    }

    public void onOpenMenu(View view)
    {
        startActivity(new Intent(this, MenuActivity.class));
    }

    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id)
    {
        WardrobeElement clicked = (WardrobeElement) getListAdapter().getItem(position);
        super.onListItemClick(listView, view, position, id);

        Intent intent = new Intent(this, WardrobeElementView.class);

        intent.putExtra(getResources().getString(R.string.WARDROBE_ELEMENT), clicked);

        startActivity(intent);
    }

    public void openWardrobeForm(View view)
    {
        startActivity(new Intent(this, WardrobeAddElement.class));
    }
}
