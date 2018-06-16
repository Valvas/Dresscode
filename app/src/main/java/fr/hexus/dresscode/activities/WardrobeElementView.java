package fr.hexus.dresscode.activities;

import android.content.DialogInterface;
import android.content.Intent;
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

import java.io.File;

import fr.hexus.dresscode.classes.Constants;
import fr.hexus.dresscode.classes.WardrobeElement;
import fr.hexus.dresscode.enums.Colors;
import fr.hexus.dresscode.enums.Types;
import fr.hexus.dresscode.classes.GlideApp;

public class WardrobeElementView extends AppCompatActivity
{
    private WardrobeElement wardrobeElement;
    private DrawerLayout myDrawer;

    private TextView elementType;
    private TextView elementColor;
    private ImageView elementPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wardrobe_element_view);

        myDrawer = findViewById(R.id.myDrawer);

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
        elementColor.setText(elementColor.getText() + " : " + getResources().getString(getResources().getIdentifier(Colors.getKey(wardrobeElement.getColor()), "string", getPackageName())));

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
                elementColor.setText(colorArray[0] + " : " + getResources().getString(getResources().getIdentifier(Colors.getKey(wardrobeElement.getColor()), "string", getPackageName())));

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
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int whichButton)
                {
                    if(removeElementFromLocal())
                    {
                        finish();
                    }
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

        return deletedRows == 1;
    }

    public void onClickEdit(View view)
    {
        Intent editIntent = new Intent(this, WardrobeElementEdit.class);
        editIntent.putExtra("wardrobeElement", wardrobeElement);
        startActivityForResult(editIntent, 1);
    }
}
