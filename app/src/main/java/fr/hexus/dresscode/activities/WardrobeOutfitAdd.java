package fr.hexus.dresscode.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.hexus.dresscode.classes.GlideApp;
import fr.hexus.dresscode.classes.Outfit;
import fr.hexus.dresscode.classes.WardrobeElement;
import fr.hexus.dresscode.enums.Colors;
import fr.hexus.dresscode.enums.Types;

public class WardrobeOutfitAdd extends AppCompatActivity
{
    private List<WardrobeElement> wardrobeElements = new ArrayList<>();

    private LinearLayout outfitElementsList;
    private FloatingActionButton outfitElementsAdd;
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

        outfitElementsEmpty = findViewById(R.id.outfitElementsEmpty);

        outfitElementsList = findViewById(R.id.outfitElementsList);

        outfitElementsAdd = findViewById(R.id.outfitElementsAdd);

        outfitElementsAdd.setOnClickListener(view ->
        {
            int[] ids = new int[wardrobeElements.size()];

            for(int i = 0; i < wardrobeElements.size(); i++)
            {
                ids[i] = wardrobeElements.get(i).getId();
            }

            Intent getItemIntent = new Intent(getApplicationContext(), ChooseWardrobeElement.class);
            getItemIntent.putExtra("selectedItems", ids);
            startActivityForResult(getItemIntent, PICK_WARDROBE_ELEMENT_REQUEST);
        });
    }

    /******************************************************************************************/
    //  CLICK ON BUTTON TO RETURN TO PREVIOUS ACTIVITY
    /******************************************************************************************/

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
                WardrobeElement newWardrobeElement = (WardrobeElement) data.getSerializableExtra("wardrobeElement");

                LinearLayout newElement = new LinearLayout(getApplicationContext());
                newElement.setOrientation(LinearLayout.HORIZONTAL);
                newElement.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                LinearLayout newElementData = new LinearLayout(getApplicationContext());
                newElementData.setOrientation(LinearLayout.VERTICAL);
                newElementData.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                newElementData.setWeightSum(1);

                /****************************************************************************************************/
                // PICTURE
                /****************************************************************************************************/

                CircleImageView wardrobeElementPicture = new CircleImageView(getApplicationContext());

                wardrobeElementPicture.setScaleType(ImageView.ScaleType.CENTER_CROP);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int) (64 * getResources().getDisplayMetrics().density), (int) (64 * getResources().getDisplayMetrics().density));
                lp.setMargins((int) (8 * getResources().getDisplayMetrics().density), (int) (8 * getResources().getDisplayMetrics().density), (int) (8 * getResources().getDisplayMetrics().density), (int) (8 * getResources().getDisplayMetrics().density));
                wardrobeElementPicture.setLayoutParams(lp);

                GlideApp.with(newElement)
                        .load(Environment.getExternalStorageDirectory() + String.valueOf(newWardrobeElement.getPath()))
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(wardrobeElementPicture);

                newElement.addView(wardrobeElementPicture);

                /****************************************************************************************************/
                // TYPE
                /****************************************************************************************************/

                TextView newElementType = new TextView(getApplicationContext());
                newElementType.setText(getResources().getString(getResources().getIdentifier(Types.getKey(newWardrobeElement.getType()), "string", getPackageName())));

                /****************************************************************************************************/
                // COLORS
                /****************************************************************************************************/

                TextView newElementColors = new TextView(getApplicationContext());

                ArrayList<Integer> colorsList = newWardrobeElement.getColors();

                StringBuilder colors = new StringBuilder();

                for(int x = 0; x < colorsList.size(); x++)
                {
                    colors.append((x + 1) == colorsList.size()
                            ? getResources().getString(getResources().getIdentifier(Colors.getKey(colorsList.get(x)), "string", getPackageName()))
                            : getResources().getString(getResources().getIdentifier(Colors.getKey(colorsList.get(x)), "string", getPackageName())) + " ");
                }

                newElementColors.setText(colors);

                /****************************************************************************************************/
                // DELETE BUTTON
                /****************************************************************************************************/

                ImageView remove = new ImageView(getApplicationContext());

                LinearLayout.LayoutParams removeLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                remove.setImageResource(R.drawable.ic_delete);
                removeLayoutParams.setMargins((int) (8 * getResources().getDisplayMetrics().density), (int) (8 * getResources().getDisplayMetrics().density), (int) (8 * getResources().getDisplayMetrics().density), (int) (8 * getResources().getDisplayMetrics().density));
                remove.setLayoutParams(removeLayoutParams);

                remove.setOnClickListener(view ->
                {
                    View currentElement = (View) view.getParent();
                    LinearLayout outfitElementsList = findViewById(R.id.outfitElementsList);

                    outfitElementsList.removeView(currentElement);

                    wardrobeElements.remove(newWardrobeElement);

                    if(wardrobeElements.size() == 0)
                    {
                        outfitElementsEmpty.setVisibility(View.VISIBLE);
                    }
                });

                /****************************************************************************************************/
                // PUTTING VIEWS
                /****************************************************************************************************/

                newElementData.addView(newElementType);
                newElementData.addView(newElementColors);

                newElement.addView(newElementData);
                newElement.addView(remove);

                outfitElementsList.addView(newElement);

                outfitElementsEmpty.setVisibility(View.GONE);

                wardrobeElements.add(newWardrobeElement);

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
