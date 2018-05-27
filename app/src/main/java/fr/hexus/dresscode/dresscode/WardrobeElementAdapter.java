package fr.hexus.dresscode.dresscode;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class WardrobeElementAdapter extends ArrayAdapter<WardrobeElement>
{
    public WardrobeElementAdapter(@NonNull Context context, List<WardrobeElement> wardrobeElements)
    {
        super(context, 0, wardrobeElements);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View row = inflater.inflate(R.layout.activity_wardrobe_element_adapter, null);

        WardrobeElement currentWardrobeElement = getItem(position);

        TextView wardrobeElementType = row.findViewById(R.id.wardrobeElementType);
        TextView wardrobeElementColor = row.findViewById(R.id.wardrobeElementColor);

        CircleImageView wardrobeElementPicture = row.findViewById(R.id.wardrobeElementPicture);

        wardrobeElementPicture.setScaleType(ImageView.ScaleType.CENTER_CROP);

        GlideApp.with(row)
                .load(Environment.getExternalStorageDirectory() + String.valueOf(currentWardrobeElement.getPath()))
                .placeholder(R.drawable.ic_launcher_background)
                .into(wardrobeElementPicture);

        wardrobeElementColor.setText(parent.getResources().getString(parent.getResources().getIdentifier(Colors.getKey(currentWardrobeElement.getColor()), "string", getContext().getPackageName())));
        wardrobeElementType.setText(parent.getResources().getString(parent.getResources().getIdentifier(Types.getKey(currentWardrobeElement.getType()), "string", getContext().getPackageName())));

        return row;
    }
}
