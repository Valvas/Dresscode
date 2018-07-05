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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.hexus.dresscode.classes.AppDatabaseCreation;
import fr.hexus.dresscode.classes.CallException;
import fr.hexus.dresscode.classes.Constants;
import fr.hexus.dresscode.classes.LogonForm;
import fr.hexus.dresscode.classes.Token;
import fr.hexus.dresscode.classes.WardrobeElement;
import fr.hexus.dresscode.classes.WardrobeElementForm;
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
    private Button wardrobeSyncButton;
    private LinearLayout wardrobeSyncInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wardrobe);

        wardrobeSyncButton = findViewById(R.id.wardrobeSyncButton);
        wardrobeSyncInfo = findViewById(R.id.wardrobeSyncInfo);

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

    /****************************************************************************************************/
    // CLICKING ON SYNC BUTTON
    /****************************************************************************************************/

    public void clickOnSync(View view)
    {
        int errorsCounter = 0;

        wardrobeSyncButton.setVisibility(View.GONE);
        wardrobeSyncInfo.setVisibility(View.VISIBLE);

        /****************************************************************************************************/
        // GET TOKEN FROM SHARED PREFERENCES
        /****************************************************************************************************/

        final SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);

        /****************************************************************************************************/
        // GET WARDROBE ELEMENTS FROM DATABASE THAT HAVE NOT ALREADY BEEN SENT TO THE API
        /****************************************************************************************************/

        AppDatabaseCreation appDatabaseCreation = new AppDatabaseCreation(this);

        SQLiteDatabase database = appDatabaseCreation.getReadableDatabase();

        Cursor wardrobeElementsCursor = database.query(Constants.WARDROBE_TABLE_NAME, new String[]{ "*" }, Constants.WARDROBE_TABLE_COLUMNS_UUID + " = ?", new String[]{ "" }, null, null, null);

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

            WardrobeElement currentWardrobeElement = new WardrobeElement(wardrobeElementsCursor.getInt(wardrobeElementsCursor.getColumnIndex("id")), wardrobeElementsCursor.getInt(wardrobeElementsCursor.getColumnIndex(Constants.WARDROBE_TABLE_COLUMNS_TYPE)), wardrobeElementsCursor.getString(wardrobeElementsCursor.getColumnIndex(Constants.WARDROBE_TABLE_COLUMNS_UUID)), wardrobeElementColors, wardrobeElementsCursor.getString(wardrobeElementsCursor.getColumnIndex(Constants.WARDROBE_TABLE_COLUMNS_PATH)), wardrobeElementsCursor.getInt(wardrobeElementsCursor.getColumnIndex(Constants.WARDROBE_TABLE_COLUMNS_STORED_ON_API)) != 0);

            try
            {
                currentWardrobeElement.sendWardrobeElementToTheAPI(sharedPreferences.getString("token", null), getApplicationContext());

            } catch(CallException e)
            {
                errorsCounter += 1;
            }

            wardrobeElementsCursor.moveToNext();
        }

        wardrobeElementsCursor.close();

        appDatabaseCreation.close();

        /****************************************************************************************************/
        // GET WARDROBE ELEMENTS FROM API
        /****************************************************************************************************/

        /*Retrofit retrofit = RetrofitClient.getClient();

        DresscodeService service = retrofit.create(DresscodeService.class);

        Call<JsonElement> call = service.getAllWardrobeElements(sharedPreferences.getString("token", null));

        call.enqueue(new Callback<JsonElement>()
        {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response)
            {
                if(response.errorBody() != null )
                {
                    wardrobeSyncInfo.setVisibility(View.GONE);
                    wardrobeSyncButton.setVisibility(View.VISIBLE);

                    try
                    {
                        String message = new JSONObject(response.errorBody().string()).getString("message");

                        Log.println(Log.ERROR, "Getting wardrobe elements from API",  message);

                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.sync_failed), Toast.LENGTH_SHORT).show();

                    } catch(JSONException e)
                    {
                        Log.println(Log.ERROR, "Getting wardrobe elements from API",  e.getMessage());

                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.sync_failed), Toast.LENGTH_SHORT).show();

                    } catch(IOException e)
                    {
                        Log.println(Log.ERROR, "Getting wardrobe elements from API",  e.getMessage());

                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.sync_failed), Toast.LENGTH_SHORT).show();
                    }
                }

                else
                {
                    fillList(response.body());
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t)
            {
                wardrobeSyncInfo.setVisibility(View.GONE);
                wardrobeSyncButton.setVisibility(View.VISIBLE);

                Toast.makeText(getApplicationContext(), getResources().getString(R.string.sync_failed), Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    /****************************************************************************************************/
    // FILL WARDROBE ELEMENTS LIST
    /****************************************************************************************************/

    public void fillList(JsonElement elements)
    {
        System.out.println(elements);

        wardrobeSyncInfo.setVisibility(View.GONE);
        wardrobeSyncButton.setVisibility(View.VISIBLE);

        Toast.makeText(this, getResources().getString(R.string.sync_done), Toast.LENGTH_SHORT).show();
    }
}
