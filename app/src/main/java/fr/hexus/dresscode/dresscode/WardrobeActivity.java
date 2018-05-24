package fr.hexus.dresscode.dresscode;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class WardrobeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private TextView emptyWardrobe;
    private DrawerLayout myDrawer;
    private NavigationView dresscodeMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wardrobe);

        myDrawer = findViewById(R.id.myDrawer);

        dresscodeMenu = findViewById(R.id.dresscodeMenu);

        dresscodeMenu.setNavigationItemSelectedListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white);
        getSupportActionBar().setTitle(getResources().getString(R.string.menu_wardrobe));
        getSupportActionBar().setIcon(R.drawable.ic_baseline_all_inbox);

        FloatingActionButton addNewWardrobeElement = findViewById(R.id.addNewWardrobeElement);

        addNewWardrobeElement.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(getApplicationContext(), WardrobeAddElement.class));
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();

        AppDatabaseCreation appDatabaseCreation = new AppDatabaseCreation(this);

        SQLiteDatabase database = appDatabaseCreation.getReadableDatabase();

        Cursor wardrobeElementsCursor = database.rawQuery("SELECT * FROM wardrobe", null);

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
                wardrobeElements.add(new WardrobeElement(wardrobeElementsCursor.getInt(wardrobeElementsCursor.getColumnIndex("id")), wardrobeElementsCursor.getInt(wardrobeElementsCursor.getColumnIndex("type")),wardrobeElementsCursor.getInt(wardrobeElementsCursor.getColumnIndex("color")), wardrobeElementsCursor.getString(wardrobeElementsCursor.getColumnIndex("name")), wardrobeElementsCursor.getString(wardrobeElementsCursor.getColumnIndex("path"))));

                wardrobeElementsCursor.moveToNext();
            }

            ListView myList = findViewById(R.id.myList);

            WardrobeElementAdapter wardrobeElementAdapter = new WardrobeElementAdapter(this, wardrobeElements);
            myList.setAdapter(wardrobeElementAdapter);

            myList.setOnItemClickListener(new returnClickedItem());
        }

        appDatabaseCreation.close();
    }

    class returnClickedItem implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            WardrobeElement clicked = (WardrobeElement) parent.getItemAtPosition(position);

            Intent intent = new Intent(parent.getContext(), WardrobeElementView.class);

            intent.putExtra(getResources().getString(R.string.WARDROBE_ELEMENT), clicked);

            startActivity(intent);
        }
    }

    public void openWardrobeForm(View view)
    {
        startActivity(new Intent(this, WardrobeAddElement.class));
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
                myDrawer.closeDrawer(Gravity.START);
                return true;

            case R.id.menuOutfits:
                finish();
                startActivity(new Intent(this, WardrobeOutfit.class));
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
