package fr.hexus.dresscode.dresscode;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class WardrobeElementView extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wardrobe_element_view);

        WardrobeElement wardrobeElement = (WardrobeElement) getIntent().getSerializableExtra(getResources().getString(R.string.WARDROBE_ELEMENT));

        TextView elementName = findViewById(R.id.elementName);
        TextView elementType = findViewById(R.id.elementType);
        TextView elementColor = findViewById(R.id.elementColor);
        TextView elementMaterial = findViewById(R.id.elementMaterial);

        ImageView elementPicture = findViewById(R.id.elementPicture);

        elementName.setText(elementName.getText() + " : " + String.valueOf(wardrobeElement.getName()));
        elementType.setText(elementType.getText() + " : " + String.valueOf(wardrobeElement.getType()));
        elementColor.setText(elementColor.getText() + " : " + String.valueOf(wardrobeElement.getColor()));
        elementMaterial.setText(elementMaterial.getText() + " : " + String.valueOf(wardrobeElement.getMaterial()));

        GlideApp.with(this)
                .load(Environment.getExternalStorageDirectory() + String.valueOf(wardrobeElement.getPath()))
                .placeholder(R.drawable.ic_launcher_background)
                .centerCrop()
                .override(elementPicture.getWidth())
                .fitCenter()
                .into((ImageView) findViewById(R.id.elementPicture));
    }

    public void onClickReturn(View view)
    {
        finish();
    }
}
