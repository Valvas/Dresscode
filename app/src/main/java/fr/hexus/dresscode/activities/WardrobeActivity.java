package fr.hexus.dresscode.activities;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.hexus.dresscode.classes.AppDatabaseCreation;
import fr.hexus.dresscode.classes.CallException;
import fr.hexus.dresscode.classes.Constants;
import fr.hexus.dresscode.classes.LogonForm;
import fr.hexus.dresscode.classes.Token;
import fr.hexus.dresscode.classes.WardrobeElement;
import fr.hexus.dresscode.classes.WardrobeElementForm;
import fr.hexus.dresscode.enums.Colors;
import fr.hexus.dresscode.enums.Types;
import fr.hexus.dresscode.retrofit.DresscodeService;
import fr.hexus.dresscode.retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class WardrobeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private TextView emptyWardrobe;
    private DrawerLayout myDrawer;
    private NavigationView dresscodeMenu;
    private ListView myList;

    private Button wardrobeSearchButton;
    private Button wardrobeSearchClose;
    private Button wardrobeSearchCancel;
    private Button wardrobeSearchLaunch;

    private LinearLayout wardrobeListPanel;
    private LinearLayout wardrobeSearchPanelTypes;
    private LinearLayout wardrobeSearchPanelColors;

    private ProgressBar progressBar;

    private ScrollView wardrobeSearchPanel;

    private FloatingActionButton addNewWardrobeElement;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wardrobe);

        myDrawer = findViewById(R.id.myDrawer);

        dresscodeMenu = findViewById(R.id.dresscodeMenu);

        dresscodeMenu.setNavigationItemSelectedListener(this);

        wardrobeSearchButton = findViewById(R.id.wardrobeSearchButton);
        wardrobeSearchClose = findViewById(R.id.wardrobeSearchClose);
        wardrobeSearchCancel = findViewById(R.id.wardrobeSearchCancel);
        wardrobeSearchLaunch = findViewById(R.id.wardrobeSearchLaunch);

        wardrobeListPanel = findViewById(R.id.wardrobeListPanel);
        wardrobeSearchPanel = findViewById(R.id.wardrobeSearchPanel);
        wardrobeSearchPanelTypes = findViewById(R.id.wardrobeSearchPanelTypes);
        wardrobeSearchPanelColors = findViewById(R.id.wardrobeSearchPanelColors);

        progressBar = findViewById(R.id.progressBar);

        for(int i = 1; i <= Colors.values().length; i++)
        {
            String type = getResources().getString(getResources().getIdentifier(Types.getKey(i), "string", getPackageName()));
            final CheckBox checkBox = new CheckBox(getApplicationContext());
            checkBox.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            checkBox.setText(type);
            checkBox.setId(i);

            wardrobeSearchPanelTypes.addView(checkBox);
        }

        for(int i = 1; i <= Colors.values().length; i++)
        {
            String color = getResources().getString(getResources().getIdentifier(Colors.getKey(i), "string", getPackageName()));
            final CheckBox checkBox = new CheckBox(getApplicationContext());
            checkBox.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            checkBox.setText(color);
            checkBox.setId(i);

            wardrobeSearchPanelColors.addView(checkBox);
        }

        wardrobeSearchButton.setOnClickListener(v -> openSearchPanel() );
        wardrobeSearchClose.setOnClickListener(v -> closeCurrentSearch() );
        wardrobeSearchCancel.setOnClickListener(v -> closeSearchPanel() );
        wardrobeSearchLaunch.setOnClickListener(v -> launchSearch() );

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white);
        getSupportActionBar().setTitle(getResources().getString(R.string.menu_wardrobe));
        getSupportActionBar().setIcon(R.drawable.ic_baseline_all_inbox);

        addNewWardrobeElement = findViewById(R.id.addNewWardrobeElement);

        addNewWardrobeElement.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), WardrobeAddElement.class)));
    }

    @Override
    public void onResume()
    {
        super.onResume();

        AppDatabaseCreation appDatabaseCreation = new AppDatabaseCreation(this);

        SQLiteDatabase database = appDatabaseCreation.getReadableDatabase();

        Cursor wardrobeElementsCursor = database.rawQuery("SELECT * FROM " + Constants.WARDROBE_TABLE_NAME, null);

        myList = findViewById(R.id.myList);

        myList.setAdapter(null);

        if(wardrobeElementsCursor.getCount() == 0)
        {
            emptyWardrobe = findViewById(R.id.emptyWardrobe);
            emptyWardrobe.setVisibility(View.VISIBLE);
        }

        else
        {
            emptyWardrobe = findViewById(R.id.emptyWardrobe);
            emptyWardrobe.setVisibility(View.GONE);

            List<WardrobeElement> wardrobeElements = new ArrayList<>();

            wardrobeElementsCursor.moveToFirst();

            for(int i = 0; i < wardrobeElementsCursor.getCount(); i++)
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

                wardrobeElementsCursor.moveToNext();
            }

            WardrobeElementAdapter wardrobeElementAdapter = new WardrobeElementAdapter(this, wardrobeElements);
            myList.setAdapter(wardrobeElementAdapter);

            myList.setOnItemClickListener(new returnClickedItem());
        }

        wardrobeElementsCursor.close();

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

    /****************************************************************************************************/
    // OPEN THE SEARCH PANEL
    /****************************************************************************************************/

    private void openSearchPanel()
    {
        wardrobeListPanel.setVisibility(View.GONE);

        addNewWardrobeElement.setVisibility(View.GONE);

        wardrobeSearchPanel.setVisibility(View.VISIBLE);
    }

    /****************************************************************************************************/
    // CLOSE THE SEARCH PANEL
    /****************************************************************************************************/

    private void closeSearchPanel()
    {
        wardrobeSearchPanel.setVisibility(View.GONE);

        addNewWardrobeElement.setVisibility(View.VISIBLE);

        wardrobeListPanel.setVisibility(View.VISIBLE);
    }

    /****************************************************************************************************/
    // LAUNCH SEARCH
    /****************************************************************************************************/

    private void launchSearch()
    {
        ArrayList<Integer> selectedTypes = new ArrayList<>();
        ArrayList<Integer> selectedColors = new ArrayList<>();

        // GET SELECTED TYPES IN AN ARRAY

        for(int i = 0; i < wardrobeSearchPanelTypes.getChildCount(); i++)
        {
            CheckBox currentCheckBox = (CheckBox) wardrobeSearchPanelTypes.getChildAt(i);

            if(currentCheckBox.isChecked())
            {
                selectedTypes.add(i + 1);
            }
        }

        // GET SELECTED COLORS IN AN ARRAY

        for(int i = 0; i < wardrobeSearchPanelColors.getChildCount(); i++)
        {
            CheckBox currentCheckBox = (CheckBox) wardrobeSearchPanelColors.getChildAt(i);

            if(currentCheckBox.isChecked())
            {
                selectedColors.add(i + 1);
            }
        }

        // NO COLORS SELECTED OR NO TYPES SELECTED

        if(selectedTypes.size() == 0 || selectedColors.size() == 0)
        {
            Toast.makeText(this, getResources().getString(R.string.pick_at_list_a_color_and_a_type), Toast.LENGTH_SHORT).show();
        }

        // GET ALL ELEMENTS FROM DATABASE AND DISPLAY ONLY THOSE SEARCHED

        else
        {
            wardrobeSearchPanel.setVisibility(View.GONE);

            progressBar.setVisibility(View.VISIBLE);

            AppDatabaseCreation appDatabaseCreation = new AppDatabaseCreation(this);

            SQLiteDatabase database = appDatabaseCreation.getReadableDatabase();

            Cursor wardrobeElementsCursor = database.rawQuery("SELECT * FROM " + Constants.WARDROBE_TABLE_NAME, null);

            wardrobeElementsCursor.moveToFirst();

            myList = findViewById(R.id.myList);

            myList.setAdapter(null);

            ArrayList<WardrobeElement> wardrobeElements = new ArrayList<>();

            // BROWSE EACH ELEMENT

            for(int i = 0; i < wardrobeElementsCursor.getCount(); i++)
            {
                ArrayList<Integer> currentElementColors = new ArrayList<>();

                Cursor currentElementColorsCursor = database.query(Constants.WARDROBE_ELEMENT_COLORS_TABLE_NAME, new String[]{ "*" }, Constants.WARDROBE_ELEMENT_COLORS_TABLE_COLUMNS_ELEMENT_ID + " = ?", new String[]{ String.valueOf(wardrobeElementsCursor.getInt(wardrobeElementsCursor.getColumnIndex("id"))) }, null, null, null);

                currentElementColorsCursor.moveToFirst();

                for(int j = 0; j < currentElementColorsCursor.getCount(); j++)
                {
                    currentElementColors.add(currentElementColorsCursor.getInt(currentElementColorsCursor.getColumnIndex(Constants.WARDROBE_ELEMENT_COLORS_TABLE_COLUMNS_COLOR_ID)));

                    currentElementColorsCursor.moveToNext();
                }

                currentElementColorsCursor.close();

                WardrobeElement currentWardrobeElement = new WardrobeElement(wardrobeElementsCursor.getInt(wardrobeElementsCursor.getColumnIndex("id")), wardrobeElementsCursor.getInt(wardrobeElementsCursor.getColumnIndex(Constants.WARDROBE_TABLE_COLUMNS_TYPE)), wardrobeElementsCursor.getString(wardrobeElementsCursor.getColumnIndex(Constants.WARDROBE_TABLE_COLUMNS_UUID)), currentElementColors, wardrobeElementsCursor.getString(wardrobeElementsCursor.getColumnIndex(Constants.WARDROBE_TABLE_COLUMNS_PATH)), true);

                boolean mustBeKept = false;

                if(selectedTypes.contains(currentWardrobeElement.getType()))
                {
                    mustBeKept = true;
                }

                for(int k = 0; k < currentWardrobeElement.getColors().size(); k++)
                {
                    if(selectedColors.contains(currentWardrobeElement.getColors().get(k)) && selectedTypes.contains(currentWardrobeElement.getType()))
                    {
                        mustBeKept = true;
                    }
                }

                if(mustBeKept)
                {
                    wardrobeElements.add(currentWardrobeElement);
                }

                wardrobeElementsCursor.moveToNext();
            }

            WardrobeElementAdapter wardrobeElementAdapter = new WardrobeElementAdapter(this, wardrobeElements);
            myList.setAdapter(wardrobeElementAdapter);

            myList.setOnItemClickListener(new returnClickedItem());

            wardrobeElementsCursor.close();

            database.close();

            progressBar.setVisibility(View.GONE);

            wardrobeSearchButton.setVisibility(View.GONE);
            wardrobeSearchClose.setVisibility(View.VISIBLE);

            wardrobeListPanel.setVisibility(View.VISIBLE);
        }
    }

    /****************************************************************************************************/
    // CLOSE CURRENT SEARCH
    /****************************************************************************************************/

    private void closeCurrentSearch()
    {
        AppDatabaseCreation appDatabaseCreation = new AppDatabaseCreation(this);

        SQLiteDatabase database = appDatabaseCreation.getReadableDatabase();

        Cursor wardrobeElementsCursor = database.rawQuery("SELECT * FROM " + Constants.WARDROBE_TABLE_NAME, null);

        myList = findViewById(R.id.myList);

        myList.setAdapter(null);

        if(wardrobeElementsCursor.getCount() == 0)
        {
            emptyWardrobe = findViewById(R.id.emptyWardrobe);
            emptyWardrobe.setVisibility(View.VISIBLE);
        }

        else
        {
            emptyWardrobe = findViewById(R.id.emptyWardrobe);
            emptyWardrobe.setVisibility(View.GONE);

            List<WardrobeElement> wardrobeElements = new ArrayList<>();

            wardrobeElementsCursor.moveToFirst();

            for(int i = 0; i < wardrobeElementsCursor.getCount(); i++)
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

                wardrobeElementsCursor.moveToNext();
            }

            WardrobeElementAdapter wardrobeElementAdapter = new WardrobeElementAdapter(this, wardrobeElements);
            myList.setAdapter(wardrobeElementAdapter);

            myList.setOnItemClickListener(new returnClickedItem());
        }

        wardrobeElementsCursor.close();

        appDatabaseCreation.close();

        wardrobeSearchClose.setVisibility(View.GONE);
        wardrobeSearchButton.setVisibility(View.VISIBLE);
    }

    /****************************************************************************************************/

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
