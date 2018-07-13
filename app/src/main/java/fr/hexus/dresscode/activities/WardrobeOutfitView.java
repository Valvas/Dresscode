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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
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
import fr.hexus.dresscode.retrofit.WardrobeOutfitCreateJobService;
import fr.hexus.dresscode.retrofit.WardrobeOutfitRemoveJobService;
import fr.hexus.dresscode.retrofit.WardrobeOutfitUpdateJobService;

public class WardrobeOutfitView extends AppCompatActivity
{
    private Outfit currentOutfit;

    private FirebaseJobDispatcher dispatcher;

    private ScrollView outfitDetailBlock;

    private LinearLayout outfitDetailBlockMain;
    private LinearLayout outfitDetailBlockRename;
    private LinearLayout outfitDetailElementsBlockList;

    private TextView outfitName;

    private EditText outfitDetailBlockRenameInput;

    private ProgressBar outfitDetailSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wardrobe_outfit_view);

        currentOutfit = (Outfit) getIntent().getExtras().getSerializable("outfit");

        dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(getApplicationContext()));

        outfitDetailBlock = findViewById(R.id.outfitDetailBlock);

        outfitDetailBlockMain = findViewById(R.id.outfitDetailBlockMain);
        outfitDetailBlockRename = findViewById(R.id.outfitDetailBlockRename);

        outfitDetailBlockRenameInput = findViewById(R.id.outfitDetailBlockRenameInput);

        outfitDetailElementsBlockList = findViewById(R.id.outfitDetailElementsBlockList);

        outfitDetailSpinner = findViewById(R.id.outfitDetailSpinner);

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
        outfitDetailBlockRenameInput.setText(outfitName.getText());

        outfitDetailBlock.setVisibility(View.GONE);
        outfitDetailBlockRename.setVisibility(View.VISIBLE);
    }

    /****************************************************************************************************/
    // CONFIRM RENAMING
    /****************************************************************************************************/

    public void confirmRenaming(View view)
    {
        outfitDetailBlockRename.setVisibility(View.GONE);
        outfitDetailSpinner.setVisibility(View.VISIBLE);

        String newName = outfitDetailBlockRenameInput.getText().toString();

        currentOutfit.setName(newName);

        if(currentOutfit.updateOutfitInDatabase(getApplicationContext()))
        {
            createUpdateTask();

            outfitName.setText(outfitDetailBlockRenameInput.getText().toString());

            outfitDetailBlockRenameInput.setText("");

            outfitDetailSpinner.setVisibility(View.GONE);
            outfitDetailBlock.setVisibility(View.VISIBLE);

            Toast.makeText(this, getResources().getString(R.string.outfit_renamed), Toast.LENGTH_SHORT).show();
        }

        else
        {
            outfitDetailSpinner.setVisibility(View.GONE);
            outfitDetailBlockRename.setVisibility(View.VISIBLE);

            Toast.makeText(this, getResources().getString(R.string.could_not_rename_outfit), Toast.LENGTH_SHORT).show();
        }
    }

    /****************************************************************************************************/
    // CREATING TASK TO UPDATE OUTFIT IN THE API
    /****************************************************************************************************/

    private void createUpdateTask()
    {
        final SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);

        // BROWSE EACH ELEMENT OF THE OUTFIT TO GET THEIR PARAMETERS AND PUT THEM IN ARRAYS FOR THE REQUEST

        int[] types = new int[currentOutfit.getElements().size()];
        String[] paths = new String[currentOutfit.getElements().size()];
        String[] uuids = new String[currentOutfit.getElements().size()];
        String[] stored = new String[currentOutfit.getElements().size()];
        String[] colors = new String[currentOutfit.getElements().size()];

        for(int i = 0; i < currentOutfit.getElements().size(); i++)
        {
            // PUT CURRENT ELEMENT'S COLORS IN A STRING

            StringBuilder currentElementColorsToString = new StringBuilder();

            for(int j = 0; j < currentOutfit.getElements().get(i).getColors().size(); j++)
            {
                if((j + 1) == currentOutfit.getElements().get(i).getColors().size())
                {
                    currentElementColorsToString.append(String.valueOf(currentOutfit.getElements().get(i).getColors().get(j)));
                }

                else
                {
                    currentElementColorsToString.append(String.valueOf(currentOutfit.getElements().get(i).getColors().get(j)) + ",");
                }
            }

            // PUT CURRENT ELEMENT'S ATTRIBUTES IN THE ARRAYS

            types[i] = currentOutfit.getElements().get(i).getType();
            paths[i] = currentOutfit.getElements().get(i).getPath();
            uuids[i] = currentOutfit.getElements().get(i).getUuid();
            stored[i] = currentOutfit.getElements().get(i).getStoredOnApi() ? "1" : "0";
            colors[i] = String.valueOf(currentElementColorsToString);
        }

        // BROWSE EACH ELEMENT'S ATTRIBUTES TO PUT THEM IN A UNIQUE STRING

        StringBuilder typesToString = new StringBuilder();
        StringBuilder uuidsToString = new StringBuilder();
        StringBuilder pathsToString = new StringBuilder();
        StringBuilder storedToString = new StringBuilder();
        StringBuilder colorsToString = new StringBuilder();

        for(int i = 0; i < currentOutfit.getElements().size(); i++)
        {
            if((i + 1) == currentOutfit.getElements().size())
            {
                typesToString.append(types[i]);
                uuidsToString.append(uuids[i]);
                pathsToString.append(paths[i]);
                storedToString.append(stored[i]);
                colorsToString.append(colors[i]);
            }

            else
            {
                typesToString.append(types[i] + ";");
                uuidsToString.append(uuids[i] + ";");
                pathsToString.append(paths[i] + ";");
                storedToString.append(stored[i] + ";");
                colorsToString.append(colors[i] + ";");
            }
        }

        // PUT THE DATA IN A BUNDLE TO GIVE TO THE JOB SERVICE

        Bundle extras = new Bundle();

        extras.putString("outfitName", currentOutfit.getName());
        extras.putString("outfitUuid", currentOutfit.getUuid());
        extras.putString("token", sharedPreferences.getString("token", null));

        extras.putString("types", String.valueOf(typesToString));
        extras.putString("uuids", String.valueOf(uuidsToString));
        extras.putString("paths", String.valueOf(pathsToString));
        extras.putString("stored", String.valueOf(storedToString));
        extras.putString("colors", String.valueOf(colorsToString));

        // CREATE A NEW TASK FOR THE API REQUEST TO ADD THIS NEW OUTFIT

        Job job = dispatcher.newJobBuilder()
                .setService(WardrobeOutfitUpdateJobService.class)
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

    /****************************************************************************************************/
    // WHEN CANCELING RENAMING
    /****************************************************************************************************/

    public void cancelRenaming(View view)
    {
        outfitDetailBlockRename.setVisibility(View.GONE);
        outfitDetailBlock.setVisibility(View.VISIBLE);
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
