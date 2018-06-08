package fr.hexus.dresscode.dresscode;

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
import java.util.List;

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

        Cursor wardrobeElementsCursor = database.rawQuery("SELECT * FROM wardrobe", null);

        myList = findViewById(R.id.myList);

        myList.setAdapter(null);

        if(wardrobeElementsCursor.getCount() == 0)
        {
            emptyWardrobe = findViewById(R.id.emptyWardrobe);
            emptyWardrobe.setText(R.string.wardrobe_no_entries);
        }

        else
        {
            emptyWardrobe = findViewById(R.id.emptyWardrobe);
            emptyWardrobe.setText("");

            List<WardrobeElement> wardrobeElements = new ArrayList<>();

            wardrobeElementsCursor.moveToFirst();

            for(int i = 0; i < wardrobeElementsCursor.getCount(); i++)
            {
                wardrobeElements.add(new WardrobeElement(wardrobeElementsCursor.getInt(wardrobeElementsCursor.getColumnIndex("id")), wardrobeElementsCursor.getInt(wardrobeElementsCursor.getColumnIndex("type")),wardrobeElementsCursor.getInt(wardrobeElementsCursor.getColumnIndex("color")), wardrobeElementsCursor.getString(wardrobeElementsCursor.getColumnIndex("path"))));

                wardrobeElementsCursor.moveToNext();
            }

            WardrobeElementAdapter wardrobeElementAdapter = new WardrobeElementAdapter(this, wardrobeElements);
            myList.setAdapter(wardrobeElementAdapter);

            myList.setOnItemClickListener(new returnClickedItem());
        }

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
