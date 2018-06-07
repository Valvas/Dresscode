package fr.hexus.dresscode.dresscode;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class WardrobeOutfit extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private ListView myList;
    private DrawerLayout myDrawer;
    private NavigationView dresscodeMenu;
    private TextView emptyWardrobeOutfits;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wardrobe_outfit);

        myDrawer = findViewById(R.id.myDrawer);

        dresscodeMenu = findViewById(R.id.dresscodeMenu);

        dresscodeMenu.setNavigationItemSelectedListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white);
        getSupportActionBar().setTitle(getResources().getString(R.string.menu_outfit));
        getSupportActionBar().setIcon(R.drawable.ic_dress);

        FloatingActionButton addNewWardrobeOutfit= findViewById(R.id.addNewWardrobeOutfit);

        addNewWardrobeOutfit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(getApplicationContext(), WardrobeOutfitAdd.class));
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();

        AppDatabaseCreation appDatabaseCreation = new AppDatabaseCreation(this);

        SQLiteDatabase database = appDatabaseCreation.getReadableDatabase();

        Cursor wardrobeOutfitsCursor = database.rawQuery("SELECT * FROM outfit", null);

        myList = findViewById(R.id.myList);

        myList.setAdapter(null);

        if(wardrobeOutfitsCursor.getCount() == 0)
        {
            emptyWardrobeOutfits = findViewById(R.id.emptyWardrobeOutfits);
            emptyWardrobeOutfits.setText(R.string.outfits_no_entries);
        }

        else
        {
            emptyWardrobeOutfits = findViewById(R.id.emptyWardrobeOutfits);
            emptyWardrobeOutfits.setText("");

            List<Outfit> wardrobeOutfits = new ArrayList<>();

            wardrobeOutfitsCursor.moveToFirst();

            for(int i = 0; i < wardrobeOutfitsCursor.getCount(); i++)
            {
                Cursor wardrobeCurrentOutfitElementsCursor = database.rawQuery("SELECT * FROM wardrobe WHERE " + Constants.WARDROBE_TABLE_COLUMNS_OUTFIT + " = ?", new String[]{String.valueOf(wardrobeOutfitsCursor.getInt(wardrobeOutfitsCursor.getColumnIndex(Constants.WARDROBE_TABLE_COLUMNS_OUTFIT)))});

                wardrobeCurrentOutfitElementsCursor.moveToFirst();

                List<WardrobeElement> wardrobeElements = new ArrayList<>();

                for(int j = 0; j < wardrobeCurrentOutfitElementsCursor.getCount(); j++)
                {
                    wardrobeElements.add(new WardrobeElement(wardrobeCurrentOutfitElementsCursor.getInt(wardrobeCurrentOutfitElementsCursor.getColumnIndex("id")), wardrobeCurrentOutfitElementsCursor.getInt(wardrobeCurrentOutfitElementsCursor.getColumnIndex("type")),wardrobeCurrentOutfitElementsCursor.getInt(wardrobeCurrentOutfitElementsCursor.getColumnIndex("color")), wardrobeCurrentOutfitElementsCursor.getString(wardrobeCurrentOutfitElementsCursor.getColumnIndex("path"))));

                    wardrobeCurrentOutfitElementsCursor.moveToNext();
                }

                wardrobeOutfits.add(new Outfit(wardrobeOutfitsCursor.getString(wardrobeOutfitsCursor.getColumnIndex(Constants.OUTFIT_TABLE_COLUMNS_NAME)), wardrobeElements));
            }

            OutfitListAdapter outfitListAdapter = new OutfitListAdapter(this, wardrobeOutfits);

            myList.setAdapter(outfitListAdapter);

            myList.setOnItemClickListener(new returnClickedItem());
        }

        appDatabaseCreation.close();
    }

    class returnClickedItem implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            /*WardrobeElement clicked = (WardrobeElement) parent.getItemAtPosition(position);

            Intent intent = new Intent(parent.getContext(), WardrobeElementView.class);

            intent.putExtra(getResources().getString(R.string.WARDROBE_ELEMENT), clicked);

            startActivity(intent);*/
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home:
                myDrawer.openDrawer(Gravity.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        myDrawer.closeDrawer(Gravity.START);

        switch(item.getItemId()) {
            case R.id.menuHome:
                finish();
                return true;

            case R.id.menuWardrobe:
                finish();
                startActivity(new Intent(this, WardrobeActivity.class));
                return true;

            case R.id.menuOutfits:
                myDrawer.closeDrawer(Gravity.START);
                return true;

            case R.id.menuExit:
                finish();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;

            default:
                return true;
        }
    }
}
