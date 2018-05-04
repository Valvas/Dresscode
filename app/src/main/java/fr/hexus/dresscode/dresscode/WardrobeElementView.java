package fr.hexus.dresscode.dresscode;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

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

        elementName.setText(elementName.getText() + " : " + String.valueOf(wardrobeElement.getName()));
        elementType.setText(elementType.getText() + " : " + String.valueOf(wardrobeElement.getType()));
        elementColor.setText(elementColor.getText() + " : " + String.valueOf(wardrobeElement.getColor()));
        elementMaterial.setText(elementMaterial.getText() + " : " + String.valueOf(wardrobeElement.getMaterial()));
    }
}
