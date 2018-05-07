package fr.hexus.dresscode.dresscode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

public class WardrobeElementView extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wardrobe_element_view);

        LinearLayout linearLayout = findViewById(R.id.elementPicture);

        WardrobeElement wardrobeElement = (WardrobeElement) getIntent().getSerializableExtra(getResources().getString(R.string.WARDROBE_ELEMENT));

        TextView elementName = findViewById(R.id.elementName);
        TextView elementType = findViewById(R.id.elementType);
        TextView elementColor = findViewById(R.id.elementColor);
        TextView elementMaterial = findViewById(R.id.elementMaterial);

        elementName.setText(elementName.getText() + " : " + String.valueOf(wardrobeElement.getName()));
        elementType.setText(elementType.getText() + " : " + String.valueOf(wardrobeElement.getType()));
        elementColor.setText(elementColor.getText() + " : " + String.valueOf(wardrobeElement.getColor()));
        elementMaterial.setText(elementMaterial.getText() + " : " + String.valueOf(wardrobeElement.getMaterial()));

        File file = new File(Environment.getExternalStorageDirectory() + String.valueOf(wardrobeElement.getPath()));

        if(file.exists())
        {
            /*ImageView elementPicture = new ImageView(this);
            Bitmap bm = BitmapFactory.decodeFile(file.getPath());

            float multiplierFactor = linearLayout.getWidth() / bm.getWidth();

            bm.setWidth(linearLayout.getWidth());
            bm.setHeight(Math.round(bm.getHeight() * multiplierFactor));

            elementPicture.setImageBitmap(bm);

            bm.setWidth(25);

            linearLayout.addView(elementPicture);*/
        }

        else
        {
            TextView noPicture = new TextView(this);

            noPicture.setText(R.string.wardrobe_view_image_could_not_be_loaded);

            linearLayout.addView(noPicture);
        }
    }
}
