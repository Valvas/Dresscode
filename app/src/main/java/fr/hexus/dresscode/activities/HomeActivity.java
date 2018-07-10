package fr.hexus.dresscode.activities;

import android.content.Intent;
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
import android.widget.Toast;

import java.io.File;

import fr.hexus.dresscode.classes.AppDatabaseCreation;
import fr.hexus.dresscode.classes.FinishListener;
import fr.hexus.dresscode.classes.Logout;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FinishListener
{
    private DrawerLayout myDrawer;
    private NavigationView dresscodeMenu;

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
