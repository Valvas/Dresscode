package fr.hexus.dresscode.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import fr.hexus.dresscode.classes.AppDatabaseCreation;
import fr.hexus.dresscode.classes.Constants;
import fr.hexus.dresscode.classes.FinishListener;
import fr.hexus.dresscode.classes.ISynchronizationObserver;
import fr.hexus.dresscode.classes.Logout;
import fr.hexus.dresscode.classes.Outfit;
import fr.hexus.dresscode.classes.SyncAgent;
import fr.hexus.dresscode.classes.WardrobeElement;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FinishListener, ISynchronizationObserver
{
    private int synchronizationAmountOfElements;
    private int synchronizationCurrentElement;
    private int synchronizationAmountOfErrors;

    private DrawerLayout myDrawer;
    private NavigationView dresscodeMenu;

    private TextView wardrobeElementsSynchronized;
    private TextView wardrobeElementsNonSynchronized;
    private TextView wardrobeOutfitsSynchronized;
    private TextView wardrobeOutfitsNonSynchronized;

    private Button syncButton;

    private TextView wardrobeSyncBlockCurrent;
    private TextView wardrobeSyncBlockErrors;
    private TextView wardrobeSyncBlockOver;

    private LinearLayout wardrobeSyncTitle;
    private LinearLayout wardrobeSyncBlock;

    private Button wardrobeSyncBlockClose;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        if(getIntent().getBooleanExtra("LOGOUT", false))
        {
            Logout.addFinishListener(this);
            Logout.logoutAccount(this);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        wardrobeSyncBlock = findViewById(R.id.wardrobeSyncBlock);

        wardrobeSyncBlockCurrent = findViewById(R.id.wardrobeSyncBlockCurrent);
        wardrobeSyncBlockErrors = findViewById(R.id.wardrobeSyncBlockErrors);
        wardrobeSyncBlockOver = findViewById(R.id.wardrobeSyncBlockOver);

        wardrobeSyncTitle = findViewById(R.id.wardrobeSyncTitle);

        wardrobeSyncBlockClose = findViewById(R.id.wardrobeSyncBlockClose);

        syncButton = findViewById(R.id.wardrobeSyncButton);

        myDrawer = findViewById(R.id.myDrawer);

        dresscodeMenu = findViewById(R.id.dresscodeMenu);

        dresscodeMenu.setNavigationItemSelectedListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white);
        getSupportActionBar().setTitle(getResources().getString(R.string.menu_home));
        getSupportActionBar().setIcon(R.drawable.ic_home_white);

        AppDatabaseCreation appDatabaseCreation = new AppDatabaseCreation(this);

        SQLiteDatabase db = appDatabaseCreation.getReadableDatabase();

        File dresscodeDirectory = new File(Environment.getExternalStorageDirectory() + "/Dresscode");

        if(!dresscodeDirectory.exists())
        {
            if(!dresscodeDirectory.mkdirs())
            {
                Toast.makeText(this, R.string.could_no_create_app_directory, Toast.LENGTH_LONG).show();

                finish();
            }
        }
    }

    /****************************************************************************************************/
    // ON RESUME UPDATE THE DATA ABOUT SYNCHRONIZED ELEMENTS
    /****************************************************************************************************/

    @Override
    protected void onResume()
    {
        super.onResume();

        synchronizationAmountOfElements = 0;
        synchronizationCurrentElement = 0;
        synchronizationAmountOfErrors = 0;

        updateSyncBlocks();
    }

    /****************************************************************************************************/
    // WHEN LAUNCHING SYNCHRONIZATION WITH THE API
    /****************************************************************************************************/

    public void syncWithApi(View view)
    {
        final SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);

        SyncAgent syncAgent = new SyncAgent(sharedPreferences.getString("token", null), getApplicationContext());

        synchronizationAmountOfElements = syncAgent.getWardrobeElements().size() + syncAgent.getWardrobeOutfits().size();
        synchronizationCurrentElement = 1;

        syncAgent.addObserver(this);

        syncButton.setVisibility(View.GONE);

        wardrobeSyncBlockCurrent.setText(String.valueOf(wardrobeSyncBlockCurrent.getText()).split(":")[0] + ": 0 / " + String.valueOf(synchronizationAmountOfElements));
        wardrobeSyncBlockErrors.setText(String.valueOf(wardrobeSyncBlockErrors.getText()).split(":")[0] + ": 0");

        wardrobeSyncBlock.setVisibility(View.VISIBLE);

        syncAgent.synchronizeDataWithApi();
    }

    /****************************************************************************************************/
    // CLOSE THE SYNC BLOCK AFTER SYNCHRONIZATION END
    /****************************************************************************************************/

    public void closeSyncBlock(View view)
    {
        wardrobeSyncBlock.setVisibility(View.GONE);

        syncButton.setVisibility(View.VISIBLE);
    }

    /****************************************************************************************************/
    // WHILE SYNCHRONIZATION IS PENDING AND A NEW ELEMENT HAS BEEN SYNCHRONIZED WITH THE API
    /****************************************************************************************************/

    @Override
    public void syncProgress(boolean lastTaskSucceeded)
    {
        wardrobeSyncBlockCurrent.setText(String.valueOf(wardrobeSyncBlockCurrent.getText()).split(":")[0] + ": " + String.valueOf(synchronizationCurrentElement) + " / " + String.valueOf(synchronizationAmountOfElements));

        if(!lastTaskSucceeded)
        {
            wardrobeSyncBlockErrors.setText(String.valueOf(wardrobeSyncBlockErrors.getText()).split(":")[0] + ": " + String.valueOf(synchronizationAmountOfErrors += 1));
        }

        updateSyncBlocks();
    }

    /****************************************************************************************************/
    // WHEN SYNCHRONIZATION WITH THE API IS OVER
    /****************************************************************************************************/

    @Override
    public void syncDone(boolean lastTaskSucceeded)
    {
        wardrobeSyncTitle.setVisibility(View.GONE);
        wardrobeSyncBlockCurrent.setVisibility(View.GONE);

        wardrobeSyncBlockOver.setVisibility(View.VISIBLE);
        wardrobeSyncBlockClose.setVisibility(View.VISIBLE);

        if(!lastTaskSucceeded)
        {
            wardrobeSyncBlockErrors.setText(String.valueOf(wardrobeSyncBlockErrors.getText()).split(":")[0] + ": " + String.valueOf(synchronizationAmountOfErrors += 1));
        }

        updateSyncBlocks();
    }

    /****************************************************************************************************/
    // UPDATE THE BLOCKS THAT SAY HOW MANY ELEMENTS ARE SYNCHRONIZED WITH THE API
    /****************************************************************************************************/

    public void updateSyncBlocks()
    {
        int amountOfSynchronizedElements = 0;
        int amountOfNonSynchronizedElements = 0;
        int amountOfSynchronizedOutfits = 0;
        int amountOfNonSynchronizedOutfits = 0;

        wardrobeElementsSynchronized        = findViewById(R.id.wardrobeElementsSynchronized);
        wardrobeElementsNonSynchronized     = findViewById(R.id.wardrobeElementsNonSynchronized);
        wardrobeOutfitsSynchronized         = findViewById(R.id.wardrobeOutfitsSynchronized);
        wardrobeOutfitsNonSynchronized      = findViewById(R.id.wardrobeOutfitsNonSynchronized);

        AppDatabaseCreation appDatabaseCreation = new AppDatabaseCreation(this);

        SQLiteDatabase database = appDatabaseCreation.getReadableDatabase();

        // GET ALL WARDROBE ELEMENTS FROM DATABASE

        Cursor wardrobeElementsCursor = database.query(Constants.WARDROBE_TABLE_NAME, new String[]{ Constants.WARDROBE_TABLE_COLUMNS_UUID, Constants.WARDROBE_TABLE_COLUMNS_STORED_ON_API }, null, null, null, null, null);

        wardrobeElementsCursor.moveToFirst();

        // BROWSE EACH ELEMENT TO GET THE AMOUNT THAT ARE NOT SYNCHRONIZED WITH THE API

        for(int i = 0; i < wardrobeElementsCursor.getCount(); i++)
        {
            if(wardrobeElementsCursor.getInt(wardrobeElementsCursor.getColumnIndex(Constants.WARDROBE_TABLE_COLUMNS_STORED_ON_API)) == 0)
            {
                amountOfNonSynchronizedElements += 1;
            }

            else
            {
                amountOfSynchronizedElements += 1;
            }

            wardrobeElementsCursor.moveToNext();
        }

        wardrobeElementsCursor.close();

        // GET ALL WARDROBE OUTFITS FROM DATABASE

        Cursor wardrobeOutfitsCursor = database.query(Constants.OUTFIT_TABLE_NAME, new String[]{ Constants.OUTFIT_TABLE_COLUMNS_UUID, Constants.OUTFIT_TABLE_COLUMNS_STORED_ON_API }, null, null, null, null, null);

        wardrobeOutfitsCursor.moveToFirst();

        // BROWSE EACH ELEMENT TO GET THE AMOUNT THAT ARE NOT SYNCHRONIZED WITH THE API

        for(int i = 0; i < wardrobeOutfitsCursor.getCount(); i++)
        {
            if(wardrobeOutfitsCursor.getInt(wardrobeOutfitsCursor.getColumnIndex(Constants.OUTFIT_TABLE_COLUMNS_STORED_ON_API)) == 0)
            {
                amountOfNonSynchronizedOutfits += 1;
            }

            else
            {
                amountOfSynchronizedOutfits += 1;
            }

            wardrobeOutfitsCursor.moveToNext();
        }

        wardrobeOutfitsCursor.close();

        database.close();

        // UPDATE TEXT VIEWS FROM DATA

        wardrobeElementsSynchronized.setText(String.valueOf(wardrobeElementsSynchronized.getText()).split(":")[0] + ": " + String.valueOf(amountOfSynchronizedElements));
        wardrobeElementsNonSynchronized.setText(String.valueOf(wardrobeElementsNonSynchronized.getText()).split(":")[0] + ": " + String.valueOf(amountOfNonSynchronizedElements));

        wardrobeOutfitsSynchronized.setText(String.valueOf(wardrobeOutfitsSynchronized.getText()).split(":")[0] + ": " + String.valueOf(amountOfSynchronizedOutfits));
        wardrobeOutfitsNonSynchronized.setText(String.valueOf(wardrobeOutfitsNonSynchronized.getText()).split(":")[0] + ": " + String.valueOf(amountOfNonSynchronizedOutfits));
    }

    /****************************************************************************************************/
    // IMPLEMENTS THE FINISH LISTENER TO END ACTIVITY ON LOGOUT
    /****************************************************************************************************/

    @Override
    public void finishActivity()
    {
        Intent logoutIntent = new Intent(this, SignInActivity.class);

        finish();

        startActivity(logoutIntent);
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

        switch(item.getItemId())
        {
            case R.id.menuHome:
                myDrawer.closeDrawer(Gravity.START);
                return true;

            case R.id.menuWardrobe:
                Intent intent = new Intent(this, WardrobeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;

            case R.id.menuOutfits:
                startActivity(new Intent(this, WardrobeOutfit.class));
                return true;

            case R.id.menuExit:
                finish();
                return true;

            case R.id.menuLogout:
                Logout.addFinishListener(this);
                Logout.logoutAccount(this);
                return true;

            default:
                return true;
        }
    }
}
