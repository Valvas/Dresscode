package fr.hexus.dresscode.activities;

import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import fr.hexus.dresscode.classes.Outfit;

public class WardrobeOutfitView extends AppCompatActivity
{
    private Outfit currentOutfit;

    private TextView outfitName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wardrobe_outfit_view);

        currentOutfit = (Outfit) getIntent().getExtras().getSerializable("outfit");

        // CREATE THE TOOLBAR

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_return_white);
        getSupportActionBar().setTitle(getResources().getString(R.string.menu_detail));
        getSupportActionBar().setIcon(R.drawable.ic_dress);

        // GET EACH LAYOUT ELEMENT

        outfitName = findViewById(R.id.outfitName);

        // COMPLETE EACH LAYOUT ELEMENT

        outfitName.setText(currentOutfit.getName());
    }

    /****************************************************************************************************/
    // WHEN CLICKING ON THE BACK BUTTON ON TOOLBAR
    /****************************************************************************************************/

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
}
