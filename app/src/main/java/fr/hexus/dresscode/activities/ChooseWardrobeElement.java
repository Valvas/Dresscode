package fr.hexus.dresscode.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.hexus.dresscode.classes.AppDatabaseCreation;
import fr.hexus.dresscode.classes.Constants;
import fr.hexus.dresscode.classes.WardrobeElement;

public class ChooseWardrobeElement extends AppCompatActivity
{
    private ListView myList;
    private TextView emptyWardrobe;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_wardrobe_element);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_return_white);
        getSupportActionBar().setTitle(getResources().getString(R.string.header_title_add_wardrobe_outfit_choose_element));
        getSupportActionBar().setIcon(R.drawable.ic_add_circle_white);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        AppDatabaseCreation appDatabaseCreation = new AppDatabaseCreation(this);

        SQLiteDatabase database = appDatabaseCreation.getReadableDatabase();

        Integer[] selectedItems = Arrays.stream(getIntent().getIntArrayExtra("selectedItems")).boxed().toArray( Integer[]::new );

        Cursor wardrobeElementsCursor = database.rawQuery("SELECT * FROM " + Constants.WARDROBE_TABLE_NAME, null);

        myList = findViewById(R.id.myList);

        myList.setAdapter(null);

        emptyWardrobe = findViewById(R.id.emptyWardrobe);

        if(wardrobeElementsCursor.getCount() == 0)
        {
            emptyWardrobe.setVisibility(View.VISIBLE);
        }

        else
        {
            List<WardrobeElement> wardrobeElements = new ArrayList<>();

            wardrobeElementsCursor.moveToFirst();

            for(int i = 0; i < wardrobeElementsCursor.getCount(); i++)
            {
                if(!Arrays.asList(selectedItems).contains(wardrobeElementsCursor.getInt(wardrobeElementsCursor.getColumnIndex("id"))))
                {
                    Cursor wardrobeElementColorsCursor = database.rawQuery("SELECT * FROM " + Constants.WARDROBE_ELEMENT_COLORS_TABLE_NAME + " WHERE " + Constants.WARDROBE_ELEMENT_COLORS_TABLE_COLUMNS_ELEMENT_ID + " = ?", new String[]{ wardrobeElementsCursor.getString(wardrobeElementsCursor.getColumnIndex("id")) });

                    wardrobeElementColorsCursor.moveToFirst();

                    ArrayList<Integer> wardrobeElementColors = new ArrayList<>();

                    for(int j = 0; j < wardrobeElementColorsCursor.getCount(); j++)
                    {
                        wardrobeElementColors.add(wardrobeElementColorsCursor.getInt(wardrobeElementColorsCursor.getColumnIndex(Constants.WARDROBE_ELEMENT_COLORS_TABLE_COLUMNS_COLOR_ID)));

                        wardrobeElementColorsCursor.moveToNext();
                    }

                    wardrobeElementColorsCursor.close();

                    wardrobeElements.add(new WardrobeElement(wardrobeElementsCursor.getInt(wardrobeElementsCursor.getColumnIndex("id")), wardrobeElementsCursor.getInt(wardrobeElementsCursor.getColumnIndex(Constants.WARDROBE_TABLE_COLUMNS_TYPE)), wardrobeElementsCursor.getString(wardrobeElementsCursor.getColumnIndex(Constants.WARDROBE_TABLE_COLUMNS_UUID)), wardrobeElementColors, wardrobeElementsCursor.getString(wardrobeElementsCursor.getColumnIndex(Constants.WARDROBE_TABLE_COLUMNS_PATH)), wardrobeElementsCursor.getInt(wardrobeElementsCursor.getColumnIndex(Constants.WARDROBE_TABLE_COLUMNS_STORED_ON_API)) != 0));
                }

                wardrobeElementsCursor.moveToNext();
            }

            WardrobeElementAdapter wardrobeElementAdapter = new WardrobeElementAdapter(this, wardrobeElements);

            myList.setAdapter(wardrobeElementAdapter);

            myList.setOnItemClickListener(new returnClickedItem());

            if(wardrobeElements.size() == 0)
            {
                emptyWardrobe.setVisibility(View.VISIBLE);
            }
        }

        wardrobeElementsCursor.close();

        appDatabaseCreation.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class returnClickedItem implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            WardrobeElement clicked = (WardrobeElement) parent.getItemAtPosition(position);

            Intent finishIntent = new Intent();
            finishIntent.putExtra("wardrobeElement", clicked);
            setResult(RESULT_OK, finishIntent);
            finish();
        }
    }
}
