package fr.hexus.dresscode.dresscode;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class WardrobeOutfit extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private DrawerLayout myDrawer;
    private ImageView menuButton;
    private NavigationView dresscodeMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wardrobe_outfit);

        myDrawer = findViewById(R.id.myDrawer);

        dresscodeMenu = findViewById(R.id.dresscodeMenu);

        dresscodeMenu.setNavigationItemSelectedListener(this);

        menuButton = findViewById(R.id.openMenu);

        menuButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                myDrawer.openDrawer(Gravity.START);
            }
        });

        FloatingActionButton addNewWardrobeElement = findViewById(R.id.addNewWardrobeOutfit);

        addNewWardrobeElement.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //startActivity(new Intent(getApplicationContext(), WardrobeAddElement.class));
            }
        });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        myDrawer.closeDrawer(Gravity.START);

        if(id == R.id.menuHome)
        {
            finish();
        }

        else if(id == R.id.menuWardrobe)
        {
            finish();
            startActivity(new Intent(this, WardrobeActivity.class));
        }

        else if(id == R.id.menuOutfits)
        {
            myDrawer.closeDrawer(Gravity.START);
        }

        else if(id == R.id.menuExit)
        {
            finish();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        return true;
    }
}
