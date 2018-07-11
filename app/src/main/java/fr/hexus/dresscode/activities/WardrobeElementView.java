package fr.hexus.dresscode.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Environment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import java.io.File;
import java.util.ArrayList;

import fr.hexus.dresscode.classes.AppDatabaseCreation;
import fr.hexus.dresscode.classes.Constants;
import fr.hexus.dresscode.classes.WardrobeElement;
import fr.hexus.dresscode.enums.Colors;
import fr.hexus.dresscode.enums.Types;
import fr.hexus.dresscode.classes.GlideApp;
import fr.hexus.dresscode.retrofit.WardrobeElementCreateJobService;
import fr.hexus.dresscode.retrofit.WardrobeElementRemoveJobService;

public class WardrobeElementView extends AppCompatActivity
{
    private WardrobeElement wardrobeElement;

    private TextView elementType;
    private TextView elementColor;
    private ImageView elementPicture;

    private FirebaseJobDispatcher dispatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wardrobe_element_view);

        dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(getApplicationContext()));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_return_white);
        getSupportActionBar().setTitle(getResources().getString(R.string.menu_detail));
        getSupportActionBar().setIcon(R.drawable.ic_description_white);

        wardrobeElement = (WardrobeElement) getIntent().getSerializableExtra(getResources().getString(R.string.WARDROBE_ELEMENT));

        elementType = findViewById(R.id.elementType);
        elementColor = findViewById(R.id.elementColor);

        elementPicture = findViewById(R.id.elementPicture);

        elementPicture.setScaleType(ImageView.ScaleType.CENTER);

        elementType.setText(elementType.getText() + " : " + getResources().getString(getResources().getIdentifier(Types.getKey(wardrobeElement.getType()), "string", getPackageName())));

        StringBuilder colors = new StringBuilder();

        ArrayList<Integer> colorsList = wardrobeElement.getColors();

        for(int x = 0; x < wardrobeElement.getColors().size(); x++)
        {
            colors.append((x + 1) == colorsList.size()
                    ? getResources().getString(getResources().getIdentifier(Colors.getKey(colorsList.get(x)), "string", getPackageName()))
                    : getResources().getString(getResources().getIdentifier(Colors.getKey(colorsList.get(x)), "string", getPackageName())) + " ");
        }

        elementColor.setText(colors);

        GlideApp.with(this)
                .load(Environment.getExternalStorageDirectory() + String.valueOf(wardrobeElement.getPath()))
                .placeholder(R.drawable.ic_launcher_background)
                .into((ImageView) findViewById(R.id.elementPicture));
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null)
        {
            if(data.getSerializableExtra("wardrobeElement") != null)
            {
                wardrobeElement = (WardrobeElement) data.getSerializableExtra("wardrobeElement");

                String type = String.valueOf(elementType.getText());
                String[] typeArray = type.split(" : ");

                String color = String.valueOf(elementColor.getText());
                String[] colorArray = color.split(" : ");

                elementType.setText(typeArray[0] + " : " + getResources().getString(getResources().getIdentifier(Types.getKey(wardrobeElement.getType()), "string", getPackageName())));

                StringBuilder colors = new StringBuilder();

                ArrayList<Integer> colorsList = wardrobeElement.getColors();

                for(int x = 0; x < wardrobeElement.getColors().size(); x++)
                {
                    colors.append((x + 1) == colorsList.size()
                            ? getResources().getString(getResources().getIdentifier(Colors.getKey(colorsList.get(x)), "string", getPackageName()))
                            : getResources().getString(getResources().getIdentifier(Colors.getKey(colorsList.get(x)), "string", getPackageName())) + " ");
                }

                elementColor.setText(colors);

                GlideApp.with(this)
                        .load(Environment.getExternalStorageDirectory() + String.valueOf(wardrobeElement.getPath()))
                        .placeholder(R.drawable.ic_launcher_background)
                        .into((ImageView) findViewById(R.id.elementPicture));
            }
        }
    }

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

    public void onClickRemove(View view)
    {
        new AlertDialog.Builder(this)
        .setTitle(getResources().getString(R.string.wardrobe_element_detail_remove_alert_title))
        .setMessage(getResources().getString(R.string.wardrobe_element_detail_remove_alert_message))
        .setIcon(R.drawable.ic_info_red)
        .setPositiveButton(android.R.string.yes, (dialog, whichButton) ->
        {
            dispatcher.cancel(wardrobeElement.getUuid());

            if(removeElementFromLocal())
            {
                final SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_FILE_NAME, MODE_PRIVATE);

                String token = sharedPreferences.getString("token", null);

                Bundle extras = new Bundle();
                extras.putString("token", token);
                extras.putString("uuid", wardrobeElement.getUuid());

                Job job = dispatcher.newJobBuilder()
                        .setService(WardrobeElementRemoveJobService.class)
                        .setTag(wardrobeElement.getUuid())
                        .setRecurring(false)
                        .setLifetime(Lifetime.FOREVER)
                        .setTrigger(Trigger.executionWindow(0, 15))
                        .setReplaceCurrent(false)
                        .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                        .setConstraints(Constraint.ON_ANY_NETWORK)
                        .setExtras(extras)
                        .build();

                dispatcher.mustSchedule(job);
                finish();
            }
        })
        .setNegativeButton(android.R.string.no, null).show();
    }

    public boolean removeElementFromLocal()
    {
        AppDatabaseCreation appDatabaseCreation = new AppDatabaseCreation(this);

        SQLiteDatabase database = appDatabaseCreation.getReadableDatabase();

        int deletedRows = database.delete(Constants.WARDROBE_TABLE_NAME, "id = ?", new String[] { String.valueOf(wardrobeElement.getId()) });

        File pictureToRemove = new File(Environment.getExternalStorageDirectory() + String.valueOf(wardrobeElement.getPath()));

        pictureToRemove.delete();

        database.close();

        return deletedRows == 1;
    }

    public void onClickEdit(View view)
    {
        Intent editIntent = new Intent(this, WardrobeElementEdit.class);
        editIntent.putExtra("wardrobeElement", wardrobeElement);
        startActivityForResult(editIntent, 1);
    }
}
