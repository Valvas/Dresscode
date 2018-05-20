package fr.hexus.dresscode.dresscode;

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

        ImageView elementPicture = findViewById(R.id.elementPicture);

        elementPicture.setScaleType(ImageView.ScaleType.CENTER);

        elementName.setText(elementName.getText() + " : " + String.valueOf(wardrobeElement.getName()));
        elementType.setText(elementType.getText() + " : " + getResources().getString(getResources().getIdentifier(Types.getKey(wardrobeElement.getType()), "string", getPackageName())));
        elementColor.setText(elementColor.getText() + " : " + getResources().getString(getResources().getIdentifier(Colors.getKey(wardrobeElement.getColor()), "string", getPackageName())));

        GlideApp.with(this)
                .load(Environment.getExternalStorageDirectory() + String.valueOf(wardrobeElement.getPath()))
                .placeholder(R.drawable.ic_launcher_background)
                .into((ImageView) findViewById(R.id.elementPicture));
    }

    public void onClickReturn(View view)
    {
        finish();
    }
}
