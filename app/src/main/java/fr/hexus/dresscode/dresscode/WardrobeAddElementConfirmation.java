package fr.hexus.dresscode.dresscode;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class WardrobeAddElementConfirmation extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wardrobe_add_element_confirmation);
    }

    public void onReturn(View view)
    {
        finish();
    }
}
