package fr.hexus.dresscode.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fr.hexus.dresscode.classes.Outfit;
import fr.hexus.dresscode.classes.WardrobeElement;

public class WardrobeOutfitAdd extends AppCompatActivity
{
    private List<WardrobeElement> wardrobeElements = new ArrayList<>();
    private WardrobeElementAdapter wardrobeElementAdapter;

    private LinearLayout outfitElementsList;
    private FloatingActionButton outfitElementsAdd;
    private ListView myList;
    private TextView outfitElementsEmpty;

    private EditText outfitNameValue;

    private FloatingActionButton outfitAddFormSave;

    private static final int PICK_WARDROBE_ELEMENT_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wardrobe_outfit_add);

        outfitNameValue = findViewById(R.id.outfitNameValue);
        outfitAddFormSave = findViewById(R.id.outfitAddFormSave);

        outfitAddFormSave.setVisibility(View.GONE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_return_white);
        getSupportActionBar().setTitle(getResources().getString(R.string.header_title_add_wardrobe_outfit));
        getSupportActionBar().setIcon(R.drawable.ic_add_circle_white);

        outfitNameValue.addTextChangedListener(new TextWatcher()
        {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                checkForm();
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        myList = findViewById(R.id.myList);

        outfitElementsEmpty = findViewById(R.id.outfitElementsEmpty);

        outfitElementsList = findViewById(R.id.outfitElementsList);

        outfitElementsAdd = findViewById(R.id.outfitElementsAdd);

        outfitElementsAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivityForResult(new Intent(getApplicationContext(), ChooseWardrobeElement.class), PICK_WARDROBE_ELEMENT_REQUEST);
            }
        });

        wardrobeElementAdapter = new WardrobeElementAdapter(this, wardrobeElements);

        myList.setAdapter(wardrobeElementAdapter);
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

    /******************************************************************************************/
    // GET THE WARDROBE ELEMENT SELECTED
    /******************************************************************************************/

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null)
        {
            if(data.getSerializableExtra("wardrobeElement") != null)
            {
                outfitElementsEmpty.setText("");

                wardrobeElements.add((WardrobeElement) data.getSerializableExtra("wardrobeElement"));

                wardrobeElementAdapter = new WardrobeElementAdapter(this, wardrobeElements);

                myList.setAdapter(wardrobeElementAdapter);

                checkForm();
            }
        }
    }

    /******************************************************************************************/
    // CHECK FORM BEFORE AUTHORIZING USER TO SAVE IT
    /******************************************************************************************/

    public void checkForm()
    {
        boolean isReady = true;

        if(outfitNameValue.getText().length() == 0)
        {
            isReady = false;
        }

        if(wardrobeElements.size() < 2)
        {
            isReady = false;
        }

        if(isReady)
        {
            outfitAddFormSave.setVisibility(View.VISIBLE);
        }
    }

    /******************************************************************************************/
    // WHEN FORM IS SUBMITTED
    /******************************************************************************************/

    public void onSubmitForm(View view)
    {
        Outfit newOutfit = new Outfit(0, String.valueOf(outfitNameValue.getText()), wardrobeElements);

        if(newOutfit.saveOutfitInDatabase(this))
        {
            Toast.makeText(this, getResources().getString(R.string.outfit_add_form_database_insert_success), Toast.LENGTH_SHORT).show();
            finish();
        }

        else
        {
            Toast.makeText(this, getResources().getString(R.string.outfit_add_form_database_insert_error), Toast.LENGTH_SHORT).show();
        }
    }
}
