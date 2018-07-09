package fr.hexus.dresscode.activities;

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

import fr.hexus.dresscode.activities.HomeActivity;
import fr.hexus.dresscode.activities.OutfitListAdapter;
import fr.hexus.dresscode.activities.R;
import fr.hexus.dresscode.activities.WardrobeActivity;
import fr.hexus.dresscode.activities.WardrobeOutfitAdd;
import fr.hexus.dresscode.classes.AppDatabaseCreation;
import fr.hexus.dresscode.classes.Constants;
import fr.hexus.dresscode.classes.Outfit;
import fr.hexus.dresscode.classes.WardrobeElement;

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

        FloatingActionButton addNewWardrobeOutfit = findViewById(R.id.addNewWardrobeOutfit);

        addNewWardrobeOutfit.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), WardrobeOutfitAdd.class)));
    }

    @Override
    public void onResume()
    {
        super.onResume();

        AppDatabaseCreation appDatabaseCreation = new AppDatabaseCreation(this);

        SQLiteDatabase database = appDatabaseCreation.getReadableDatabase();

        Cursor wardrobeOutfitsCursor = database.query(Constants.OUTFIT_TABLE_NAME, null, null, null, null, null, null);

        myList = findViewById(R.id.myList);

        myList.setAdapter(null);

        emptyWardrobeOutfits = findViewById(R.id.emptyWardrobeOutfits);

        if(wardrobeOutfitsCursor.getCount() == 0)
        {
            emptyWardrobeOutfits.setVisibility(View.VISIBLE);

            wardrobeOutfitsCursor.close();
        }

        else
        {
            emptyWardrobeOutfits.setVisibility(View.GONE);

            List<Outfit> wardrobeOutfits = new ArrayList<>();

            wardrobeOutfitsCursor.moveToFirst();

            /****************************************************************************************************/
            // BROWSE OUTFITS
            /****************************************************************************************************/

            for(int i = 0; i < wardrobeOutfitsCursor.getCount(); i++)
            {
                List<WardrobeElement> wardrobeOutfitElements = new ArrayList<>();

                Cursor wardrobeOutfitElementsCursor = database.query(Constants.OUTFIT_ELEMENTS_TABLE_NAME, null, Constants.OUTFIT_ELEMENTS_TABLE_COLUMNS_OUTFIT_UUID + " = ?", new String[]{ wardrobeOutfitsCursor.getString(wardrobeOutfitsCursor.getColumnIndex(Constants.OUTFIT_TABLE_COLUMNS_UUID)) }, null, null, null);

                wardrobeOutfitElementsCursor.moveToFirst();

                /****************************************************************************************************/
                // BROWSE ELEMENTS FROM EACH OUTFIT
                /****************************************************************************************************/

                for(int j = 0; j < wardrobeOutfitElementsCursor.getCount(); j++)
                {
                    /****************************************************************************************************/
                    // GET THE DETAIL OF EACH ELEMENT
                    /****************************************************************************************************/

                    Cursor wardrobeOutfitElementsCurrentCursor = database.query(Constants.WARDROBE_TABLE_NAME, null, "id = ?", new String[]{ wardrobeOutfitElementsCursor.getString(wardrobeOutfitElementsCursor.getColumnIndex(Constants.OUTFIT_ELEMENTS_TABLE_COLUMNS_ELEMENT_ID)) }, null, null, null);

                    wardrobeOutfitElementsCurrentCursor.moveToFirst();

                    Cursor wardrobeOutfitElementsCurrentColorsCursor = database.query(Constants.WARDROBE_ELEMENT_COLORS_TABLE_NAME, null, Constants.WARDROBE_ELEMENT_COLORS_TABLE_COLUMNS_ELEMENT_ID + " = ?", new String[]{ wardrobeOutfitElementsCurrentCursor.getString(wardrobeOutfitElementsCurrentCursor.getColumnIndex("id")) }, null, null, null);

                    wardrobeOutfitElementsCurrentColorsCursor.moveToFirst();

                    ArrayList<Integer> currentElementColor = new ArrayList<>();

                    /****************************************************************************************************/
                    // BROWSE COLORS OF EACH OUTFIT ELEMENT
                    /****************************************************************************************************/

                    for(int k = 0; k < wardrobeOutfitElementsCurrentColorsCursor.getCount(); k++)
                    {
                        currentElementColor.add(wardrobeOutfitElementsCurrentColorsCursor.getInt(wardrobeOutfitElementsCurrentColorsCursor.getColumnIndex(Constants.WARDROBE_ELEMENT_COLORS_TABLE_COLUMNS_COLOR_ID)));

                        wardrobeOutfitElementsCurrentColorsCursor.moveToNext();
                    }

                    wardrobeOutfitElementsCurrentColorsCursor.close();

                    wardrobeOutfitElements.add(new WardrobeElement(wardrobeOutfitElementsCurrentCursor.getInt(wardrobeOutfitElementsCurrentCursor.getColumnIndex("id")), wardrobeOutfitElementsCurrentCursor.getInt(wardrobeOutfitElementsCurrentCursor.getColumnIndex(Constants.WARDROBE_TABLE_COLUMNS_TYPE)), wardrobeOutfitElementsCurrentCursor.getString(wardrobeOutfitElementsCurrentCursor.getColumnIndex(Constants.WARDROBE_TABLE_COLUMNS_UUID)), currentElementColor, wardrobeOutfitElementsCurrentCursor.getString(wardrobeOutfitElementsCurrentCursor.getColumnIndex(Constants.WARDROBE_TABLE_COLUMNS_PATH)), wardrobeOutfitElementsCurrentCursor.getInt(wardrobeOutfitElementsCurrentCursor.getColumnIndex(Constants.WARDROBE_TABLE_COLUMNS_STORED_ON_API)) != 0));

                    wardrobeOutfitElementsCurrentCursor.close();

                    wardrobeOutfitElementsCursor.moveToNext();
                }

                wardrobeOutfitElementsCursor.close();

                wardrobeOutfits.add(new Outfit(wardrobeOutfitsCursor.getString(wardrobeOutfitsCursor.getColumnIndex(Constants.OUTFIT_TABLE_COLUMNS_NAME)), wardrobeOutfitElements, wardrobeOutfitsCursor.getString(wardrobeOutfitsCursor.getColumnIndex(Constants.OUTFIT_TABLE_COLUMNS_UUID)), wardrobeOutfitsCursor.getInt(wardrobeOutfitsCursor.getColumnIndex(Constants.OUTFIT_TABLE_COLUMNS_STORED_ON_API)) == 1));

                wardrobeOutfitsCursor.moveToNext();
            }

            wardrobeOutfitsCursor.close();

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
            Outfit clicked = (Outfit) parent.getItemAtPosition(position);

            Intent outfitDetailIntent = new Intent(parent.getContext(), WardrobeOutfitView.class);

            outfitDetailIntent.putExtra("outfit", clicked);

            startActivity(outfitDetailIntent);
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

            case R.id.menuLogout:
                finish();
                Intent logoutIntent = new Intent(this, HomeActivity.class);
                logoutIntent.putExtra("LOGOUT", true);
                startActivity(logoutIntent);
                return true;

            default:
                return true;
        }
    }
}
