package fr.hexus.dresscode.dresscode;

import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class WardrobeOutfitAdd extends AppCompatActivity
{
    private List<WardrobeElement> wardrobeElements = new ArrayList<>();
    private WardrobeElementAdapter wardrobeElementAdapter;

    private LinearLayout outfitElementsList;
    private FloatingActionButton outfitElementsAdd;
    private ListView myList;
    private TextView outfitElementsEmpty;

    private static final int PICK_WARDROBE_ELEMENT_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wardrobe_outfit_add);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_return_white);
        getSupportActionBar().setTitle(getResources().getString(R.string.header_title_add_wardrobe_outfit));
        getSupportActionBar().setIcon(R.drawable.ic_add_circle_white);

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
            }
        }
    }
}
