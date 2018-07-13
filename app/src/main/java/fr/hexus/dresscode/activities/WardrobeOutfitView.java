package fr.hexus.dresscode.activities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import fr.hexus.dresscode.classes.Constants;
import fr.hexus.dresscode.classes.Outfit;
import fr.hexus.dresscode.retrofit.WardrobeElementRemoveJobService;
import fr.hexus.dresscode.retrofit.WardrobeOutfitRemoveJobService;

public class WardrobeOutfitView extends AppCompatActivity
{
    private Outfit currentOutfit;

    private TextView outfitName;

    private FirebaseJobDispatcher dispatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wardrobe_outfit_view);

        currentOutfit = (Outfit) getIntent().getExtras().getSerializable("outfit");

        dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(getApplicationContext()));

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

    /****************************************************************************************************/
    // WHEN CLICKING ON THE BUTTON TO RENAME THE OUTFIT
    /****************************************************************************************************/

    public void renameOutfit(View view)
    {

    }

    /****************************************************************************************************/
    // WHEN CLICKING ON THE BUTTON TO REMOVE THE OUTFIT
    /****************************************************************************************************/

    public void removeOutfit(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.outfit_remove_title))
                .setMessage(getResources().getString(R.string.outfit_remove_message))
                .setIcon(R.drawable.ic_info_red)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) ->
                {
                    if(currentOutfit.removeOutfitFromTheDatabase(getApplicationContext()))
                    {
                        removeOutfitFromApi();
                    }

                    else
                    {
                        Toast.makeText(this, getResources().getString(R.string.could_not_remove_outfit), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.no, null);

        AlertDialog alert = builder.create();

        alert.show();
    }

    /****************************************************************************************************/
    // CREATE TASK TO REMOVE THE OUTFIT FROM THE API
    /****************************************************************************************************/

    private void removeOutfitFromApi()
    {
        final SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);

        Bundle extras = new Bundle();

        extras.putString("token", sharedPreferences.getString("token", null));
        extras.putString("uuid", currentOutfit.getUuid());

        Job job = dispatcher.newJobBuilder()
                .setService(WardrobeOutfitRemoveJobService.class)
                .setTag(currentOutfit.getUuid())
                .setRecurring(false)
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                .setTrigger(Trigger.executionWindow(0, 15))
                .setReplaceCurrent(true)
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setExtras(extras)
                .build();

        dispatcher.mustSchedule(job);
    }
}
